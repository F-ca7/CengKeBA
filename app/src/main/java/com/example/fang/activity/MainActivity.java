package com.example.fang.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.fang.model.ActivatedUser;
import com.example.fang.model.Captcha;
import com.google.gson.Gson;


import net.sf.json.*;

import org.apache.commons.logging.impl.LogFactoryImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.fang.utils.AppUtils.isNetworkConnected;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private OkHttpClient okHttpClient ;
    private Button btnLogin;
    //private Button reg;
    private EditText etUsername;
    private EditText etPassword;
    private EditText tvYzm;
    private ImageView about;
    private ImageView ivCaptcha;
    private String mainUrl;
    private LinearLayout llYzm;
    private boolean isActivating = false;

    private void showDial(String title, String message){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.show();
    }

    private void resetInput(){
        etUsername.setText("");
        etPassword.setText("");
        tvYzm.setText("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//   保存当前activity的状态信息
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        okHttpClient = new OkHttpClient();
        initView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<>();
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            }
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }

            if (permissions.size() != 0) {
                requestPermissionsForM(permissions);
            }
        }

    }

    private void initView(){
        mainUrl = getString(R.string.base_url);
        etUsername = findViewById(R.id.username);     //用户名文本框
        etPassword = findViewById(R.id.password);          //密码文本框
        tvYzm = findViewById(R.id.yzm);               //验证码文本框
        btnLogin = findViewById(R.id.login);        //登录按钮
        about = findViewById(R.id.about);           //关于按钮
        ivCaptcha = findViewById(R.id.captcha);    //验证码图片
        llYzm = findViewById(R.id.ll_yzm);

        //获取登录所需信息
        //getAccountYzm();

        btnLogin.setOnClickListener(this);
        about.setOnClickListener(this);
        ivCaptcha.setOnClickListener(this);

        loadOldData();
    }

    private void loadOldData() {
        SharedPreferences userInfo = getSharedPreferences("userInfo",MODE_PRIVATE);
        String id = userInfo.getString("stu_ID","").trim();
        if(!id.equals("")){
            etUsername.setText(id);
            etPassword.requestFocus();
        }
    }

    private void requestPermissionsForM(final ArrayList<String> per) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(per.toArray(new String[per.size()]), 1);
        }
    }

    public void onClick(View v){
        Intent intent = null;
        switch (v.getId()){
            case R.id.login:
                if(!isActivating){
                    Log.i("onClick", "loginBtn");
                    checkNetwork();
                    String userID = etUsername.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();
                    loginAccount(userID, password);
                }else {
                    activateAccount();
                }
                break;
            case R.id.about:
                Log.i("onClick", "about");
                intent = new Intent(MainActivity.this, AboutUsActivity.class);
                startActivity(intent);
                break;
            case R.id.captcha:
                Log.i("onClick", "yzm");
                checkNetwork();
                getAccountYzm();
                break;
        }
    }

    private boolean checkNetwork(){
        if(!isNetworkConnected(this)){
            Toast.makeText(getApplicationContext(),"无网络连接，请重试",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showMainInter() {
        Intent intent;
        intent = new Intent(MainActivity.this, MainInterActivity.class);
        startActivity(intent);
        this.finish();
    }

    //登录用户
    private void loginAccount(final String userID, String password) {
        final String loginUrl =mainUrl + "/account/login/";
        final EditText edPwd = findViewById(R.id.password);

        RequestBody requestBody = new FormBody.Builder()
                .add("username",userID)
                .add("password",password).build();
        Request request = (new Request.Builder().url(loginUrl)).post(requestBody).build();
        Log.i("activatingAccount","posting data...");
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("Fail","Cannot get from "+ loginUrl);
            }

            /*{
                "is_activated": true,
                "term": "2018-2019学年下学期",
                "real_name": "周健恒",
                "grade": 2017,
                "token": "854ac057180bf9906304f310606e44ff19e72155",
                "school": "计算机学院"
            }*/
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String loginInfo = response.body().string();
                Log.i("FROM /account/login ",loginInfo);
                try {
                    JSONObject job = JSONObject.fromObject(loginInfo);
                    if(job.has("is_activated")){
                        String activateStatus = job.getString("is_activated");
                        String name;
                        String token;
                        Log.i("登录: ", activateStatus);
                        if(activateStatus.equals("true")){
                            Log.i("登录: ", "成功");
                            name = job.getString("real_name");
                            token = job.getString("token");
                            SharedPreferences userInfo = getSharedPreferences("userInfo",MODE_PRIVATE);
                            SharedPreferences.Editor editor = userInfo.edit();
                            editor.putString("real_name", name);
                            editor.putString("stu_ID", userID);
                            editor.putString("token", token);
                            editor.apply();
                            Log.i("FROM /account/login ","Login success!");
                            Looper.prepare();
                            Toast.makeText(MainActivity.this,"welcome "+name, Toast.LENGTH_SHORT).show();
                            MainActivity.this.showMainInter();
                            Looper.loop();
                        }
                    } else {
                        Log.i("登录: ", "失败");
                        Looper.prepare();
                        if(job.has("is_pwd_correct")){
                            String passCorrect = job.getString("is_pwd_correct");
                            if(passCorrect.equals("false")){
                                Toast.makeText(MainActivity.this,"用户密码错误", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this,"用户尚未激活", Toast.LENGTH_SHORT).show();
                            edPwd.setText("");
                            showActivation();
                        }
                        Looper.loop();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void showActivation() {
        llYzm.setVisibility(View.VISIBLE);
        btnLogin.setText("激   活");
        isActivating = true;
    }


    //获取登录cookie并设置登录图片
    private void getAccountYzm(){
        String dir = "/account/yzm/";
        //将域名与虚拟目录拼接起来
        final String request_url = mainUrl+dir;
        Request request = new Request.Builder().url(request_url).build();
        Log.i("yzm", "Getting yzm...");
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("Fail","Cannot get from "+request_url);
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    if(response.isSuccessful()){
                        String info = response.body().string();

                        Log.i("FROM /account/yzm/ ",info);
                        Gson gson = new Gson();
                        Captcha captchaInfo = gson.fromJson(info,Captcha.class);
                        String captchaDir = captchaInfo.getYzm_url();
                        String cookie = captchaInfo.getYzm_cookie();

                        //将cookie值传入sp中
                        SharedPreferences cookieSp = getSharedPreferences("cookie",MODE_PRIVATE);
                        SharedPreferences.Editor editor = cookieSp.edit();
                        editor.putString("cookie",cookie);
                        editor.commit();

                        getCaptchaImg(mainUrl+"/account"+captchaDir);   //更新验证码图片


                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    //设置登录图片
    private void getCaptchaImg(String image_url){
        /*http://47.106.107.107/account/media/yzm/BE939C62AA194C917D17C37A912A00E8.jpg*/

        Request request = new Request.Builder().url(image_url).build();

        Call call = okHttpClient.newCall(request);

        //请求加入队列
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("getCaptchaImg","Failed...");
                //此处处理请求失败的业务逻辑
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //我写的这个例子是请求一个图片
                //response的body是图片的byte字节
                byte[] bytes = response.body().bytes();
                //response.body().close();

                //把byte字节组装成图片
                final Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                //回调是运行在非ui主线程，
                //数据请求成功后，在主线程中更新
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //网络图片请求成功，更新到主线程的ImageView
                        ivCaptcha.setImageBitmap(bmp);
                    }
                });
            }
        });

    }


    //激活用户
    private void activateAccount(){
        final String activateUrl =mainUrl+"/account/activate/";
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String yzm_text = tvYzm.getText().toString().trim();
        SharedPreferences cookieSp = getSharedPreferences("cookie",MODE_PRIVATE);
        String yzm_cookie =cookieSp.getString("cookie","");
        RequestBody requestBody = new FormBody.Builder()
                .add("username",username)
                .add("password",password)
                .add("yzm_text",yzm_text)
                .add("yzm_cookie",yzm_cookie).build();
        Request request = (new Request.Builder().url(activateUrl)).post(requestBody).build();
        Log.i("activatingAccount","posting data...");
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("Fail","Cannot get from "+activateUrl);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String info = response.body().string();
                Log.i("FROM /account/yzm/ ",info);
                Gson gson = new Gson();
                ActivatedUser activatedUser = gson.fromJson(info,ActivatedUser.class);
                updateUserInfo(activatedUser);
            }
        });
    }

    private void updateUserInfo(ActivatedUser user){
        String activatedUsername = user.getUsername();
        List<String> activatedUserTerm = user.getTerm();
        SharedPreferences userInfo = getSharedPreferences("userInfo",MODE_PRIVATE);
        SharedPreferences.Editor editor = userInfo.edit();
        editor.putString("username",activatedUsername);
        editor.putString("term-year",activatedUserTerm.get(0));
        editor.putString("term-week",activatedUserTerm.get(1));
        editor.apply();
    }

}
