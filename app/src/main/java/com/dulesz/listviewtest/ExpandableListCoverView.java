package com.dulesz.listviewtest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
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
        View expandView = inflater.inflate(R.layout.list_expand_item,this,false);
        mTitleView = expandView.findViewById(R.id.title);
        mNameView = expandView.findViewById(R.id.name);
        mContentView = expandView.findViewById(R.id.content);
        expandView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mCoverContentView = expandView;
    }

    public void init(ListView listView){
        init(mCoverContentView,listView);
    }

    public void bindView(MyItem item){
        mTitleView.setText(item.title);
        mNameView.setText(item.name);
        mContentView.setText(item.content);
    }
}
