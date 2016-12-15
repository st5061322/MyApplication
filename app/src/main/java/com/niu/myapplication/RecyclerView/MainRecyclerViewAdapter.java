package com.niu.myapplication.RecyclerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.niu.myapplication.R;


import java.util.List;

/**
 * Created by A on 2016/12/13.
 */

public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private List<MainSubject> subList;

    @Override
    public MainRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_subject_main, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MainRecyclerViewAdapter.ViewHolder holder, int position) {
        MainSubject subject = subList.get(position);
        holder.title.setText(subject.getName());

        Glide.with(mContext).load(subject.getThumbnail()).into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return subList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView thumbnail;
        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
        }
    }
    public MainRecyclerViewAdapter(Context mContext, List<MainSubject> subList) {
        this.mContext = mContext;
        this.subList = subList;
    }
}
