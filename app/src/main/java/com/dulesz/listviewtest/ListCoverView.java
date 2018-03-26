package com.dulesz.listviewtest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ListView;

/**
 * Created by jason.shen on 2018/3/23.
 */

public class ListCoverView extends FrameLayout {
    private static final String TAG = ListCoverView.class.getSimpleName();

    public static final int ANIM_DUCATION = 300;
    private View mListView;
    private View mCoverContentView;
    private View mContentContainerView;
    private View mSelectListItemView;

    private int mNeedPadding = 0;
    private Animator mAnimator;

    private int mExpandedHeight = 0;
    private int mCollapsedHeight = 0;
    private int mShowAlpha = 0;
    private int mHideAlpha = 0;

    private int mExtraTop = 0;

    public ListCoverView(@NonNull Context context) {
        super(context);
        init(context,null);
    }

    public ListCoverView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public ListCoverView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ListCoverView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context,attrs);
    }

    private void initAttrs(Context context,AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ListCoverView);
        mShowAlpha = typedArray.getInteger(R.styleable.ListCoverView_showAlpha,100);
        mHideAlpha = typedArray.getInteger(R.styleable.ListCoverView_hideAlpha,0);
        mExpandedHeight = typedArray.getDimensionPixelSize(R.styleable.ListCoverView_expandedHeight,300);
        mCollapsedHeight = typedArray.getDimensionPixelSize(R.styleable.ListCoverView_collapsedHeight,0);
        typedArray.recycle();
    }

    private void init(Context context,AttributeSet attrs){
        if(attrs != null){
            initAttrs(context,attrs);
        }
        setVisibility(View.GONE);
        Drawable drawable = getBackground();
        drawable.setAlpha(mHideAlpha);

        this.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAnimRunning()){
                    return;
                }
                showCoverView(false);
            }
        });
    }

    public void setExpandedHeight(int expandedHeight) {
        mExpandedHeight = expandedHeight;
    }

    public void setCollapsedHeight(int collapsedHeight) {
        mCollapsedHeight = collapsedHeight;
    }

    public void setShowAlpha(int showAlpha) {
        mShowAlpha = showAlpha;
    }

    public void setHideAlpha(int hideAlpha) {
        mHideAlpha = hideAlpha;
    }

    public void setViews(View expandView, View listView, View contentContainerView){
        mCoverContentView = expandView;
        mListView = listView;
        mContentContainerView = contentContainerView;

        if(expandView.getParent() == null){
            addView(expandView);
        }

        mListView.post(new Runnable() {
            @Override
            public void run() {
                caculateTop();
            }
        });
    }

    public void setSelectListItemView(View selectListItemView) {
        mSelectListItemView = selectListItemView;
    }

    public boolean isAnimRunning(){
        return mAnimator != null && mAnimator.isRunning();
    }

    private void check(){
        if(mListView == null){
            throw new NullPointerException("NULL Listview");
        }

        if(mCoverContentView == null){
            throw new NullPointerException("NULL CoverContentView");
        }

        if(mSelectListItemView == null){
            throw new NullPointerException("NULL SelectedItemView");
        }

        if(mShowAlpha <= 0){
            Log.e(TAG,"showAlpha is 0");
        }

        if(mExpandedHeight <= 0){
            Log.e(TAG,"ExpandedHeight is 0");
        }
    }

    public void start(){
        check();

        int height = mListView.getHeight();
        int top = mSelectListItemView.getTop();
        if(top < 0){
            mSelectListItemView.setTop(0);
            top = 0;
        }
        mNeedPadding = (mExpandedHeight - (height - top));
        Log.i(TAG,"diff=" + mNeedPadding);

        showCoverView(true);
    }

    private void showCoverView(final boolean show){
        if(mSelectListItemView == null){
            return;
        }
        setVisibility(View.VISIBLE);
        int startHeight = mCollapsedHeight;
        int endHeight = mExpandedHeight;
        int startAlpha = mHideAlpha;
        int endAlpha = mShowAlpha;
        int startPadding = 0;
        if(mNeedPadding <= 0){
            mNeedPadding = 0;
        }
        int endPadding = -1 * mNeedPadding;
        if(!show){
            int temp = startHeight;
            startHeight = endHeight;
            endHeight = temp;

            temp = startAlpha;
            startAlpha = endAlpha;
            endAlpha = temp;

            temp = startPadding;
            startPadding = endPadding;
            endPadding = temp;
        }

        final AnimItem startItem = new AnimItem();
        startItem.itemHeight = startHeight;
        startItem.coverAlpha = startAlpha;
        startItem.listMargin = startPadding;
        startItem.itemTop = mSelectListItemView.getTop();
        startItem.extraTop = mExtraTop;

        AnimItem endItem = new AnimItem();
        endItem.itemHeight = endHeight;
        endItem.coverAlpha = endAlpha;
        endItem.listMargin = endPadding;
        endItem.itemTop = mSelectListItemView.getTop();
        endItem.extraTop = mExtraTop;

        Log.i(TAG,"startHeight:" + startHeight + ", startAlpha:" + startAlpha + ", startPadding:" + startPadding);

        Log.i(TAG,"endHeight:" + endHeight + ", endAlpha:" + endAlpha + ", endPadding:" + endPadding);

        ObjectAnimator animator = ObjectAnimator.ofObject(new AnimObject(this, mCoverContentView, mSelectListItemView, mListView),
                "value",
                new TypeEvaluator<AnimItem>() {
                    @Override
                    public AnimItem evaluate(float fraction, AnimItem startValue, AnimItem endValue) {
                        AnimItem item = new AnimItem();
                        item.itemHeight = (int) (startValue.itemHeight + fraction * (endValue.itemHeight - startValue.itemHeight));
                        item.coverAlpha = (int) (startValue.coverAlpha + fraction * (endValue.coverAlpha - startValue.coverAlpha));
                        item.listMargin = (int) (startValue.listMargin + fraction * (endValue.listMargin - startValue.listMargin));
                        item.itemTop = startValue.itemTop;
                        item.extraTop = startValue.extraTop;
                        item.translationY = item.itemTop + Math.min(0,item.listMargin) + item.extraTop;
                        return item;
                    }
                },startItem,endItem);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                Log.i(TAG,"onAnimationEnd");
                if(!show){
                    setVisibility(View.GONE);
                    mSelectListItemView = null;
                    mNeedPadding = 0;
                }
                mAnimator = null;
            }
        });

        animator.setDuration(ANIM_DUCATION);
        animator.start();

        mAnimator = animator;
    }

    private void caculateTop(){
        int[] listLoc = new int[2];
        mListView.getLocationInWindow(listLoc);

        int[] coverLoc = new int[2];
        mContentContainerView.getLocationInWindow(coverLoc);

        mExtraTop = listLoc[1] - coverLoc[1];
    }

    private class AnimItem{
        public int itemTop;
        public int extraTop;
        public int coverAlpha;
        public int itemHeight;
        public int listMargin;
        public int translationY;
    }

    private class AnimObject{
        private View coverView;
        private View coverContentView,itemContentView;
        private ListView listview;

        public AnimObject(View coverView,  View coverContentView, View itemContentView, View listview) {
            this.coverView = coverView;
            this.coverContentView = coverContentView;
            this.itemContentView = itemContentView;
            this.listview = (ListView) listview;
        }

        public void setValue(AnimItem value){
            Log.i(TAG,"value:" + value.coverAlpha + ","
                    + value.itemHeight + "," + value.itemTop + ","
                    + value.listMargin + "," + value.translationY);

            ViewGroup.MarginLayoutParams lvParams = (ViewGroup.MarginLayoutParams) this.listview.getLayoutParams();
            lvParams.topMargin = value.listMargin;
            this.listview.setLayoutParams(lvParams);

            ViewGroup.LayoutParams params =  this.itemContentView.getLayoutParams();
            params.height = value.itemHeight;
            this.itemContentView.setLayoutParams(params);

            Drawable drawable = coverView.getBackground();
            drawable.setAlpha(value.coverAlpha);

            ViewGroup.LayoutParams params2 =  this.coverContentView.getLayoutParams();
            params2.height = value.itemHeight;
            this.coverContentView.setTranslationY(value.translationY);
            this.coverContentView.setLayoutParams(params2);

        }
    }
}
