package com.example.fang.model;

import android.app.Application;
import android.appwidget.AppWidgetProvider;
import android.content.SharedPreferences;
import android.support.v4.content.res.FontResourcesParserCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.fang.activity.MainActivity;
import com.example.fang.activity.R;
import com.google.gson.JsonObject;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Building {
    private OkHttpClient okHttpClient ;
    private String area;
    private String building;
    private Application application;


    public Building(String area, String building, Application application) {
        this.area = area;
        this.building = building;
        this.application = application;
        okHttpClient = new OkHttpClient();
    }

    public String getBuildingName(){
        String res;
        if(isNormalArea(area)){
            res = area+"区";
        }else {
            res = area;
        }
        return res+"-"+building;
    }

    private boolean isNormalArea(String str){
        Pattern pattern = Pattern.compile("[1-4]*");
        Matcher isArea = pattern.matcher(str);
        if( !isArea.matches() ){
            return false;
        }
        return true;
    }

    public ArrayList<Course> getCourseListInBuilding(){
        ArrayList<Course> courseList = new ArrayList<>();
        final String courseListUrl = application.getString(R.string.base_url)+ "/course/building_courses/";
        RequestBody requestBody = new FormBody.Builder()
                .add("area", area)
                .add("building", building).build();
        Request request = (new Request.Builder().url(courseListUrl)).post(requestBody).build();
        Log.i("FROM building","posting data..."+requestBody);

        Call call = okHttpClient.newCall(request);

        //用同步请求，否则异步更新UI界面得用别的方法
        try {
            Response response = call.execute();
            if (response.isSuccessful()){
                String courseInfo = response.body().string();
                Log.i("courselist:", courseInfo);
                courseList = CourseParser.parseCourseListFromJson(courseInfo);
            }else {
                Log.i("Fail","Cannot get from "+ courseListUrl);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return courseList;
    }




}
