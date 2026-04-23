package com.example.baidu_map;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private MapView mMapView = null;
    private RadioGroup mapType;
    private RadioButton normalBtn;
    private RadioButton satelliteBtn;
    private CheckBox trafficEnabled;
    private CheckBox heatMapEnabled;

    private BaiduMap mBaiduMap;
    private BDLocationListener myListener;
    private LocationClient mLocationClient;
    private boolean isFirstLoc = true;
    private float direction = 0f;
    private SensorManager sensorManager;

    private Locator locator;
    private double[] bd09 = {30.2707, 120.1198};

    private boolean isCarLoc = false;
    private Button setSelfCenter;
    private Button setLocatorCenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView = findViewById(R.id.bmapView);
        mapType = findViewById(R.id.id_rp_maptype);
        normalBtn = findViewById(R.id.id_btn_normal);
        satelliteBtn = findViewById(R.id.id_btn_satellite);
        trafficEnabled = findViewById(R.id.id_cb_trafficEnabled);
        heatMapEnabled = findViewById(R.id.id_cb_heatMapEnabled);
        mBaiduMap = mMapView.getMap();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        locator = new Locator(bd09[0], bd09[1], mBaiduMap);
        setSelfCenter = findViewById(R.id.btn_locate);
        setLocatorCenter = findViewById(R.id.btn_locator);

        initLocation();
        initEvent();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            direction = event.values[0];
            Log.d("MainActivity", "direction" + direction);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void initLocation() {
        LocationClient.setAgreePrivacy(true);
        try {
            mLocationClient = new LocationClient(getApplicationContext());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        myListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myListener);

        MyLocationConfiguration.LocationMode mCurrentMode =
                MyLocationConfiguration.LocationMode.NORMAL;
        BitmapDescriptor mCurrentMarker = null;
        int accuracyCircleFillColor = 0xAAFFFF88;
        int accuracyCircleStrokeColor = 0xAA00FF00;
        MyLocationConfiguration mLocationConfiguration = new MyLocationConfiguration(
                mCurrentMode,
                true,
                mCurrentMarker,
                accuracyCircleFillColor,
                accuracyCircleStrokeColor
        );
        mBaiduMap.setMyLocationConfiguration(mLocationConfiguration);

        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        option.setOpenGps(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    .direction(direction)
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            Log.d(
                    "MainActivity",
                    "Longitude:" + location.getLongitude() + " Latitude:" + location.getLatitude()
            );
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                float zoomLevel = 18.0f;
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, zoomLevel);
                mBaiduMap.animateMapStatus(u);
                isFirstLoc = false;
            }
            if (isCarLoc) {
                LatLng ll = new LatLng(bd09[0], bd09[1]);
                // 设置当前位置为地图中心点
                float zoomLevel = 18.0f;
                // 设置地图缩放级数，例如设置为1~21
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, zoomLevel);
                // 更新坐标位置并设置缩放级数
                mBaiduMap.animateMapStatus(u);
                // 动画效果更新地图状态
                isCarLoc = false;
            }
        }
    }

    private void initEvent() {
        mapType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == normalBtn.getId()) {
                    mMapView.getMap().setMapType(BaiduMap.MAP_TYPE_NORMAL);
                } else if (i == satelliteBtn.getId()) {
                    mMapView.getMap().setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                }
            }
        });

        trafficEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mMapView.getMap().setTrafficEnabled(b);
            }
        });

        heatMapEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mMapView.getMap().setBaiduHeatMapEnabled(b);
            }
        });

        setSelfCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 直接请求位置更新
                mLocationClient.requestLocation(); // 请求一次定位更新
                isFirstLoc = true;
            }
        });

        setLocatorCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 直接请求位置更新
                mLocationClient.requestLocation(); // 请求一次定位更新
                isCarLoc = true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBaiduMap.setMyLocationEnabled(true);
        mLocationClient.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME
        );
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }
}
