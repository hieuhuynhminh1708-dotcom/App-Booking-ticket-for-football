package com.example.client.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.client.R;
import com.example.client.models.TicketRequest;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.VH> {

    // Nếu bạn vẫn muốn click vào item để mở chi tiết thì dùng listener này
    public interface OnItemClickListener {
        void onClick(TicketRequest item);
    }

    private final List<TicketRequest> data = new ArrayList<>();
    private final OnItemClickListener listener;

    public HistoryAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setData(List<TicketRequest> list) {
        data.clear();
        if (list != null) data.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        TicketRequest o = data.get(position);

        h.tvMatch.setText(o.matchName != null ? o.matchName : "Trận đấu");

        String info = (o.date == null ? "" : o.date) + " - " + (o.time == null ? "" : o.time)
                + " • " + (o.stadium == null ? "" : o.stadium);
        h.tvInfo.setText(info.trim());

        h.tvTotal.setText("Tổng tiền: " + formatVnd(o.total));

        String statusText = mapStatus(o.status);
        h.tvStatus.setText(statusText);

        // Màu theo status
        String st = (o.status == null) ? "" : o.status.trim().toLowerCase();
        if (st.contains("paid") || st.contains("approved") || st.contains("active") || st.contains("aktif")) {
            h.tvStatus.setTextColor(0xFF2E7D32); // xanh
        } else if (st.contains("await") || st.contains("pending") || st.contains("menunggu")) {
            h.tvStatus.setTextColor(0xFFF9A825); // vàng
        } else if (st.contains("cancel")) {
            h.tvStatus.setTextColor(0xFFC62828); // đỏ
        } else {
            h.tvStatus.setTextColor(0xFF616161); // xám
        }

        // Click toàn bộ item (nếu bạn muốn mở chi tiết)
        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(o);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvMatch, tvInfo, tvTotal, tvStatus;

        VH(@NonNull View itemView) {
            super(itemView);
            tvMatch = itemView.findViewById(R.id.tvMatch);
            tvInfo = itemView.findViewById(R.id.tvInfo);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }

    private String mapStatus(String s) {
        if (s == null) return "KHÔNG RÕ";
        s = s.trim().toLowerCase();

        // chờ thanh toán
        if (s.equals("await_payment") || s.equals("pending") || s.equals("menunggu")) return "CHỜ THANH TOÁN";

        // đã thanh toán
        if (s.equals("paid") || s.equals("approved") || s.equals("active") || s.equals("aktif")) return "ĐÃ THANH TOÁN";

        // đã hủy (xử lý cả canceled và cancelled)
        if (s.equals("canceled") || s.equals("cancelled") || s.equals("cancel")) return "ĐÃ HỦY";

        return s.toUpperCase();
    }

    private String formatVnd(int v) {
        return String.format("%,d", v).replace(",", ".") + " VND";
    }
}
