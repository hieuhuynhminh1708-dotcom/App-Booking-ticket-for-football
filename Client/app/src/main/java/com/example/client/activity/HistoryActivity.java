package com.example.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.client.R;
import com.example.client.adapter.HistoryAdapter;
import com.example.client.models.TicketRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        rvHistory = findViewById(R.id.rvHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));

        adapter = new HistoryAdapter(item -> {
            // ✅ Dùng item.id (push key)
            Toast.makeText(this, "Chi tiết: " + item.id, Toast.LENGTH_SHORT).show();

            // Nếu bạn có màn chi tiết thì mở như sau:
            // Intent i = new Intent(HistoryActivity.this, TicketDetailActivity.class);
            // i.putExtra("requestId", item.id);
            // startActivity(i);
        });

        rvHistory.setAdapter(adapter);

        loadHistory();
    }

    private void loadHistory() {
        FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();
        if (current == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            adapter.setData(new ArrayList<>());
            return;
        }

        String uid = current.getUid();

        Query q = FirebaseDatabase.getInstance()
                .getReference("ticket_requests")
                .orderByChild("userId")
                .equalTo(uid);

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<TicketRequest> list = new ArrayList<>();

                for (DataSnapshot child : snapshot.getChildren()) {
                    TicketRequest item = child.getValue(TicketRequest.class);
                    if (item == null) continue;

                    // ✅ Gán ID từ key của node
                    item.id = child.getKey();
                    list.add(item);
                }

                // ✅ Sort theo createdAt nếu model có field createdAt
                // Nếu bạn CHƯA thêm createdAt vào model -> comment đoạn sort này lại
                try {
                    Collections.sort(list, (a, b) -> Long.compare(b.createdAt, a.createdAt));
                } catch (Exception ignored) {
                    // model chưa có createdAt thì bỏ sort
                }

                adapter.setData(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HistoryActivity.this,
                        "Lỗi tải lịch sử: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
