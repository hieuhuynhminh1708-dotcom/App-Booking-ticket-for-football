package com.example.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.client.R;
import com.example.client.adapter.HighlightAdapter;
import com.example.client.models.Highlight;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ClientHomeActivity extends AppCompatActivity {

    // ================= UI =================
    private TextView tvName, tvPhone;
    private ImageView imgAvatarHome;
    private RecyclerView rvHighlight;
    private ShimmerFrameLayout shimmerLayout;

    // ✅ NEW: nút chuông thông báo
    private View btnNotification;

    // ================= MENU =================
    private View btnProfile, btnMatches, btnStandings, btnMyTickets, btnHistory, btnLogout;

    // ================= DATA =================
    private HighlightAdapter adapter;
    private final List<Highlight> highlightList = new ArrayList<>();
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_home);

        initView();
        setupRecyclerView();
        initFirebase();
        loadUserInfoFromFirebase();
        loadHighlightStatic();
        setupMenuActions();
        setupNotificationAction(); // ✅ NEW
    }

    private void initView() {
        tvName = findViewById(R.id.tvName);
        tvPhone = findViewById(R.id.tvPhone);
        imgAvatarHome = findViewById(R.id.imgAvatarHome);

        rvHighlight = findViewById(R.id.rvHighlight);
        shimmerLayout = findViewById(R.id.shimmerLayout);

        btnProfile = findViewById(R.id.btnProfile);
        btnMatches = findViewById(R.id.btnMatches);
        btnStandings = findViewById(R.id.btnStandings);
        btnMyTickets = findViewById(R.id.btnMyTickets);
        btnHistory = findViewById(R.id.btnHistory);
        btnLogout = findViewById(R.id.btnLogout);

        // ✅ NEW: chuông thông báo (id đúng theo XML bạn gửi)
        btnNotification = findViewById(R.id.btnNotification);
    }

    private void setupRecyclerView() {
        adapter = new HighlightAdapter(this, highlightList);
        rvHighlight.setLayoutManager(new LinearLayoutManager(this));
        rvHighlight.setAdapter(adapter);
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void loadUserInfoFromFirebase() {
        if (mAuth.getCurrentUser() == null) {
            tvName.setText("Chưa đăng nhập");
            tvPhone.setText("");
            imgAvatarHome.setImageResource(R.drawable.img_placeholder);
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) return;

                String name = snapshot.child("fullName").getValue(String.class);
                String phone = snapshot.child("phone").getValue(String.class);
                String avatarName = snapshot.child("photoURL").getValue(String.class);

                tvName.setText(name == null ? "" : name);
                tvPhone.setText(phone == null ? "" : phone);

                int resId = getResources().getIdentifier(avatarName, "drawable", getPackageName());
                if (resId != 0) imgAvatarHome.setImageResource(resId);
                else imgAvatarHome.setImageResource(R.drawable.img_placeholder);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ClientHomeActivity.this,
                        "Không tải được thông tin người dùng",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ================= HIGHLIGHT =================
    private void loadHighlightStatic() {
        shimmerLayout.setVisibility(View.VISIBLE);
        shimmerLayout.startShimmer();
        rvHighlight.setVisibility(View.GONE);

        new Handler().postDelayed(() -> {
            highlightList.clear();

            String video1 = "android.resource://" + getPackageName() + "/" + R.raw.highlight1;
            String video2 = "android.resource://" + getPackageName() + "/" + R.raw.highlight2;

            String thumb1 = "android.resource://" + getPackageName() + "/" + R.drawable.video1;
            String thumb2 = "android.resource://" + getPackageName() + "/" + R.drawable.video2;

            highlightList.add(new Highlight("HIGHLIGHTS BARCELONA VS FRANKFURT", video1, thumb1));
            highlightList.add(new Highlight("HIGHLIGHTS U23 VIỆT NAM VS U23 JORDAN", video2, thumb2));

            shimmerLayout.stopShimmer();
            shimmerLayout.setVisibility(View.GONE);
            rvHighlight.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }, 700);
    }

    // ================= MENU =================
    private void setupMenuActions() {
        btnProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        btnMatches.setOnClickListener(v -> startActivity(new Intent(this, ClientScheduleActivity.class)));
        btnStandings.setOnClickListener(v -> startActivity(new Intent(this, StandingsActivity.class)));
        btnMyTickets.setOnClickListener(v -> startActivity(new Intent(this, MyTicketsActivity.class)));
        btnHistory.setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class)));

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    // ✅ NEW: mở trang thông báo khi bấm chuông
    private void setupNotificationAction() {
        if (btnNotification == null) return;

        btnNotification.setOnClickListener(v ->
                startActivity(new Intent(this, NotificationActivity.class))
        );
    }
}
