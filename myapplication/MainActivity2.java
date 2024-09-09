//package com.example.myapplication;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//public class MainActivity2 extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.dris);
//
//        Button sendEmailButton = findViewById(R.id.sendEmailButton);
//        sendEmailButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendEmail();
//            }
//        });
//    }
//
//    private void sendEmail() {
//        Intent intent = new Intent(Intent.ACTION_SEND);
//        intent.setType("message/rfc822");
//        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"annachatzipap@gmail.com"});
//        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
//        intent.putExtra(Intent.EXTRA_TEXT, "Hello");
//
//        try {
//            startActivity(Intent.createChooser(intent, "Send email using..."));
//        } catch (android.content.ActivityNotFoundException ex) {
//            Toast.makeText(this, "No email client found", Toast.LENGTH_SHORT).show();
//        }
//    }
//}
