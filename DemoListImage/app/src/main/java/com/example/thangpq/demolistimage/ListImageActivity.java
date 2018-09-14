package com.example.thangpq.demolistimage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ListImageActivity extends AppCompatActivity {

    ListImageAdapter listImageAdapter;
    RecyclerView recyclerView;
    List<MediaFile> listFiles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_image);
        recyclerView = findViewById(R.id.rv_listImage);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        listFiles =  bundle.getParcelableArrayList("list");
        listImageAdapter = new ListImageAdapter(this,listFiles);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new SpacesItemDecoration(2));
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        });
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        recyclerView.setAdapter(listImageAdapter);
    }
}
