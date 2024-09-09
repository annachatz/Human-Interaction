package com.example.myapplication;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TimeApi {
    @GET("timezone/{region}/{city}")
    Call<TimeResponse> getCurrentTime(@Path("region") String region, @Path("city") String city);
}
