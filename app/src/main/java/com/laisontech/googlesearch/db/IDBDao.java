package com.laisontech.googlesearch.db;

import java.util.List;

/**
 * Created by SDP on 2017/4/19.
 * 接口有增删该查的功能
 */

public interface IDBDao<T> {
    /**
     * 插入数据
     */
    void insert(T t);

    /**
     * 更新数据
     */
    void createOrUpdate(T t);


    /**
     * delete
     */
    void delete(T t);

    /**
     * delete by list
     */
    void deleteList(List<T> list);

    /**
     * delete by list--<one by one delete>
     */
    void deleteListData(List<T> list);

    /**
     * 查询所有
     */
    List<T> listAll();

}
