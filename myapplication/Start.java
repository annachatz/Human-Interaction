package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.FaceLandmark;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.controls.Facing;

import java.util.ArrayList;
import java.util.List;

public class Start extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private CameraView cameraView;
    private List<Bitmap> referenceImages;
    private FaceDetector faceDetector;
    private static final String TAG = "Start";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        cameraView = findViewById(R.id.cameraView);
        cameraView.setLifecycleOwner(this);

        // Load reference images
        referenceImages = new ArrayList<>();
        referenceImages.add(BitmapFactory.decodeResource(getResources(), R.drawable.anna));
        referenceImages.add(BitmapFactory.decodeResource(getResources(), R.drawable.anna2));  // Add more reference images as needed
        referenceImages.add(BitmapFactory.decodeResource(getResources(), R.drawable.anna3));
        referenceImages.add(BitmapFactory.decodeResource(getResources(), R.drawable.anna4));
        referenceImages.add(BitmapFactory.decodeResource(getResources(), R.drawable.anna5));
        referenceImages.add(BitmapFactory.decodeResource(getResources(), R.drawable.anna6));

        // Set up face detector
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build();
        faceDetector = FaceDetection.getClient(options);

        // Check camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            startCamera();
        }
    }

    private void startCamera() {
        cameraView.setFacing(Facing.FRONT); // Set the camera to use the front-facing camera
        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(@NonNull PictureResult result) {
                result.toBitmap(bitmap -> {
                    if (bitmap != null) {
                        processImage(bitmap);
                    }
                });
            }
        });

        // Start the camera preview and take a picture after a short delay
        cameraView.open();
        cameraView.postDelayed(() -> cameraView.takePicture(), 2000);
    }

    private void processImage(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);

        faceDetector.process(image)
                .addOnSuccessListener(faces -> {
                    if (faces.isEmpty()) {
                        // No faces detected
                        Toast.makeText(this, "No faces detected", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Compare with each reference image
                    for (Bitmap referenceImage : referenceImages) {
                        InputImage refImage = InputImage.fromBitmap(referenceImage, 0);

                        faceDetector.process(refImage)
                                .addOnSuccessListener(refFaces -> {
                                    if (isMatchingFace(faces, refFaces)) {
                                        // Face matched, proceed to the main app
                                        openMainApp();
                                        return;
                                    }
                                })
                                .addOnFailureListener(e -> Log.e(TAG, "Reference face detection failed", e));
                    }

                    Toast.makeText(this, "No matching face found", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Log.e(TAG, "Image face detection failed", e));
    }

    private boolean isMatchingFace(List<Face> faces, List<Face> refFaces) {
        if (!faces.isEmpty() && !refFaces.isEmpty()) {
            Face face = faces.get(0);
            Face refFace = refFaces.get(0);

            // Log bounding boxes and landmarks
            Log.d(TAG, "Face BoundingBox: " + face.getBoundingBox().toString());
            Log.d(TAG, "Reference Face BoundingBox: " + refFace.getBoundingBox().toString());
            Log.d(TAG, "Face Landmarks: " + face.getAllLandmarks().size());
            Log.d(TAG, "Reference Face Landmarks: " + refFace.getAllLandmarks().size());

            // Normalize bounding boxes
            Rect normalizedFaceBox = normalizeBoundingBox(face.getBoundingBox(), face.getBoundingBox().width(), face.getBoundingBox().height());
            Rect normalizedRefBox = normalizeBoundingBox(refFace.getBoundingBox(), refFace.getBoundingBox().width(), refFace.getBoundingBox().height());

            boolean boundingBoxMatch = compareBoundingBoxes(normalizedFaceBox, normalizedRefBox, 0.1f);
            boolean landmarksMatch = compareLandmarks(face, refFace, 0.1f);

            return boundingBoxMatch && landmarksMatch;
        }
        return false;
    }
    private boolean compareLandmarks(Face face, Face refFace, float tolerance) {
        for (FaceLandmark landmark : face.getAllLandmarks()) {
            FaceLandmark refLandmark = refFace.getLandmark(landmark.getLandmarkType());
            if (refLandmark == null) {
                return false;
            }

            float xDiff = Math.abs(landmark.getPosition().x - refLandmark.getPosition().x);
            float yDiff = Math.abs(landmark.getPosition().y - refLandmark.getPosition().y);

            if (xDiff > tolerance || yDiff > tolerance) {
                return false;
            }
        }
        return true;
    }
    private boolean compareBoundingBoxes(Rect box1, Rect box2, float tolerance) {
        float widthDiff = Math.abs(box1.width() - box2.width()) / (float) box1.width();
        float heightDiff = Math.abs(box1.height() - box2.height()) / (float) box1.height();

        return widthDiff < tolerance && heightDiff < tolerance;
    }
    private Rect normalizeBoundingBox(Rect box, int imageWidth, int imageHeight) {
        int left = box.left / imageWidth;
        int top = box.top / imageHeight;
        int right = box.right / imageWidth;
        int bottom = box.bottom / imageHeight;

        return new Rect(left, top, right, bottom);
    }

    private void openMainApp() {
        // Intent to open the main part of your app
        // startActivity(new Intent(this, MainAppActivity.class));
        Toast.makeText(this, "Face matched! Opening main app...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            Toast.makeText(this, "Camera permission is required", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        faceDetector.close();
    }
}


