package com.example.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.client.R;
import com.example.client.models.AppNotification;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final Context context;
    private final List<AppNotification> list;

    public NotificationAdapter(Context context, List<AppNotification> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        AppNotification n = list.get(position);

        h.tvTitle.setText(n.title == null ? "" : n.title);
        h.tvContent.setText(n.content == null ? "" : n.content);

        // createdAt nên là long (milliseconds). Nếu null/0 thì để trống
        if (n.createdAt > 0) {
            h.tvTime.setText(android.text.format.DateFormat.format("dd/MM/yyyy HH:mm", n.createdAt));
        } else {
            h.tvTime.setText("");
        }

        // Làm mờ nếu đã đọc
        h.itemView.setAlpha(n.isRead ? 0.5f : 1f);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvTime;

        ViewHolder(@NonNull View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvContent = v.findViewById(R.id.tvContent);
            tvTime = v.findViewById(R.id.tvTime);
        }
    }
}
