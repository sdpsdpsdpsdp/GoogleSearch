package com.laisontech.googlesearch.utils;

import com.laisontech.googlesearch.R;
import com.laisontech.googlesearch.app.MapApp;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by SDP on 2018/4/23.
 */

public class Const {
    //google导航url
    public static final String GOOGLE_BASE_URL_START = "https://maps.googleapis.com/maps/api/directions/json?";
    public static final String KEY_SP_LOCAL_NAME = "localSpName";
    public static final int QUERY_LIMIT_HISTORY = 12;
    //请求的code
    public static final int SEARCH_POINT_CODE_FOR_RESULT = 0X110;
    //默认的markerID
    public static final int DEFAULT_MARKER_ID = 0;
    //默认的缩放比例
    public static final float ERROR_MAP_ZOOM = -1;
    public static final float CURRENT_MAP_ZOOM = 0;

    //url
    public static final String QueryTextBase = "https://maps.googleapis.com/maps/api/place/textsearch/json?";
    public static final String QueryTextBeforeUrl = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=";
    public static final String QueryTextAfterUrl = "&key=" + MapApp.getInstance().getResStr(R.string.GoogleKey);
    public static final String OK = "OK";
    public static final String KEY_SAVE_DATABASE_QUERY_POINT = "KEY_SAVE_DATABASE_QUERY_POINT";
    public static final String TAG_PATH_PLANING = "TAG_PATH_PLANING";

    //获取google Map的地点搜索的Text信息
    public static String queryTextUrl(LinkedHashMap<String, String> map) {
        if (map == null || map.size() < 1) return null;
        StringBuilder sb = new StringBuilder();
        sb.append(QueryTextBase);
        int queryIndex = 0;
        for (String key : map.keySet()) {
            String value = map.get(key);
            if (queryIndex < map.size() - 1) {
                sb.append(key).append("=").append(value).append("&");
            } else {
                sb.append(key).append("=").append(value);
            }
            queryIndex++;
        }
        return sb.toString();
    }
}
