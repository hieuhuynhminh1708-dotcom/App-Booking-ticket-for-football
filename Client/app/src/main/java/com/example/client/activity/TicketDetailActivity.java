package com.example.client.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.client.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TicketDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvInfo, tvTicketType, tvStatus, tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);

        tvTitle = findViewById(R.id.tvTitle);
        tvInfo = findViewById(R.id.tvInfo);
        tvTicketType = findViewById(R.id.tvTicketType);
        tvStatus = findViewById(R.id.tvStatus);
        tvTotal = findViewById(R.id.tvTotal);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        String requestId = getIntent().getStringExtra("requestId");
        if (requestId == null) {
            finish();
            return;
        }

        FirebaseDatabase.getInstance()
                .getReference("ticket_requests")
                .child(requestId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            finish();
                            return;
                        }

                        String matchName = safe(snapshot.child("matchName").getValue(String.class));
                        String date = safe(snapshot.child("date").getValue(String.class));
                        String time = safe(snapshot.child("time").getValue(String.class));
                        String stadium = safe(snapshot.child("stadium").getValue(String.class));
                        String status = safe(snapshot.child("status").getValue(String.class));
                        Integer total = snapshot.child("total").getValue(Integer.class);

                        // 👉 Lấy TÊN LOẠI VÉ (key của tickets)
                        String ticketType = getTicketType(snapshot.child("tickets"));

                        tvTitle.setText(matchName);
                        tvInfo.setText(date + " - " + time + "\nSân: " + stadium);
                        tvTicketType.setText("Loại vé: " + ticketType);
                        tvStatus.setText("Trạng thái: " + status);
                        tvTotal.setText("Tổng tiền: " + formatVnd(total == null ? 0 : total));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    // Chỉ lấy tên loại vé (ví dụ: Khán đài D)
    private String getTicketType(DataSnapshot ticketsSnap) {
        if (ticketsSnap == null || !ticketsSnap.exists()) {
            return "(không có dữ liệu)";
        }

        for (DataSnapshot child : ticketsSnap.getChildren()) {
            return safe(child.getKey()); // chỉ lấy key đầu tiên
        }

        return "(không có dữ liệu)";
    }

    private String safe(String s) { return s == null ? "" : s; }
    private String formatVnd(int v) { return String.format("%,d", v).replace(",", ".") + " VND"; }
}
