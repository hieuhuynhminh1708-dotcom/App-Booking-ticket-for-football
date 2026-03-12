package com.example.client.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.client.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    // UI
    private ImageView btnBack;
    private Button btnSave;

    private EditText edtFullName, edtEmail, edtNik,
            edtPhone, edtBirthDate, edtGender, edtAddress;

    // ✅ Avatar
    private ImageView imgAvatarEdit;
    private TextView btnChooseAvatarEdit;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    // ✅ avatar local
    private String selectedAvatarName = "avatar1";
    private final String[] avatarNames = {"avatar1", "avatar2", "avatar3"};

    // ✅ Giới tính chọn
    private final String[] genderOptions = {"Nam", "Nữ", "Khác"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initView();
        initFirebase();
        setupActions();
        loadProfileFromFirebase();
    }

    // ================= INIT =================
    private void initView() {
        btnBack = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btnSave);

        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtNik = findViewById(R.id.edtNik);
        edtPhone = findViewById(R.id.edtPhone);
        edtBirthDate = findViewById(R.id.edtBirthDate);
        edtGender = findViewById(R.id.edtGender);
        edtAddress = findViewById(R.id.edtAddress);

        // ✅ Avatar
        imgAvatarEdit = findViewById(R.id.imgAvatarEdit);
        btnChooseAvatarEdit = findViewById(R.id.btnChooseAvatarEdit);

        // default
        imgAvatarEdit.setImageResource(getDrawableIdByName(selectedAvatarName));

        // ✅ chặn gõ tay giới tính
        edtGender.setKeyListener(null);
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
    }

    // ================= ACTIONS =================
    private void setupActions() {

        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> saveProfile());

        btnChooseAvatarEdit.setOnClickListener(v -> showAvatarPicker());
        imgAvatarEdit.setOnClickListener(v -> showAvatarPicker());

        // ✅ chọn giới tính như trang đăng ký
        edtGender.setOnClickListener(v -> showGenderPicker());

        // (Khuyến nghị) ngày sinh chọn bằng DatePicker giống register
        edtBirthDate.setOnClickListener(v -> showDatePickerDialog());
    }

    private void showAvatarPicker() {
        new AlertDialog.Builder(this)
                .setTitle("Chọn ảnh đại diện")
                .setItems(avatarNames, (dialog, which) -> {
                    selectedAvatarName = avatarNames[which];

                    int resId = getDrawableIdByName(selectedAvatarName);
                    if (resId != 0) imgAvatarEdit.setImageResource(resId);
                    else imgAvatarEdit.setImageResource(R.drawable.img_placeholder);
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

    // (Khuyến nghị) DatePicker cho ngày sinh
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

    // ================= LOAD DATA =================
    private void loadProfileFromFirebase() {

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
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
                    Toast.makeText(EditProfileActivity.this, "Không có dữ liệu hồ sơ", Toast.LENGTH_SHORT).show();
                    return;
                }

                edtFullName.setText(getValue(snapshot, "fullName"));
                edtEmail.setText(getValue(snapshot, "email"));
                edtNik.setText(getValue(snapshot, "nik"));
                edtPhone.setText(getValue(snapshot, "phone"));
                edtBirthDate.setText(getValue(snapshot, "birthDate"));
                edtGender.setText(getValue(snapshot, "gender"));
                edtAddress.setText(getValue(snapshot, "address"));

                // ✅ Load avatar hiện tại từ DB
                String avatarName = snapshot.child("photoURL").getValue(String.class);
                if (avatarName != null && !avatarName.trim().isEmpty()) {
                    selectedAvatarName = avatarName;
                }

                int resId = getDrawableIdByName(selectedAvatarName);
                if (resId != 0) imgAvatarEdit.setImageResource(resId);
                else imgAvatarEdit.setImageResource(R.drawable.img_placeholder);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(EditProfileActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ================= SAVE DATA =================
    private void saveProfile() {

        String fullName = edtFullName.getText().toString().trim();
        String nik = edtNik.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String birthDate = edtBirthDate.getText().toString().trim();
        String gender = edtGender.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();

        if (fullName.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("fullName", fullName);
        updates.put("nik", nik);
        updates.put("phone", phone);
        updates.put("birthDate", birthDate);
        updates.put("gender", gender);
        updates.put("address", address);

        // ✅ update luôn avatar
        updates.put("photoURL", selectedAvatarName);

        userRef.updateChildren(updates)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(EditProfileActivity.this, "Cập nhật hồ sơ thành công", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(EditProfileActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
                );
    }

    private String getValue(DataSnapshot snapshot, String key) {
        String v = snapshot.child(key).getValue(String.class);
        return v == null ? "" : v;
    }

    private int getDrawableIdByName(String name) {
        if (name == null || name.trim().isEmpty()) return 0;
        return getResources().getIdentifier(name, "drawable", getPackageName());
    }
}
