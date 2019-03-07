package com.example.fang.activity;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.Toast;

import com.example.fang.adapter.CourseAdapter;
import com.example.fang.model.Course;
import com.example.fang.utils.MyJSONParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {
    final private String TAG = "SearchActivity";
    private String searchUrl;
    private ListView searchListView;
    private SearchView searchView;
    private List<Course> courseList = new ArrayList<>();
    private List<Course> searchResultList = new ArrayList<>();
    public Handler resultUIHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消actionbar标题
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search);
        initCourseList();
        initView();
        resultUIHandler = new Handler();
    }

    private void initCourseList() {

    }

    private void initView() {
        initToolbar();
        CourseAdapter courseAdapter = new CourseAdapter(SearchActivity.this,R.layout.item_course,courseList);
        searchListView = findViewById(R.id.lv_course_list);
        searchListView.setTextFilterEnabled(true);
        searchListView.setAdapter(courseAdapter);
        searchUrl = getString(R.string.base_url)+"/course/search/";

    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);      //这句代码使启用Activity回退功能，并显示Toolbar上的左侧回退图标
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_menu,menu);
        // Get the SearchView and set the searchable configuration
        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("课程/教师/教学楼");

        final Runnable updateResult = new Runnable() {
            @Override
            public void run() {
                updateResultUI();
            }
        };
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(final String query) {
                searchResultList.clear();
                // 得到输入管理对象
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    // 让键盘在所有的情况下都被隐藏
                    imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0); // 输入法如果是显示状态，那么就隐藏输入法
                }
                searchView.clearFocus(); // 不获取焦点

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        searchForQuery(query);
                        resultUIHandler.post(updateResult);
                    }
                }).start();



                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    private void updateResultUI() {
        if(searchResultList.isEmpty()){
            Toast.makeText(SearchActivity.this,"无查询结果", Toast.LENGTH_SHORT).show();
        }
        CourseAdapter courseAdapter = new CourseAdapter(SearchActivity.this,R.layout.item_course, searchResultList);
        searchListView.setAdapter(courseAdapter);
    }

    private void searchForQuery(String query) {
        OkHttpClient okHttpClient = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("keyword", query).build();
        Request request = (new Request.Builder().url(searchUrl)).post(requestBody).build();
        Log.i("FROM building","posting data..."+requestBody);

        Call call = okHttpClient.newCall(request);

        //用同步请求，否则异步更新UI界面得用别的方法
        try {
            Response response = call.execute();
            if (response.isSuccessful()){
                String courseInfo = response.body().string();
                Log.i("courselist:", courseInfo);
                searchResultList = MyJSONParser.parseCourseList(courseInfo);
            }else {
                Log.i("Fail","Cannot get from "+ searchUrl);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            Log.i(TAG,"back");
            //Toast.makeText(this,"back", Toast.LENGTH_SHORT).show();
            finish();
        }
        return true;
    }


}
