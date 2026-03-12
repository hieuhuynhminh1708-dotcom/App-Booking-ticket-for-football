package com.example.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.client.R;
import com.example.client.models.StandingItem;

import java.util.List;

public class StandingAdapter extends RecyclerView.Adapter<StandingAdapter.ViewHolder> {

    private final Context context;
    private final List<StandingItem> list;

    public StandingAdapter(Context context, List<StandingItem> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_standing, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        StandingItem item = list.get(position);
        if (item == null) return;

        // STT
        holder.tvRank.setText(String.valueOf(position + 1));

        // TÊN ĐỘI
        holder.tvTeam.setText(
                item.team != null ? item.team : "Đang cập nhật"
        );

        // SỐ TRẬN
        holder.tvPlayed.setText(String.valueOf(item.played));

        // ĐIỂM
        holder.tvPoints.setText(String.valueOf(item.points));

        // LOGO (chưa có → dùng icon mặc định)
        holder.imgLogo.setImageResource(R.drawable.ic_vleague_ball);
        // nếu chưa có ic_ball → dùng:
        // android.R.drawable.ic_menu_gallery
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvRank, tvTeam, tvPlayed, tvPoints;
        ImageView imgLogo;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvRank);
            tvTeam = itemView.findViewById(R.id.tvTeam);
            tvPlayed = itemView.findViewById(R.id.tvPlayed);
            tvPoints = itemView.findViewById(R.id.tvPoints);
            imgLogo = itemView.findViewById(R.id.imgLogo);
        }
    }
}
