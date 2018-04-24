package com.laisontech.googlesearch.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.laisontech.googlesearch.db.query.QueryPoint;
import com.laisontech.googlesearch.utils.Const;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by SDP on 2017/5/5.
 * 记录：所以在有新的版本时，则直接对数据进行移植即可，表单的增加，需要数据库版本进行更改，但是如果对某个表单增加了列，或者删除了列，则需要进行数据库升级。
 * 2017 12 21日：版本13 抄表的表单，定位的表单等
 * <p>
 * 2017 12 25日：版本14，增加了手动录入的数据库，以及判断和升级
 * <p>
 * 2018 3 5日：版本15，增加了抄表任务名称字段
 * 2018 3 15日：版本16，增加新的数据库
 * 2018 3 28日：版本17，增加新的数据库，保存总使用购买信息
 * 4 6 日，版本 18，增加了数据库字段（是否上传） 。
 * <p>
 * 需要增加判断，增加新的表单时，对当前的的数据库版本进行保存，然后在数据库升级时对之前的版本进行判断
 */

public class DBHelper extends OrmLiteSqliteOpenHelper {
    //数据库名称 从网络端下载的表计信息
    private static final String DB_NAME = "googlesearch.db";
    //版本号，从10开始，每次数据库增加列则加1即可
    private static final int DB_VERSION = 2;
    private Map<String, Dao> maps = new HashMap<>();
    //使用单利进行数据的访问
    private static DBHelper instance;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (DBHelper.class) {
                if (instance == null) {
                    instance = new DBHelper(context);
                }
            }
        }
        return instance;
    }

    //获取
    @SuppressWarnings(value = {"unchecked"})
    public synchronized Dao getDao(Class clazz) throws SQLException {
        Dao dao = null;
        String className = clazz.getSimpleName();
        if (maps.containsKey(className)) {
            dao = maps.get(className);
        }
        if (dao == null) {
            dao = (Dao) super.getDao(clazz);
            maps.put(className, dao);
        }
        return dao;
    }

    //onCreate只会创建一回
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        createOrUpdateDB(-1, Const.KEY_SAVE_DATABASE_QUERY_POINT, QueryPoint.class, sqLiteDatabase, connectionSource);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        if (oldVersion<DB_VERSION){
            createOrUpdateDB(newVersion, Const.KEY_SAVE_DATABASE_QUERY_POINT, QueryPoint.class, sqLiteDatabase, connectionSource);
        }
    }

    private void createOrUpdateDB(int newVersion, String keySave, Class<?> clz, SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            if (DatabaseOperate.getDaveDatabaseVersion(keySave) == 0) {
                TableUtils.createTable(connectionSource, clz);
                DatabaseOperate.saveDatabaseVersion(keySave, DB_VERSION);
            } else {
                //表单对应的数据库的版本不为0，并且保存的版本小于新版本，则进行数据迁移
                if (DatabaseOperate.getDaveDatabaseVersion(keySave) < newVersion) {
                    DatabaseUtil.upgradeTable(sqLiteDatabase, connectionSource, clz, DatabaseUtil.OperationType.ADD);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //关闭所有操作
    @Override
    public void close() {
        super.close();
        for (String key : maps.keySet()) {
            Dao dao = maps.get(key);
            dao = null;
        }
        instance = null;
    }
}
