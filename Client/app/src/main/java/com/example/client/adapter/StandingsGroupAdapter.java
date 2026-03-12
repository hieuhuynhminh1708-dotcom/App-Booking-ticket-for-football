package com.example.client.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.client.R;
import com.example.client.models.StandingItem;

import java.util.ArrayList;
import java.util.List;

public class StandingsGroupAdapter extends RecyclerView.Adapter<StandingsGroupAdapter.VH> {

    public static class Group {
        public String title;
        public List<StandingItem> rows;

        public Group(String title, List<StandingItem> rows) {
            this.title = title;
            this.rows = rows;
        }
    }

    private final List<Group> data = new ArrayList<>();

    public void setData(List<Group> groups) {
        data.clear();
        if (groups != null) data.addAll(groups);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group_standings, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Group g = data.get(position);

        h.tvGroupTitle.setText(g.title);
        h.layoutRows.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(h.itemView.getContext());
        if (g.rows == null) return;

        for (StandingItem r : g.rows) {
            View row = inflater.inflate(R.layout.item_standing_row, h.layoutRows, false);

            ((TextView) row.findViewById(R.id.tvTeam))
                    .setText(r.rank + ". " + r.team);
            ((TextView) row.findViewById(R.id.tvP))
                    .setText(String.valueOf(r.played));
            ((TextView) row.findViewById(R.id.tvW))
                    .setText(String.valueOf(r.win));
            ((TextView) row.findViewById(R.id.tvD))
                    .setText(String.valueOf(r.draw));
            ((TextView) row.findViewById(R.id.tvL))
                    .setText(String.valueOf(r.lose));

            int gd = r.goal_diff;
            String gdText = (gd > 0 ? "+" : "") + gd;
            ((TextView) row.findViewById(R.id.tvGD)).setText(gdText);

            ((TextView) row.findViewById(R.id.tvPTS))
                    .setText(String.valueOf(r.points));

            h.layoutRows.addView(row);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvGroupTitle;
        LinearLayout layoutRows;

        VH(@NonNull View itemView) {
            super(itemView);
            tvGroupTitle = itemView.findViewById(R.id.tvGroupTitle);
            layoutRows = itemView.findViewById(R.id.layoutRows);
        }
    }
}
