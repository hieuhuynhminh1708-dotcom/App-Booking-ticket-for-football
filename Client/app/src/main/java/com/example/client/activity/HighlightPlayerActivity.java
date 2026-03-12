package com.example.client.activity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.client.R;

public class HighlightPlayerActivity extends AppCompatActivity {

    private VideoView videoView;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highlight_player);

        videoView = findViewById(R.id.videoView);
        tvTitle = findViewById(R.id.tvTitle);

        String title = getIntent().getStringExtra("title");
        String videoUrl = getIntent().getStringExtra("videoUrl");

        tvTitle.setText(title == null ? "Highlight" : title);

        if (videoUrl != null) {
            videoView.setVideoURI(Uri.parse(videoUrl));
            videoView.setOnPreparedListener(mp -> {
                mp.setLooping(true);
                videoView.start();
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoView.stopPlayback();
    }
}
