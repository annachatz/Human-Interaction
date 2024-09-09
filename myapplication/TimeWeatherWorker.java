package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TimeWeatherWorker extends Worker {

    public TimeWeatherWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        fetchCurrentTime();
        fetchWeatherData();
        return Result.success();
    }

    private void fetchCurrentTime() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://worldtimeapi.org/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TimeApi timeApi = retrofit.create(TimeApi.class);
        Call<TimeResponse> call = timeApi.getCurrentTime("Europe", "London");
        call.enqueue(new Callback<TimeResponse>() {
            @Override
            public void onResponse(Call<TimeResponse> call, Response<TimeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TimeResponse timeResponse = response.body();
                    String dateTime = timeResponse.datetime;

                    SharedPreferences prefs = getApplicationContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
                    prefs.edit().putString("current_time", dateTime).apply();
                }
            }

            @Override
            public void onFailure(Call<TimeResponse> call, Throwable t) {
                // Handle failure
            }
        });
    }

    private void fetchWeatherData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherApi weatherApi = retrofit.create(WeatherApi.class);
        Call<WeatherResponse> call = weatherApi.getWeather("London", "d1e8b028a9ee35eda3efc1ec1f95768f", "metric");
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();
                    String weatherText = weatherResponse.main.temp + "C " + weatherResponse.weather.get(0).description;

                    SharedPreferences prefs = getApplicationContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
                    prefs.edit().putString("current_weather", weatherText).apply();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                // Handle failure
            }
        });
    }
}
