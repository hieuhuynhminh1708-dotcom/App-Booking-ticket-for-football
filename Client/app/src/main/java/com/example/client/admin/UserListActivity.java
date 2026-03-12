package com.example.client.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.client.R;
import com.example.client.adapter.UserAdapter;
import com.example.client.models.User;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class UserListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private ArrayList<User> userList = new ArrayList<>();
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new UserAdapter(this, userList, user -> {
            Intent i = new Intent(UserListActivity.this, UserDetailActivity.class);
            i.putExtra("uid", user.getUid()); // BÂY GIỜ CÓ UID
            startActivity(i);
        });

        recyclerView.setAdapter(adapter);

        userRef = FirebaseDatabase.getInstance().getReference("Users");
        loadUsers();
    }

    private void loadUsers() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    if (user != null) {
                        user.setUid(ds.getKey()); // ✨ GẮN UID CHO USER
                        userList.add(user);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
