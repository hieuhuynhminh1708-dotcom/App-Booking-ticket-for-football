package com.example.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.client.R;
import com.example.client.models.TicketRequest;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AdminTicketRequestAdapter
        extends RecyclerView.Adapter<AdminTicketRequestAdapter.VH> {

    private final Context context;
    private final List<TicketRequest> list;

    public AdminTicketRequestAdapter(Context context, List<TicketRequest> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_admin_ticket_request, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        TicketRequest t = list.get(position);

        // ===== MATCH NAME =====
        h.tvMatch.setText(t.matchName != null ? t.matchName : "Trận đấu");

        // ===== INFO =====
        String info = safe(t.date) + " - " + safe(t.time) + "\nSân: " + safe(t.stadium);
        h.tvInfo.setText(info);

        // ===== TOTAL =====
        h.tvTotal.setText("Tổng tiền: " + formatVnd(t.total));

        // ===== STATUS (TIẾNG VIỆT) =====
        h.tvStatus.setText(getStatusText(t.status));

        // ===== RESET BUTTON =====
        h.btnApprove.setVisibility(View.GONE); // không dùng duyệt mua nữa
        h.btnCancel.setVisibility(View.GONE);

        String status = t.status == null ? "" : t.status;

        // ✅ Admin chỉ duyệt HỦY
        if ("cancel_requested".equals(status)) {
            h.btnCancel.setVisibility(View.VISIBLE);
            h.btnCancel.setText("DUYỆT HỦY");

            h.btnCancel.setOnClickListener(v -> {
                if (t.id == null || t.id.trim().isEmpty()) {
                    Toast.makeText(context, "Thiếu id đơn", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseDatabase.getInstance()
                        .getReference("ticket_requests")
                        .child(t.id)
                        .child("status")
                        .setValue("cancelled")
                        .addOnSuccessListener(unused ->
                                Toast.makeText(context, "Đã duyệt hủy", Toast.LENGTH_SHORT).show()
                        )
                        .addOnFailureListener(e ->
                                Toast.makeText(context, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                        );
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // ================= VIEW HOLDER =================
    static class VH extends RecyclerView.ViewHolder {

        TextView tvMatch, tvInfo, tvTotal, tvStatus;
        Button btnApprove, btnCancel;

        VH(@NonNull View v) {
            super(v);
            tvMatch = v.findViewById(R.id.tvMatchName);
            tvInfo = v.findViewById(R.id.tvInfo);
            tvTotal = v.findViewById(R.id.tvTotal);
            tvStatus = v.findViewById(R.id.tvStatus);
            btnApprove = v.findViewById(R.id.btnApprove);
            btnCancel = v.findViewById(R.id.btnCancel);
        }
    }

    // ================= HELPER =================
    private String safe(String s) {
        return s == null ? "" : s;
    }

    private String formatVnd(int v) {
        return String.format("%,d", v).replace(",", ".") + " VND";
    }

    private String getStatusText(String s) {
        if (s == null) return "Không xác định";
        switch (s) {
            case "await_payment":
                return "Chờ thanh toán";
            case "paid":
                return "Đã thanh toán";
            case "cancel_requested":
                return "Chờ duyệt hủy";
            case "cancelled":
                return "Đã hủy";
            default:
                return s;
        }
    }
}
