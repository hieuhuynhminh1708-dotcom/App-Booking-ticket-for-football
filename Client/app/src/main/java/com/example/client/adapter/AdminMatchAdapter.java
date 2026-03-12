package com.example.client.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.client.R;
import com.example.client.admin.AdminAddEditMatchActivity;
import com.example.client.models.Match;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Map;

public class AdminMatchAdapter extends RecyclerView.Adapter<AdminMatchAdapter.VH> {

    private final Context context;
    private final List<Match> list;

    public AdminMatchAdapter(Context context, List<Match> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_admin_match, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Match m = list.get(position);

        h.tvTeams.setText(m.homeTeam + " vs " + m.awayTeam);
        h.tvInfo.setText(m.date + " • " + m.time + " • " + m.stadium);

        h.tvPrices.setText(formatPrices(m.ticketPrices));

        // ✏️ SỬA: truyền matchId + fields + giá vé (nếu có)
        h.btnEdit.setOnClickListener(v -> {
            Intent i = new Intent(context, AdminAddEditMatchActivity.class);
            i.putExtra("matchId", m.id);
            i.putExtra("homeTeam", m.homeTeam);
            i.putExtra("awayTeam", m.awayTeam);
            i.putExtra("date", m.date);
            i.putExtra("time", m.time);
            i.putExtra("stadium", m.stadium);

            // truyền giá để fill form
            if (m.ticketPrices != null) {
                i.putExtra("vip", getPrice(m.ticketPrices, "vip"));
                i.putExtra("A", getPrice(m.ticketPrices, "A"));
                i.putExtra("B", getPrice(m.ticketPrices, "B"));
                i.putExtra("C", getPrice(m.ticketPrices, "C"));
                i.putExtra("D", getPrice(m.ticketPrices, "D"));
            }
            context.startActivity(i);
        });

        // 🗑 XÓA
        h.btnDelete.setOnClickListener(v -> new AlertDialog.Builder(context)
                .setTitle("Xóa trận đấu")
                .setMessage("Bạn chắc chắn muốn xóa trận này?")
                .setPositiveButton("Xóa", (d, w) ->
                        FirebaseDatabase.getInstance()
                                .getReference("matches")
                                .child(m.id)
                                .removeValue()
                )
                .setNegativeButton("Hủy", null)
                .show());
    }

    private int getPrice(Map<String, Integer> map, String key) {
        Integer v = map.get(key);
        return v == null ? 0 : v;
    }

    private String formatPrices(Map<String, Integer> p) {
        if (p == null) return "VIP: 0 | A: 0 | B: 0 | C: 0 | D: 0";
        return "VIP: " + getPrice(p, "vip")
                + " | A: " + getPrice(p, "A")
                + " | B: " + getPrice(p, "B")
                + " | C: " + getPrice(p, "C")
                + " | D: " + getPrice(p, "D");
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTeams, tvInfo, tvPrices;
        Button btnEdit, btnDelete;

        VH(@NonNull View v) {
            super(v);
            tvTeams = v.findViewById(R.id.tvTeams);
            tvInfo = v.findViewById(R.id.tvInfo);
            tvPrices = v.findViewById(R.id.tvPrices);
            btnEdit = v.findViewById(R.id.btnEdit);
            btnDelete = v.findViewById(R.id.btnDelete);
        }
    }
}
