package com.example.client.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.client.R;
import com.example.client.activity.TicketDetailActivity;
import com.example.client.models.TicketRequest;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MyTicketsAdapter extends RecyclerView.Adapter<MyTicketsAdapter.VH> {

    private final Context context;
    private final List<TicketRequest> list;

    public MyTicketsAdapter(Context context, List<TicketRequest> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_my_ticket, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int i) {
        TicketRequest t = list.get(i);
        String status = t.status == null ? "" : t.status;

        h.tvMatch.setText(safe(t.matchName));
        h.tvInfo.setText(
                safe(t.date) + " - " + safe(t.time) +
                        "\nSân: " + safe(t.stadium)
        );
        h.tvStatus.setText(getStatusText(status));

        // ✅ CLICK ITEM -> mở vé (chỉ khi paid)
        h.itemView.setOnClickListener(v -> {
            if (t.id == null || t.id.trim().isEmpty()) {
                Toast.makeText(context, "Thiếu id vé", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!"paid".equals(status)) {
                Toast.makeText(context, "Vé chưa thanh toán nên chưa thể xem", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(context, TicketDetailActivity.class);
            intent.putExtra("requestId", t.id);
            context.startActivity(intent);
        });

        // ✅ NÚT YÊU CẦU HỦY
        if ("await_payment".equals(status) || "paid".equals(status)) {
            h.btnCancel.setVisibility(View.VISIBLE);
            h.btnCancel.setText("YÊU CẦU HỦY");

            h.btnCancel.setOnClickListener(v -> {
                if (t.id == null || t.id.trim().isEmpty()) {
                    Toast.makeText(context, "Thiếu id vé", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseDatabase.getInstance()
                        .getReference("ticket_requests")
                        .child(t.id)
                        .child("status")
                        .setValue("cancel_requested")
                        .addOnSuccessListener(unused ->
                                Toast.makeText(context, "Đã gửi yêu cầu hủy vé", Toast.LENGTH_SHORT).show()
                        )
                        .addOnFailureListener(e ->
                                Toast.makeText(context, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                        );
            });
        } else {
            h.btnCancel.setVisibility(View.GONE);
            h.btnCancel.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {

        TextView tvMatch, tvInfo, tvStatus;
        Button btnCancel;

        public VH(@NonNull View v) {
            super(v);
            tvMatch = v.findViewById(R.id.tvMatchName);
            tvInfo = v.findViewById(R.id.tvInfo);
            tvStatus = v.findViewById(R.id.tvStatus);
            btnCancel = v.findViewById(R.id.btnCancel);
        }
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    private String getStatusText(String s) {
        switch (s) {
            case "await_payment":
                return "Chờ thanh toán";
            case "paid":
                return "Đã thanh toán";
            case "cancel_requested":
                return "Chờ duyệt hủy";
            case "cancelled":
                return "Đã hủy";
            case "pending":
                return "Chờ xác nhận";
            case "approved":
                return "Đã mua";
            default:
                return s == null ? "" : s;
        }
    }
}
