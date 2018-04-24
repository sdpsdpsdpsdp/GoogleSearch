package com.laisontech.googlesearch.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by panda on 2017/1/10.
 */

public abstract class CommonAdapter<T> extends BaseAdapter {
    protected Context mContext;
    protected List<T> mData;
    private int mLayoutId;

    public CommonAdapter(Context mContext, List<T> mData) {
        this.mContext = mContext;
        this.mData = mData;
        this.mLayoutId = layoutId();
    }
    //传入的item布局
    public abstract int layoutId();

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommonViewHolder holder = CommonViewHolder.getViewHolder(mContext, convertView, parent, mLayoutId, position);
        convert(holder, getItem(position),position);
        return holder.getConvertView();
    }
    //返回的数据
    public abstract void convert(CommonViewHolder holder, T t,int position);

}
