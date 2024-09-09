package com.example.myapplication;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class AlarmActivity extends AppCompatActivity {

    private static final int REQUEST_VIBRATE_PERMISSION = 123;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_screen);

        String pillName = getIntent().getStringExtra("pillName");
        String time = getIntent().getStringExtra("time");

        TextView pillNameTextView = findViewById(R.id.pill_name);
        TextView timeTextView = findViewById(R.id.time);
        Button dismissButton = findViewById(R.id.dismiss_button);

        pillNameTextView.setText(pillName);
        timeTextView.setText(time);


        // Play alarm sound
        mediaPlayer = MediaPlayer.create(this, R.raw.alarms_sound); // Ensure alarm_sound.mp3 is in res/raw folder
        mediaPlayer.setLooping(true);
        mediaPlayer.start();


        // Check for vibration permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.VIBRATE}, REQUEST_VIBRATE_PERMISSION);
        } else {
            startVibration();
        }

        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAlarm();
                finish();
            }
        });
    }

    private void startVibration() {
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            long[] pattern = {0, 1000, 1000}; // Vibrate for 1 second, pause for 1 second
            vibrator.vibrate(pattern, 0); // Repeat indefinitely
        }
    }

    private void stopAlarm() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAlarm();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_VIBRATE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startVibration();
            } else {
                Toast.makeText(this, "Vibration permission is required for the alarm.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
