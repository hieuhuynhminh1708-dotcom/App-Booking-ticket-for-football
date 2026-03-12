package com.example.client.client;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.client.R;
import com.example.client.models.Match;

import java.util.List;

public class ClientMatchAdapter
        extends RecyclerView.Adapter<ClientMatchAdapter.MatchVH> {

    public interface OnBuyClick {
        void onBuy(Match match);
    }

    private final List<Match> list;
    private final OnBuyClick listener;

    public ClientMatchAdapter(List<Match> list, OnBuyClick listener) {
        this.list = list;
        this.listener = listener;
    }

    static class MatchVH extends RecyclerView.ViewHolder {

        TextView tvName, tvTime, tvStadium;
        Button btnBuy;

        MatchVH(@NonNull View v) {
            super(v);
            tvName = v.findViewById(R.id.tvName);
            tvTime = v.findViewById(R.id.tvTime);
            tvStadium = v.findViewById(R.id.tvStadium);
            btnBuy = v.findViewById(R.id.btnBuy);
        }
    }

    @NonNull
    @Override
    public MatchVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_client_match, parent, false);
        return new MatchVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchVH h, int position) {
        Match m = list.get(position);

        h.tvName.setText(m.homeTeam + " vs " + m.awayTeam);
        h.tvTime.setText(m.date + " - " + m.time);
        h.tvStadium.setText("Sân: " + m.stadium);

        h.btnBuy.setOnClickListener(v -> listener.onBuy(m));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
