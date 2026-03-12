package com.example.client.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.client.R;
import com.example.client.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserManagerActivity extends AppCompatActivity {

    private ListView listViewUsers;

    private final ArrayList<User> users = new ArrayList<>();
    private final ArrayList<String> userIdList = new ArrayList<>();

    private DatabaseReference userRef;
    private ArrayAdapter<User> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manager);

        userRef = FirebaseDatabase.getInstance().getReference("users");

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        listViewUsers = findViewById(R.id.listViewUsers);

        adapter = new ArrayAdapter<User>(this, 0, users) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    v = LayoutInflater.from(getContext()).inflate(R.layout.item_user_manager, parent, false);
                }

                TextView tvName = v.findViewById(R.id.tvName);
                TextView tvEmail = v.findViewById(R.id.tvEmail);
                TextView tvExtra = v.findViewById(R.id.tvExtra);

                User u = getItem(position);

                String name = safe(u == null ? null : u.getFullName(), "Chưa có tên");
                String email = safe(u == null ? null : u.getEmail(), "");
                String phone = safe(u == null ? null : u.getPhone(), "");
                String nik = safe(u == null ? null : u.getNik(), "");

                tvName.setText(name);
                tvEmail.setText(email);

                String extra = "";
                if (!phone.isEmpty()) extra += "SĐT: " + phone;
                if (!nik.isEmpty()) extra += (extra.isEmpty() ? "" : " • ") + "NIK: " + nik;

                tvExtra.setText(extra.isEmpty() ? " " : extra);

                return v;
            }
        };

        listViewUsers.setAdapter(adapter);

        listViewUsers.setOnItemClickListener((parent, view, position, id) -> {
            if (position < 0 || position >= userIdList.size()) return;
            String userId = userIdList.get(position);
            showUserDetail(userId);
        });

        loadUsers();
    }

    private void loadUsers() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                userIdList.clear();

                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    String uid = userSnap.getKey();
                    if (uid == null) continue;

                    // 1) thử map theo model
                    User user = userSnap.getValue(User.class);
                    if (user == null) user = new User();

                    // 2) set uid chắc chắn
                    user.setUid(uid);

                    // 3) fallback nếu DB key khác tên field -> đọc thẳng snapshot
                    if (isEmpty(user.getFullName())) {
                        user.setFullName(
                                pickFirst(userSnap, "fullName", "fullname", "name", "full_name")
                        );
                    }
                    if (isEmpty(user.getEmail())) {
                        user.setEmail(
                                pickFirst(userSnap, "email", "mail")
                        );
                    }
                    if (isEmpty(user.getPhone())) {
                        user.setPhone(
                                pickFirst(userSnap, "phone", "phoneNumber", "sdt", "soDienThoai")
                        );
                    }
                    if (isEmpty(user.getNik())) {
                        user.setNik(
                                pickFirst(userSnap, "nik", "cccd", "cmnd")
                        );
                    }

                    users.add(user);
                    userIdList.add(uid);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserManagerActivity.this, "Lỗi tải dữ liệu: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUserDetail(String userId) {
        Intent intent = new Intent(UserManagerActivity.this, UserDetailActivity.class);
        intent.putExtra("USER_ID", userId);
        startActivity(intent);
    }

    // ===== helpers =====

    private static String safe(String s, String fallback) {
        if (s == null) return fallback;
        s = s.trim();
        return s.isEmpty() ? fallback : s;
    }

    private static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String pickFirst(DataSnapshot snap, String... keys) {
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
