package com.example.client.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.client.R;
import com.example.client.activity.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class AdminHomeActivity extends AppCompatActivity {

    private LinearLayout btnUserManager,
            btnMatchManager,
            btnTicketManager,
            btnApproveTicket,
            btnRanking;

    // ✅ NEW: nút gửi thông báo
    private LinearLayout btnSendNotification;

    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        btnUserManager = findViewById(R.id.btnUserManager);
        btnMatchManager = findViewById(R.id.btnMatchManager);
        btnTicketManager = findViewById(R.id.btnTicketManager);
        btnApproveTicket = findViewById(R.id.btnApproveTicket);
        btnRanking = findViewById(R.id.btnRanking);

        // ✅ NEW
        btnSendNotification = findViewById(R.id.btnSendNotification);

        btnLogout = findViewById(R.id.btnLogout);

        btnUserManager.setOnClickListener(v ->
                startActivity(new Intent(this, UserManagerActivity.class)));

        btnMatchManager.setOnClickListener(v ->
                startActivity(new Intent(this, AdminMatchListActivity.class)));

        btnTicketManager.setOnClickListener(v ->
                startActivity(new Intent(this, AdminMatchListActivity.class)));

        btnApproveTicket.setOnClickListener(v ->
                startActivity(new Intent(this, AdminTicketRequestActivity.class)));

        btnRanking.setOnClickListener(v ->
                startActivity(new Intent(this, RankingManagerActivity.class)));

        // ✅ NEW: mở trang gửi thông báo
        btnSendNotification.setOnClickListener(v ->
                startActivity(new Intent(this, AdminNotificationActivity.class)));

        // ✅ Đăng xuất: về Login + clear back stack
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();

            Intent i = new Intent(AdminHomeActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });
    }
}
