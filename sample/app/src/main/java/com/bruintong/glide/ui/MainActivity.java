package com.bruintong.glide.ui;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bruintong.glide.R;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;

    private String[] names = {"A", "B", "C"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new MyAdapter(this, names));
    }



    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        private Context context;
        private String[] data;

        public MyAdapter(Context context, String[] data) {
            this.context = context;
            this.data = data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.mText.setText(data[position]);
            holder.mText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return data.length;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView mText;

            public MyViewHolder(View itemView) {
                super(itemView);
                mText = (TextView) itemView.findViewById(R.id.name);
            }
        }
    }


}
