package com.dulesz.listviewtest;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private ListCoverView mCoverView;

    private TextView mTitleView, mNameView, mContentView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        mListView = findViewById(R.id.lv);
        mCoverView = findViewById(R.id.cover);

        View expandView = getLayoutInflater().inflate(R.layout.list_expand_item,mCoverView,false);
        mCoverView.init(expandView,mListView);
        expandView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mTitleView = expandView.findViewById(R.id.title);
        mNameView = expandView.findViewById(R.id.name);
        mContentView = expandView.findViewById(R.id.content);

        mCoverView.setVisibility(View.GONE);
        Drawable drawable = mCoverView.getBackground();
        if(drawable instanceof ColorDrawable){
            drawable.setAlpha(0);
        }

        initAdapter();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mCoverView.isAnimRunning()){
                    return;
                }

                MyAdapter adapter = (MyAdapter) mListView.getAdapter();
                MyItem item = (MyItem) adapter.getItem(position);

                mTitleView.setText(item.title);
                mNameView.setText(item.name);
                mContentView.setText(item.content);

                mCoverView.setSelectListItemView(view);

                mCoverView.start();

//                ViewGroup.LayoutParams params = mSelectListItemView.getLayoutParams();
//                params.height = getResources().getDimensionPixelSize(R.dimen.list_item_expand_height);
//                mSelectListItemView.setLayoutParams(params);
//
//                FrameLayout.LayoutParams params1 = (FrameLayout.LayoutParams) mListView.getLayoutParams();
//                params1.topMargin = - mNeedPadding;
//                mListView.setLayoutParams(params1);
            }
        });
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
}
