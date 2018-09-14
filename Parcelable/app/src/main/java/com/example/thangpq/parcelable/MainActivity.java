package com.example.thangpq.parcelable;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Student student = new Student("Thang",18);
        Student student1 = new Student("Thang1",18);
        Student student2 = new Student("Thang2",18);
        List<Student> list = new ArrayList<>();
        list.add(student);
        list.add(student1);
        list.add(student2);

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("list", (ArrayList<? extends Parcelable>) list);

        Intent intent = new Intent(this,Main2Activity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
