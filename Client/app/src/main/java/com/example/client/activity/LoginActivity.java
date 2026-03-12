package com.example.client.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.client.R;
import com.example.client.admin.AdminHomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    Button btnLogin;
    TextView txtRegister;

    FirebaseAuth auth;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Mapping
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegister);

        // Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference("users");

        // Đi đến màn hình Register
        txtRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        // Login
        btnLogin.setOnClickListener(v -> login());
    }

    private void login() {
        String email = edtEmail.getText().toString().trim();
        String pass = edtPassword.getText().toString().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập email và mật khẩu!", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener(result -> {

                    String uid = auth.getCurrentUser().getUid();

                    db.child(uid).get()
                            .addOnSuccessListener(snapshot -> {

                                if (!snapshot.exists()) {
                                    Toast.makeText(this, "Không tìm thấy dữ liệu User!", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                String role = snapshot.child("role").getValue(String.class);

                                if (role == null) {
                                    Toast.makeText(this, "User không có role!", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if (role.equals("admin")) {
                                    Toast.makeText(this, "Đăng nhập Admin!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(this, AdminHomeActivity.class));
                                } else {
                                    Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(this, ClientHomeActivity.class));
                                }

                                finish();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Không đọc được role: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Đăng nhập lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
