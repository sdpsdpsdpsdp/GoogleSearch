package com.laisontech.googlesearch.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.maps.android.SphericalUtil;
import com.laisontech.googlesearch.app.MapApp;
import com.laisontech.googlesearch.db.query.QueryPoint;
import com.laisontech.googlesearch.db.query.QueryPointDao;
import com.laisontech.googlesearch.entity.SearchTextInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SDP on 2017/8/22.
 * 数据库操作帮助类
 */

public class DatabaseOperate {
    /**
     * 当数据库中需要增加表单时，对此时的数据库的版本号进行保存
     */
    public static void saveDatabaseVersion(String databaseTableName, int version) {
        MapApp.getInstance().getSP().edit().putInt(databaseTableName, version).apply();
    }

    /**
     * 获取增加新的数据库表单时的版本号
     */
    public static int getDaveDatabaseVersion(String databaseTableName) {
        return MapApp.getInstance().getSP().getInt(databaseTableName, 0);
    }

    public static List<QueryPoint> queryBySearch(SearchTextInfo searchTextInfo) {
        if (searchTextInfo == null || searchTextInfo.results == null) return null;
        List<QueryPoint> queryPointList = new ArrayList<>();
        for (SearchTextInfo.Results result : searchTextInfo.results) {
            List<SearchTextInfo.Photos> photos = result.photos;
            String photo = "";
            if (photos != null && photos.size() > 0) {
                photo = photos.get(0).photo_reference;
            }
            double lat = 0;
            double lng = 0;
            if (result.geometry != null) {
                SearchTextInfo.Location location = result.geometry.location;
                lat = location.lat;
                lng = location.lng;
            }
            QueryPoint queryPoint = new QueryPoint(result.name
                    , result.icon
                    , result.formatted_address
                    , result.place_id
                    , photo
                    , lat
                    , lng
                    , System.currentTimeMillis());
            queryPointList.add(queryPoint);
        }
        return queryPointList;
    }

    public static void saveSearchInfo(SearchTextInfo searchTextInfo) {
        if (searchTextInfo == null || searchTextInfo.results == null) return;
        List<SearchTextInfo.Results> results = searchTextInfo.results;
        if (results.size() == 1) {//只保存搜索出来的一个数据
            SearchTextInfo.Results result = results.get(0);
            new QueryPointDao(MapApp.getInstance().getLocalContext()).saveOrUpdate(result);
        }
    }

    public static void saveSearchInfo(QueryPoint queryPoint) {
        if (queryPoint == null) return;
        QueryPointDao dao = new QueryPointDao(MapApp.getInstance().getLocalContext());
        dao.saveOrUpdate(queryPoint);
    }
}
