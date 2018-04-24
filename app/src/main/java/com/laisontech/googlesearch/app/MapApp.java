package com.laisontech.googlesearch.app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.baidu.mapapi.SDKInitializer;
import com.laisontech.googlesearch.service.BaiduLocationService;
import com.laisontech.googlesearch.utils.Const;

/**
 * Created by SDP on 2018/4/23.
 */

public class MapApp extends Application {
    @SuppressLint("StaticFieldLeak")
    private static MapApp mInstance;

    public static MapApp getInstance() {
        return mInstance;
    }

    private LocationType mLocationType;
    //百度定位管理服务
    public BaiduLocationService mLocationService;
    //sp
    private SharedPreferences mSp;
    //全局的context
    private Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mSp = getSharedPreferences(Const.KEY_SP_LOCAL_NAME, Context.MODE_PRIVATE);
        mContext = getApplicationContext();
        mLocationService = new BaiduLocationService(getApplicationContext());
        setLocationType(LocationType.TYPE_GOOGLE);
        SDKInitializer.initialize(getApplicationContext());
    }

    public LocationType getLocationType() {
        return mLocationType;
    }

    public void setLocationType(LocationType locationType) {
        this.mLocationType = locationType;
    }

    public String getResStr(int resId) {
        return getResources().getString(resId);
    }

    public SharedPreferences getSP() {
        return mSp;
    }

    public Context getLocalContext() {
        return mContext;
    }


    public enum LocationType {
        TYPE_GOOGLE,
        TYPE_BAIDU
    }
}
