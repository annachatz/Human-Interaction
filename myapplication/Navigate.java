package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;
public class Navigate extends AppCompatActivity {
    private static final int REQUEST_CODE_SPEECH_INPUT = 100;






    //    private EditText locationEditText;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigate);
//        locationEditText = findViewById(R.id.locationEditText);

        Button location1Button = findViewById(R.id.location1);
        Button location2Button = findViewById(R.id.location2);
        Button location3Button = findViewById(R.id.location3);
        Button location4Button = findViewById(R.id.location4);
        Button location5Button = findViewById(R.id.location5);
        Button location6Button = findViewById(R.id.location6);
        ImageButton back = findViewById(R.id.back_button);
        ImageButton menu = findViewById(R.id.menu_button);




        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Navigate.this, MainActivity5.class);
                startActivity(intent);
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Navigate.this, MainActivity5.class);
                startActivity(intent);
            }
        });


        // Set OnClickListener for each button
        location1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMaps("37.993210010831085", "23.73036773829935"); // Example coordinates for location 1
            }
        });

        location2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMaps("37.989425876027425", "23.763703629778608"); // Example coordinates for location 2
            }
        });

        // Repeat the same process for other buttons...
        location3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMaps("34.0522", "23.263703529778608"); // Example coordinates for location 3
            }
        });
        location4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMaps("33.0522", "23.293703529778608"); // Example coordinates for location 3
            }
        });
        location5Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMaps("33.9", "23.7"); // Example coordinates for location 1
            }
        });

//        location6Button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                searchLocation();
//            }
//        });
        location6Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start SecondActivity
                Intent intent = new Intent(Navigate.this, NavigateSearch.class);
                startActivity(intent);
            }
        });




        // Define a method to open Google Maps


    }


    private void openGoogleMaps(String latitude, String longitude) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            // Handle case where Google Maps is not installed
            // You may choose to open the location in a web browser or provide a message to the user
        }
    }

    //    private void searchLocation() {
//        String location = locationEditText.getText().toString().trim();
//        if (!location.isEmpty()) {
//            // If location is typed, directly open Google Maps
//            openGoogleMaps2(location);
//        } else {
//            // If location is not typed, prompt for voice input
//            promptSpeechInput();
//        }
//    }
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak the location...");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast.makeText(this, "Speech input not supported", Toast.LENGTH_SHORT).show();
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result != null && !result.isEmpty()) {
                    // Get the recognized speech and open Google Maps
                    String spokenText = result.get(0);
                    openGoogleMaps2(spokenText);
                }
            }
        }
    }
    private void openGoogleMaps2(String location) {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + location);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(this, "Google Maps app not found", Toast.LENGTH_SHORT).show();
        }
    }


}