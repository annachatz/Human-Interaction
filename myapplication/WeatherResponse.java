package com.example.myapplication;
import java.util.List;

public class WeatherResponse {
    public Main main;
    public List<Weather> weather;

    public class Main {
        public float temp;
    }

    public class Weather {
        public String description;
    }
}

