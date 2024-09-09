package com.example.myapplication;
import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity4 extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 50;
    private boolean hasFlash;
    private boolean isFlashOn = false;
    private Camera camera;
    private Camera.Parameters params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button flashButton = findViewById(R.id.flashlight);

        hasFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!hasFlash) {
            Toast.makeText(this, "No flash available on your device", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isEnabled = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;

        if (!isEnabled) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
        }

        flashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity4.this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    toggleFlashlight();
                } else {
                    ActivityCompat.requestPermissions(MainActivity4.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
                }
            }
        });
    }

    private void getCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
                params = camera.getParameters();
            } catch (RuntimeException e) {
                Log.e("Camera Error", "Camera failed to open: " + e.getMessage());
            }
        }
    }

    private void toggleFlashlight() {
        if (camera == null || params == null) {
            getCamera();
            if (camera == null || params == null) {
                Toast.makeText(this, "Failed to access camera", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        try {
            if (isFlashOn) {
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(params);
                camera.stopPreview();
                isFlashOn = false;
                Toast.makeText(this, "Flashlight turned off", Toast.LENGTH_SHORT).show();
            } else {
                params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(params);
                camera.startPreview();
                isFlashOn = true;
                Toast.makeText(this, "Flashlight turned on", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("Flashlight Error", "Failed to toggle flashlight: " + e.getMessage());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
