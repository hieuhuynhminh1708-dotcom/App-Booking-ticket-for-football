package com.example.client.api;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RssApi {

    // RSS ổn định, parse được chắc chắn
    @GET("rss/tin-moi-nhat.rss")
    Call<RssResponse> getNews();
}
