package com.laisontech.googlesearch.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by panda on 2017/1/9.
 */

public class CommonViewHolder {
    private SparseArray<View> mViews;//key 为int，value为object的情况先推荐使用，效率更�?
    private View mConvertView;

    private CommonViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
        this.mViews = new SparseArray<>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        mConvertView.setTag(this);
    }

    public static CommonViewHolder getViewHolder(Context context, View convertView, ViewGroup parent, int layoutId, int position) {
        if (convertView == null) {
            return new CommonViewHolder(context, parent, layoutId, position);
        } else {
            CommonViewHolder holder = (CommonViewHolder) convertView.getTag();
            //更新position
            return holder;
        }
    }

    //通过id获得控件
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getConvertView() {
        return mConvertView;
    }

    public CommonViewHolder setText(int viewId, String text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

    //设置ImageView的�?
    public CommonViewHolder setSrc(int viewId, int resId) {
        ImageView iv = getView(viewId);
        iv.setImageResource(resId);
        return this;
    }

    public CommonViewHolder setBitmap(int viewId, Bitmap bitmap) {
        ImageView iv = getView(viewId);
        iv.setImageBitmap(bitmap);
        return this;
    }

    //设置ImageView的
    public CommonViewHolder setImageURI(Context context, int viewId, String url) {
        ImageView iv = getView(viewId);
        return this;
    }

    public interface IOnItemClickListener {
        void onItemClick(int position, View convertView);
    }
}
