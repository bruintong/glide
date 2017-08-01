package com.bruintong.glide.ui;


import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.bruintong.glide.R;
import com.bumptech.glide.load.engine.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;

    private HomeAdapter mAdapter;
    private List<HashMap<String, String>> list;
    private String[] clazzNames;
    private String[] titles;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        mContext = this;

        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        Resources res = getResources();
        clazzNames = res.getStringArray(R.array.clazz_list);
        titles = res.getStringArray(R.array.title_list);

        mAdapter = new HomeAdapter(this, titles);


        mAdapter.setOnItemClickListener(new HomeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                try {
                    ComponentName componentName = new ComponentName(mContext, clazzNames[position]);
                    Intent intent = new Intent();
                    intent.setComponent(componentName);
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Log.d(TAG, position + " : " + view.toString() + " long click.");
            }
        });
        mRecyclerView.setAdapter(mAdapter);

    }


}
