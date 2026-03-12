package com.example.client.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.client.R;
import com.example.client.models.Match;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ClientScheduleActivity extends AppCompatActivity {

    private RecyclerView rvMatches;
    private com.example.client.client.ClientMatchAdapter adapter;
    private final List<Match> list = new ArrayList<>();
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_schedule);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        rvMatches = findViewById(R.id.rvMatches);
        rvMatches.setLayoutManager(new LinearLayoutManager(this));

        adapter = new com.example.client.client.ClientMatchAdapter(list, match -> {
            Intent i = new Intent(this, com.example.client.activity.BuyTicketActivity.class);
            i.putExtra("matchId", match.id);
            startActivity(i);
        });
        rvMatches.setAdapter(adapter);

        ref = FirebaseDatabase.getInstance().getReference("matches");
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
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
