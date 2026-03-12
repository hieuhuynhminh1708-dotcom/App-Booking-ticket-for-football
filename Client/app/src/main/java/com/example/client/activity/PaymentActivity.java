package com.example.client.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.client.R;
import com.google.firebase.database.FirebaseDatabase;

public class PaymentActivity extends AppCompatActivity {

    private TextView tvAmount, tvNote;
    private Button btnPaid, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        tvAmount = findViewById(R.id.tvAmount);
        tvNote = findViewById(R.id.tvNote);
        btnPaid = findViewById(R.id.btnPaid);
        btnCancel = findViewById(R.id.btnCancel);

        String requestId = getIntent().getStringExtra("requestId");
        int total = getIntent().getIntExtra("total", 0);
        String matchName = getIntent().getStringExtra("matchName");

        if (requestId == null) {
            Toast.makeText(this, "Thiếu mã yêu cầu", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvAmount.setText("Số tiền: " + formatVnd(total));
        tvNote.setText("Trận: " + safe(matchName) + "\nMã đơn: " + requestId);

        // ✅ XÁC NHẬN MUA → CHỌN PHƯƠNG THỨC → HIỂN THỊ QR NGAY (không sang activity khác)
        btnPaid.setOnClickListener(v -> showPaymentMethodDialog(requestId, total, matchName));

        // ❌ HỦY → GỬI YÊU CẦU HỦY (ADMIN DUYỆT)
        btnCancel.setOnClickListener(v -> {
            FirebaseDatabase.getInstance()
                    .getReference("ticket_requests")
                    .child(requestId)
                    .child("status")
                    .setValue("cancel_requested");

            Toast.makeText(this, "Đã gửi yêu cầu hủy", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void showPaymentMethodDialog(String requestId, int total, String matchName) {
        String[] methods = {"MoMo", "VNPAY", "ZaloPay"};

        new AlertDialog.Builder(PaymentActivity.this)
                .setTitle("Chọn phương thức thanh toán")
                .setItems(methods, (dialog, which) -> {
                    String selectedMethod = methods[which];
                    showQrDialog(selectedMethod, requestId, total, matchName);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showQrDialog(String method, String requestId, int total, String matchName) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_qr_pay, null);

        TextView tvMethod = view.findViewById(R.id.tvMethod);
        TextView tvAmount = view.findViewById(R.id.tvAmount);
        TextView tvNote = view.findViewById(R.id.tvNote);
        ImageView imgQr = view.findViewById(R.id.imgQr);

        tvMethod.setText("Phương thức: " + method);
        tvAmount.setText("Số tiền: " + formatVnd(total));
        tvNote.setText("Trận: " + safe(matchName) + "\nMã đơn: " + safe(requestId));

        // ✅ QR gán cứng
        imgQr.setImageResource(R.drawable.qr_pay);

        new AlertDialog.Builder(this)
                .setTitle("Quét mã để thanh toán")
                .setView(view)
                // Nút này CHỈ để cập nhật trạng thái paid (QR đã hiển thị ngay khi dialog mở)
                .setPositiveButton("Tôi đã thanh toán", (d, w) -> {
                    FirebaseDatabase.getInstance()
                            .getReference("ticket_requests")
                            .child(requestId)
                            .child("status")
                            .setValue("paid");

                    Toast.makeText(this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Đóng", null)
                .show();
    }

    private String formatVnd(int v) {
        return String.format("%,d", v).replace(",", ".") + " VND";
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
