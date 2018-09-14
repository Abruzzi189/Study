package com.example.thangpq.demolistimage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.List;

public class ListImageAdapter extends RecyclerView.Adapter<ListImageAdapter.ListMediaViewHolder>{
    Context context;
    List<MediaFile> mediaFileList;
    private static final String TAG = "ListImageAdapter";
    public ListImageAdapter(Context context, List<MediaFile> mediaFileList) {
        this.context = context;
        this.mediaFileList = mediaFileList;
    }

    @NonNull
    @Override
    public ListMediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.custom_image,parent,false);
        return new ListMediaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListMediaViewHolder holder, int position) {
        holder.tvTitle.setText(mediaFileList.get(position).getName());
       // holder.tvTime.setText(String.valueOf(mediaFileList.get(position).getTime()));
        Glide.with(context).load(mediaFileList.get(position).getPath()).into(holder.ivImage);
    }

    @Override
    public int getItemCount() {
        return mediaFileList.size();
        
    }


    public class ListMediaViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView ivImage;
        public TextView tvTime;
        public TextView tvTitle;

        public ListMediaViewHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_image);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvTime = itemView.findViewById(R.id.tv_time);
        }

    }
}
