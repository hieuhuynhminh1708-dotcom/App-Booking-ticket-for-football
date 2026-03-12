package com.example.client.models;

import java.util.Map;

public class Match {

    public String id;
    public String homeTeam;
    public String awayTeam;
    public String date;
    public String time;
    public String stadium;

    // Giá vé 5 loại
    public Map<String, Integer> ticketPrices;

    // 🔴 BẮT BUỘC cho Firebase
    public Match() {}

    // ✅ CONSTRUCTOR ĐÚNG
    public Match(String homeTeam,
                 String awayTeam,
                 String date,
                 String time,
                 String stadium,
                 Map<String, Integer> ticketPrices) {

        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.date = date;
        this.time = time;
        this.stadium = stadium;
        this.ticketPrices = ticketPrices;
    }
}
