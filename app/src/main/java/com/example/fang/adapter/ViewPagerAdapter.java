package com.example.fang.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by FANG on 2018/2/13.
 */

public class ViewPagerAdapter extends PagerAdapter{
    private List<View> views;//使用List来承载所有的view
    private Context context;//上下文

    /**
     * 构造函数和ListView的相像
     * @param views
     * @param context
     */
    public ViewPagerAdapter(List<View> views, Context context){
        this.views=views;
        this.context=context;
    }


    /**
     * view不需要是将其销毁
     * 不要使用super构造方法
     * @param container
     * @param position
     * @param object
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));//通过position来进行索引获取所要移除的view
    }


    /**
     * 加载view，类似与ListView的Adapter中的getView()
     * @param container
     * @param position
     * @return 当前的View
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ((ViewPager)container).addView(views.get(position));
        return views.get(position);
    }

    /**
     * 需复写的方法
     * 返回view的数量
     * @return
     */
    @Override
    public int getCount() {
        return views.size();
    }


    /**
     * 需复写的方法
     * 判断当前的view是不是我们需要的对象
     * @param view
     * @param object
     * @return
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }
}