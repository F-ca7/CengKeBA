package com.example.fang.model;

import android.widget.Toast;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by FANG on 2018/2/18.
 */

public class Course implements Serializable {


    public Course(String name, String teacher, int day_in_week,int start_time, int end_time, String room) {
        this.name = name;
        this.teacher = teacher;
        this.day_in_week = day_in_week;
        this.start_time = start_time;
        this.end_time = end_time;
        this.room = room;
    }

    public Course(String name, String teacher, int start_week, int end_week, int gap,int day_in_week, int start_time, int end_time, String area, String building, String room) {
        this.name = name;
        this.teacher = teacher;
        this.start_week = start_week;
        this.end_week = end_week;
        this.gap = gap;
        this.day_in_week = day_in_week;
        this.start_time = start_time;
        this.end_time = end_time;
        this.area = area;
        this.building = building;
        this.room = room;
    }

    /**
     * data_id : 201720053411
     * course_id : 20172005341
     * name : 创业与法律
     * type : 研究与领导类
     * school : 经济与管理学院
     * major : 研究与领导类
     * teacher : 樊志勇
     * credit : 1.0
     * start_week : 0
     * end_week : 0
     * gap : 0
     * day_in_week : 0
     * start_time : 0
     * end_time : 0
     * area : 0
     * building : 0
     * room : 0
     * weight : 0.0
     */

    private long data_id;
    private long course_id;
    private String name;
    private String type;
    private String school;
    private String major;
    private String teacher;
    private double credit;
    private int start_week;
    private int end_week;
    private int gap;
    private int day_in_week;
    private int start_time;
    private int end_time;
    private String area;
    private String building;
    private String room;
    private double weight;


    public long getData_id() {
        return data_id;
    }

    public void setData_id(long data_id) {
        this.data_id = data_id;
    }

    public long getCourse_id() {
        return course_id;
    }

    public void setCourse_id(long course_id) {
        this.course_id = course_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    public int getStart_week() {
        return start_week;
    }

    public void setStart_week(int start_week) {
        this.start_week = start_week;
    }

    public int getEnd_week() {
        return end_week;
    }

    public void setEnd_week(int end_week) {
        this.end_week = end_week;
    }

    public int getGap() {
        return gap;
    }

    public void setGap(int gap) {
        this.gap = gap;
    }

    public int getDay_in_week() {
        return day_in_week;
    }

    public void setDay_in_week(int day_in_week) {
        this.day_in_week = day_in_week;
    }

    public int getStart_time() {
        return start_time;
    }

    public void setStart_time(int start_time) {
        this.start_time = start_time;
    }

    public int getEnd_time() {
        return end_time;
    }

    public void setEnd_time(int end_time) {
        this.end_time = end_time;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getLocation(){
        return area+"区"+building+"-"+room;
    }

    //return the Chinese of a certain weekday
    public String getWeekDay(){
        String[] weekday={"周一","周二","周三","周四","周五","周六","周日"};
        if(day_in_week>-1 && day_in_week<7){
            return  weekday[day_in_week];
        }else {
            return "未知周数";
        }
    }

    //return the Chinese of course time
    public String getTime(){
        String time = getWeekDay()+" "+getStart_time()+"-"+getEnd_time()+"节";
        return time;
    }


}



