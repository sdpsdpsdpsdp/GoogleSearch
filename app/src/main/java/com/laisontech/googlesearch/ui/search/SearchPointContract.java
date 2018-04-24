package com.laisontech.googlesearch.ui.search;

import com.laisontech.googlesearch.db.query.QueryPoint;
import com.laisontech.googlesearch.entity.SearchTextInfo;
import com.laisontech.mvp.mvp.BasePresenter;
import com.laisontech.mvp.mvp.BaseView;

import java.util.List;

/**
 * Created by SDP on 2018/4/23.
 */

public class SearchPointContract {
    interface View extends BaseView {

        void showQueryInfoError(String errorMsg);

        void showQueryInfoSuccess(SearchTextInfo searchTextInfo);

        void showNoSearchRecord();

        void showSearchRecord(List<QueryPoint> queryPointList);
    }

    interface Presenter extends BasePresenter<SearchPointContract.View> {
        void queryPointInfo(String queryInfo);

        void showSearchHistory();
    }
}
