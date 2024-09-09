package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity5 extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int MANAGE_PILLS_REQUEST = 1;

    private ArrayList<Pill> morningPills = new ArrayList<>();
    private ArrayList<Pill> afternoonPills = new ArrayList<>();
    private ArrayList<Pill> eveningPills = new ArrayList<>();
    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final int RECORD_AUDIO_PERMISSION_REQUEST_CODE = 3;
    private boolean locationPermissionGranted;
    private boolean audioPermissionGranted;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private TextView timeTextView;
    private TextView dateTextView;
    private TextView weatherTextView;
    private TextView recognizedTextView;
    private Button navigateButton, fastCallButton, myPillsButton, flashButton, panicButton, info, microphoneButton;
    private CameraManager cameraManager;
    private String cameraId;
    private List<Contact> allContacts;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private boolean isFlashlightOn = false;
    private Map<String, Set<String>> keywordSynonyms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2);
        timeTextView = findViewById(R.id.time);
        dateTextView = findViewById(R.id.date);
        weatherTextView = findViewById(R.id.weather);
        recognizedTextView = findViewById(R.id.recognized_text);

        displayCurrentTimeAndDate();
        fetchWeatherData();

        navigateButton = findViewById(R.id.navigate);
        fastCallButton = findViewById(R.id.fast_call);
        myPillsButton = findViewById(R.id.my_pills);
        flashButton = findViewById(R.id.flashlight);
        panicButton = findViewById(R.id.panic_button);
        info = findViewById(R.id.settings);
        microphoneButton = findViewById(R.id.microphone);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        getLocationPermission();
        getAudioPermission();

        panicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocationAndSendEmail();
            }
        });

        flashButton.setOnClickListener(view -> {
            if (isFlashlightOn) {
                turnOffFlashlight();
            } else {
                turnOnFlashlight();
            }
        });

        fastCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity5.this, ContactListActivity.class);
                startActivity(intent);
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity5.this, InfoActivity.class);
                startActivity(intent);
            }
        });

        myPillsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity5.this, ManagePills.class);
                intent.putParcelableArrayListExtra("morningPills", morningPills);
                intent.putParcelableArrayListExtra("afternoonPills", afternoonPills);
                intent.putParcelableArrayListExtra("eveningPills", eveningPills);
                startActivityForResult(intent, MANAGE_PILLS_REQUEST);
            }
        });




        microphoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startListening();
            }
        });
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity5.this, Navigate.class);
                startActivity(intent);
            }
        });

        initializeKeywordSynonyms();
        initializeSpeechRecognizer();
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void getAudioPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            audioPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    RECORD_AUDIO_PERMISSION_REQUEST_CODE);
        }
    }

    private void getLocationAndSendEmail() {
        if (locationPermissionGranted) {
            try {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location location = task.getResult();
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            sendEmail(latitude, longitude);
                        } else {
                            Toast.makeText(MainActivity5.this, "Unable to get location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendEmail(double latitude, double longitude) {
        String locationUrl = "https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude;
        String message = "Current location: " +
                "Latitude: " + latitude +
                ", Longitude: " + longitude +
                "\n\nOpen location in Google Maps: " + locationUrl;

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"annachatzipap@gmail.com", "p3200219@aueb.gr"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "SOS-EMERGENCY-HELP");
        intent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            startActivity(Intent.createChooser(intent, "Send email using..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No email client found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        } else if (requestCode == RECORD_AUDIO_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                audioPermissionGranted = true;
            }
        }
    }

    private void displayCurrentTimeAndDate() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE dd MMMM", Locale.getDefault());
        String currentTime = timeFormat.format(new Date());
        String currentDate = dateFormat.format(new Date());

        timeTextView.setText(currentTime);
        dateTextView.setText(currentDate);
    }

    private void fetchWeatherData() {
        String cityName = "London";
        String apiKey = "d1e8b028a9ee35eda3efc1ec1f95768f";
        String units = "metric";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherApi weatherApi = retrofit.create(WeatherApi.class);
        Call<WeatherResponse> call = weatherApi.getWeather(cityName, apiKey, units);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();
                    String weatherText = weatherResponse.main.temp + "C " + weatherResponse.weather.get(0).description;
                    weatherTextView.setText(weatherText);
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                weatherTextView.setText("Error fetching weather data");
            }
        });
    }

    private void initializeKeywordSynonyms() {
        keywordSynonyms = new HashMap<>();
        Set<String> navigationSynonyms = new HashSet<>();
        navigationSynonyms.add("navigate");
        navigationSynonyms.add("navigation");
        navigationSynonyms.add("directions");
        navigationSynonyms.add("route");

        Set<String> sosSynonyms = new HashSet<>();
        sosSynonyms.add("sos");
        sosSynonyms.add("help");
        sosSynonyms.add("emergency");


        Set<String> CallSynonyms = new HashSet<>();
        CallSynonyms.add("call");
        CallSynonyms.add("phone");
        CallSynonyms.add("telephone");

        Set<String> flashlightOnSynonyms = new HashSet<>();
        flashlightOnSynonyms.add("lumos");
        flashlightOnSynonyms.add("on");
        flashlightOnSynonyms.add("flashlight on");
        flashlightOnSynonyms.add("flash on");

        Set<String> flashlightOffSynonyms = new HashSet<>();
        flashlightOffSynonyms.add("nox");
        flashlightOffSynonyms.add("Knox");
        flashlightOffSynonyms.add("off");
        flashlightOffSynonyms.add("flashlight off");
        flashlightOffSynonyms.add("flash off");

        Set<String> PillSynonyms = new HashSet<>();
        PillSynonyms.add("pill");
        PillSynonyms.add("pills");
        PillSynonyms.add("my pills");
        PillSynonyms.add("medicine");
        PillSynonyms.add("meds");

        Set<String> infoSynonyms = new HashSet<>();
        infoSynonyms.add("info");
        infoSynonyms.add("information");
        infoSynonyms.add("help me");
        infoSynonyms.add("what to do");

        keywordSynonyms.put("navigation", navigationSynonyms);
        keywordSynonyms.put("sos", sosSynonyms);
        keywordSynonyms.put("call", CallSynonyms);
        keywordSynonyms.put("flashlight_on", flashlightOnSynonyms);
        keywordSynonyms.put("flashlight_off", flashlightOffSynonyms);
        keywordSynonyms.put("pills", PillSynonyms);
        keywordSynonyms.put("info", infoSynonyms);
    }

    private void initializeSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Toast.makeText(MainActivity5.this, "Please start speaking...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBeginningOfSpeech() {
                Toast.makeText(MainActivity5.this, "Listening...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                // Optionally update a UI element to reflect the current sound level
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                // Optionally process the raw audio data
            }

            @Override
            public void onEndOfSpeech() {
                Toast.makeText(MainActivity5.this, "Processing...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int error) {
                Toast.makeText(MainActivity5.this, "Speech recognition error: " + error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null) {
                    String recognizedText = matches.get(0);
                    recognizedTextView.setText(recognizedText);

                    for (String command : matches) {
                        if (containsKeyword(command.toLowerCase(), "navigation")) {
                            navigateButton.performClick();
                            break;
                        } else if (containsKeyword(command.toLowerCase(), "sos")) {
                            panicButton.performClick();
                            break;
                        } else if (containsKeyword(command.toLowerCase(), "call")) {
                            fastCallButton.performClick();
                            break;
                        } else if (containsKeyword(command.toLowerCase(), "flashlight_on")) {
                            turnOnFlashlight();
                            break;
                        } else if (containsKeyword(command.toLowerCase(), "flashlight_off")) {
                            turnOffFlashlight();
                            break;
                        } else if (containsKeyword(command.toLowerCase(), "pills")) {
                            myPillsButton.performClick();
                            break;
                        }
                        else if (containsKeyword(command.toLowerCase(), "info")) {
                            info.performClick();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                ArrayList<String> partialMatches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (partialMatches != null) {
                    recognizedTextView.setText(partialMatches.get(0));
                }
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                // Optionally handle events
            }
        });
    }

    private boolean containsKeyword(String command, String keyword) {
        Set<String> synonyms = keywordSynonyms.get(keyword);
        if (synonyms != null) {
            for (String synonym : synonyms) {
                if (command.contains(synonym)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void startListening() {
        if (audioPermissionGranted) {
            speechRecognizer.startListening(speechRecognizerIntent);
        } else {
            Toast.makeText(this, "Audio permission not granted", Toast.LENGTH_SHORT).show();
        }
    }
    private void turnOnFlashlight() {
        CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId, true);
            }
            isFlashlightOn = true;
            flashButton.setText("Turn Off");
            flashButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_orange_light));
        } catch (CameraAccessException e) {
            Toast.makeText(this, "Error turning on flashlight", Toast.LENGTH_SHORT).show();
        }
    }

    private void turnOffFlashlight() {
        CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId, false);
            }
            isFlashlightOn = false;
            flashButton.setText("Turn On");
            flashButton.setBackgroundColor(Color.parseColor("#01579b"));
        } catch (CameraAccessException e) {
            Toast.makeText(this, "Error turning off flashlight", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MANAGE_PILLS_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                morningPills = data.getParcelableArrayListExtra("morningPills");
                afternoonPills = data.getParcelableArrayListExtra("afternoonPills");
                eveningPills = data.getParcelableArrayListExtra("eveningPills");
            }
        }
    }
}

