package com.laisontech.googlesearch.service;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.google.android.gms.maps.model.LatLng;
import com.laisontech.googlesearch.R;
import com.laisontech.googlesearch.app.MapApp;
import com.laisontech.googlesearch.utils.LocationUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by SDP on 2017/9/25.
 */

public class DiffLocationService extends Service {
    //google原生定位
    public static final String ACTION_GOOGLE_LOCATION = "com.laisontech.action.google_location";
    //百度定位
    private static final String ACTION_BAIDU_LOCATION = "com.laisontech.action.baidu_location";
    //获取到定位信息
    private static final String ACTION_GET_LOCATION_INFO = "com.laisontech.action.get_location_info";
    //定位信息Key
    private static final String KEY_GET_LOCATION_INFO = "key_get_location_info";
    private Context mContext;
    //启用的定时器 默认为1分钟
    private Timer mStartGoogleTimer = null;
    //google定位管理类
    protected LocationManager mLocationManager;
    protected Location mLastKnownLocation;
    protected MyLocationListener mLocationListener;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 0 meters

    private static final long MIN_TIME_BW_UPDATES = 1000; // 1 sec

    private static final int CHECK_GOOGLE_TIMES = 8 * 1000;
    //百度定位
    BaiduLocationService mBaiduLocation = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = DiffLocationService.this;
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    //google定位，开启timer定时器，在一段时间内分如果没有获取定位信息时，就启动百度定位，当百度定位获取到定位信息后，尝试再次获取google定位，
                    // 如果gps能获取到定位则结束百度定位，使用google定位
                    case ACTION_GOOGLE_LOCATION:
                        startGoogleLocation();
                        break;
                    //百度定位
                    case ACTION_BAIDU_LOCATION:
                        startBaiduLocation();
                        break;
                }
            }
        }
        return START_STICKY;
    }

    //启动google定位
    private void startGoogleLocation() {
        mLocationListener = new MyLocationListener();
        googleLocation();
        startCheckGoogleGetLocation();
    }

    //启动google定位
    private void googleLocation() {

        boolean isGPSEnabled = mLocationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        boolean isNetworkEnabled = mLocationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled) {
            Toast.makeText(mContext, mContext.getString(R.string.PleaseOpenLocationPer), Toast.LENGTH_SHORT).show();
        } else {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mContext, mContext.getString(R.string.PleaseOpenLocationPer), Toast.LENGTH_SHORT).show();
                return;
            }
            //  网络定位开启
            if (isNetworkEnabled) {
                if (mLastKnownLocation == null) {
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, mLocationListener);
                    if (mLocationManager != null) {
                        mLastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
                MapApp.getInstance().setLocationType(MapApp.LocationType.TYPE_GOOGLE);
            }
            // gps开启状态
            if (isGPSEnabled) {
                if (mLastKnownLocation == null) {
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, mLocationListener);
                    if (mLocationManager != null) {
                        mLastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                }
                MapApp.getInstance().setLocationType(MapApp.LocationType.TYPE_GOOGLE);
            }
        }
    }

    //启用定时器检查是否获取到定位信息
    private void startCheckGoogleGetLocation() {
        final Location currentLocation = getCurrentLocation();
        if (currentLocation != null && currentLocation.getLongitude() != 0) {
            MapApp.getInstance().setLocationType(MapApp.LocationType.TYPE_GOOGLE);
            sendLocationBroadcastToUI();
            return;
        }
        mStartGoogleTimer = new Timer();
        mStartGoogleTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                //25秒后获取定位信息
                //如果获取到的定位信息还是为空，则启动百度定位
                if (currentLocation == null || currentLocation.getLongitude() == 0) {
                    startLocationService(mContext, ACTION_BAIDU_LOCATION);
                    return;
                }
                //不为空则直接开始发送广播到
                MapApp.getInstance().setLocationType(MapApp.LocationType.TYPE_GOOGLE);
                sendLocationBroadcastToUI();
            }
        }, CHECK_GOOGLE_TIMES);
    }

    public static void unRegisterLocationReceiver(Context context, MyLocationReceiver locationReceiver) {
        context.unregisterReceiver(locationReceiver);
    }


    //定位接口
    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            mLastKnownLocation = location;
            if (mLastKnownLocation != null) {
                //不为空则直接开始发送广播到
                MapApp.getInstance().setLocationType(MapApp.LocationType.TYPE_GOOGLE);
                sendLocationBroadcastToUI();
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    //结束定位
    public void stopGoogleGPS() {
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }

    //获取当前点的经纬度
    public Location getCurrentLocation() {
        return mLastKnownLocation;
    }

    /**
     * 启用百度定位
     */
    private void startBaiduLocation() {
        mBaiduLocation = ((MapApp) getApplication()).mLocationService;
        mBaiduLocation.registerListener(mBaiDuLocationListener);
        mBaiduLocation.setLocationOption(mBaiduLocation.getDefaultLocationClientOption());
        mBaiduLocation.start();
    }

    private BDLocationListener mBaiDuLocationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {

                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mLastKnownLocation = mLocationManager.getLastKnownLocation(LocationUtils.getBestProvider(mLocationManager));
                if (mLastKnownLocation != null && mBaiduLocation != null) {
                    Log.e("dingwei", "google定位：" + location.toString());
                    mBaiduLocation.stop();
                    MapApp.getInstance().setLocationType(MapApp.LocationType.TYPE_GOOGLE);
                    sendLocationBroadcastToUI();
                    return;
                }
                Log.e("dingwei", "百度定位：" + "定位类型：" + location.getLocType() + "，定位结果：" + location.toString());
                mLastKnownLocation = new Location(LocationUtils.getBestProvider(mLocationManager));
                mLastKnownLocation.setLongitude(location.getLongitude());
                mLastKnownLocation.setLatitude(location.getLatitude());
                MapApp.getInstance().setLocationType(MapApp.LocationType.TYPE_BAIDU);
                sendLocationBroadcastToUI();
                mLastKnownLocation = null;
            }
        }

    };


    /**
     * 开启定位服务
     */
    public static void startLocationService(Context context, String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.setClass(context, DiffLocationService.class);
        context.startService(intent);
    }

    private void sendLocationBroadcastToUI() {
        Intent intent = new Intent();
        if (mLastKnownLocation != null) {
            LatLng latLng = LocationUtils.transformFromWGSToGCJ(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
            Log.e("dingwei", "转换" + latLng.toString());
            mLastKnownLocation.setLatitude(latLng.latitude);
            mLastKnownLocation.setLongitude(latLng.longitude);
        }
        intent.setAction(ACTION_GET_LOCATION_INFO);
        intent.putExtra(KEY_GET_LOCATION_INFO, mLastKnownLocation);
        mContext.sendBroadcast(intent);
    }

    /**
     * 注册定位广播
     */
    public static void registerLocationReceiver(Context context, MyLocationReceiver receiver) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_GET_LOCATION_INFO);
        context.registerReceiver(receiver, filter);
    }

    public static class MyLocationReceiver extends BroadcastReceiver {
        private OnGetLocationInfoListener listener;

        public MyLocationReceiver(OnGetLocationInfoListener listener) {
            this.listener = listener;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null) return;
            switch (intent.getAction()) {
                case ACTION_GET_LOCATION_INFO:
                    if (listener != null) {
                        listener.onGetLocation((Location) intent.getParcelableExtra(KEY_GET_LOCATION_INFO));
                    }
                    break;
            }
        }
    }

    public interface OnGetLocationInfoListener {
        void onGetLocation(Location location);
    }

    //字节码计数器
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mStartGoogleTimer != null) {
            mStartGoogleTimer.cancel();
            mStartGoogleTimer = null;
        }
        stopGoogleGPS();
        if (mBaiduLocation != null) {
            mBaiduLocation.unregisterListener(mBaiDuLocationListener);
            mBaiduLocation.stop();
        }
    }
}
