package com.example.fang.activity;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.sql.Time;

/**
 * Created by FANG on 2018/2/16.
 */

public class AboutUsActivity extends AppCompatActivity {
    private ImageView ivAboutUs;
    private final int CLICK_COUNT = 5;  //连续点击次数
    private final int CLICK_TIME_SPAN = 1000;  //在1000ms以内
    private long[] mHits = new long[CLICK_COUNT];
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        initView();
    }

    private void initView() {
        ivAboutUs = findViewById(R.id.iv_bout_us);
        ivAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //每点击一次 实现左移一格数据
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                //当0处的值大于当前时间-1000时  证明在1000秒内点击了5次
                if (mHits[0] > SystemClock.uptimeMillis() - CLICK_TIME_SPAN) {
                    Toast.makeText(getApplicationContext(), getString(R.string.about_extra), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}
