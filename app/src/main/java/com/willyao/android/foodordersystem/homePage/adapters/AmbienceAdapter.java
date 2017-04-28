/*
 * Created by Will Yao on 04/13/17
 * Copyright (c) 2017.  All rights reserved
 * Last modified 4/25/17 2:32 PM
 */

package com.willyao.android.foodordersystem.homePage.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.willyao.android.foodordersystem.R;
import com.willyao.android.foodordersystem.model.Ambience;

import java.util.ArrayList;

import static com.willyao.android.foodordersystem.R.drawable.food;

/**
 * Created by mitya on 4/25/2017.
 */

public class AmbienceAdapter extends RecyclerView.Adapter<AbViewHolder>{
    private Context mContext;
    private ArrayList<Ambience> ambiences;

    public AmbienceAdapter(Context context, ArrayList<Ambience> ambiences) {
        mContext = context;
        this.ambiences = ambiences;
    }

    @Override
    public AbViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cardview_ambience, parent, false);
        AbViewHolder aBHolder = new AbViewHolder(view);
        return aBHolder;
    }

    @Override
    public void onBindViewHolder(AbViewHolder holder, int position) {
        Ambience ambience = ambiences.get(position);
        holder.abId.setText(ambience.getmId());
        holder.abTitle.setText(ambience.getmTitle());
        //load img here
        Picasso.with(mContext)
                .load(ambience.getmPicUrl())
                .placeholder(R.drawable.food_img_placeholder)
                .into(holder.abImg);
        holder.itemView.setTag(ambience.getmId());
    }

    @Override
    public int getItemCount() {
        return ambiences.size();
    }

    public void notifyData(ArrayList<Ambience> ambiences) {
        this.ambiences = ambiences;
        notifyDataSetChanged();
    }
}
class AbViewHolder extends RecyclerView.ViewHolder{
    ImageView abImg;
    TextView abId, abTitle;

    public AbViewHolder(View itemView) {
        super(itemView);
        abImg = (ImageView) itemView.findViewById(R.id.ambience_img);
        abId = (TextView) itemView.findViewById(R.id.pic_id);
        abTitle = (TextView) itemView.findViewById(R.id.pic_title);
    }
}
