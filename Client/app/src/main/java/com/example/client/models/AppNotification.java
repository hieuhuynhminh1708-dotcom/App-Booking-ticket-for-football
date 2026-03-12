package com.example.client.models;

public class AppNotification {
    public String id;
    public String title;
    public String content;
    public boolean isRead;     // broadcast: false mặc định
    public long createdAt;     // millis

    public AppNotification() {}
}
