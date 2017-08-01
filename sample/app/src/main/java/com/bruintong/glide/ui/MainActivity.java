package com.bruintong.glide.ui;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.bruintong.glide.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;

    private String[] names = {"A", "B", "C"};

    private HomeAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new HomeAdapter(this, names);
        mAdapter.setOnItemClickListener(new HomeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d(TAG, position + " : " + view.toString() + " click.");
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Log.d(TAG, position + " : " + view.toString() + " long click.");
            }
        });
        mRecyclerView.setAdapter(mAdapter);

    }


}
