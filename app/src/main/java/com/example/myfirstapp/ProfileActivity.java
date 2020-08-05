package com.example.myfirstapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.*;
import static android.widget.Toast.makeText;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private List<User> userList;

    private RecyclerView recyclerView;
//    private ProgressBar progressBar = findViewById(R.id.progressBar);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        FirebaseMessaging.getInstance().subscribeToTopic("general");
        //progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();

        loadUser();

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            String token = task.getResult().getToken();
                            Log.d("MyToken", token);
                            saveToken(token);
                        }
                        else{
                            String msg = "not successful";
                            Log.d("MyToken", msg);
                        }
                    }
                });
    }

    private void loadUser(){
//        progressBar.setVisibility(VISIBLE);
        userList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        DatabaseReference dbUsers = FirebaseDatabase.getInstance().getReference("users");
        dbUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                progressBar.setVisibility(GONE);
                if(snapshot.exists()){
                    for(DataSnapshot dsUsers: snapshot.getChildren()){
                        User user = dsUsers.getValue(User.class);
                        userList.add(user);

                    }

                    UserAdapter adapter = new UserAdapter(ProfileActivity.this, userList);
                    recyclerView.setAdapter(adapter);
                }
                else{
                    Toast.makeText(ProfileActivity.this, "No user found!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private void saveToken(String token) {
        String email = mAuth.getCurrentUser().getEmail();

        User user = new User(email, token);

        DatabaseReference dbUser = FirebaseDatabase.getInstance().getReference("users");
        dbUser.child(mAuth.getCurrentUser().getUid())
                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                    makeText(ProfileActivity.this, "token saved", Toast.LENGTH_LONG).show();
            }
        });
    }
}