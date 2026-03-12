package com.example.client.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.client.R;
import com.example.client.models.Match;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BuyTicketActivity extends AppCompatActivity {

    private TextView tvMatchName, tvInfo, tvTotal, tvWeekendSurcharge, tvSurchargeDetail;
    private LinearLayout layoutTickets;
    private Button btnConfirm;
    private ImageView imgStadium;

    private Match match;
    private String matchId;

    private Map<String, Integer> prices;

    // ✅ chọn 1 loại vé
    private String selectedType = null;
    private int baseSelectedPrice = 0; // giá gốc
    private int selectedFinalPrice = 0; // giá cuối cùng (có phụ thu nếu weekend)

    private boolean isWeekendMatch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_ticket);

        tvMatchName = findViewById(R.id.tvMatchName);
        tvInfo = findViewById(R.id.tvMatchInfo);
        tvTotal = findViewById(R.id.tvTotal);
        tvWeekendSurcharge = findViewById(R.id.tvWeekendSurcharge);
        tvSurchargeDetail = findViewById(R.id.tvSurchargeDetail);

        layoutTickets = findViewById(R.id.layoutTickets);
        btnConfirm = findViewById(R.id.btnConfirm);
        imgStadium = findViewById(R.id.imgStadium);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        matchId = getIntent().getStringExtra("matchId");
        if (matchId == null || matchId.trim().isEmpty()) {
            Toast.makeText(this, "Thiếu matchId", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadMatch();

        btnConfirm.setOnClickListener(v -> submitRequest());
    }

    // ================= LOAD MATCH =================
    private void loadMatch() {
        FirebaseDatabase.getInstance()
                .getReference("matches")
                .child(matchId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        match = snapshot.getValue(Match.class);
                        if (match == null) {
                            Toast.makeText(BuyTicketActivity.this, "Không tìm thấy trận đấu", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                        match.id = snapshot.getKey();

                        tvMatchName.setText(match.homeTeam + " vs " + match.awayTeam);
                        tvInfo.setText(match.date + " - " + match.time + "\nSân: " + match.stadium);

                        // ✅ kiểm tra cuối tuần (chỉ dùng để tính khi chọn)
                        isWeekendMatch = isWeekend(match.date);

                        prices = match.ticketPrices;

                        // reset lựa chọn
                        selectedType = null;
                        baseSelectedPrice = 0;
                        selectedFinalPrice = 0;

                        // ẩn thông báo lúc mới vào
                        tvWeekendSurcharge.setVisibility(View.GONE);
                        tvSurchargeDetail.setVisibility(View.GONE);

                        renderTickets();
                        updateTotalSingle();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(BuyTicketActivity.this, "Lỗi tải trận đấu", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ================= RENDER UI VÉ (CHỌN 1) =================
    // ✅ Danh sách hiển thị GIÁ GỐC
    private void renderTickets() {
        layoutTickets.removeAllViews();

        if (prices == null || prices.isEmpty()) return;

        for (String type : prices.keySet()) {
            Integer p = prices.get(type);
            int basePrice = (p == null) ? 0 : p;

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(12, 14, 12, 14);

            // Radio
            RadioButton rb = new RadioButton(this);
            rb.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            ));
            rb.setText(getTypeName(type));
            rb.setTextSize(16f);

            // ✅ Giá hiển thị = giá gốc
            TextView tvPrice = new TextView(this);
            tvPrice.setText(formatVnd(basePrice));
            tvPrice.setTextSize(14f);

            row.addView(rb);
            row.addView(tvPrice);

            // bấm cả row cũng chọn
            row.setOnClickListener(v -> rb.setChecked(true));

            final int finalBasePrice = basePrice;

            rb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!isChecked) return;

                // bỏ chọn các radio khác
                for (int i = 0; i < layoutTickets.getChildCount(); i++) {
                    LinearLayout otherRow = (LinearLayout) layoutTickets.getChildAt(i);
                    RadioButton otherRb = (RadioButton) otherRow.getChildAt(0);
                    if (otherRb != rb) otherRb.setChecked(false);
                }

                selectedType = type;
                baseSelectedPrice = finalBasePrice;

                // ✅ chỉ khi chọn mới tính phụ thu
                selectedFinalPrice = applySurchargeIfWeekend(finalBasePrice);

                // ✅ chỉ khi chọn mới hiện thông báo (nếu weekend)
                if (isWeekendMatch) {
                    tvWeekendSurcharge.setVisibility(View.VISIBLE);
                } else {
                    tvWeekendSurcharge.setVisibility(View.GONE);
                }

                updateTotalSingle();
            });

            layoutTickets.addView(row);
        }
    }

    // ✅ Tổng tiền: khi chọn sẽ hiển thị giá đã tăng (nếu weekend)
    private void updateTotalSingle() {
        if (selectedType == null) {
            tvTotal.setText("Tổng tiền: " + formatVnd(0));
            tvSurchargeDetail.setVisibility(View.GONE);
            return;
        }

        int total = selectedFinalPrice;
        tvTotal.setText("Tổng tiền: " + formatVnd(total));

        int surcharge = calcSurchargeAmount(baseSelectedPrice);
        if (surcharge > 0) {
            tvSurchargeDetail.setVisibility(View.VISIBLE);
            tvSurchargeDetail.setText("Phụ thu cuối tuần (10%): +" + formatVnd(surcharge));
        } else {
            tvSurchargeDetail.setVisibility(View.GONE);
        }
    }

    // ================= SUBMIT (QR PAYMENT) =================
    private void submitRequest() {
        if (prices == null || prices.isEmpty() || match == null) return;

        if (selectedType == null) {
            Toast.makeText(this, "Vui lòng chọn 1 loại vé", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();
        if (current == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = current.getUid();

        // ✅ mỗi lần mua 1 vé
        Map<String, Integer> tickets = new HashMap<>();
        tickets.put(selectedType, 1);

        // ✅ tổng tiền đã bao gồm phụ thu nếu weekend
        int total = selectedFinalPrice;

        // ✅ lưu giá cuối cùng của loại đã chọn
        Map<String, Integer> onlyPrice = new HashMap<>();
        onlyPrice.put(selectedType, selectedFinalPrice);

        Map<String, Object> req = new HashMap<>();
        req.put("userId", userId);

        req.put("matchId", match.id);
        req.put("matchName", match.homeTeam + " vs " + match.awayTeam);
        req.put("date", match.date);
        req.put("time", match.time);
        req.put("stadium", match.stadium);

        req.put("tickets", tickets);
        req.put("prices", onlyPrice);
        req.put("total", total);

        // thêm để nhìn cho rõ (không bắt buộc)
        req.put("isWeekendSurcharge", isWeekendMatch);
        req.put("basePrice", baseSelectedPrice);
        req.put("surchargeAmount", calcSurchargeAmount(baseSelectedPrice));

        req.put("status", "await_payment");
        req.put("createdAt", System.currentTimeMillis());

        DatabaseReference listRef = FirebaseDatabase.getInstance().getReference("ticket_requests");
        DatabaseReference newRef = listRef.push();
        String requestId = newRef.getKey();

        if (requestId == null) {
            Toast.makeText(this, "Lỗi tạo đơn mua vé", Toast.LENGTH_SHORT).show();
            return;
        }

        btnConfirm.setEnabled(false);

        final int totalFinal = total;
        final String requestIdFinal = requestId;
        final String matchNameFinal = match.homeTeam + " vs " + match.awayTeam;

        newRef.setValue(req)
                .addOnSuccessListener(unused -> showPaymentMethodDialog(requestIdFinal, totalFinal, matchNameFinal))
                .addOnFailureListener(e -> {
                    btnConfirm.setEnabled(true);
                    Toast.makeText(this, "Lỗi gửi yêu cầu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // ================= PAYMENT UI (Dialog) =================
    private void showPaymentMethodDialog(String requestId, int total, String matchName) {
        String[] methods = {"MoMo", "VNPAY", "ZaloPay"};

        new AlertDialog.Builder(this)
                .setTitle("Chọn phương thức thanh toán")
                .setItems(methods, (dialog, which) -> {
                    String method = methods[which];
                    showQrDialog(method, requestId, total, matchName);
                })
                .setNegativeButton("Hủy", (d, w) -> btnConfirm.setEnabled(true))
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
                .setTitle("Quét QR để thanh toán")
                .setView(view)
                .setPositiveButton("ĐÃ THANH TOÁN", (d, w) -> {
                    FirebaseDatabase.getInstance()
                            .getReference("ticket_requests")
                            .child(requestId)
                            .child("status")
                            .setValue("paid");
                    Toast.makeText(this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNeutralButton("ĐÓNG", (d, w) -> btnConfirm.setEnabled(true))
                .show();
    }

    // ================= WEEKEND LOGIC =================
    private boolean isWeekend(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            Date d = sdf.parse(dateStr);
            if (d == null) return false;

            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            int day = cal.get(Calendar.DAY_OF_WEEK);

            return day == Calendar.SATURDAY || day == Calendar.SUNDAY;
        } catch (Exception e) {
            return false;
        }
    }

    private int applySurchargeIfWeekend(int basePrice) {
        if (!isWeekendMatch) return basePrice;
        return (int) Math.round(basePrice * 1.10);
    }

    private int calcSurchargeAmount(int basePrice) {
        if (!isWeekendMatch) return 0;
        return applySurchargeIfWeekend(basePrice) - basePrice;
    }

    // ================= UTILS =================
    private String getTypeName(String type) {
        if ("vip".equalsIgnoreCase(type)) return "VIP";
        return "Khán đài " + type;
    }

    private String formatVnd(int v) {
        return String.format("%,d", v).replace(",", ".") + " VND";
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
