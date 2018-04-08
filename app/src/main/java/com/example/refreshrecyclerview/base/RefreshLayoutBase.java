package com.example.refreshrecyclerview.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.TextView;

import com.example.refreshrecyclerview.listener.OnRefreshListener;

/**
 * 作 者： ZUST_YTH
 * 日 期： 2018/4/8
 * 时 间： 22:49
 * 项 目： RefreshRecyclerView
 * 描 述：
 */


public abstract class RefreshLayoutBase<T extends View> extends ViewGroup implements AbsListView.OnScrollListener {

    protected Scroller mScroller;
    protected View mHeaderView;
    protected View mFooterView;
    protected T mContentView;

    private ImageView mArrowImageView;
    private TextView mTipsTextView;
    private TextView mTimeTextView;
    private ProgressBar mProgressBar;

    private Boolean isArrowUp;

    protected int mYOffset;
    protected int mInitScrollY = 0;
    protected int mLastY = 0;

    private int mScreenHeight;
    private int mHeaderHeight;


    /*空闲状态*/
    public static final int STATUS_IDLE = 0;
    /*未达到刷新状态*/
    public static final int STATUS_PULL_TO_REFRESH = 1;
    /*释放刷新*/
    public static final int STATUS_RELEASE_TO_REFRESH = 2;
    /*刷新中*/
    public static final int STATUS_REFRESHNG = 3;
    protected int mCurrentStatus = STATUS_IDLE;

    protected OnRefreshListener mOnReFreshListener;


    public RefreshLayoutBase(Context context) {
        super(context);
    }

    public RefreshLayoutBase(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshLayoutBase(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
