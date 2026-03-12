package com.example.client.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.client.R;
import com.example.client.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserDetailActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvNik, tvPhone, tvBirthDate, tvGender, tvAddress;
    private ImageView btnBack;
    private Button btnDelete;

    private String userId;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        userId = getIntent().getStringExtra("USER_ID");
        if (userId == null || userId.trim().isEmpty()) {
            Toast.makeText(this, "Thiếu USER_ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnBack = findViewById(R.id.btnBack);
        btnDelete = findViewById(R.id.btnDelete);

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvNik = findViewById(R.id.tvNik);
        tvPhone = findViewById(R.id.tvPhone);
        tvBirthDate = findViewById(R.id.tvBirthDate);
        tvGender = findViewById(R.id.tvGender);
        tvAddress = findViewById(R.id.tvAddress);

        btnBack.setOnClickListener(v -> finish());

        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        loadUser();

        btnDelete.setOnClickListener(v -> deleteUser());
    }

    private void loadUser() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(UserDetailActivity.this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                User u = snapshot.getValue(User.class);
                if (u == null) u = new User();

                // fallback theo key thực tế trong DB (phòng trường hợp DB đặt tên khác)
                String name = pick(snapshot, u.getFullName(), "fullName", "fullname", "name", "full_name");
                String email = pick(snapshot, u.getEmail(), "email", "mail");
                String nik = pick(snapshot, u.getNik(), "nik", "cccd", "cmnd");
                String phone = pick(snapshot, u.getPhone(), "phone", "phoneNumber", "sdt", "soDienThoai");
                String birthDate = pick(snapshot, u.getBirthDate(), "birthDate", "birthday", "dob", "ngaySinh");
                String gender = pick(snapshot, u.getGender(), "gender", "gioiTinh");
                String address = pick(snapshot, u.getAddress(), "address", "diaChi");

                tvName.setText(emptyToDash(name));
                tvEmail.setText(emptyToDash(email));
                tvNik.setText(emptyToDash(nik));
                tvPhone.setText(emptyToDash(phone));
                tvBirthDate.setText(emptyToDash(birthDate));
                tvGender.setText(emptyToDash(gender));
                tvAddress.setText(emptyToDash(address));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserDetailActivity.this, "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteUser() {
        // Lưu ý: chỉ xóa profile trong Realtime DB, không xóa tài khoản FirebaseAuth
        userRef.removeValue()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Đã xóa người dùng (DB)", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi xóa: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private static String emptyToDash(String s) {
        s = (s == null) ? "" : s.trim();
        return s.isEmpty() ? "(Chưa có)" : s;
    }

    private static String pick(DataSnapshot snap, String prefer, String... keys) {
        if (prefer != null && !prefer.trim().isEmpty()) return prefer.trim();
        for (String k : keys) {
            Object v = snap.child(k).getValue();
            if (v != null) {
                String s = String.valueOf(v).trim();
                if (!s.isEmpty()) return s;
            }
        }
        return "";
    }
}
