package com.example.client.models;

import java.util.Map;

public class TicketRequest {

    // 🔑 ID của request (key push từ Firebase)
    public String id;

    // 🔐 User
    public String userId;

    // ⚽ Thông tin trận đấu
    public String matchId;
    public String matchName;
    public String date;
    public String time;
    public String stadium;


    // 🎟 Vé
    public Map<String, Integer> tickets; // {vip:2, A:1}
    public Map<String, Integer> prices;  // {vip:100000, A:80000}

    // 💰 Thanh toán
    public int total;

    /**
     * Trạng thái đơn:
     * await_payment  : chờ thanh toán
     * paid           : đã thanh toán
     * cancelled      : đã hủy
     */
    public String status;

    // 🕒 Thời gian tạo đơn (để sort lịch sử)
    public long createdAt;

    // Firebase bắt buộc
    public TicketRequest() {}
}
