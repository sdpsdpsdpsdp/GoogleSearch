package com.laisontech.googlesearch.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.Arrays;


/**
 * Created by SDP on 2017/12/12.
 * 数据库帮助类
 * 用于操作增减数据库的字段
 * 首先将原来的表进行改名称rename table（临时表），接着创建新的表create table，
 * 然后将旧表内的数据迁移到新表内，
 * 最后drop table删除临时表。
 * 切记，当前方法只能删除表单，如果想要更改字段或者删除字段需要另外处理，因为删除某个字段，如果已经使用了某个字段
 * 就会在新的表单上因为没有改字段，出现了旧数据无处安放的错误，所以智能在新表单上增加字段。
 */

public class DatabaseUtil {
    public static final String TAG = DatabaseUtil.class.getSimpleName();

    public enum OperationType {
        ADD,//增加字段
        DELETE//删除字段
    }

    /**
     * 升级表，增加字段
     */
    public static <T> void upgradeTable(SQLiteDatabase db, ConnectionSource source, Class<T> clz, OperationType type) {
        //不需要更新则不进行数据迁移，省去了耗时操作
        String tableName = DatabaseTableConfig.extractTableName(clz);//获取表名
        db.beginTransaction();//开启处理
        try {
            //设置临时的数据库表
            String tempTableName = tableName + "_temp";
            String sql = "ALTER TABLE " + tableName + " RENAME TO " + tempTableName;//更改名称为临时的表名
            db.execSQL(sql);
            //创建新的表格
            try {
                sql = TableUtils.getCreateTableStatements(source, clz).get(0);
                db.execSQL(sql);
            } catch (SQLException e) {
                e.printStackTrace();
                TableUtils.createTable(source, clz);
            }
            String columns;
            //增加了字段
            if (type == OperationType.ADD) {
                columns = Arrays.toString(getColumnNames(db, tempTableName)).replace("[", "").replace("]", "");
            } else if (type == OperationType.DELETE) {
                columns = Arrays.toString(getColumnNames(db, tableName)).replace("[", "").replace("]", "");
            } else {
                throw new IllegalArgumentException("OperationType error");
            }
            //将临时表的数据复制到之前的表中
            sql = "INSERT INTO " + tableName +
                    " (" + columns + ") " +
                    " SELECT " + columns + " FROM " + tempTableName;
            db.execSQL(sql);

            //删除临时的数据库表
            sql = "DROP TABLE IF EXISTS " + tempTableName;
            db.execSQL(sql);
            //处理成功
            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

    }

    /**
     * 获取表的列名
     */
    private static String[] getColumnNames(SQLiteDatabase db, String tableName) {
        String[] columnNames = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);
            if (cursor != null) {
                int columnIndex = cursor.getColumnIndex("name");
                if (columnIndex == -1) {
                    return null;
                }

                int index = 0;
                columnNames = new String[cursor.getCount()];
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    columnNames[index] = cursor.getString(columnIndex);
                    index++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return columnNames;
    }
}
