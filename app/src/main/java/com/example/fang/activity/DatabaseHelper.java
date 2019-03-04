package com.example.fang.activity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by FANG on 2018/2/15.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_USER = "create table User(" +
            "id integer primary key autoincrement,"+
            "username text,"+
            "userpsw text)";
    //Context context----上下文对象
    //String name ---数据库的名称
    //CursorFactory factory---游标工厂对象
    //int version---数据库版本号
    //factory：可选的数据库游标工厂类，当查询(query)被提交时，该对象会被调用来实例化一个游标。
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_USER);   //创建user表
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
