package com.example.client.api;

import com.example.client.models.StandingApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface StandingApi {

    @GET("YOUR_ENDPOINT_HERE")
    Call<StandingApiResponse> getStandings();
}
