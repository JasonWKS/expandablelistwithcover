package com.dulesz.listviewtest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private View mCoverView;
    private View mCoverContentView;
    private TextView mTitleView, mNameView, mContentView;
    private View mSelectListItemView;

    private int mNeedPadding = 0;
    private Animator mAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        mListView = findViewById(R.id.lv);
        mCoverView = findViewById(R.id.cover);
        mCoverContentView = mCoverView.findViewById(R.id.cover_content);
        mTitleView = mCoverContentView.findViewById(R.id.title);
        mNameView = mCoverContentView.findViewById(R.id.name);
        mContentView = mCoverContentView.findViewById(R.id.content);

        mCoverView.setVisibility(View.GONE);
        Drawable drawable = mCoverView.getBackground();
        if(drawable instanceof ColorDrawable){
            drawable.setAlpha(0);
        }

        initAdapter();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mAnimator != null && mAnimator.isRunning()){
                    return;
                }

                MyAdapter adapter = (MyAdapter) mListView.getAdapter();
                MyItem item = (MyItem) adapter.getItem(position);
                mTitleView.setText(item.title);
                mNameView.setText(item.name);
                mContentView.setText(item.content);

                mSelectListItemView = view;

                int height = mListView.getHeight();
                int top = mSelectListItemView.getTop();
                int expandHeight = getResources().getDimensionPixelSize(R.dimen.list_item_expand_height);
                mNeedPadding = (expandHeight - (height - top));
                Log.e("TAG222","diff=" + mNeedPadding);

                showCoverView(true);

//                ViewGroup.LayoutParams params = mSelectListItemView.getLayoutParams();
//                params.height = getResources().getDimensionPixelSize(R.dimen.list_item_expand_height);
//                mSelectListItemView.setLayoutParams(params);
//
//                FrameLayout.LayoutParams params1 = (FrameLayout.LayoutParams) mListView.getLayoutParams();
//                params1.topMargin = - mNeedPadding;
//                mListView.setLayoutParams(params1);
            }
        });

        mCoverView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAnimator != null && mAnimator.isRunning()){
                    return;
                }
                showCoverView(false);
            }
        });

        mCoverContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private void showCoverView(final boolean show){
        if(mSelectListItemView == null){
            return;
        }
        mCoverView.setVisibility(View.VISIBLE);
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

        ObjectAnimator animator = ObjectAnimator.ofObject(new AnimObject(mCoverView, mCoverContentView, mSelectListItemView, mListView),
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
                MainActivity.this.onAnimEnd(show);
            }
        });

        animator.setDuration(300);
        animator.start();

        mAnimator = animator;
    }

    private void onAnimEnd(boolean show){
        Log.e("TAG333","MainActivity.onAnimationEnd");
        if(!show){
            mCoverView.setVisibility(View.GONE);
            mSelectListItemView = null;
            mNeedPadding = 0;
        }
        mAnimator = null;
    }

    private void initAdapter() {
        List<MyItem> dataList = new ArrayList<>(10);
        for (int i = 0; i < 30; i++) {
            MyItem item = new MyItem();
            item.title = "Title " + i;
            item.name = "Name " + i;
            item.content = "Content " + i;
            dataList.add(item);
        }
        MyAdapter adapter = new MyAdapter(this, dataList);
        mListView.setAdapter(adapter);
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
            FrameLayout.LayoutParams lvParams = (FrameLayout.LayoutParams) this.listview.getLayoutParams();
            lvParams.topMargin = value.listMargin;
            this.listview.setLayoutParams(lvParams);

            ViewGroup.LayoutParams params =  this.itemContentView.getLayoutParams();
            params.height = value.itemHeight;
            this.itemContentView.setLayoutParams(params);

            Drawable drawable = coverView.getBackground();
            if(drawable instanceof ColorDrawable){
                drawable.setAlpha(value.coverAlpha);
            }

            ViewGroup.LayoutParams params2 =  this.coverContentView.getLayoutParams();
            params2.height = value.itemHeight;
            this.coverContentView.setTranslationY(value.translationY);
            this.coverContentView.setLayoutParams(params2);

        }
    }
}
