package com.example.myapplication;

import com.example.myapplication.Models.Pill;

import java.util.ArrayList;
import java.util.List;

public class Database {
    List<Pill> Morning = new ArrayList<>();
    List<Pill> Lunch = new ArrayList<>();
    List<Pill> Dinner = new ArrayList<>();

    public void update(List<Pill> m, List<Pill> l, List<Pill> d){
        Morning = m;
        Lunch = l;
        Dinner = d;
    }

    public List<Pill> fetch_m(){
        return Morning;
    }
    public List<Pill> fetch_l(){
        return Lunch;
    }
    public List<Pill> fetch_d(){
        return Dinner;
    }
}