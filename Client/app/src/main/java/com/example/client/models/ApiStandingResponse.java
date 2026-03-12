package com.example.client.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ApiStandingResponse {
    @SerializedName("data")
    public StandingData data;

    public static class StandingData {
        @SerializedName("standings")
        public List<StandingItem> standings;
    }
}
