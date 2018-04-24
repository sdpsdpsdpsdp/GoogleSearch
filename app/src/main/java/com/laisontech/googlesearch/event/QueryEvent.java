package com.laisontech.googlesearch.event;

import com.laisontech.googlesearch.db.query.QueryPoint;

/**
 * Created by SDP on 2018/4/24.
 */

public class QueryEvent {
    private QueryPoint message;
    private boolean needNav;
    public QueryEvent(QueryPoint message,boolean needNav) {
        this.message = message;
        this.needNav = needNav;
    }

    public QueryPoint getMessage() {
        return message;
    }

    public void setMessage(QueryPoint message) {
        this.message = message;
    }

    public boolean isNeedNav() {
        return needNav;
    }

    public void setNeedNav(boolean needNav) {
        this.needNav = needNav;
    }
}
