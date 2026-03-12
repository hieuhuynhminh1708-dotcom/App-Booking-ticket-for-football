package com.example.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.client.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    // UI
    private ImageView btnBack;
    private Button btnEditProfile;

    // ✅ Avatar
    private ImageView imgAvatarProfile;

    private TextView tvFullName, tvEmail, tvNik,
            tvPhone, tvBirthDate, tvGender, tvAddress;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_profile);

        initView();
        initFirebase();
        setupActions();
        loadProfileFromFirebase();
    }

    // ================= INIT =================
    private void initView() {
        btnBack = findViewById(R.id.btnBack);
        btnEditProfile = findViewById(R.id.btnEditProfile);

        // ✅ Thêm ImageView avatar trong XML với id này
        imgAvatarProfile = findViewById(R.id.imgAvatarProfile);

        tvFullName = findViewById(R.id.tvFullName);
        tvEmail = findViewById(R.id.tvEmail);
        tvNik = findViewById(R.id.tvNik);
        tvPhone = findViewById(R.id.tvPhone);
        tvBirthDate = findViewById(R.id.tvBirthDate);
        tvGender = findViewById(R.id.tvGender);
        tvAddress = findViewById(R.id.tvAddress);
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
    }

    // ================= ACTIONS =================
    private void setupActions() {

        // 🔙 Quay lại
        btnBack.setOnClickListener(v -> finish());

        // ✏️ Chỉnh sửa thông tin
        btnEditProfile.setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class))
        );
    }

    // ================= LOAD DATA =================
    private void loadProfileFromFirebase() {

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();

        userRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (!snapshot.exists()) {
                    Toast.makeText(ProfileActivity.this, "Không có dữ liệu hồ sơ", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Text info
                tvFullName.setText(getValue(snapshot, "fullName"));
                tvEmail.setText(getValue(snapshot, "email"));
                tvNik.setText(getValue(snapshot, "nik"));
                tvPhone.setText(getValue(snapshot, "phone"));
                tvBirthDate.setText(getValue(snapshot, "birthDate"));
                tvGender.setText(getValue(snapshot, "gender"));
                tvAddress.setText(getValue(snapshot, "address"));

                // ✅ Avatar đồng bộ theo đăng ký (photoURL lưu tên drawable: avatar1/avatar2/...)
                String avatarName = snapshot.child("photoURL").getValue(String.class);

                int resId = getDrawableIdByName(avatarName);
                if (resId != 0) {
                    imgAvatarProfile.setImageResource(resId);
                } else {
                    imgAvatarProfile.setImageResource(R.drawable.img_placeholder);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Lỗi tải hồ sơ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Tránh null -> rỗng
    private String getValue(DataSnapshot snapshot, String key) {
        String v = snapshot.child(key).getValue(String.class);
        return v == null ? "" : v;
    }

    // Map tên drawable -> id
    private int getDrawableIdByName(String name) {
        if (name == null || name.trim().isEmpty()) return 0;
        return getResources().getIdentifier(name, "drawable", getPackageName());
    }
}
