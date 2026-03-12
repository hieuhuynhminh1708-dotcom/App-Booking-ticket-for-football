package com.example.client.admin;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.client.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AdminNotificationActivity extends AppCompatActivity {

    private EditText edtTitle, edtContent;
    private Button btnSendAll;
    private ProgressBar progress;
    private TextView txtResult;

    // ✅ NEW: nút quay lại
    private TextView btnBack;

    private DatabaseReference broadcastRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_notification);

        btnBack = findViewById(R.id.btnBack); // ✅ NEW
        edtTitle = findViewById(R.id.edtTitle);
        edtContent = findViewById(R.id.edtContent);
        btnSendAll = findViewById(R.id.btnSendAll);
        progress = findViewById(R.id.progress);
        txtResult = findViewById(R.id.txtResult);

        // ✅ NEW: quay lại
        btnBack.setOnClickListener(v -> finish());

        // ✅ broadcast node
        broadcastRef = FirebaseDatabase.getInstance().getReference("broadcast_notifications");

        btnSendAll.setOnClickListener(v -> {
            String title = edtTitle.getText().toString().trim();
            String content = edtContent.getText().toString().trim();

            if (TextUtils.isEmpty(title)) {
                edtTitle.setError("Vui lòng nhập tiêu đề");
                edtTitle.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(content)) {
                edtContent.setError("Vui lòng nhập nội dung");
                edtContent.requestFocus();
                return;
            }

            // ✅ NEW: ẩn bàn phím trước khi gửi
            hideKeyboard();

            sendBroadcast(title, content);
        });
    }

    private void setLoading(boolean loading) {
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);

        // ✅ NEW: chống bấm gửi liên tục
        btnSendAll.setEnabled(!loading);

        edtTitle.setEnabled(!loading);
        edtContent.setEnabled(!loading);
    }

    private void sendBroadcast(String title, String content) {
        setLoading(true);
        txtResult.setText("");

        String id = broadcastRef.push().getKey();
        if (id == null) {
            setLoading(false);
            Toast.makeText(this, "Không tạo được ID", Toast.LENGTH_LONG).show();
            return;
        }

        long now = System.currentTimeMillis();

        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("title", title);
        data.put("content", content);
        data.put("createdAt", now);

        broadcastRef.child(id).setValue(data)
                .addOnSuccessListener(unused -> {
                    setLoading(false);
                    txtResult.setText("Đã gửi thông báo chung thành công!");
                    Toast.makeText(this, "Đã gửi thông báo chung!", Toast.LENGTH_LONG).show();

                    // ✅ NEW: clear input
                    edtTitle.setText("");
                    edtContent.setText("");
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    txtResult.setText("Gửi thất bại: " + e.getMessage());
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    // ✅ NEW: Hàm ẩn bàn phím
    private void hideKeyboard() {
        try {
            View view = this.getCurrentFocus();
            if (view == null) view = new View(this);

            InputMethodManager imm =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception ignored) {}
    }
}
