package com.example.fang.activity;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;


import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;

import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.example.fang.adapter.CourseAdapter;
import com.example.fang.model.Building;
import com.example.fang.model.Course;
import com.example.fang.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import com.example.fang.utils.AppUtils;
import com.github.ybq.android.spinkit.style.Circle;



import static com.example.fang.utils.AppUtils.toastDebug;

/**
 * Created by FANG on 2018/2/14.
 */

@RuntimePermissions
public class MainInterActivity extends AppCompatActivity {
    private final String TAG = "MainInterActivity";
    private long firstTime = 0;
    private ImageButton userCenterBtn;
    private EditText search_edit_text;
    private LinearLayout llBuildingCourseList;
    private ListView lvBuildingCourseList;
    ProgressBar progressBar;

    private boolean isShowingCourse = false;    //是否正在显示教学楼内课程列表

    private boolean clickormove = true;     //点击或拖动，点击为true，拖动为false
    private int downX, downY;               //按下时的X，Y坐标
    private boolean hasMeasured = false;    //ViewTree是否已被测量过，是为true，否为false
    private View content;                   //界面的ViewTree
    private int screenWidth,screenHeight;   //ViewTree的宽和高
    //地图控件
    public MapView mapView = null;
    //百度地图对象
    private BaiduMap baiduMap = null;
    //定位相关声明
    private LocationClient locationClient = null;
    //自定义图标
    BitmapDescriptor mCurrentMarket = null;
    //是否首次定位
    boolean isFirstLoc = true;

    //得到经纬度
    private double longitude;
    private double latitude;

    //教学楼用的经纬度
    final private LatLng GREEN_HOUSE_1_LL = new LatLng(30.532767204603044,114.36761327108664);
    final private LatLng GREEN_HOUSE_2_LL = new LatLng(30.532759428029625,114.36883496658021);
    final private LatLng GREEN_HOUSE_5_LL = new LatLng(30.53209064036299,114.36645445697877);
    final private LatLng WENLI_HOUSE_1_LL = new LatLng(30.543428295336547,114.37177242559784);
    final private LatLng WENLI_HOUSE_3_LL = new LatLng(30.54573765374643,114.36659818586037);
    final private LatLng WENLI_HOUSE_5_LL = new LatLng(30.542448550765936,114.36783784746414);
    final private LatLng WENLI_HOUSE_4_LL = new LatLng(30.543171696533136,114.36825106799873);
    final private LatLng GONG_HOUSE_11_LL = new LatLng(30.546950627859676,114.36727191499286);
    private HashMap<LatLng, Building> ll_building_map = new HashMap<>();

