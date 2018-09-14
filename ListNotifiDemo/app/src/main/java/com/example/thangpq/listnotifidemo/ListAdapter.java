package com.example.thangpq.listnotifidemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListHolder> {
    private static final String TAG = "ListAdapter";
    public List<String> list;
    Context context;

    public ListAdapter(List<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        View view = LayoutInflater.from(context).inflate(R.layout.item_list,parent,false);
        return new ListHolder(view);
}

    @Override
    public void onBindViewHolder(@NonNull ListHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: ");
        holder.setText(list.get(position));
    }

    @Override
    public int getItemCount() {

        Log.d(TAG, "getItemCount: "+list.size());
        return list.size();
    }

    class ListHolder extends RecyclerView.ViewHolder
    {
        TextView tvName;
        public ListHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);

        }
        public void setText(String a)
        {
            tvName.setText(a);
        }
    }
}
