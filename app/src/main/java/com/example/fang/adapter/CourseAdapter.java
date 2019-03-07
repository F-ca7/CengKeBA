package com.example.fang.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fang.activity.CourseActivity;
import com.example.fang.activity.R;
import com.example.fang.model.Course;
import com.example.fang.model.TakeCourseEnum;
import com.example.fang.utils.MyJSONParser;
import com.example.fang.utils.SharedPreferenceUtils;
import com.like.LikeButton;
import com.like.OnLikeListener;

import net.sf.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CourseAdapter extends ArrayAdapter<Course> {
    private int newResourceId;
    private String mainUrl;
    private OkHttpClient okHttpClient;

    public CourseAdapter(Context context, int resourceId, List<Course> courseList){
        super(context, resourceId, courseList);
        newResourceId = resourceId;
        okHttpClient = new OkHttpClient();
        mainUrl = context.getString(R.string.base_url);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Course course = getItem(position);
        final View view = LayoutInflater.from (getContext ()).inflate (newResourceId, parent, false);
        TextView tvCourseName = view.findViewById(R.id.item_course_name);
        TextView tvCourseRoom = view.findViewById(R.id.item_course_room);
        TextView tvCourseTime = view.findViewById(R.id.item_course_time);
        final LikeButton likeButton = view.findViewById(R.id.btn_collect_course);
        tvCourseName.setText(course.getName());
        tvCourseRoom.setText(course.getLocation());
        tvCourseTime.setText(course.getTime());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                //load a View object by the layout file
                View v = LayoutInflater.from(view.getContext()).inflate(R.layout.view_course_detail, null);
                builder.setView(v);

                final AlertDialog dialog = builder.show();
                final EditText edtName = v.findViewById(R.id.edt_course_name);
                final EditText edtRoom = v.findViewById(R.id.edt_course_room);
                final EditText edtTime = v.findViewById(R.id.edt_course_time);
                final EditText edtWeek = v.findViewById(R.id.edt_course_week);
                final EditText edtTeacher = v.findViewById(R.id.edt_course_teacher);
                final EditText edtCredit = v.findViewById(R.id.edt_course_credit);
                final Button btnSave = v.findViewById(R.id.btn_save_course_info);
                final Button btnClose = v.findViewById(R.id.btn_course_close);
                final Button btnModify = v.findViewById(R.id.btn_modify_course_info);

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
                btnModify.setText("我要蹭课");
                btnModify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        takeCourse(course, dialog, likeButton);
                    }
                });
            }
        });

        likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked() {
                likeCourse(course);
            }
            @Override
            public void unLiked() {
                unlikeCourse(course);
                Toast.makeText(view.getContext(), "取消收藏成功", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private void takeCourse(Course course, final AlertDialog dialog, final LikeButton likeButton) {
        String token = SharedPreferenceUtils.getCurrentToken(getContext());
        final String takeCourseUrl = mainUrl + "/course/do_it/";
        Log.i("takeCourse",takeCourseUrl);
        RequestBody requestBody = new FormBody.Builder()
                .add("data_id", course.getData_id())
                .add("token", token).build();
        Request request = (new Request.Builder().url(takeCourseUrl)).post(requestBody).build();
        Log.i("takeCourse","posting data...");
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("Fail","Cannot get from "+ takeCourseUrl);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                TakeCourseEnum code = MyJSONParser.parseTakingCourse(result);
                Looper.prepare();
                switch (code){
                    case SUCCESS:
                        Toast.makeText(getContext(), "蹭课成功", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        likeButton.setLiked(true);
                        break;
                    case FAILED:
                        Toast.makeText(getContext(), "蹭课失败", Toast.LENGTH_SHORT).show();
                        break;
                }
                Looper.loop();
            }
        });
    }

    private void unlikeCourse(Course course) {

    }

    private void likeCourse(Course course) {
        String token = SharedPreferenceUtils.getCurrentToken(getContext());
        final String collectCourseUrl = mainUrl + "/course/collect/";
        //Log.i("collectCourse",collectCourseUrl);
        final Context context = getContext();
        RequestBody requestBody = new FormBody.Builder()
                .add("data_id", course.getData_id())
                .add("token", token).build();
        Request request = (new Request.Builder().url(collectCourseUrl)).post(requestBody).build();
        Log.i("takeCourse","posting data...");
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("Fail","Cannot get from "+ collectCourseUrl);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                TakeCourseEnum code = MyJSONParser.parseCollectingCourse(result);
                Looper.prepare();
                switch (code){
                    case SUCCESS:
                        Toast.makeText(getContext(), "收藏成功", Toast.LENGTH_SHORT).show();
                        break;
                    case FAILED:
                        Toast.makeText(getContext(), "收藏失败", Toast.LENGTH_SHORT).show();
                        break;
                }
                Looper.loop();
            }
        });
    }
}
