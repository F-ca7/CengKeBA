package com.example.fang.utils;

import android.util.Log;

import com.example.fang.model.Course;
import com.example.fang.model.LoginStatusEnum;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;

public class MyJSONParser {
    /*[
    {
    "data_id": 201720141631,
    "course_id": 20172014163,
    "name": "流域环境模型",
    "type": "自然与工程类",
    "school": "资源与环境科学学院",
    "major": "自然与工程类",
    "teacher": "张万顺",
    "credit": 2.0,
    "start_week": 1,
    "end_week": 13,
    "gap": 1,
    "day_in_week": 1,
    "start_time": 11,
    "end_time": 13,
    "area": "3",
    "building": "1",
    "room": "303",
    "weight": 0.0,
    "art": 0.0,
    "communication": 0.0,
    "society": 0.0,
    "internation": 0.0,
    "leader": 0.0,
    "science": 1.0,
    "logic": 0.0,
    "others": 0.0
    }]*/
    public static ArrayList<Course> parseCourseList(String data){
        ArrayList<Course> courseList = new ArrayList<>();
        JSONArray jsonArray = JSONArray.fromObject(data);
        JSONObject job;
        int count = jsonArray.length();

        for(int i =0; i<count; i++){
            job = jsonArray.getJSONObject(i);
            courseList.add(new Course(job.getString("name"), job.getString("teacher"),
                    job.getInt("start_week"), job.getInt("end_week"), job.getInt("gap"),
                    job.getInt("day_in_week"), job.getInt("start_time"), job.getInt("end_time"),
                    job.getString("area"), job.getString("building"), job.getString("room")));
        }
        return courseList;
    }


    /*{
        "is_activated": true,
        "term": "2018-2019学年下学期",
        "real_name": "周健恒",
        "grade": 2017,
        "token": "854ac057180bf9906304f310606e44ff19e72155",
        "school": "计算机学院"
    }*/
    public static LoginStatusEnum parseLoginStatus(String data){
        JSONObject job = JSONObject.fromObject(data);
        if(job.has("is_activated")) {
            boolean flag = job.getBoolean("is_activated");
            if(flag){
                Log.i("登录: ", "成功");
                return LoginStatusEnum.SUCCESS;
            }else {
                Log.i("登录: ", "未激活");
                return LoginStatusEnum.NOT_ACTIVATED;
            }
        }
        if(job.has("is_pwd_correct") && job.getString("is_pwd_correct").equals("false")){
            Log.i("登录: ", "密码错误");
            return LoginStatusEnum.WRONG_PWD;
        }
        //都不匹配，未知错误
        return LoginStatusEnum.UNKNOWN;
    }
}
