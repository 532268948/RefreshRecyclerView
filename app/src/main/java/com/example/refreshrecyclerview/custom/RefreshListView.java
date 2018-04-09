package com.example.refreshrecyclerview.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.example.refreshrecyclerview.base.RefreshAdapterView;

/**
 * 作 者： ZUST_YTH
 * 日 期： 2018/4/9
 * 时 间： 20:25
 * 项 目： RefreshRecyclerView
 * 描 述：
 */


public class RefreshListView extends RefreshAdapterView<ListView> {

    public RefreshListView(Context context) {
        this(context, null);
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void setupContentView(Context context) {
        mContentView = new ListView(context);
        mContentView.setOnScrollListener(this);
    }

    @Override
    protected Boolean isTop() {
        return mContentView.getFirstVisiblePosition() == 0 && getScrollY() <= mHeaderView.getMeasuredHeight();
    }

    @Override
    protected Boolean isBottom() {
        return mContentView != null && mContentView.getAdapter() != null && mContentView.getLastVisiblePosition() == mContentView.getAdapter().getCount() - 1;
    }
}
