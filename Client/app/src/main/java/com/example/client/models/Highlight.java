package com.example.client.models;

public class Highlight {
    private String title;
    private String videoUrl;   // link video mp4 / youtube
    private String imageUrl;   // thumbnail (ảnh)

    public Highlight() {}

    public Highlight(String title, String videoUrl, String imageUrl) {
        this.title = title;
        this.videoUrl = videoUrl;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
