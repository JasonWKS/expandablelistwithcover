package com.dulesz.listviewtest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by jason.shen on 2018/3/23.
 */

public class ExpandableListCoverView extends ListCoverView {
    private TextView mTitleView, mNameView, mContentView;

    private View mCoverContentView;

    public ExpandableListCoverView(@NonNull Context context) {
        super(context);

        init();
    }

    public ExpandableListCoverView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public ExpandableListCoverView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    public ExpandableListCoverView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();
    }

    private void init(){
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.list_expand_item,this,true);
        mCoverContentView = findViewById(R.id.cover_content);
        mTitleView = mCoverContentView.findViewById(R.id.title);
        mNameView = mCoverContentView.findViewById(R.id.name);
        mContentView = mCoverContentView.findViewById(R.id.content);
        mCoverContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void setViews(View listView, View contentContainerView){
        setViews(mCoverContentView,listView,contentContainerView);
    }

    public void bindView(MyItem item){
        mTitleView.setText(item.title);
        mNameView.setText(item.name);
        mContentView.setText(item.content);
    }
}
