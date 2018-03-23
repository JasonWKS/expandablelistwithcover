package com.dulesz.listviewtest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
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
    private ListView mListView;
    private View mCoverContentView;
    private View mSelectListItemView;

    private int mNeedPadding = 0;
    private Animator mAnimator;

    public ListCoverView(@NonNull Context context) {
        super(context);
        init();
    }

    public ListCoverView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ListCoverView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ListCoverView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        setVisibility(View.GONE);
        Drawable drawable = getBackground();
        drawable.setAlpha(0);

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

    public void init(View expandView,ListView listView){
        mCoverContentView = expandView;
        mListView = listView;

        addView(expandView);
    }

    public void setSelectListItemView(View selectListItemView) {
        mSelectListItemView = selectListItemView;
    }

    public boolean isAnimRunning(){
        return mAnimator != null && mAnimator.isRunning();
    }

    public void start(){
        int height = mListView.getHeight();
        int top = mSelectListItemView.getTop();
        int expandHeight = getResources().getDimensionPixelSize(R.dimen.list_item_expand_height);
        mNeedPadding = (expandHeight - (height - top));
        Log.e("TAG222","diff=" + mNeedPadding);

        showCoverView(true);
    }

    private void showCoverView(final boolean show){
        if(mSelectListItemView == null){
            return;
        }
        setVisibility(View.VISIBLE);
        int startHeight = getResources().getDimensionPixelSize(R.dimen.list_item_height);
        int endHeight = getResources().getDimensionPixelSize(R.dimen.list_item_expand_height);
        int startAlpha = 0;
        int endAlpha = 100;
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

        AnimItem startItem = new AnimItem();
        startItem.itemHeight = startHeight;
        startItem.coverAlpha = startAlpha;
        startItem.listMargin = startPadding;
        startItem.itemTop = mSelectListItemView.getTop();

        AnimItem endItem = new AnimItem();
        endItem.itemHeight = endHeight;
        endItem.coverAlpha = endAlpha;
        endItem.listMargin = endPadding;
        endItem.itemTop = mSelectListItemView.getTop();

        Log.e("TAG222","startHeight:" + startHeight + ", startAlpha:" + startAlpha + ", startPadding:" + startPadding);

        Log.e("TAG222","endHeight:" + endHeight + ", endAlpha:" + endAlpha + ", endPadding:" + endPadding);

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
                        item.translationY = item.itemTop + Math.min(0,item.listMargin);
                        return item;
                    }
                },startItem,endItem);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                Log.e("TAG333","onAnimationEnd");
                if(!show){
                    setVisibility(View.GONE);
                    mSelectListItemView = null;
                    mNeedPadding = 0;
                }
                mAnimator = null;
            }
        });

        animator.setDuration(300);
        animator.start();

        mAnimator = animator;
    }

    private class AnimItem{
        public int itemTop;
        public int coverAlpha;
        public int itemHeight;
        public int listMargin;
        public int translationY;
    }

    private class AnimObject{
        private View coverView;
        private View coverContentView,itemContentView;
        private View listview;

        public AnimObject(View coverView,  View coverContentView, View itemContentView, View listview) {
            this.coverView = coverView;
            this.coverContentView = coverContentView;
            this.itemContentView = itemContentView;
            this.listview = listview;
        }

        public void setValue(AnimItem value){
            Log.e("TAG222","value:" + value.coverAlpha + ","
                    + value.itemHeight + "," + value.itemTop + ","
                    + value.listMargin + "," + value.translationY);

            FrameLayout.LayoutParams lvParams = (FrameLayout.LayoutParams) this.listview.getLayoutParams();
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
