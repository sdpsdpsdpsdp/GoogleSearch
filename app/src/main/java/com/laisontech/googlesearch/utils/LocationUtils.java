package com.laisontech.googlesearch.utils;

import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.laisontech.googlesearch.R;
import com.laisontech.googlesearch.app.MapApp;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SDP on 2018/4/23.
 * 执行定位帮助类
 */

public class LocationUtils {

    public static String getBestProvider(LocationManager manager) {
        String provider;
        provider = manager.getBestProvider(createFineCriteria(), true);
        return provider;
    }

    //获取定位参数
    private static Criteria createFineCriteria() {
        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_FINE);//高精度
        c.setAltitudeRequired(true);//包含高度信息
        c.setBearingRequired(true);//包含方位信息
        c.setSpeedRequired(true);//包含速度信息
        c.setCostAllowed(false);//允许付费
        c.setPowerRequirement(Criteria.POWER_HIGH);//高耗电
        return c;
    }

    /**
     * 将gps坐标转换为国内的火星坐标
     */
    public static LatLng transformFromWGSToGCJ(LatLng wgLoc) {
        double a = 6378245.0;
        double ee = 0.00669342162296594323;
        //如果在国外，则默认不进行转换
        if (outOfChina(wgLoc.latitude, wgLoc.longitude)) {
            return new LatLng(wgLoc.latitude, wgLoc.longitude);
        }
        //如果使用的是百度定位，则直接显示
        if (MapApp.getInstance().getLocationType() == MapApp.LocationType.TYPE_BAIDU) {
            return new LatLng(wgLoc.latitude, wgLoc.longitude);
        }
        double dLat = transformLat(wgLoc.longitude - 105.0,
                wgLoc.latitude - 35.0);
        double dLon = transformLon(wgLoc.longitude - 105.0,
                wgLoc.latitude - 35.0);
        double radLat = wgLoc.latitude / 180.0 * Math.PI;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * Math.PI);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * Math.PI);

        return new LatLng(wgLoc.latitude + dLat, wgLoc.longitude + dLon);
    }

    public static boolean outOfChina(double lat, double lon) {
        if (lon < 72.004 || lon > 137.8347)
            return true;
        if (lat < 0.8293 || lat > 55.8271)
            return true;
        return false;
    }

    private static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y
                + 0.2 * Math.sqrt(x > 0 ? x : -x);
        ret += (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x
                * Math.PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * Math.PI) + 40.0 * Math.sin(y / 3.0
                * Math.PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * Math.PI) + 320 * Math.sin(y
                * Math.PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1
                * Math.sqrt(x > 0 ? x : -x);
        ret += (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x
                * Math.PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * Math.PI) + 40.0 * Math.sin(x / 3.0
                * Math.PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * Math.PI) + 300.0 * Math.sin(x
                / 30.0 * Math.PI)) * 2.0 / 3.0;
        return ret;
    }

    //构建marker
    public static Marker buildMarker(GoogleMap map, Location location) {
        return map.addMarker(getMarkerOptions(location.getLatitude(),
                location.getLongitude(),
                MapApp.getInstance().getResStr(R.string.CurrentLocation)
                , ""
                , Const.DEFAULT_MARKER_ID));
    }

    //位置移动动画
    public static void moveCamera(GoogleMap map, Location location, boolean needMove, float zoom) {
        if (map == null || location == null) return;
        float currentZoom;
        if (zoom == Const.ERROR_MAP_ZOOM) {
            currentZoom = 14.2f;
        } else if (zoom == Const.CURRENT_MAP_ZOOM) {
            currentZoom = map.getCameraPosition().zoom;
        } else {
            currentZoom = 15.2f;
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (needMove) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, currentZoom));
        }
    }

    //获取MarkOption
    private static MarkerOptions getMarkerOptions(double lat, double lng, String title, String content, Object obj) {
        MarkerOptions markerOptions = new MarkerOptions();
        BitmapDescriptor bitmapDescriptor = null;
        if (obj instanceof Integer) {
            int iconResId = (int) obj;
            if (iconResId == Const.DEFAULT_MARKER_ID) {
                bitmapDescriptor = BitmapDescriptorFactory.defaultMarker();
            } else {
                bitmapDescriptor = BitmapDescriptorFactory.fromResource(iconResId);
            }
        } else if (obj instanceof Bitmap) {
            bitmapDescriptor = BitmapDescriptorFactory.fromBitmap((Bitmap) obj);
        }
        if (bitmapDescriptor == null) return null;
        markerOptions.icon(bitmapDescriptor)
                .anchor(0.0f, 1.0f)
                .position(new LatLng(lat, lng)).title(title).snippet(content);
        return markerOptions;
    }

    //显示搜索的marker
    public static Marker showMarker(GoogleMap mGoogleMap, String name, String address, double lat, double lng, Bitmap bitmap) {
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 16f));
        Marker marker = null;
        if (bitmap == null) {
            marker = mGoogleMap.addMarker(getMarkerOptions(lat, lng, autoChangeLine(name, 20), autoChangeLine(address, 25), Const.DEFAULT_MARKER_ID));
        } else {
            marker = mGoogleMap.addMarker(getMarkerOptions(lat, lng, autoChangeLine(name, 20), autoChangeLine(address, 25), bitmap));
        }
        marker.showInfoWindow();
        return marker;
    }

    //将字符串进行每多少个字符进行换行
    private static String autoChangeLine(String line, int changeCount) {
        if (line == null || line.length() < 1 || changeCount < 0) return null;
        StringBuilder sb = new StringBuilder(line);
        for (int index = changeCount; index < sb.length(); index += (changeCount + 1)) {
            sb.insert(index, '\n');
        }
        return sb.toString();
    }

    public static String getGoogleMapUrl(LinkedHashMap<String, Object> mapCache) {
        StringBuilder sb = new StringBuilder();
        sb.append(Const.GOOGLE_BASE_URL_START);
        int i = 0;
        if (mapCache != null && mapCache.size() > 0) {
            for (Map.Entry<String, Object> entry : mapCache.entrySet()) {
                if (i < mapCache.size() - 1) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    sb.append(key).append("=").append(value).append("&");
                } else {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    sb.append(key).append("=").append(value);
                }
                i++;
            }
        }
        return sb.toString();
    }

    public static void setAllPointsInScreen(GoogleMap googleMap, PolylineOptions polylineOptions) {
        if (polylineOptions == null) return;
        List<LatLng> points = polylineOptions.getPoints();
        if (points == null || points.size() < 1) return;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < points.size(); i++) {
            LatLng latLng = points.get(i);
            builder.include(new LatLng(latLng.latitude, latLng.longitude));
        }
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 200));
    }
}
