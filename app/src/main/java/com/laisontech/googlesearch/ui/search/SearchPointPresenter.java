package com.laisontech.googlesearch.ui.search;

import android.util.Log;

import com.google.gson.Gson;
import com.laisontech.googlesearch.R;
import com.laisontech.googlesearch.app.MapApp;
import com.laisontech.googlesearch.db.query.QueryPoint;
import com.laisontech.googlesearch.db.query.QueryPointDao;
import com.laisontech.googlesearch.entity.SearchTextInfo;
import com.laisontech.googlesearch.ui.main.MainContract;
import com.laisontech.googlesearch.utils.Const;
import com.laisontech.googlesearch.utils.JsonParse;
import com.laisontech.mvp.laisonjsonparse.JsonResultParseUtils;
import com.laisontech.mvp.mvp.BasePresenterImpl;
import com.laisontech.mvp.net.OkHttpConnect;
import com.laisontech.mvp.net.OnConnectResultListener;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by SDP on 2018/4/23.
 */

public class SearchPointPresenter extends BasePresenterImpl<SearchPointContract.View> implements SearchPointContract.Presenter {
    @Override
    public void queryPointInfo(String queryInfo) {
        OkHttpConnect.getInstance().cancelTag(SearchPointActivity.TAG);//取消上次的连接
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("query", queryInfo);
//        map.put("radius", "10000");
        map.put("key", MapApp.getInstance().getResStr(R.string.GoogleKey));
        Log.e(SearchPointActivity.TAG, "queryPointInfo: " + "url\n" + Const.queryTextUrl(map));
        OkHttpConnect.getInstance().buildGetString(Const.queryTextUrl(map), SearchPointActivity.TAG, new OnConnectResultListener() {
            @Override
            public void onResponse(String s) {
                if (s == null || s.isEmpty()) {
                    mView.showQueryInfoError("Json数据为空");
                    return;
                }
                boolean isJson = JsonResultParseUtils.checkStrIsJsonFormat(s);
                if (!isJson) {
                    mView.showQueryInfoError("Json格式错误");
                    return;
                }
                SearchTextInfo searchTextInfo = new Gson().fromJson(s, SearchTextInfo.class);
                if (searchTextInfo == null) {
                    mView.showQueryInfoError("Json格式错误");
                    return;
                }
                String status = searchTextInfo.status;
                if (!status.equals(Const.OK)) {
                    mView.showQueryInfoSuccess(null);
                    return;
                }
                mView.showQueryInfoSuccess(searchTextInfo);
            }

            @Override
            public void onError(String s) {
                mView.showQueryInfoError(s);
            }
        });
    }

    //查询本地搜索的信息，只显示最近保存的前12条
    @Override
    public void showSearchHistory() {
        QueryPointDao queryPointDao = new QueryPointDao(MapApp.getInstance().getLocalContext());
        List<QueryPoint> queryPointList = queryPointDao.queryInfoByDateAndLimit(Const.QUERY_LIMIT_HISTORY);
        if (queryPointList == null || queryPointList.size() < 1) {
            mView.showNoSearchRecord();
            return;
        }
        mView.showSearchRecord(queryPointList);
    }
}
