package com.example.fang.model;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;

public class CourseParser {
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
    public static ArrayList<Course> parseCourseListFromJson(String data){
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
}
