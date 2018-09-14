package com.example.thangpham.testfirebasenhap.chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.util.Log;
import com.example.thangpham.testfirebasenhap.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class ListUserActivity extends AppCompatActivity {
  RecyclerView rvList;
  FirebaseDatabase firebaseDatabase;
  DatabaseReference databaseReference;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list_chat);
    rvList = findViewById(R.id.rv_list_user);
    final UserModel userModel = (UserModel) getIntent().getSerializableExtra(MainActivity.USER_FROM);
    firebaseDatabase = FirebaseDatabase.getInstance();
    databaseReference = firebaseDatabase.getReference().child("user");
    databaseReference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {

        List<UserModel> list = new ArrayList<>();
        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
          UserModel userModel1 = dataSnapshot1.getValue(UserModel.class);
          list.add(userModel1);

        }

        ListChatAdapter listChatAdapter = new ListChatAdapter(ListUserActivity.this,list,userModel);
        rvList.setLayoutManager(new LinearLayoutManager(ListUserActivity.this));
        rvList.setAdapter(listChatAdapter);
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });
  }
}
