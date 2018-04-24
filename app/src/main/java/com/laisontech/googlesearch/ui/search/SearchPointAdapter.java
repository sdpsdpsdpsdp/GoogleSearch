package com.laisontech.googlesearch.ui.search;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.laisontech.googlesearch.R;
import com.laisontech.googlesearch.base.CommonAdapter;
import com.laisontech.googlesearch.base.CommonViewHolder;
import com.laisontech.googlesearch.db.DatabaseOperate;
import com.laisontech.googlesearch.db.query.QueryPoint;
import com.laisontech.googlesearch.interfaces.OnPreviewSearchListener;

import java.util.List;

/**
 * Created by SDP on 2018/4/24.
 */

public class SearchPointAdapter extends CommonAdapter<QueryPoint> {
    public SearchPointAdapter(Context mContext, List<QueryPoint> mData) {
        super(mContext, mData);
    }

    private OnPreviewSearchListener listener;

    @Override
    public int layoutId() {
        return R.layout.lv_search_point_item;
    }

    @Override
    public void convert(CommonViewHolder holder, final QueryPoint queryPoint, final int position) {
        ((TextView) holder.getView(R.id.tv_search_name)).setText(queryPoint.getQueryName());
        holder.getView(R.id.ll_preview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onPreview(queryPoint, false);
                }
                DatabaseOperate.saveSearchInfo(queryPoint);
            }
        });
        holder.getView(R.id.rl_nav).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onPreview(queryPoint, true);
                }
                DatabaseOperate.saveSearchInfo(queryPoint);
            }
        });
    }

    public void setData(List<QueryPoint> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public void clearData() {
        mData = null;
        notifyDataSetChanged();
    }

    /**
     * @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
     * if (mAdapter == null) return;
     * QueryPoint queryPoint = mAdapter.getItem(position);
     * if (queryPoint == null) return;
     * EventBus.getDefault().post(new QueryEvent(queryPoint));
     * finish();
     * }
     */
    public void setOnPreviewListener(OnPreviewSearchListener listener) {
        this.listener = listener;
    }
}
