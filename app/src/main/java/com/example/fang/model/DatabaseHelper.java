package com.example.fang.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table course(" +
                "id integer primary key autoincrement," +
                "data_id varchar(20),"+
                "course_id varchar(20),"+
                "name text," +
                "type varchar(20),school varchar(20),major varchar(20), teacher varchar(20), credit varchar(5),"+
                "start_week integer, end_week integer,"+
                "day_in_week integer," +
                "start_time integer,end_time integer,"+
                "area varchar(20),building varchar(20),room varchar(20))");
        db.execSQL("create table user(" +
                "stu_id varchar(20)," +
                "stu_pwd varchar(30)," +
                "school varchar(20)," +
                "major varchar(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
