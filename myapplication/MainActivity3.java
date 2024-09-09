//package com.example.myapplication;
//
//import android.Manifest;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//
//public class MainActivity3 extends AppCompatActivity {
//
//    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
//    private boolean locationPermissionGranted;
//    private FusedLocationProviderClient fusedLocationProviderClient;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.dris);
//
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//
//        Button sendEmailButton = findViewById(R.id.sendEmailButton);
//        sendEmailButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getLocationAndSendEmail();
//            }
//        });
//
//        getLocationPermission();
//    }
//
//    private void getLocationPermission() {
//        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            locationPermissionGranted = true;
//        } else {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//        }
//    }
//
//
//
//    private void getLocationAndSendEmail() {
//        if (locationPermissionGranted) {
//            try {
//                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
//                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Location> task) {
//                        if (task.isSuccessful() && task.getResult() != null) {
//                            Location location = task.getResult();
//                            double latitude = location.getLatitude();
//                            double longitude = location.getLongitude();
//                            sendEmail(latitude, longitude);
//                        } else {
//                            Toast.makeText(MainActivity3.this, "Unable to get location", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            } catch (SecurityException e) {
//                e.printStackTrace();
//            }
//        } else {
//            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void sendEmail(double latitude, double longitude) {
//        String locationUrl = "https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude;
//        String message = "Current location: " +
//                "Latitude: " + latitude +
//                ", Longitude: " + longitude +
//                "\n\nOpen location in Google Maps: " + locationUrl;
//
//        Intent intent = new Intent(Intent.ACTION_SEND);
//        intent.setType("message/rfc822");
//        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"annachatzipap@gmail.com", "recipient2@example.com"});
//        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
//        intent.putExtra(Intent.EXTRA_TEXT, message);
//
//        try {
//            startActivity(Intent.createChooser(intent, "Send email using..."));
//        } catch (android.content.ActivityNotFoundException ex) {
//            Toast.makeText(this, "No email client found", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        locationPermissionGranted = false;
//        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
//            if (grantResults.length > 0
//                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                locationPermissionGranted = true;
//            }
//        }
//    }
//}
