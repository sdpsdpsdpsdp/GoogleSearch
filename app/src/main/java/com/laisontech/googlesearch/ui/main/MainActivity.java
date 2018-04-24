package com.laisontech.googlesearch.ui.main;


import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.laisontech.googlesearch.R;
import com.laisontech.googlesearch.db.DBHelper;
import com.laisontech.googlesearch.event.QueryEvent;
import com.laisontech.googlesearch.service.DiffLocationService;
import com.laisontech.googlesearch.ui.search.SearchPointActivity;
import com.laisontech.googlesearch.utils.Const;
import com.laisontech.googlesearch.utils.LocationUtils;
import com.laisontech.googlesearch.utils.WindowUtils;
import com.laisontech.mvp.mvp.MVPBasePermissionsActivity;
import com.laisontech.mvp.utils.ActivityStack;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.OnClick;


public class MainActivity extends MVPBasePermissionsActivity<MainContract.View, MainPresenter> implements DiffLocationService.OnGetLocationInfoListener, MainContract.View, OnMapReadyCallback, GoogleMap.OnPolylineClickListener {
    private DiffLocationService.MyLocationReceiver locationReceiver;
    private boolean isRegister = false;
    //googleMap
    private GoogleMap mGoogleMap;
    private Location mCurrentLocation;
    private Marker mCurrentLocationMarker;
    private boolean isFirstEntryActivity = true;
    private Polyline mLastPolyline;
    private Marker mSearchMarker;

    @Override
    protected int setContentViewID() {
        return R.layout.activity_main;
    }

    @Override
    protected void initEvent() {
        isNeedOnKeyDown = true;
        ActivityStack.getScreenManager().pushActivity(this);
        WindowUtils.setLayoutInStatusBar(this);
        WindowUtils.showStatusBar(this, android.R.color.black);
        EventBus.getDefault().register(this);
        registerLocation();
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map)).getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mGoogleMap == null) {
            mGoogleMap = googleMap;
        }
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        mGoogleMap.setOnPolylineClickListener(this);
    }

    @Override
    public void onPolylineClick(Polyline polyline) {

    }

    @OnClick({R.id.ll_search, R.id.rl_location})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_search:
                openActivity(SearchPointActivity.class);
                break;
            case R.id.rl_location:
                location(true, Const.CURRENT_MAP_ZOOM);
                break;
        }
    }

    /**
     * 获取定位信息
     */
    @Override
    public void onGetLocation(Location location) {
        mCurrentLocation = location;
        if (mGoogleMap == null || mCurrentLocation == null) {
            return;
        }
        hideWaitingDialog();
        if (isFirstEntryActivity) {
            location(true, Const.ERROR_MAP_ZOOM);
            isFirstEntryActivity = false;
        } else {
            location(false, Const.CURRENT_MAP_ZOOM);
        }
    }


    /**
     * 定位
     */
    private void location(boolean moveCamera, float zoomLevel) {
        if (mCurrentLocation == null) return;
        if (mCurrentLocationMarker != null) {
            mCurrentLocationMarker.remove();
        }
        mCurrentLocationMarker = LocationUtils.buildMarker(mGoogleMap, mCurrentLocation);
        LocationUtils.moveCamera(mGoogleMap, mCurrentLocation, moveCamera, zoomLevel);
    }

    private void registerLocation() {
        showWaitingDialog(getResStr(R.string.LocationIng), true);
        DiffLocationService.startLocationService(this, DiffLocationService.ACTION_GOOGLE_LOCATION);
        locationReceiver = new DiffLocationService.MyLocationReceiver(this);
        DiffLocationService.registerLocationReceiver(this, locationReceiver);
        isRegister = true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(QueryEvent messageEvent) {
        //设置数据到地图上，先查询，再显示
        mPresenter.querySearchPoint(messageEvent, mCurrentLocation);
    }

    @Override
    public void showDialog() {
        showWaitingDialog(getResStr(R.string.Loading));
    }

    @Override
    public void onPathSuccess(PolylineOptions polylineOptions) {
        if (polylineOptions == null) return;
        removePolyline();
        mLastPolyline = mGoogleMap.addPolyline(polylineOptions);
        LocationUtils.setAllPointsInScreen(mGoogleMap, polylineOptions);
    }

    @Override
    public void onFailedPath(String s) {
        showToast(s);
    }

    @Override
    public void showQueryMarker(String name, String address, double lat, double lng, Bitmap bitmap) {
        hideWaitingDialog();
        removeMarker();
        mSearchMarker = LocationUtils.showMarker(mGoogleMap, name, address, lat, lng, bitmap);
    }

    private void removePolyline() {
        if (mLastPolyline != null) {
            mLastPolyline.remove();
            mLastPolyline = null;
        }
    }

    private void removeMarker() {
        if (mSearchMarker != null) {
            mSearchMarker.remove();
            mSearchMarker = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityStack.getScreenManager().popActivity(this);
        stopService(new Intent(this, DiffLocationService.class));
        if (isRegister && locationReceiver != null) {
            DiffLocationService.unRegisterLocationReceiver(this, locationReceiver);
            isRegister = false;
        }
        DBHelper.getInstance(this).close();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

}
