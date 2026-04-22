package com.example.baidu_map;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.CoordType;

public class MapApp extends  Application{
    @Override
    public void onCreate(){
        super.onCreate();
        SDKInitializer.setAgreePrivacy(this, true);
        SDKInitializer.initialize(this);
        SDKInitializer.setCoordType(CoordType.BD09LL);
    }
}
