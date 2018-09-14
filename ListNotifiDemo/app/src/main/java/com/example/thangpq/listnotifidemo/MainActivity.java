package com.example.thangpq.listnotifidemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    Button btnAdd;
    List<String> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.rv_list);
        btnAdd = findViewById(R.id.btnAdd);
        list = new ArrayList<>();
        list.add("Thang");
        final int[] index = {0};
        final ListAdapter listAdapter = new ListAdapter(list,this);
        recyclerView.setAdapter(listAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.add(1,"new Student"+ index[0]);
                index[0]++;
                listAdapter.notifyDataSetChanged();
            }
        });
    }
}
