package com.example.client.models;

import com.google.gson.annotations.SerializedName;

public class Standing {

    @SerializedName("intRank")
    public int rank;

    @SerializedName("strTeam")
    public String teamName;

    @SerializedName("intPlayed")
    public int played;

    @SerializedName("intPoints")
    public int points;

    @SerializedName("strTeamBadge")
    public String logo;
}
