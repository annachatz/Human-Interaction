package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

public class KeypadActivity extends AppCompatActivity {


    static int PERMISSION_CODE = 100;
    private TextView tvPhoneNumber;
    private String phoneNumber = "";

    private List<Contact> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keypad);

        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        ImageButton menu = findViewById(R.id.menu_button);


        if(ContextCompat.checkSelfPermission(KeypadActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(KeypadActivity.this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_CODE);
        }

        findViewById(R.id.buttonDial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);

                intent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(intent);
            }
        });
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KeypadActivity.this, MainActivity5.class);

                startActivity(intent);
            }
        });

        findViewById(R.id.buttonBackspace).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackspaceClick();
            }
        });


        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("contact_list")) {
            contactList = (List<Contact>) intent.getSerializableExtra("contact_list");
        }

        setupBottomNavigation();
    }


    public void onNumberClick(View view) {
        if (phoneNumber.length() < 10) {
            Button button = (Button) view;
            phoneNumber += button.getText().toString();
            tvPhoneNumber.setText(phoneNumber);
        }
    }

    public void onBackspaceClick() {
        if (!phoneNumber.isEmpty()) {
            phoneNumber = phoneNumber.substring(0, phoneNumber.length() - 1);
            tvPhoneNumber.setText(phoneNumber);
        }
    }


    private void setupBottomNavigation() {
        findViewById(R.id.btnContacts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KeypadActivity.this, ContactListActivity.class);
                intent.putExtra("contact_list", new ArrayList<>(contactList));
                startActivity(intent);
            }
        });

        findViewById(R.id.btnFavorites).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KeypadActivity.this, FavoritesActivity.class);
                intent.putExtra("contact_list", new ArrayList<>(contactList));
                startActivity(intent);
            }
        });

        findViewById(R.id.btnKeypad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {}
        });
    }
}
