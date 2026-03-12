package com.example.client.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.client.R;
import com.example.client.activity.HighlightPlayerActivity;
import com.example.client.models.Highlight;

import java.util.List;

public class HighlightAdapter extends RecyclerView.Adapter<HighlightAdapter.ViewHolder> {

    private final Context context;
    private final List<Highlight> list;

    public HighlightAdapter(Context context, List<Highlight> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_highlight, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Highlight highlight = list.get(position);

        holder.tvTitle.setText(highlight.getTitle());

        // Load thumbnail
        Glide.with(context)
                .load(highlight.getImageUrl())
                .placeholder(R.drawable.img_placeholder)
                .into(holder.imgHighlight);

        // Click → mở màn hình phát video trong app
        holder.itemView.setOnClickListener(v -> {
            String videoUrl = highlight.getVideoUrl();

            if (videoUrl == null || videoUrl.trim().isEmpty()) {
                Toast.makeText(context, "Không có video highlight", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(context, HighlightPlayerActivity.class);
            intent.putExtra("videoUrl", videoUrl);
            intent.putExtra("title", highlight.getTitle());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgHighlight;
        TextView tvTitle;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgHighlight = itemView.findViewById(R.id.imgHighlight);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }
    }
}
