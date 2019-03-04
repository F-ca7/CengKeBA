package com.example.fang.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.example.fang.utils.SecuritySharedPreference;


/**
 * Created by FANG on 2018/2/22.
 */

public class WelcomeActivity extends AppCompatActivity {
    private final long DELAY = 1500;
    Handler handler = new Handler();
    private SecuritySharedPreference ssp;
    private String temp_username;
    private String temp_password;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  //无title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);  //全屏
        setContentView(R.layout.activity_welcome);

        ssp = new SecuritySharedPreference(getApplicationContext(),"userInfo",MODE_PRIVATE);
        temp_username = ssp.getString("USERNAME","");
        temp_password = ssp.getString("PASSWORD","");




        SharedPreferences sp = getSharedPreferences("record",MODE_PRIVATE);
//      此处如果key "isFirstRun"对应的value没有值则默认为true，
        boolean isFirstRun = sp.getBoolean("isFirstRun",true);
        if(isFirstRun){
//            不要忘记commit提交
            sp.edit().putBoolean("isFirstRun",false).commit();
            Log.i("welcome界面","正在跳转Guide");
            toGuide();
        }else {
            Log.i("welcome界面","正在跳转Login");
            toLogin();
        }

    }

    protected void toGuide(){
//        延时1.5s跳转到Guide界面
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WelcomeActivity.this,GuideActivity.class);
                startActivity(intent);
                finish();
            }
        },DELAY);
    }

    protected void toLogin(){

//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                int result = SqliteDB.getInstance(getApplicationContext()).Query(temp_username,temp_password);
//                if(result==1){
//                    Intent intent = new Intent(WelcomeActivity.this,MainInterActivity.class);
//                    startActivity(intent);
//                    finish();
//                }else {
//                    Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
//
//            }
//        },DELAY);
        //        延时1.5s跳转到Guide界面
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },DELAY);

    }

}
