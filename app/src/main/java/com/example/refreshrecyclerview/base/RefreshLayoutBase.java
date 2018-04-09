package com.example.refreshrecyclerview.base;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.TextView;

import com.example.refreshrecyclerview.R;
import com.example.refreshrecyclerview.listener.OnLoadListener;
import com.example.refreshrecyclerview.listener.OnRefreshListener;

import java.text.SimpleDateFormat;
import java.util.Date;

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

    private Boolean isArrowUp=false;

    /*本次触摸Y坐标偏移量*/
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
    public static final int STATUS_REFRESHING = 3;
    /*加载中*/
    public static final int STATUS_LOADING = 4;
    /*当前状态*/
    protected int mCurrentStatus = STATUS_IDLE;

    protected OnRefreshListener mOnRefreshListener;
    protected OnLoadListener mOnLoadListener;


    public RefreshLayoutBase(Context context) {
        this(context, null);
    }

    public RefreshLayoutBase(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshLayoutBase(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScroller = new Scroller(context);
        mScreenHeight = context.getResources().getDisplayMetrics().heightPixels;
        mHeaderHeight = mScreenHeight / 4;
        initLayout(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int childCount = getChildCount();
        int finalHeight = 0;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            finalHeight += child.getMeasuredHeight();
        }
        setMeasuredDimension(width, finalHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int left = getPaddingLeft();
        int top = getPaddingTop();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            child.layout(left, top, child.getMeasuredWidth() + top, top += child.getMeasuredHeight());
        }
        mInitScrollY = mHeaderView.getMeasuredHeight() + getPaddingTop();
        scrollTo(0, mInitScrollY);
    }



    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            return false;
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                mYOffset = (int) ev.getRawY() - mLastY;
                if (isTop() && mYOffset > 0) {
                    return true;
                }
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                int currentY = (int) event.getRawY();
                mYOffset = currentY - mLastY;
                if (mCurrentStatus != STATUS_LOADING) {
                    changeScrollY(mYOffset);
                }
                rotateHeaderArrow();
                changeTips();
                mLastY = currentY;
                break;
            case MotionEvent.ACTION_UP:
                doRefresh();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mOnLoadListener != null && isBottom() && mScroller.getCurrY() <= mInitScrollY && mYOffset <= 0 && mCurrentStatus == STATUS_IDLE) {
            showFooterView();
            doLoadMore();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    private void doLoadMore() {
        if(mOnLoadListener!=null){
            mOnLoadListener.onLoadMore();
        }
    }

    private void showFooterView() {
        startScroll(mFooterView.getMeasuredHeight());
        mCurrentStatus=STATUS_LOADING;
    }

    private void startScroll(int yOffset) {
        mScroller.startScroll(getScrollX(), getScrollY(), 0, yOffset);
        invalidate();
    }

    public void refreshComplete() {
        mCurrentStatus = STATUS_IDLE;
        mScroller.startScroll(getScrollX(), getScrollY(), 0, mInitScrollY - getScrollY());
        invalidate();
        updateHeaderTimeStamp();
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                mArrowImageView.setVisibility(VISIBLE);
                mProgressBar.setVisibility(GONE);
            }
        }, 100);
    }

    public void loadCompelte() {
        startScroll(mInitScrollY - getScrollY());
        mCurrentStatus = STATUS_IDLE;
    }

    private void updateHeaderTimeStamp() {
        mTimeTextView.setText(R.string.pull_to_refresh_update_time_label);
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getInstance();
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
        mTimeTextView.append(sdf.format(new Date()));
    }

    private void doRefresh() {
        changeHeaderViewStaus();
        if (mCurrentStatus == STATUS_REFRESHING && mOnRefreshListener != null) {
            mOnRefreshListener.onRefresh();
        }
    }

    private void changeHeaderViewStaus() {
        int curScrollY = getScrollY();
        if (curScrollY < mInitScrollY / 2) {
            mScroller.startScroll(getScrollX(), curScrollY, 0, mHeaderView.getPaddingTop() - curScrollY);
            mCurrentStatus = STATUS_REFRESHING;
            mTipsTextView.setText(R.string.pull_to_refresh_refreshing_label);
            mArrowImageView.clearAnimation();
            mArrowImageView.setVisibility(GONE);
            mProgressBar.setVisibility(VISIBLE);
        } else {
            mScroller.startScroll(getScrollX(), curScrollY, 0, mInitScrollY - curScrollY);
            mCurrentStatus = STATUS_IDLE;
        }
        invalidate();
    }

    private void changeTips() {
        if (mCurrentStatus == STATUS_PULL_TO_REFRESH) {
            mTipsTextView.setText(R.string.pull_to_refresh_pull_label);
        } else if (mCurrentStatus == STATUS_RELEASE_TO_REFRESH) {
            mTipsTextView.setText(R.string.pull_to_refresh_release_label);
        }
    }

    private void rotateHeaderArrow() {
        if (mCurrentStatus == STATUS_REFRESHING) {
            return;
        } else if (mCurrentStatus == STATUS_PULL_TO_REFRESH && !isArrowUp) {
            return;
        } else if (mCurrentStatus == STATUS_RELEASE_TO_REFRESH && isArrowUp) {
            return;
        }
        mProgressBar.setVisibility(GONE);
        mArrowImageView.setVisibility(VISIBLE);
        float pivtoX = mArrowImageView.getWidth() / 2f;
        float pivtoY = mArrowImageView.getHeight() / 2f;
        float fromDegrees = 0f;
        float toDegrees = 0f;
        if (mCurrentStatus == STATUS_PULL_TO_REFRESH) {
            fromDegrees = 180f;
            toDegrees = 360f;
        } else if (mCurrentStatus == STATUS_RELEASE_TO_REFRESH) {
            fromDegrees = 0f;
            toDegrees = 180f;
        }
        RotateAnimation animation = new RotateAnimation(fromDegrees, toDegrees, pivtoX, pivtoY);
        animation.setDuration(100);
        animation.setFillAfter(true);
        mArrowImageView.setAnimation(animation);
        if (mCurrentStatus == STATUS_RELEASE_TO_REFRESH) {
            isArrowUp = true;
        } else {
            isArrowUp = false;
        }
    }

    private void changeScrollY(int distance) {
        int curY = getScrollY();
        if (distance > 0 && curY - distance > getPaddingTop()) {
            scrollBy(0, -distance);
        } else if (distance < 0 && curY - distance <= mInitScrollY) {
            scrollBy(0, -distance);
        }
        curY = getScrollY();
        int slop = mInitScrollY / 2;
        if (curY > 0 && curY < slop) {
            mCurrentStatus = STATUS_RELEASE_TO_REFRESH;
        } else if (curY > 0 && curY > slop) {
            mCurrentStatus = STATUS_PULL_TO_REFRESH;
        }
    }

    private final void initLayout(Context context) {
        setupHeaderView(context);
        setupContentView(context);
        setDefaultContentLayoutParams();
        addView(mContentView);
        setupFooterView(context);
    }


    protected void setupHeaderView(Context context) {
        mHeaderView = LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header, this, false);
        mHeaderView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, mHeaderHeight));
        mHeaderView.setBackgroundColor(Color.RED);
        mHeaderView.setPadding(0, mHeaderHeight - 100, 0, 0);
        addView(mHeaderView);

        mArrowImageView = (ImageView) mHeaderView.findViewById(R.id.pull_to_arrow_image);
        mTipsTextView = (TextView) mHeaderView.findViewById(R.id.pull_to_refresh_text);
        mTimeTextView = (TextView) mHeaderView.findViewById(R.id.pull_to_refresh_updated_at);
        mProgressBar = (ProgressBar) mHeaderView.findViewById(R.id.pull_to_refresh_progress);
    }

    protected abstract void setupContentView(Context context);

    protected void setDefaultContentLayoutParams() {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mContentView.setLayoutParams(params);
    }

    protected void setupFooterView(Context context) {
        mFooterView = LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_footer, this, false);
        addView(mFooterView);
    }

    protected abstract Boolean isTop();

    protected abstract Boolean isBottom();

    public void setOnRefreshListener(OnRefreshListener listener) {
        mOnRefreshListener = listener;
    }

    public void setOnLoadListener(OnLoadListener listener) {
        mOnLoadListener = listener;
    }



}
