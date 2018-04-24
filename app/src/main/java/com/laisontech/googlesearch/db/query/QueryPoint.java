package com.laisontech.googlesearch.db.query;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by SDP on 2018/4/24.
 */
@DatabaseTable(tableName = "querypointinfotable")
public class QueryPoint implements Serializable {
    private static final long serialVersionUID = 201804241736L;
    @DatabaseField(generatedId = true, dataType = DataType.INTEGER)//数据库的主键--primary key
    private int id;
    //下载任务名称
    @DatabaseField(columnName = "queryName", dataType = DataType.STRING)
    private String queryName;
    //地点的基本概述图片
    @DatabaseField(columnName = "icon", dataType = DataType.STRING)
    private String icon;
    //地点详情
    @DatabaseField(columnName = "formattedAddress", dataType = DataType.STRING)
    private String formattedAddress;
    @DatabaseField(columnName = "placeId", dataType = DataType.STRING)
    private String placeId;//用于查询信息详情
    @DatabaseField(columnName = "photoReference", dataType = DataType.STRING)
    private String photoReference;//照片引用，用于显示照片
    //纬度
    @DatabaseField(columnName = "lat", dataType = DataType.DOUBLE)
    private double lat;
    //经度
    @DatabaseField(columnName = "lng", dataType = DataType.DOUBLE)
    private double lng;
    //日期
    @DatabaseField(columnName = "queryDate", dataType = DataType.LONG)
    private long queryDate;

    public QueryPoint() {
    }

    public QueryPoint(String queryName, String icon, String formattedAddress, String placeId, long queryDate) {
        this.queryName = queryName;
        this.icon = icon;
        this.formattedAddress = formattedAddress;
        this.placeId = placeId;
        this.queryDate = queryDate;
    }

    public QueryPoint(String queryName, String icon, String formattedAddress, String placeId, String photoReference, long queryDate) {
        this.queryName = queryName;
        this.icon = icon;
        this.formattedAddress = formattedAddress;
        this.placeId = placeId;
        this.photoReference = photoReference;
        this.queryDate = queryDate;
    }

    public QueryPoint(String queryName, String icon, String formattedAddress, String placeId, String photoReference, double lat, double lng, long queryDate) {
        this.queryName = queryName;
        this.icon = icon;
        this.formattedAddress = formattedAddress;
        this.placeId = placeId;
        this.photoReference = photoReference;
        this.lat = lat;
        this.lng = lng;
        this.queryDate = queryDate;
    }

    public String getQueryName() {
        return queryName;
    }

    public void setQueryName(String queryName) {
        this.queryName = queryName;
    }

    public long getQueryDate() {
        return queryDate;
    }

    public void setQueryDate(long queryDate) {
        this.queryDate = queryDate;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public String getPhotoReference() {
        return photoReference;
    }

    public void setPhotoReference(String photoReference) {
        this.photoReference = photoReference;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "QueryPoint{" +
                "id=" + id +
                ", queryName='" + queryName + '\'' +
                ", icon='" + icon + '\'' +
                ", formattedAddress='" + formattedAddress + '\'' +
                ", placeId='" + placeId + '\'' +
                ", photoReference='" + photoReference + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", queryDate=" + queryDate +
                '}';
    }
}
