package com.example.client.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.client.R;
import com.example.client.adapter.AdminMatchAdapter;
import com.example.client.models.Match;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class AdminMatchListActivity extends AppCompatActivity {

    private RecyclerView rvMatches;
    private AdminMatchAdapter adapter;
    private final List<Match> list = new ArrayList<>();
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_match_list);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        rvMatches = findViewById(R.id.rvMatches);
        rvMatches.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminMatchAdapter(this, list);
        rvMatches.setAdapter(adapter);

        findViewById(R.id.btnSave).setOnClickListener(v ->
                startActivity(new Intent(this, AdminAddEditMatchActivity.class)));

        ref = FirebaseDatabase.getInstance().getReference("matches");
        loadMatches();
    }

    private void loadMatches() {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot d : snapshot.getChildren()) {
                    Match m = d.getValue(Match.class);
                    if (m != null) {
                        m.id = d.getKey();
                        list.add(m);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}
