package com.bruintong.glide.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bruintong.glide.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by tangweixiong on 2017/8/1.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder>  {

    private Context context;
    private String[] titles;

    private OnItemClickListener mOnItemClickListener;

    public HomeAdapter(Context context, String[] titles) {
        this.context = context;
        this.titles = titles;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.mTextName.setText(titles[position]);
        if(mOnItemClickListener != null) {
            holder.mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            });

            holder.mItemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.onItemLongClick(holder.itemView, position);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private View mItemView;
        private TextView mTextName;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.mItemView = itemView;
            this.mTextName = (TextView) itemView.findViewById(R.id.name);
        }
    }
}
