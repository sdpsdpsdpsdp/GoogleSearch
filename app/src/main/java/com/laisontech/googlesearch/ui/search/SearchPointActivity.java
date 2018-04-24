package com.laisontech.googlesearch.ui.search;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.laisontech.googlesearch.R;
import com.laisontech.googlesearch.db.DatabaseOperate;
import com.laisontech.googlesearch.db.query.QueryPoint;
import com.laisontech.googlesearch.entity.SearchTextInfo;
import com.laisontech.googlesearch.event.QueryEvent;
import com.laisontech.googlesearch.interfaces.OnEditTextSearchListener;
import com.laisontech.googlesearch.interfaces.OnPreviewSearchListener;
import com.laisontech.googlesearch.utils.WindowUtils;
import com.laisontech.mvp.mvp.MVPBaseActivity;
import com.laisontech.mvp.utils.ActivityStack;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by SDP on 2018/4/23.
 */

public class SearchPointActivity extends MVPBaseActivity<SearchPointContract.View, SearchPointPresenter> implements OnEditTextSearchListener, SearchPointContract.View, OnPreviewSearchListener {
    public static final String TAG = SearchPointActivity.class.getSimpleName();
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.iv_delete)
    ImageView ivDelete;
    @BindView(R.id.pb_search)
    ProgressBar pbSearch;
    @BindView(R.id.ll_search)
    LinearLayout llSearch;
    @BindView(R.id.lv_searchInf)
    ListView lvSearchInfo;
    private SearchPointAdapter mAdapter;

    @Override
    protected int setContentViewID() {
        return R.layout.activity_search_point;
    }

    @Override
    protected void initEvent() {
        ActivityStack.getScreenManager().pushActivity(this);
        WindowUtils.setLayoutInStatusBar(this);
        WindowUtils.showStatusBar(this, android.R.color.black);
        WindowUtils.setEtEvent(etSearch, ivDelete, pbSearch, this);
        mPresenter.showSearchHistory();
    }

    @Override
    public void onSearch() {
        String searchInfo = etSearch.getText().toString();
        if (searchInfo.isEmpty()) return;
        querySearchInfoByNet(searchInfo);
    }

    @Override
    public void onShowLocalData() {
        mPresenter.showSearchHistory();
    }

    @OnClick({R.id.iv_back, R.id.iv_delete, R.id.tv_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                this.finish();
                break;
            case R.id.iv_delete:
                etSearch.setText("");
                clearLvData();
                mPresenter.showSearchHistory();
                break;
            case R.id.tv_search:
                String s = etSearch.getText().toString();
                if (s.isEmpty()) {
                    showToast(R.string.PleaseInputSearchInfo);
                    return;
                }
                querySearchInfoByNet(s);
                break;
        }
    }

    private void querySearchInfoByNet(String queryInfo) {
        showUIVisible(true);
        mPresenter.queryPointInfo(queryInfo);
    }

    @Override
    public void showQueryInfoError(String errorMsg) {
        showUIVisible(false);
        showToast(errorMsg);
    }

    @Override
    public void showQueryInfoSuccess(SearchTextInfo searchTextInfo) {
        showUIVisible(false);
        clearLvData();
        setAdapter(DatabaseOperate.queryBySearch(searchTextInfo));
    }


    //本地没有搜索记录
    @Override
    public void showNoSearchRecord() {

    }

    //本地有记录需要显示
    @Override
    public void showSearchRecord(List<QueryPoint> queryPointList) {
        setAdapter(queryPointList);
    }

    private void setAdapter(List<QueryPoint> queryPointList) {
        if (mAdapter == null) {
            mAdapter = new SearchPointAdapter(this, queryPointList);
            lvSearchInfo.setAdapter(mAdapter);
        } else {
            mAdapter.setData(queryPointList);
        }
        if (mAdapter != null) {
            mAdapter.setOnPreviewListener(this);
        }
    }

    //显示信息和导航路线
    @Override
    public void onPreview(QueryPoint queryPoint, boolean needNav) {
        if (queryPoint == null) return;
        EventBus.getDefault().post(new QueryEvent(queryPoint, needNav));
        finish();
    }

    private void clearLvData() {
        if (mAdapter != null) {
            mAdapter.clearData();
        }
    }

    private void showUIVisible(boolean showPB) {
        int length = etSearch.getText().toString().length();
        if (length == 0) {
            pbSearch.setVisibility(View.GONE);
            ivDelete.setVisibility(View.GONE);
        } else {
            if (showPB) {
                pbSearch.setVisibility(View.VISIBLE);
                ivDelete.setVisibility(View.GONE);
            } else {
                pbSearch.setVisibility(View.GONE);
                ivDelete.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityStack.getScreenManager().popActivity(this);
    }

}