    private MyLocationListener myListener = new MyLocationListener();
    //设置了地图初始信息
    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            boolean isLocateFailed = false;//定位是否成功
            //MAP VIEW 销毁后不在处理新接收的位置
            if (location == null || mapView == null)
                return;
            MyLocationData locData = new MyLocationData.Builder()
                    //此处设置开发者获取到的方向信息，顺时针0-360
                    .accuracy(location.getRadius())
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            baiduMap.setMyLocationData(locData);
            //设置定位数据
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll;
                ll = new LatLng(location.getLatitude(),location.getLongitude());
                //如果一开始是经纬度4.9E-，说明没打开定位权限
                //Toast.makeText(getApplicationContext(),"当前位置"+ll.toString(),Toast.LENGTH_SHORT).show();
                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(ll, 19);
                //设置地图中心点以及缩放级别
                baiduMap.animateMapStatus(mapStatusUpdate);
            }
        }
    }

    //测试使用
    private List<Course> courseList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main_interface);

        initView();
        initMapData();

        initBuildingMarker();

    }




    private void initView() {
        mapView =  findViewById(R.id.bmapView);
        //enable GPS
        if(!AppUtils.isGPSOPen(this)){
            Toast.makeText(this,"未开启定位", Toast.LENGTH_SHORT).show();
            openGPS();
        }
        setMap();

        //设置加载动画
        progressBar = findViewById(R.id.spin_loading);
        progressBar.setIndeterminateDrawable(new Circle());
        userCenterBtn = findViewById(R.id.user_center);
        setFloatingButton();

        search_edit_text = findViewById(R.id.search_edit_text);
        //switch to search activity
        search_edit_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
            if(hasFocus){
                search_edit_text.clearFocus();
                Intent intent = new Intent(MainInterActivity.this, SearchActivity.class);
                startActivity(intent);
            }else {

            }
            }
        });
        llBuildingCourseList = findViewById(R.id.ll_building_course);
        lvBuildingCourseList = findViewById(R.id.lv_building_course_list);

    }



    @NeedsPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    void openGPS() {

    }


    /**
     * 申请权限被拒绝时
     *
     */
    @OnPermissionDenied(Manifest.permission.ACCESS_COARSE_LOCATION)
    void onMapDenied() {
        Toast.makeText(this,"无法使用定位功能",Toast.LENGTH_LONG).show();
    }

    /**
     * 申请权限被拒绝并勾选不再提醒时
     */
    @OnNeverAskAgain(Manifest.permission.ACCESS_COARSE_LOCATION)
    void onMapNeverAskAgain() {

    }

    /**
     * 告知用户具体需要权限的原因
     * @param messageResId
     * @param request
     */
    private void showRationaleDialog(String messageResId, final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.proceed();//请求权限
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .setCancelable(false)
                .setMessage(messageResId)
                .show();
    }

    //make userCenterBtn-center button floating
    private void setFloatingButton() {
        content = getWindow().findViewById(Window.ID_ANDROID_CONTENT);//获取界面的ViewTree根节点View

        DisplayMetrics dm = getResources().getDisplayMetrics();//获取显示屏属性
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        ViewTreeObserver vto = content.getViewTreeObserver();   //获取ViewTree的监听器
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {

                if(!hasMeasured) {
                    screenHeight = content.getMeasuredHeight();//获取ViewTree的高度
                    hasMeasured = true;//设置为true，使其不再被测量。

                }
                return true;//如果返回false，界面将为空。

            }

        });
        userCenterBtn.setOnTouchListener(new View.OnTouchListener() {//设置按钮被触摸的时间
            int lastX, lastY; // 记录移动的最后的位置
            int left = 0;
            int top = 0;
            int right = 0;
            int bottom = 0;
            boolean onRightSide = true;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int ea = event.getAction();//获取事件类型
                switch (ea) {
                    case MotionEvent.ACTION_DOWN: // 按下事件
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        downX = lastX;
                        downY = lastY;
                        break;
                    case MotionEvent.ACTION_MOVE: // 拖动事件
                        //移动中动态设置位置
                        int dx = (int) event.getRawX() - lastX;//位移量X
                        int dy = (int) event.getRawY() - lastY;//位移量Y
                        left = v.getLeft() + dx;
                        top = v.getTop() + dy;
                        right = v.getRight() + dx;
                        bottom = v.getBottom() + dy;
                        //限定按钮被拖动的范围
                        if (left < 0) {
                            left = 0;
                            right = left + v.getWidth();
                        }
                        if (right > screenWidth) {
                            right = screenWidth;
                            left = right - v.getWidth();

                        }
                        if (top < 0) {
                            top = 0;
                            bottom = top + v.getHeight();
                        }
                        if (bottom > screenHeight) {

                            bottom = screenHeight;
                            top = bottom - v.getHeight();

                        }
                        //限定按钮被拖动的范围
                        v.layout(left, top, right, bottom);//按钮重画
                        // 记录当前的位置
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        break;

                    case MotionEvent.ACTION_UP: // 弹起事件
                        //判断是单击事件或是拖动事件，位移量大于5则断定为拖动事件
                        if (Math.abs((int) (event.getRawX() - downX)) > 5
                                || Math.abs((int) (event.getRawY() - downY)) > 5) {
                            //以下是拖动事件
                            clickormove = false;
                            //刷新界面不会回到原位，要setLayoutParams
                            Rect frame = new Rect();
                            getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
                            int statusBarHeight = frame.top;
                            RelativeLayout.LayoutParams params =
                                    (RelativeLayout.LayoutParams) userCenterBtn.getLayoutParams();
                            //向左停靠
                            if(left < screenWidth/2 && onRightSide){
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                    params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                    params.removeRule(RelativeLayout.ALIGN_PARENT_END);
                                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                    onRightSide = false;
                                }
                            }
                            //向右停靠
                            if(left > screenWidth/2 && !onRightSide){
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                    params.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                    params.addRule(RelativeLayout.ALIGN_PARENT_END);
                                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                    onRightSide = true;
                                }
                            }
                            params.setMargins(left, top, screenWidth-right, screenHeight-bottom-statusBarHeight);    //控件相对父控件左上右下的距离


                            params.width = userCenterBtn.getWidth();
                            params.height = userCenterBtn.getHeight();
                            userCenterBtn.setLayoutParams(params);

                        } else {
                            clickormove = true;
                        }
                        break;

                }
                return false;

            }

        });
        //switch to userCenterBtn center
        userCenterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickormove){
                    Intent intent = new Intent(MainInterActivity.this, UserCenterActivity.class);
                    startActivity(intent);
                }
            }
        });

    }


    //Configure Baidu map
    private void setMap(){
        mapView.showScaleControl(false);
        mapView.showZoomControls(false);
        baiduMap = mapView.getMap();
        baiduMap.setBuildingsEnabled(false);

        UiSettings mUisettings =baiduMap.getUiSettings();
        //禁用3D
        mUisettings. setOverlookingGesturesEnabled(false);
        //关闭指南针
        mUisettings.setCompassEnabled(false);
        //开启定位图层
        baiduMap.setMyLocationEnabled(true);

        baiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL,true,null));
        locationClient = new LocationClient(getApplicationContext());//实例化LocationClient类
        locationClient.registerLocationListener(myListener);//注册监听函数
        this.setLocationOption();//设置定位参数

        baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(isShowingCourse){
                    hideCourseList();
                }
                String text = String.format("经纬度为%f,%f",latLng.latitude,latLng.longitude);
                //Toast.makeText(getApplicationContext(),text, Toast.LENGTH_LONG).show();
                Log.i("经纬度记录",text);
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
    }


    private void initMapData() {
        locationClient.start();//开始定位
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);//设置为一般地图
        baiduMap.setTrafficEnabled(false);//开启交通图

        CoordinateConverter converter  = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.COMMON);

        ll_building_map.put(GREEN_HOUSE_1_LL, new Building("3","1", getApplication()));
        ll_building_map.put(GREEN_HOUSE_2_LL, new Building("3","2", getApplication()));
        ll_building_map.put(GREEN_HOUSE_5_LL, new Building("3","5", getApplication()));
        ll_building_map.put(WENLI_HOUSE_1_LL, new Building("1","1", getApplication()));
        ll_building_map.put(WENLI_HOUSE_3_LL, new Building("1","3", getApplication()));
        ll_building_map.put(WENLI_HOUSE_4_LL, new Building("1","4", getApplication()));
        ll_building_map.put(WENLI_HOUSE_5_LL, new Building("1","5", getApplication()));
        ll_building_map.put(GONG_HOUSE_11_LL, new Building("2","11", getApplication()));

    }

    //添加教学楼的marker
    private void initBuildingMarker() {
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.school);
        for (Map.Entry<LatLng, Building> llBuildingEntry : ll_building_map.entrySet()) {
            addMarker(llBuildingEntry.getKey(),bitmap,llBuildingEntry.getValue().getBuildingName());
        }
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                progressBar.setVisibility(View.VISIBLE);
                String buildingName = marker.getTitle();
                LatLng buildingLL = marker.getPosition();

                Log.i("marker:", marker.getTitle() + "获取中...@" + marker.getPosition().latitude + "," + marker.getPosition().longitude);
                isShowingCourse = true;
                final Building building = ll_building_map.get(buildingLL);
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        courseList = building.getCourseListInBuilding();
                        if(courseList.isEmpty()){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    toastDebug(getApplicationContext(),"该教学楼课程列表为空");
                                    progressBar.setVisibility(View.GONE);

                                }
                            });

                        }else {
                            showCourseList();
                        }
                    }
                }).start();
                return false;
            }
        });
    }


    private void showCourseList() {
        if(isShowingCourse){
            hideCourseList();
        }
        isShowingCourse = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                CourseAdapter courseAdapter = new CourseAdapter(MainInterActivity.this, R.layout.item_course, courseList);
                lvBuildingCourseList.setAdapter(courseAdapter);
                //当指向另一个ArrayList时，就不能这样做
                //courseAdapter.notifyDataSetChanged();
                final TranslateAnimation ctrlAnimation = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                        TranslateAnimation.RELATIVE_TO_SELF, 1, TranslateAnimation.RELATIVE_TO_SELF, 0);
                ctrlAnimation.setDuration(600L);     //设置动画的过渡时间
                llBuildingCourseList.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        llBuildingCourseList.setVisibility(View.VISIBLE);
                        llBuildingCourseList.startAnimation(ctrlAnimation);
                    }
                }, 200);
            }
        });

    }

    private void hideCourseList() {
        //toastDebug(getApplicationContext(),"hiding course list");
        final TranslateAnimation ctrlAnimation = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 1);
        ctrlAnimation.setDuration(600L);     //设置动画的过渡时间
        llBuildingCourseList.postDelayed(new Runnable() {
            @Override
            public void run() {
                llBuildingCourseList.setVisibility(View.GONE);
                llBuildingCourseList.startAnimation(ctrlAnimation);
            }
        }, 100);
        isShowingCourse = false;
    }


    //添加自定义marker
    private void addMarker(LatLng latLng, BitmapDescriptor bitmap, String title){
        OverlayOptions option = new MarkerOptions().position(latLng).icon(bitmap).title(title).perspective(true).draggable(false);
        baiduMap.addOverlay(option);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mapView.onPause();
    }



    @Override
    public void onBackPressed(){
        if(isShowingCourse){
            hideCourseList();
            return;
        }
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            firstTime = secondTime;
        } else {
            this.finish();
            System.exit(0);
        }
    }


    private void setLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);//打开GPS
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度，默认值是gcj02
        option.setScanSpan(4000);//设置发起定位请求的时间间隔为4000ms
        option.setIsNeedAddress(true);//返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true);// 返回的定位信息包含手机的机头方向
        locationClient.setLocOption(option);
    }
}