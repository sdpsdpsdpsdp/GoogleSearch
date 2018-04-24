package com.laisontech.googlesearch.ui.main;


import android.graphics.Bitmap;
import android.location.Location;

import com.google.android.gms.maps.model.PolylineOptions;
import com.laisontech.googlesearch.event.QueryEvent;
import com.laisontech.mvp.mvp.BasePresenter;
import com.laisontech.mvp.mvp.BaseView;

/**
 * 登录接口类的连接类
 */

public class MainContract {
    interface View extends BaseView {
        void showQueryMarker(String name, String address, double lat, double lng, Bitmap bitmap);

        void showDialog();

        void onPathSuccess(PolylineOptions obj);

        void onFailedPath(String s);
    }

    interface Presenter extends BasePresenter<View> {
        void querySearchPoint(QueryEvent queryEvent,Location location);
    }
}
