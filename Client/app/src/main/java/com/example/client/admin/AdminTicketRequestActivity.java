package com.example.client.admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.client.R;
import com.example.client.adapter.AdminTicketRequestAdapter;
import com.example.client.models.TicketRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminTicketRequestActivity extends AppCompatActivity {

    private RecyclerView rvTickets;
    private AdminTicketRequestAdapter adapter;
    private final List<TicketRequest> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_ticket_request);

        // ===== NÚT QUAY LẠI =====
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // ===== RECYCLERVIEW =====
        rvTickets = findViewById(R.id.rvTickets);
        rvTickets.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AdminTicketRequestAdapter(this, list);
        rvTickets.setAdapter(adapter);

        loadTicketRequests();
    }

    // ================= LOAD DATA =================
    private void loadTicketRequests() {
        FirebaseDatabase.getInstance()
                .getReference("ticket_requests")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();

                        for (DataSnapshot d : snapshot.getChildren()) {
                            TicketRequest t = d.getValue(TicketRequest.class);
                            if (t == null) continue;

                            // 🔥 CHỈ DUYỆT VÉ CẦN XỬ LÝ
                            if ("pending".equals(t.status)
                                    || "cancel_requested".equals(t.status)) {

                                t.id = d.getKey();
                                list.add(t);
                            }
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
