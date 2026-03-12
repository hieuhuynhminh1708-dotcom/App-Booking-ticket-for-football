package com.example.client.models;

public class UserSession {

    private static String name;
    private static String memberId;
    private static boolean isLoggedIn = false;

    // Gọi khi đăng nhập thành công
    public static void login(String userName, String userMemberId) {
        name = userName;
        memberId = userMemberId;
        isLoggedIn = true;
    }

    public static String getName() {
        return name != null ? name : "Khách";
    }

    public static String getMemberId() {
        return memberId != null ? memberId : "Chưa có mã";
    }

    public static boolean isLoggedIn() {
        return isLoggedIn;
    }

    // Gọi khi logout
    public static void clear() {
        name = null;
        memberId = null;
        isLoggedIn = false;
    }
}
