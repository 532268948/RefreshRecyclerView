package com.example.refreshrecyclerview.base;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListAdapter;

/**
 * 作 者： ZUST_YTH
 * 日 期： 2018/4/9
 * 时 间： 20:21
 * 项 目： RefreshRecyclerView
 * 描 述：
 */


public abstract class RefreshAdapterView<T extends AbsListView> extends RefreshLayoutBase<T>{
    public RefreshAdapterView(Context context) {
        this(context,null);
    }

    public RefreshAdapterView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RefreshAdapterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAdapter(ListAdapter adapter) {
        mContentView.setAdapter(adapter);
    }

    public ListAdapter getAdapter() {
        return mContentView.getAdapter();
    }
}
