package com.example.thangpham.testfirebasenhap.chat;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.thangpham.testfirebasenhap.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    public static String USER_FROM ="user_from";
    public static String USER_TO ="user_to";
    FirebaseAuth firebaseAuth;
    EditText etEmail,etPass;
    Button btRegister,btLogin;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        setupUI();

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseAuth.createUserWithEmailAndPassword(etEmail.getText().toString(),etPass.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, "Register successfully!", Toast.LENGTH_SHORT).show();
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            UserProfileChangeRequest.Builder userProfileChangeRequest = new UserProfileChangeRequest.Builder();
                            userProfileChangeRequest.setDisplayName("Thang Pham");
                            userProfileChangeRequest.setPhotoUri(Uri.parse("https://dantricdn.com/thumb_w/640/2018/6/8/sen-sexy-5-15284139645842019540842.jpg"));
                            firebaseAuth.getCurrentUser().updateProfile(userProfileChangeRequest.build());

                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signInWithEmailAndPassword(
                        etEmail.getText().toString(),
                        etPass.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful())
                                {
                                    FirebaseUser firebaseUser = task.getResult().getUser();

                                    Toast.makeText(MainActivity.this, "Login successfully!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MainActivity.this,ListUserActivity.class);
                                    UserModel userModel = new UserModel(firebaseUser.getUid(),firebaseUser.getDisplayName(),firebaseUser.getPhotoUrl().toString());
                                    databaseReference.child(firebaseUser.getUid()).setValue(userModel);
                                    intent.putExtra(USER_FROM,userModel);
                                    startActivity(intent);
                                }
                                else
                                {
                                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        });
            }
        });
    }

    private void setupUI() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("user");
        etEmail = findViewById(R.id.et_email);
        etPass = findViewById(R.id.et_password);
        btLogin = findViewById(R.id.bt_sign_in);
        btRegister = findViewById(R.id.bt_sign_up);

    }
}
