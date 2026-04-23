package com.example.baidu_map;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;

public class MainActivity extends AppCompatActivity {
    private MapView mMapView = null;
    private RadioGroup mapType;
    private RadioButton normalBtn;
    private RadioButton satelliteBtn;
    private CheckBox trafficEnabled;
    private CheckBox heatMapEnabled;

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
        initEvent();
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
}
