package com.example.thangpham.testfirebasenhap.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.example.thangpham.testfirebasenhap.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

  FirebaseDatabase firebaseDatabase;
  DatabaseReference databaseReference;
  RecyclerView rvListChat;
  EditText etText;
  Button btnSend;
  UserModel userFrom;
  UserModel userTo;
  ChatAdapter chatAdapter;
  List<ChatMessage> list;
  boolean isFistTime;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chat);
    setupUI();
    Intent intent = getIntent();
    userFrom = (UserModel) intent.getSerializableExtra(MainActivity.USER_FROM);
    userTo = (UserModel) intent.getSerializableExtra(MainActivity.USER_TO);
    loadData();
    addListener();
  }

  @Override
  protected void onResume() {
    super.onResume();
    isFistTime = true;
  }

  private void loadData() {
    databaseReference.child("123").addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        // lần đầu tiên
        if(isFistTime){
          for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
            ChatMessage chatMessage = dataSnapshot1.getValue(ChatMessage.class);
            list.add(chatMessage);
          }
          isFistTime=false;
          chatAdapter = new ChatAdapter(ChatActivity.this,list);
          rvListChat.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
          rvListChat.setAdapter(chatAdapter);

        }else{   // chỉ append
          ChatMessage chatMessage=null;
          for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
            chatMessage = dataSnapshot1.getValue(ChatMessage.class);
          }
          list.add(chatMessage);
          chatAdapter.notifyDataSetChanged();
        }


      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });
  }

  private void addListener() {
    btnSend.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        String content = etText.getText().toString();
        if(!content.equals("")){
          sendContent(content);
          etText.setText("");
        }else{
          Toast.makeText(ChatActivity.this, "Not Empty~~~~~", Toast.LENGTH_SHORT).show();
        }

      }
    });
  }

  private void sendContent(String content) {
    long time = System.currentTimeMillis();
    final ChatMessage chatMessage = new ChatMessage(content,time);
    databaseReference.child("123").child(String.valueOf(time)).setValue(chatMessage);
    chatAdapter.notifyDataSetChanged();
  }

  private void setupUI() {
    list = new ArrayList<>();
    firebaseDatabase = FirebaseDatabase.getInstance();
    databaseReference = firebaseDatabase.getReference().child("chat");
    rvListChat = findViewById(R.id.rv_list_chat);
    btnSend = findViewById(R.id.btnSend);
    etText = findViewById(R.id.et_text);
  }
}
