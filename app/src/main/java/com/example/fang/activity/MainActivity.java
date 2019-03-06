package com.example.fang.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.fang.model.ActivatedUser;
import com.example.fang.model.Captcha;
import com.example.fang.model.LoginInfo;
import com.example.fang.model.LoginStatusEnum;
import com.example.fang.utils.MyJSONParser;
import com.example.fang.utils.SecuritySharedPreference;
import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.SuperToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;
import com.google.gson.Gson;
//import com.muddzdev.styleabletoast.StyleableToast;


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
    private CheckBox ckbRememberPwd;
    private String mainUrl;
    private LinearLayout llYzm;
    private boolean isActivating = false;

    private SecuritySharedPreference ssp;

    public Handler loginHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            LoginInfo info = (LoginInfo)message.obj;
            LoginStatusEnum status = info.getStatus();


            switch (status){
                case SUCCESS:
                    Toast.makeText(MainActivity.this,"welcome "+info.getName(), Toast.LENGTH_SHORT).show();
                    MainActivity.this.showMainInter();
                    break;
                case WRONG_PWD:
                case UNKNOWN:
                    Toast.makeText(MainActivity.this,"用户密码错误", Toast.LENGTH_SHORT).show();
                    resetPwdEdt();
                    break;
                case NOT_ACTIVATED:
                    Toast.makeText(MainActivity.this,"用户尚未激活 ", Toast.LENGTH_SHORT).show();
                    resetPwdEdt();
                    showActivation();
                    break;
            }
            return false;
        }
    });

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
        ckbRememberPwd = findViewById(R.id.ckb_remember_pwd);   //记住密码
        //获取登录所需信息
        //getAccountYzm();

        btnLogin.setOnClickListener(this);
        about.setOnClickListener(this);
        ivCaptcha.setOnClickListener(this);
        ssp = new SecuritySharedPreference(getApplicationContext(), "settings", MODE_PRIVATE);
        loadOldData();
        getAccountYzm();
    }

    private void loadOldData() {
        SharedPreferences userInfo = getSharedPreferences("userInfo",MODE_PRIVATE);
        String id = userInfo.getString("stu_ID","").trim();
        if(!id.equals("")){
            etUsername.setText(id);
            etPassword.requestFocus();
        }
        if(ssp != null){
            //获取信息
            boolean isRemembered = ssp.getBoolean("is_save_pwd",false);
            if(isRemembered){
                String pwd = ssp.getString("password","");
                etPassword.setText(pwd);
                etPassword.setSelection(pwd.length());
                ckbRememberPwd.setChecked(true);
            }
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
    private void loginAccount(final String userID, final String password) {
        final String loginUrl = mainUrl + "/account/login/";
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
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String loginResult = response.body().string();
                Log.i("FROM /account/login ", loginResult);
                processLoginResult(loginResult, userID, password);

            }
        });
    }

    private void processLoginResult(String loginResult, String userID, String password) {
        try {
            JSONObject job = JSONObject.fromObject(loginResult);
            LoginStatusEnum code = MyJSONParser.parseLoginStatus(loginResult);
            LoginInfo info = null;
            switch (code){
                case SUCCESS:
                    String name = job.getString("real_name");
                    String token = job.getString("token");
                    info = new LoginInfo(name, userID, token, code);
                    if(ckbRememberPwd.isChecked()){
                        storeLoginInfo(info, password);
                    }else {
                        clearRemember();
                    }

                    break;
                case WRONG_PWD:
                case UNKNOWN:
                    info = new LoginInfo(code);
                    break;
                case NOT_ACTIVATED:
                    info = new LoginInfo(code);
                    break;
            }
            Message message = new Message();
            message.obj = info;
            loginHandler.sendMessage(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void storeLoginInfo(LoginInfo info, String password) {
        SharedPreferences userInfo = getSharedPreferences("userInfo",MODE_PRIVATE);
        SharedPreferences.Editor editor = userInfo.edit();
        editor.putString("real_name", info.getName());
        editor.putString("stu_ID", info.getId());
        editor.putString("token", info.getToken());
        editor.apply();
        if(ckbRememberPwd.isChecked()){
            rememberPwd(password);
        }else {
            clearRemember();
        }
    }

    private void resetPwdEdt() {
        etPassword.setText("");
    }



    private void clearRemember() {
        SecuritySharedPreference.Editor editor = ssp.edit();
        editor.putString("password", "");
        editor.putBoolean("is_save_pwd",false);
        editor.apply();
    }

    private void rememberPwd(String pwd) {
        SecuritySharedPreference ssp = new SecuritySharedPreference(getApplicationContext(), "settings",MODE_PRIVATE);
        SecuritySharedPreference.Editor editor = ssp.edit();
        editor.putString("password", pwd);
        editor.putBoolean("is_save_pwd",true);
        editor.apply();
    }

    private void showActivation() {
        llYzm.setVisibility(View.VISIBLE);
        ivCaptcha.setVisibility(View.VISIBLE);
        btnLogin.setText("激   活");
        isActivating = true;
        getAccountYzm();
    }


    //获取登录cookie并设置登录图片
    private void getAccountYzm(){
        if(!isActivating)
            return;
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
                        Captcha captchaInfo = gson.fromJson(info, Captcha.class);
                        String captchaDir = captchaInfo.getYzm_url();
                        String cookie = captchaInfo.getYzm_cookie();
                        saveCookie(cookie);
                        getCaptchaImg(mainUrl+"/account"+captchaDir);   //更新验证码图片
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void saveCookie(String cookie) {
        //将cookie值传入sp中
        SharedPreferences cookieSp = getSharedPreferences("cookie",MODE_PRIVATE);
        SharedPreferences.Editor editor = cookieSp.edit();
        editor.putString("cookie",cookie);
        editor.commit();
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
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                byte[] bytes = response.body().bytes();

                //把byte字节组装成图片
                final Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
