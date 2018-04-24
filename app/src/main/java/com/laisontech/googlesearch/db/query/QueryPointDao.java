package com.laisontech.googlesearch.db.query;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.DatabaseConnection;
import com.laisontech.googlesearch.db.DBHelper;
import com.laisontech.googlesearch.db.IDBDao;
import com.laisontech.googlesearch.entity.SearchTextInfo;

import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.List;

/**
 * Created by SDP on 2018/4/24.
 */

public class QueryPointDao implements IDBDao<QueryPoint> {
    private Dao<QueryPoint, Integer> mDao;

    public QueryPointDao(Context context) {
        DBHelper helper = DBHelper.getInstance(context);
        try {
            mDao = helper.getDao(QueryPoint.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insert(QueryPoint queryPoint) {
        try {
            mDao.create(queryPoint);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createOrUpdate(QueryPoint queryPoint) {

    }

    @Override
    public void delete(QueryPoint queryPoint) {
        try {
            mDao.delete(queryPoint);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteList(List<QueryPoint> list) {
        try {
            if (list != null) {
                mDao.delete(list);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteListData(List<QueryPoint> list) {
        if (list != null && list.size() > 0) {
            for (QueryPoint dcuDetailDB : list) {
                delete(dcuDetailDB);
            }
        }
    }

    @Override
    public List<QueryPoint> listAll() {
        try {
            return mDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void createOrDeleteList(List<QueryPoint> list, boolean isCreate) {
        if (list == null || list.size() < 1) return;
        try {
            DatabaseConnection conn = mDao.startThreadConnection();
            Savepoint savepoint = conn.setSavePoint(null);
            for (QueryPoint taskInfo : list) {
                if (isCreate) {
                    insert(taskInfo);
                } else {
                    delete(taskInfo);
                }
            }
            conn.commit(savepoint);
            mDao.endThreadConnection(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<QueryPoint> queryInfoByDateAndLimit(long limit) {
        try {
            return mDao.queryBuilder()
                    .orderBy("queryDate", false)
                    .limit(limit)
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void saveOrUpdate(SearchTextInfo.Results result) {
        try {
            if (result.place_id == null || result.place_id.isEmpty()) return;
            List<QueryPoint> query = mDao.queryBuilder()
                    .where()
                    .eq("placeId", result.place_id)
                    .query();
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
            if (query == null || query.size() < 1) {
                QueryPoint queryPoint = new QueryPoint(result.name
                        , result.icon
                        , result.formatted_address
                        , result.place_id
                        , photo
                        , lat
                        , lng
                        , System.currentTimeMillis());
                insert(queryPoint);
            } else {
                QueryPoint queryPoint = query.get(0);
                queryPoint.setQueryName(result.name);
                queryPoint.setIcon(result.icon);
                queryPoint.setFormattedAddress(result.formatted_address);
                queryPoint.setPhotoReference(photo);
                queryPoint.setLat(lat);
                queryPoint.setLng(lng);
                queryPoint.setQueryDate(System.currentTimeMillis());
                mDao.update(queryPoint);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveOrUpdate(QueryPoint queryPointNew) {
        if (queryPointNew == null) return;
        try {
            List<QueryPoint> query = mDao.queryBuilder()
                    .where()
                    .eq("placeId", queryPointNew.getPlaceId())
                    .query();
            if (query == null || query.size() < 1) {
                insert(queryPointNew);
            } else {
                QueryPoint queryPointOld = query.get(0);
                queryPointOld.setQueryName(queryPointNew.getQueryName());
                queryPointOld.setIcon(queryPointNew.getIcon());
                queryPointOld.setFormattedAddress(queryPointNew.getFormattedAddress());
                queryPointOld.setPhotoReference(queryPointNew.getPhotoReference());
                queryPointOld.setLat(queryPointNew.getLat());
                queryPointOld.setLng(queryPointNew.getLng());
                queryPointOld.setQueryDate(System.currentTimeMillis());
                mDao.update(queryPointOld);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
