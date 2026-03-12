package com.example.client.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.client.R;
import com.example.client.adapter.NotificationAdapter;
import com.example.client.models.AppNotification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView rv;
    private NotificationAdapter adapter;
    private final List<AppNotification> list = new ArrayList<>();

    private DatabaseReference broadcastRef;
    private DatabaseReference personalRef;

    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        rv = findViewById(R.id.rvNotification);
        ImageView btnBack = findViewById(R.id.btnBack);

        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(this, list);
        rv.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        uid = FirebaseAuth.getInstance().getUid();
        broadcastRef = FirebaseDatabase.getInstance().getReference("broadcast_notifications");

        // personal có thể null nếu chưa đăng nhập
        if (uid != null) {
            personalRef = FirebaseDatabase.getInstance().getReference("notifications").child(uid);
        }

        loadAllNotifications();
    }

    private void loadAllNotifications() {
        // clear list rồi load broadcast trước
        list.clear();
        adapter.notifyDataSetChanged();

        // 1) Load broadcast (thông báo chung)
        broadcastRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Xoá các item broadcast cũ khỏi list (để tránh trùng)
                // Cách đơn giản: reload lại toàn bộ 2 nguồn
                reloadBothSources();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(NotificationActivity.this, "Lỗi broadcast: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // 2) Load personal (nếu có)
        if (personalRef != null) {
            personalRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    reloadBothSources();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(NotificationActivity.this, "Lỗi cá nhân: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void reloadBothSources() {
        List<AppNotification> temp = new ArrayList<>();

        // broadcast
        broadcastRef.get().addOnSuccessListener(bSnap -> {
            for (DataSnapshot s : bSnap.getChildren()) {
                AppNotification n = s.getValue(AppNotification.class);
                if (n != null) {
                    n.isRead = false; // broadcast mặc định
                    temp.add(n);
                }
            }

            // personal
            if (personalRef == null) {
                applyList(temp);
                return;
            }

            personalRef.get().addOnSuccessListener(pSnap -> {
                for (DataSnapshot s : pSnap.getChildren()) {
                    AppNotification n = s.getValue(AppNotification.class);
                    if (n != null) temp.add(n);
                }
                applyList(temp);
            }).addOnFailureListener(e -> applyList(temp));

        }).addOnFailureListener(e -> {
            // nếu broadcast fail thì vẫn thử personal
            if (personalRef == null) {
                applyList(temp);
                return;
            }
            personalRef.get().addOnSuccessListener(pSnap -> {
                for (DataSnapshot s : pSnap.getChildren()) {
                    AppNotification n = s.getValue(AppNotification.class);
                    if (n != null) temp.add(n);
                }
                applyList(temp);
            }).addOnFailureListener(ex -> applyList(temp));
        });
    }

    private void applyList(List<AppNotification> temp) {
        // sort theo createdAt giảm dần
        Collections.sort(temp, (a, b) -> Long.compare(b.createdAt, a.createdAt));

        list.clear();
        list.addAll(temp);
        adapter.notifyDataSetChanged();
    }
}
