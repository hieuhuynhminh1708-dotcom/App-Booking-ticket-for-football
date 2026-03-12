package com.example.client.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.client.R;
import com.example.client.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtName, edtPhone, edtEmail, edtNik, edtBirthDate, edtGender, edtAddress, edtPassword;
    private Button btnRegister;
    private TextView btnGoLogin;

    private ImageView imgAvatar;
    private TextView btnChooseAvatar;

    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    // ✅ Lưu tên drawable avatar (để đồng bộ qua DB)
    private String selectedAvatarName = "avatar1";

    // ✅ Danh sách avatar có sẵn trong drawable (bạn có thể thêm/bớt)
    private final String[] avatarNames = {"avatar1", "avatar2", "avatar3"};

    // ✅ Danh sách giới tính để chọn
    private final String[] genderOptions = {"Nam", "Nữ", "Khác"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang xử lý...");
        progressDialog.setCancelable(false);

        initViews();
        initListeners();
    }

    private void initViews() {
        imgAvatar = findViewById(R.id.imgAvatar);
        btnChooseAvatar = findViewById(R.id.btnChooseAvatar);

        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        edtNik = findViewById(R.id.edtNik);
        edtBirthDate = findViewById(R.id.edtBirthDate);
        edtGender = findViewById(R.id.edtGender);
        edtAddress = findViewById(R.id.edtAddress);
        edtPassword = findViewById(R.id.edtPassword);

        btnRegister = findViewById(R.id.btnRegister);
        btnGoLogin = findViewById(R.id.btnGoLogin);

        // Avatar mặc định
        imgAvatar.setImageResource(getDrawableIdByName(selectedAvatarName));

        // ✅ chặn gõ tay cho giới tính (đảm bảo mọi máy)
        edtGender.setKeyListener(null);
    }

    private void initListeners() {

        btnGoLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        btnRegister.setOnClickListener(v -> registerUser());

        edtBirthDate.setOnClickListener(v -> showDatePickerDialog());

        // ✅ Giới tính: bấm để chọn (Nam/Nữ/Khác), không nhập
        edtGender.setOnClickListener(v -> showGenderPicker());

        btnChooseAvatar.setOnClickListener(v -> showAvatarPicker());
        imgAvatar.setOnClickListener(v -> showAvatarPicker());
    }

    private void showAvatarPicker() {
        new AlertDialog.Builder(this)
                .setTitle("Chọn ảnh đại diện")
                .setItems(avatarNames, (dialog, which) -> {
                    selectedAvatarName = avatarNames[which];
                    int resId = getDrawableIdByName(selectedAvatarName);
                    if (resId != 0) imgAvatar.setImageResource(resId);
                    else imgAvatar.setImageResource(R.drawable.img_placeholder);
                })
                .show();
    }

    // ✅ Picker giới tính
    private void showGenderPicker() {
        new AlertDialog.Builder(this)
                .setTitle("Chọn giới tính")
                .setItems(genderOptions, (dialog, which) -> edtGender.setText(genderOptions[which]))
                .show();
    }

    private int getDrawableIdByName(String name) {
        return getResources().getIdentifier(name, "drawable", getPackageName());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String selectedDate = String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year1);
                    edtBirthDate.setText(selectedDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void registerUser() {
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String nik = edtNik.getText().toString().trim();
        String birthDate = edtBirthDate.getText().toString().trim();
        String gender = edtGender.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        // Validate
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(nik) || TextUtils.isEmpty(birthDate) || TextUtils.isEmpty(gender) ||
                TextUtils.isEmpty(address) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        // Tạo tài khoản Firebase Auth
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

                        // ✅ photoURL = tên drawable avatar (vd: "avatar1")
                        User user = new User(
                                userId,
                                name,
                                email,
                                nik,
                                phone,
                                birthDate,
                                gender,
                                address,
                                selectedAvatarName, // ✅ LƯU TÊN AVATAR
                                "client"
                        );

                        // Lưu vào Realtime Database
                        mDatabase.child("users").child(userId).setValue(user)
                                .addOnCompleteListener(dbTask -> {
                                    progressDialog.dismiss();
                                    if (dbTask.isSuccessful()) {
                                        Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(this, "Lỗi lưu dữ liệu: " + Objects.requireNonNull(dbTask.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(this,
                                "Đăng ký thất bại: " + Objects.requireNonNull(task.getException()).getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
