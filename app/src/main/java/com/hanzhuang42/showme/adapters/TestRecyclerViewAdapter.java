package com.hanzhuang42.showme.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hanzhuang42.showme.R;
import com.hanzhuang42.showme.activitys.ShowActivity;
import com.hanzhuang42.showme.db.DetectObject;

import java.io.File;
import java.util.List;

public class TestRecyclerViewAdapter extends RecyclerView.Adapter<TestRecyclerViewAdapter.ViewHolder> {

    List<DetectObject> detectObjectList;

    static final int TYPE_HEADER = 0;
    static final int TYPE_CELL = 1;
    private Context mContext = null;

    static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView card_image;
        TextView name_text;
        TextView date_text;
        CardView card_view;
        CardView small_card;

        public ViewHolder(View itemView) {
            super(itemView);
            card_image = itemView.findViewById(R.id.card_image);
            name_text = itemView.findViewById(R.id.name_text);
            date_text = itemView.findViewById(R.id.date_text);
            card_view = itemView.findViewById(R.id.card_view);
            small_card = itemView.findViewById(R.id.small_card);
        }
    }

    public TestRecyclerViewAdapter(List<DetectObject> detectObjectList) {
        this.detectObjectList = detectObjectList;
    }

    @Override
    public int getItemViewType(int position) {
        int size = detectObjectList.size();
        if(position == size) {
            return TYPE_CELL;
        }
        else{
            return TYPE_HEADER;
        }
    }

    @Override
    public int getItemCount() {
        return detectObjectList.size() + 1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        mContext = parent.getContext();
        switch (viewType) {
            case TYPE_HEADER:
                view = LayoutInflater.from(mContext)
                        .inflate(R.layout.list_item_card_big, parent, false);
                final ViewHolder holder = new ViewHolder(view);

                holder.card_image.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        int position = holder.getAdapterPosition();
                        DetectObject detectObject = detectObjectList.get(position);
                        Intent intent = new Intent(mContext, ShowActivity.class);
                        intent.putExtra("detectObject_id",detectObject.getId());
                        mContext.startActivity(intent);
                    }
                });
//                  small_card无法初始化
//                holder.small_card.setOnClickListener(new View.OnClickListener(){
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(mContext,"你碰我的底线了！",Toast.LENGTH_SHORT).show();
//                    }
//                });

                return holder;
            case TYPE_CELL:
                view = LayoutInflater.from(mContext)
                        .inflate(R.layout.list_item_card_small, parent, false);
                return new ViewHolder(view) {};
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int size = detectObjectList.size();
        if(position != size) {
            DetectObject detectObject = detectObjectList.get(position);
            String imgPath = detectObject.getImgPath();
            if (imgPath != null) {
                File file = new File(imgPath);
                if (file.exists()) {
                    Uri uri = Uri.fromFile(file);
                    Glide.with(mContext).load(uri).into(holder.card_image);
//                    holder.card_image.setImageURI(uri);
                    holder.name_text.setText(detectObject.getName());
                    holder.date_text.setText(detectObject.getDate());
                }
            }
        }
    }

}