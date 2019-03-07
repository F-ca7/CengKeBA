package com.example.fang.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferenceUtils {
    public static String getCurrentToken(Context context){
        SharedPreferences sp = context.getSharedPreferences("userInfo",MODE_PRIVATE);
        String token =  sp.getString("token","");
        return token;
    }

}
