package com.example.fang.utils;

import android.content.Context;
import android.util.TypedValue;

public class PxDpUtils {
    //dp转换成像素

    /**
     * dp转像素
     *
     * @param value
     * @param context
     * @return 像素值
     */
    public static int dp2px(int value, Context context)
    {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,context.getResources().getDisplayMetrics() );
    }

//像素转换成dp

    /**
     * 像素转dp
     *
     * @param value
     * @param context
     * @return dp值
     */
    public static int dp2sp(int value,Context context)
    {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value,context.getResources().getDisplayMetrics() );
    }

}
