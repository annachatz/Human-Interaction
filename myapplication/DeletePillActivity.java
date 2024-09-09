package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Models.Pill;

import java.util.ArrayList;
import java.util.List;
public class DeletePillActivity extends AppCompatActivity {

    private RecyclerView deleteRecyclerView;
    private DeletePillAdapter deletePillAdapter;
    private List<Pill> pillList = new ArrayList<>();
    private List<Pill> selectedPills = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_pill);

        deleteRecyclerView = findViewById(R.id.delete_recycler_view);
        Button deleteButton = findViewById(R.id.delete_button);
        Button cancelButton = findViewById(R.id.cancel_button);

        // Retrieve the pills passed from MainActivity
        Intent intent = getIntent();
        if (intent != null) {
            ArrayList<Pill> pills = intent.getParcelableArrayListExtra("pills");
            if (pills != null) {
                pillList.addAll(pills);
            }
        }

        deletePillAdapter = new DeletePillAdapter(pillList, selectedPills ,new DeletePillAdapter.OnItemClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onItemClick(Pill pill) {
                if (selectedPills.contains(pill)) {
                    selectedPills.remove(pill);
                } else {
                    selectedPills.add(pill);
                }
                deletePillAdapter.notifyDataSetChanged();
            }
        });

        deleteRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        deleteRecyclerView.setAdapter(deletePillAdapter);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                resultIntent.putParcelableArrayListExtra("selectedPills", new ArrayList<>(selectedPills));
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }


}
