package com.example.fang.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fang.utils.ImageUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by FANG on 2018/2/17.
 */

public class UserCenterActivity extends AppCompatActivity implements View.OnClickListener, ImageUtil.CropHandler{
    private Button btnShowCourse;
    private Button btnRecomCourse;
    private Button btnCourseHistory;
    private Button btnStudyRoom;
    private Button btnLogout;
    private TextView username;
    private SharedPreferences spUser;
    private ImageView photo;
    private String mainUrl;
    private Toast toast;
    private OkHttpClient okHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);
        initView();

    }

    private void initView(){
        okHttpClient = new OkHttpClient();
        mainUrl = getString(R.string.base_url);
        username = findViewById(R.id.name);
        btnShowCourse = findViewById(R.id.show_course);
        btnRecomCourse = findViewById(R.id.course_recommend);
        btnCourseHistory = findViewById(R.id.course_history);
        btnStudyRoom = findViewById(R.id.study_room);
        btnLogout = findViewById(R.id.logout);
        photo = findViewById(R.id.photo);
        toast = new Toast(this);

        spUser = getSharedPreferences("userInfo",MODE_PRIVATE);

        String name = spUser.getString("real_name","");
        username.setText(name);

        photo.setOnClickListener(this);
        btnShowCourse.setOnClickListener(this);
        btnRecomCourse.setOnClickListener(this);
        btnCourseHistory.setOnClickListener(this);
        btnStudyRoom.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.photo:
                //选择相册
                Intent galleryIntent = ImageUtil
                        .getCropHelperInstance()
                        .buildGalleryIntent();
                startActivityForResult(galleryIntent,
                        ImageUtil.REQUEST_GALLERY);
                break;
            case R.id.show_course:
                Intent course_intent = new Intent(UserCenterActivity.this, CourseActivity.class);
                startActivity(course_intent);
                break;
            case R.id.course_recommend:
            case R.id.course_history:
            case R.id.study_room:
                toast.setText("即将开发");
                toast.show();
                break;
            case R.id.logout:
                userLogout();
                break;
        }
    }

    private void userLogout() {
        final String logoutUrl = mainUrl + "/account/logout/";
        String token =  spUser.getString(getString(R.string.sp_user_token),"");

        RequestBody requestBody = new FormBody.Builder()
                .add("token", token).build();
        Request request = (new Request.Builder().url(logoutUrl)).post(requestBody).build();
        Log.i("logoutAccount","posting data...");
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("Fail","Cannot get from "+ logoutUrl);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String loginResult = response.body().string();
                Log.i("登出", loginResult);
            }
        });
        clearUserdata();

        Intent logoutIntent = new Intent(UserCenterActivity.this, MainActivity.class);
        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(logoutIntent);
    }

    private void clearUserdata() {
        SharedPreferences.Editor editor = spUser.edit();
        editor.clear();
        editor.apply();
        SharedPreferences.Editor settingEditor = getSharedPreferences("settings",MODE_PRIVATE).edit();
        //不保存密码
        settingEditor.putBoolean(getString(R.string.sp_settings_is_save_pwd),false);
        settingEditor.remove(getString(R.string.sp_settings_pwd));
        settingEditor.apply();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageUtil.getCropHelperInstance().sethandleResultListerner( UserCenterActivity.this, requestCode, resultCode, data);
    }

    @Override
    public void onPhotoCropped(Bitmap photo, int requestCode) {
        switch (requestCode){
            case ImageUtil.RE_GALLERY:
                this.photo.setImageBitmap(photo);
                break;
        }
    }

    @Override
    public void onCropCancel() {

    }

    @Override
    public void onCropFailed(String message) {

    }

    @Override
    public Activity getContext() {
        return UserCenterActivity.this;
    }

}
