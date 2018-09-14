package com.example.thangpham.testfirebasenhap;

import android.graphics.drawable.RippleDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class DatabaseActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "DatabaseActivity";
    EditText etTitle,etAuthor;
    Button btAdd,btRead,btUpdate,btDelete;
    EditText et_id;
    TextView tvInfo;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        setupUI();
        FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
        firebaseMessaging.subscribeToTopic("qk");
    }

    private void setupUI() {
        et_id = findViewById(R.id.et_id);
        etAuthor = findViewById(R.id.et_author);
        etTitle = findViewById(R.id.et_title);
        btAdd = findViewById(R.id.bt_add);
        btRead = findViewById(R.id.bt_read);
        btUpdate = findViewById(R.id.bt_update);
        btDelete = findViewById(R.id.bt_delete);
        tvInfo= findViewById(R.id.tv_info);
        btAdd.setOnClickListener(this);
        btRead.setOnClickListener(this);
        btUpdate.setOnClickListener(this);
        btDelete.setOnClickListener(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("book");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.bt_add:
            {
                databaseReference.child(et_id.getText().toString()).setValue(new BookModel(et_id.getText().toString(),etTitle.getText().toString(),etAuthor.getText().toString()))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(DatabaseActivity.this, "Successfully", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(DatabaseActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
//                databaseReference.orderByChild("author").equalTo(etAuthor.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if(dataSnapshot.exists())
//                        {
//                            Log.d(TAG, "onDataChange: OK");
//                        }
//                        else
//                            Log.d(TAG, "onDataChange: Not ok");
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        Log.d(TAG, "onCancelled: ");
//                    }
//                });
                break;
            }
            case R.id.bt_read:
            {
                // thay đổi liên tục
              //  databaseReference.addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                        Log.d(TAG, "onChildAdded: ");
//                    }
//
//                    @Override
//                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                        Log.d(TAG, "onChildChanged: ");
//                    }
//
//                    @Override
//                    public void onChildRemoved(DataSnapshot dataSnapshot) {
//                        Log.d(TAG, "onChildRemoved: ");
//                    }
//
//                    @Override
//                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//                        Log.d(TAG, "onChildMoved: ");
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        Log.d(TAG, "onCancelled: ");
//                    }
 //               });
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        List<BookModel> bookModels = new ArrayList<>();
                        for(DataSnapshot bookSnapshot : dataSnapshot.getChildren())
                        {
                            BookModel bookModel = bookSnapshot.getValue(BookModel.class); // ep kieu moi, firese k cho ep kieu kia
                            bookModels.add(bookModel);

                        }

                        tvInfo.setText(bookModels.toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                break;
            }
            case R.id.bt_update:
            {
                // chỉ động đến 1 lần
//                databaseReference.child(et_id.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        BookModel bookModel = dataSnapshot.getValue(BookModel.class);
//                        bookModel.author=etAuthor.getText().toString();
//                        bookModel.title = etTitle.getText().toString();
//                        databaseReference.child(et_id.getText().toString()).setValue(bookModel);
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
                // timf theo tac gia, ko phai theo id

                databaseReference.orderByChild("author").equalTo(etAuthor.getText().toString())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot bookSnapshot : dataSnapshot.getChildren())
                                {
                                    BookModel bookModel = bookSnapshot.getValue(BookModel.class); // ep kieu moi, firese k cho ep kieu kia
                                    bookModel.title = etTitle.getText().toString();
                                    // databaseReference.child(bookModel.getId()).setValue(bookModel);
                                    databaseReference.child(bookSnapshot.getKey()).setValue(bookModel);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                break;
            }
            case R.id.bt_delete:
            {
                databaseReference.orderByChild("author").equalTo(etAuthor.getText().toString())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot bookSnapshot: dataSnapshot.getChildren())
                        {
                         //   BookModel bookModel = bookSnapshot.getValue(BookModel.class);
                            databaseReference.child(bookSnapshot.getKey()).removeValue();

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                break;

            }
        }
    }
}
