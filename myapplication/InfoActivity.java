package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        ImageButton menu = findViewById(R.id.menu_button);

        Button navigateInfoButton = findViewById(R.id.navigateInfoButton);
        Button fastCallInfoButton = findViewById(R.id.fastCallInfoButton);
        Button myPillsInfoButton = findViewById(R.id.myPillsInfoButton);
        Button flashLightInfoButton = findViewById(R.id.flashLightInfoButton);
        Button panicButtonInfoButton = findViewById(R.id.panicButtonInfoButton);
        Button infoHelpInfoButton = findViewById(R.id.infoHelpInfoButton);

        navigateInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDialog("Navigate", "This feature allows you to navigate to a specific location, along with some pre saved locations. You can also speak on the microphone and say where you want to go");
            }
        });

        fastCallInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDialog("Fast Call", "This feature allows you to quickly call a saved contact, put them on favorites");
            }
        });
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InfoActivity.this, MainActivity5.class);

                startActivity(intent);
            }
        });

        myPillsInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDialog("My Pills", "The my pills button is a button which allows the user to view a list with the pills he needs to take. The pills are organized based on the hour of the day that they are taken. In order to add a pill to the list press the add pill button which will lead you to a form that requires you to fill in a form about the name the times in a day and the hours to be taken. After submit an alarm is created to inform you when it is time. You can also delete existing pills on the list by pressing the delete button and the selecting the pills which are to be deleted.");
            }
        });

        flashLightInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDialog("Flash Light", "Tap on the \"Flashlight\" box.\n" +
                        "The flashlight will turn on.\n" +
                        "Tap the \"Flashlight\" box again to turn off the flashlight. You can also speak on the speak button to open and close the flashlight");
            }
        });

        panicButtonInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDialog("Panic Button", "This feature sends an emergency alert to your pre-saved contacts...");
            }
        });

        infoHelpInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDialog("Info Help", "I am here to help!!  :)");
            }
        });
    }

    private void showInfoDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

