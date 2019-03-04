package com.example.fang.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fang.utils.ImageUtil;
import com.example.fang.utils.SecuritySharedPreference;

/**
 * Created by FANG on 2018/2/17.
 */

public class UserCenterActivity extends AppCompatActivity implements View.OnClickListener, ImageUtil.CropHandler{
    private Button show_course;
    private Button recom_course;
    private Button course_history;
    private Button study_room;
    private Button logout;
    private TextView username;
    private SharedPreferences sp;
    private ImageView photo;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);
        username = findViewById(R.id.name);
        show_course = findViewById(R.id.show_course);
        recom_course = findViewById(R.id.course_recommend);
        course_history = findViewById(R.id.course_history);
        study_room = findViewById(R.id.study_room);
        logout = findViewById(R.id.logout);
        photo = findViewById(R.id.photo);
        sp = getSharedPreferences("userInfo",MODE_PRIVATE);

        String name = sp.getString("real_name",null);
//        返回当前用户名
        username.setText(name);

        photo.setOnClickListener(this);
        show_course.setOnClickListener(this);
        recom_course.setOnClickListener(this);
        course_history.setOnClickListener(this);
        study_room.setOnClickListener(this);
        logout.setOnClickListener(this);

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
                Toast.makeText(getApplicationContext(),"即将开发",Toast.LENGTH_SHORT).show();
                break;
            case R.id.logout:
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.apply();
                Intent logout_intent = new Intent(UserCenterActivity.this, MainActivity.class);
                logout_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(logout_intent);
                break;


        }
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
