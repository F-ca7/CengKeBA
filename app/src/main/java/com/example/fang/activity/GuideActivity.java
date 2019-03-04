package com.example.fang.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.example.fang.adapter.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FANG on 2018/2/12.
 */

public class GuideActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private ViewPagerAdapter myAdapter;
    private List<View> views;
    private ImageView[] dots;
    private int[] ids={R.id.guide_1,R.id.guide_2,R.id.guide_3};
    private ImageView goOn;



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        init();
        initDots();
        initEvent();
    }

    private void init() {
        //使用layoutInflater来进行视图加载
        LayoutInflater inflater = LayoutInflater.from(this);
        //对集合进行实例化操作
        views = new ArrayList<View>();
        //layoutInflater的使用，root传入null
        views.add(inflater.inflate(R.layout.guide_one, null));
        views.add(inflater.inflate(R.layout.guide_two, null));
        views.add(inflater.inflate(R.layout.guide_three, null));
        //实例化适配器
        myAdapter = new ViewPagerAdapter(views, this);
        //加载viewPager对象
        viewPager =  findViewById(R.id.viewpager);
        //ViewPager绑定适配器
        viewPager.setAdapter(myAdapter);


        goOn=findViewById(R.id.goOn);
    }



    private void initDots(){
        dots=new ImageView[views.size()];
        for (int i=0;i<views.size();i++){
            dots[i]= (ImageView) findViewById(ids[i]);
        }
    }

    private void initEvent(){
        goOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(GuideActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                goOn.setVisibility(position==views.size()-1? View.VISIBLE : View.GONE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



    }


}

