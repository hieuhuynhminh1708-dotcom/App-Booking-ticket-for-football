package com.example.client.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.client.R;
import com.example.client.adapter.MyTicketsAdapter;
import com.example.client.models.TicketRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyTicketsActivity extends AppCompatActivity {

    private RecyclerView rvTickets;
    private MyTicketsAdapter adapter;
    private final List<TicketRequest> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tickets);

        // ===== BACK =====
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // ===== RECYCLERVIEW =====
        rvTickets = findViewById(R.id.rvTickets);
        rvTickets.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MyTicketsAdapter(this, list);
        rvTickets.setAdapter(adapter);

        loadMyTickets();
    }

    // ================= LOAD DATA =================
    private void loadMyTickets() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("ticket_requests");

        ref.orderByChild("userId")
                .equalTo(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();

                        for (DataSnapshot d : snapshot.getChildren()) {
                            TicketRequest t = d.getValue(TicketRequest.class);
                            if (t == null) continue;

                            t.id = d.getKey();

                            // ✅ CÁCH 1: Vé đã hủy sẽ KHÔNG hiển thị
                            String st = t.status == null ? "" : t.status;
                            if ("cancelled".equals(st)) continue;

                            list.add(t);
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // không cần xử lý
                    }
                });
    }
}
