package com.example.client.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.client.R;
import com.example.client.adapter.StandingsGroupAdapter;
import com.example.client.models.StandingApiResponse;
import com.example.client.models.StandingItem;
import com.google.gson.Gson;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StandingsActivity extends AppCompatActivity {

    private RecyclerView rvStandings;
    private StandingsGroupAdapter adapter;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standings);

        rvStandings = findViewById(R.id.rvStandings);
        btnBack = findViewById(R.id.btnBack);

        rvStandings.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StandingsGroupAdapter();
        rvStandings.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        loadStandingsFromAssets();
    }

    private void loadStandingsFromAssets() {
        try {
            InputStream is = getAssets().open("standings_vleague.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String json = new String(buffer, StandardCharsets.UTF_8);

            Gson gson = new Gson();
            StandingApiResponse response = gson.fromJson(json, StandingApiResponse.class);

            if (response == null || response.table == null || response.table.isEmpty()) {
                Toast.makeText(this, "Không có dữ liệu bảng xếp hạng", Toast.LENGTH_SHORT).show();
                adapter.setData(new ArrayList<>());
                return;
            }

            // V-League không chia bảng => 1 group duy nhất (giống card Figma)
            String title = (response.league != null ? response.league : "Bảng xếp hạng");
            if (response.season != null && !response.season.trim().isEmpty()) {
                title = title + " (" + response.season + ")";
            }

            List<StandingsGroupAdapter.Group> groups = Collections.singletonList(
                    new StandingsGroupAdapter.Group(title, response.table)
            );

            adapter.setData(groups);

        } catch (Exception e) {
            Toast.makeText(this, "Lỗi đọc dữ liệu: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
