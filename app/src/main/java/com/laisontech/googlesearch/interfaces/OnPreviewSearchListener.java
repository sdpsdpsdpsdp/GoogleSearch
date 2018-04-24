package com.laisontech.googlesearch.interfaces;

import com.laisontech.googlesearch.db.query.QueryPoint;

/**
 * Created by SDP on 2018/4/24.
 */

public interface OnPreviewSearchListener {
    void onPreview(QueryPoint queryPoint, boolean needNav);
}
