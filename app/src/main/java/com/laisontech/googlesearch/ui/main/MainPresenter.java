package com.laisontech.googlesearch.ui.main;

import android.graphics.Bitmap;
import android.location.Location;

import com.google.android.gms.maps.model.PolylineOptions;
import com.laisontech.googlesearch.db.query.QueryPoint;
import com.laisontech.googlesearch.event.QueryEvent;
import com.laisontech.googlesearch.interfaces.OnLoadDataFromTaskListener;
import com.laisontech.googlesearch.utils.Const;
import com.laisontech.googlesearch.utils.LocationUtils;
import com.laisontech.mvp.mvp.BasePresenterImpl;
import com.laisontech.mvp.net.OkHttpConnect;
import com.laisontech.mvp.net.OnConnectResultListener;

import java.util.LinkedHashMap;

/**
 * Created by SDP on 2018/4/23.
 */

public class MainPresenter extends BasePresenterImpl<MainContract.View> implements MainContract.Presenter {
    //根据信息查询
    @Override
    public void querySearchPoint(QueryEvent queryEvent, Location location) {
        QueryPoint message = queryEvent.getMessage();
        if (message == null) return;
        executeLoadImg(message);
        if (queryEvent.isNeedNav()) {
            executeLoadPathPlaning(message, location);
        }
    }

    private void executeLoadImg(final QueryPoint message) {
        mView.showDialog();
        new LoadBitmapTask(new OnLoadDataFromTaskListener() {
            @Override
            public void onLoadDataFromTask(Object obj) {
                String name = message.getQueryName();
                String address = message.getFormattedAddress();
                double lat = message.getLat();
                double lng = message.getLng();
                //图片另查询
                mView.showQueryMarker(name, address, lat, lng, (Bitmap) obj);
            }
        }).execute(message.getIcon());
    }

    //路径导航
    private void executeLoadPathPlaning(QueryPoint queryPoint, Location location) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("origin", location.getLatitude() + "," + location.getLongitude());
        map.put("destination", queryPoint.getLat() + "," + queryPoint.getLng());
        map.put("sensor", false);
        map.put("mode", "walking");
        map.put("language", "en-US");
        String googleMapUrl = LocationUtils.getGoogleMapUrl(map);
        OkHttpConnect.getInstance().buildGetString(googleMapUrl, Const.TAG_PATH_PLANING, new OnConnectResultListener() {
            @Override
            public void onResponse(String s) {
                new GoogleParserTask(new OnLoadDataFromTaskListener() {
                    @Override
                    public void onLoadDataFromTask(Object obj) {
                        mView.onPathSuccess((PolylineOptions) obj);
                    }
                }).execute(s);
            }

            @Override
            public void onError(String s) {
                mView.onFailedPath(s);
            }
        });
    }
}
