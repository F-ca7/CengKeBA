package com.example.fang.activity;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fang.model.Course;
import com.example.fang.utils.MyJSONParser;
import com.example.fang.utils.SerializeUtils;
import com.example.fang.utils.SharedPreferenceUtils;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.fang.utils.PxDpUtils.dp2px;

/**
 * Created by FANG on 2018/2/17.
 */




public class CourseActivity extends AppCompatActivity {
    private Button btnAddCourse;
    //SQLite Helper类
    private DatabaseHelper databaseHelper = new DatabaseHelper
            (this, "cengkeba_database.db", null, 1);
    private OkHttpClient okHttpClient = new OkHttpClient();
    private String mainUrl;
    private ArrayList<Course> courseList;


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // 从savedInstanceState中恢复数据, 如果没有数据需要恢复savedInstanceState为null

        setContentView(R.layout.activity_course_table);
        initView();

        btnAddCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //toastDebug("点击添加课程");
                loadUserCourse();
            }
        });
    }

    private void loadUserCourse() {
        final String courseTableUrl = mainUrl +"/course/course_table/" + SharedPreferenceUtils.getCurrentToken(getApplicationContext());

        Request request = new Request.Builder()
                .url(courseTableUrl).get().build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("Fail","Cannot get from "+ courseTableUrl);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();
                try{
                    Log.i("课程表", result);
                    courseList = MyJSONParser.parseCourseList(result);
                    saveCourseList(courseList);     //每次更新保存课程表
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (Course course:courseList){
                                addCourseView(course);
                            }
                        }
                    });
                }catch (JsonSyntaxException e){
                    e.printStackTrace();
                }
            }
        });
    }

    //保存课程表到文件中
    private void saveCourseList(ArrayList<Course> courseList) {
        SharedPreferences sp = getSharedPreferences("userInfo",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Set<String> stringSet = new HashSet<>();
        try {
            for(Course course: courseList){
                stringSet.add(SerializeUtils.serialize(course));
            }
            editor.putStringSet("course_list", stringSet);
            editor.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void initView(){
        mainUrl = getString(R.string.base_url);
        courseList = new ArrayList<>();
        btnAddCourse=findViewById(R.id.btn_add_course);
        //加载之前保存的课程表
        loadCourseData();
    }

    //update database from server
    private void updateCourseData(){
        ArrayList<Course> courseList = getNewCourseListFromServer();
        if (courseList.isEmpty()){
            Log.d("updating info:","获取课表为空");
            return;
        }
        SQLiteDatabase sqLiteDatabase =  databaseHelper.getWritableDatabase();
        //delete all entries in table 'course'
        sqLiteDatabase.execSQL("delete from course;");
        sqLiteDatabase.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = 'course';");
        //save each course into the table
        for (Course course : courseList) {
            /*todo 插入的sql语句*/
            sqLiteDatabase.execSQL("insert into course(name, teacher,start_week,end_week,gap,day_in_week,start_time,end_time" +
                    "area,building,romm) values(?,?)",new Object[]{});
        }
    }

    private ArrayList<Course> getNewCourseListFromServer() {
        ArrayList<Course> courseList = new ArrayList<>(); //课程列表
        return courseList;
    }

    //load course data from local database
    private void loadCourseData(){
        SharedPreferences sp = getSharedPreferences("userInfo",MODE_PRIVATE);
        Set<String> stringSet = sp.getStringSet("course_list", null);
        if(stringSet==null)
            return;
        try {
            for (String courseData: stringSet) {
                courseList.add((Course) SerializeUtils.serializeToObject(courseData));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        for (Course course : courseList) {
            addCourseView(course);
        }
//        SQLiteDatabase sqLiteDatabase =  databaseHelper.getWritableDatabase();
//        Cursor cursor = sqLiteDatabase.rawQuery("select * from course", null);
//        if (cursor.moveToFirst()) {
//
//        }
//        cursor.close();
        //load the course table via the course info from the local database

    }

    private void addCourseView(final Course course){
        final int height = dp2px(61, this.getApplicationContext());
        int weekday = course.getDay_in_week();
        //toastDebug("正在添加课程:"+course.getName()+"@weekday"+weekday);
        LinearLayout day;
        switch (weekday){
            case 1: day = findViewById(R.id.mon_panel); break;
            case 2: day = findViewById(R.id.tues_panel); break;
            case 3: day = findViewById(R.id.wed_panel); break;
            case 4: day = findViewById(R.id.thur_panel);break;
            case 5: day = findViewById(R.id.fri_panel);break;
            case 6: day = findViewById(R.id.sat_panel);break;
            case 7: day = findViewById(R.id.sun_panel);break;
            default: day = findViewById(R.id.mon_panel);
        }
        View courseView = LayoutInflater.from(this).inflate(R.layout.view_course_card, null); //加载单个课程布局
        courseView.setY(height * (course.getStart_time()-1)); //设置开始高度,即第几节课开始
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT,(course.getEnd_time()-course.getStart_time()+1)*height); //设置布局高度,即跨多少节课
        courseView.setLayoutParams(params);
        TextView tvCourseName = courseView.findViewById(R.id.tv_course_name);
        TextView tvCourseRoom = courseView.findViewById(R.id.tv_course_room);
        tvCourseName.setText(course.getName());
        tvCourseRoom.setText(course.getLocation());
        courseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCourseDetail(course);
            }
        });
        day.addView(courseView);
    }

    //display the details of a specific course
    private void showCourseDetail(Course course) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(CourseActivity.this);
        //load a View object by the layout file
        View view = LayoutInflater.from(CourseActivity.this).inflate(R.layout.view_course_detail, null);
        builder.setView(view);

        final AlertDialog dialog = builder.show();
        final EditText edtName = view.findViewById(R.id.edt_course_name);
        final EditText edtRoom = view.findViewById(R.id.edt_course_room);
        final EditText edtTime = view.findViewById(R.id.edt_course_time);
        final EditText edtWeek = view.findViewById(R.id.edt_course_week);
        final EditText edtTeacher = view.findViewById(R.id.edt_course_teacher);
        final EditText edtCredit = view.findViewById(R.id.edt_course_credit);
        final Button btnSave = view.findViewById(R.id.btn_save_course_info);
        final Button btnClose = view.findViewById(R.id.btn_course_close);
        final Button btnModify = view.findViewById(R.id.btn_modify_course_info);
        edtName.setText(course.getName());
        edtRoom.setText(course.getLocation());
        edtTime.setText(course.getWeekDay()+" "+course.getStart_time()+"-"+course.getEnd_time()+"节");
        edtWeek.setText(course.getStart_week()+"-"+course.getEnd_week()+"周");
        edtTeacher.setText(course.getTeacher());
        edtCredit.setText(String.valueOf(course.getCredit()));
        final ArrayList<EditText> edtGroup = new ArrayList<>();
        edtGroup.add(edtName);
        edtGroup.add(edtRoom);
        edtGroup.add(edtTime);
        edtGroup.add(edtWeek);
        edtGroup.add(edtTeacher);
        edtGroup.add(edtCredit);

        //close info dialog
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //close the dialog
                dialog.dismiss();
            }
        });
        //modify course info
        btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSave.setVisibility(View.VISIBLE);
                btnClose.setText("取消");
                btnModify.setVisibility(View.GONE);
                for(EditText edt:edtGroup){
                    edt.setInputType(InputType.TYPE_CLASS_TEXT);
                    edt.setFocusableInTouchMode(true);
                    edt.setFocusable(true);
                }
            }
        });
       btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //saveCourseInfo();
                toastDebug("保存成功");
                dialog.dismiss();
            }
        });

    }

    //has course at that time already？
    private boolean checkTime(){
        boolean flag = true;

        return flag;
    }

    private void toastDebug(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onPause() {
        super.onPause();

    }
    @Override
    protected void onRestart() {
        super.onRestart();

    }
}
