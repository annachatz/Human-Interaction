//package com.example.myapplication;
//
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//
//import java.io.BufferedReader;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.InputStreamReader;
//import java.lang.reflect.Type;
//import java.util.ArrayList;
//import java.util.List;
//
//public class FavoritesActivity extends AppCompatActivity {
//
//    private RecyclerView recyclerView;
//    private ContactAdapter contactAdapter;
//    private static final String FILE_NAME = "favorites_data.txt";
//    private List<Contact> favoriteList;
//    private List<Contact> allContacts;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_favorites);
//
//        favoriteList = new ArrayList<>();
//        loadFavFromFile();
//
//        recyclerView = findViewById(R.id.recyclerViewFavorites);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        Intent intent = getIntent();
//        if (intent != null && intent.hasExtra("contact_list")) {
//            allContacts = (List<Contact>) intent.getSerializableExtra("contact_list");
//            for (Contact contact : allContacts) {
//                if (contact.isFavorite()) {
//                    favoriteList.add(contact);
//                }
//            }
//        }
//
//        contactAdapter = new ContactAdapter(favoriteList, new ContactAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(Contact contact) {
//                showContactDetailsDialog(contact);
//            }
//
//            @Override
//            public void onDeleteClick(int position) {
//                favoriteList.remove(position);
//                contactAdapter.notifyItemRemoved(position);
//            }
//
//            @Override
//            public void onFavoriteClick(int position) {
//                Contact contact = favoriteList.get(position);
//                contact.setFavorite(!contact.isFavorite());
//                favoriteList.remove(position);
//                contactAdapter.notifyItemRemoved(position);
//            }
//        });
//
//        recyclerView.setAdapter(contactAdapter);
//
//        setupBottomNavigation();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        saveFavsToFile();
//    }
//
//    private void saveFavsToFile() {
//        try (FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE)) {
//            Gson gson = new Gson();
//            String favoritesJson = gson.toJson(favoriteList);
//            fos.write(favoritesJson.getBytes());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void loadFavFromFile() {
//        try (FileInputStream fis = openFileInput(FILE_NAME);
//             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {
//            Gson gson = new Gson();
//            StringBuilder stringBuilder = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                stringBuilder.append(line);
//            }
//            String favoritesJson = stringBuilder.toString();
//            Type type = new TypeToken<ArrayList<Contact>>() {}.getType();
//            favoriteList = gson.fromJson(favoritesJson, type);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        if (favoriteList == null) {
//            favoriteList = new ArrayList<>();
//        }
//    }
//
//    private void setupBottomNavigation() {
//        findViewById(R.id.btnContacts).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(FavoritesActivity.this, ContactListActivity.class);
//                intent.putExtra("contact_list", new ArrayList<>(allContacts));
//                startActivity(intent);
//            }
//        });
//
//        findViewById(R.id.btnFavorites).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Current Activity, do nothing
//            }
//        });
//
//        findViewById(R.id.btnKeypad).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(FavoritesActivity.this, KeypadActivity.class);
//                intent.putExtra("contact_list", new ArrayList<>(allContacts));
//                startActivity(intent);
//            }
//        });
//    }
//
//    private void showContactDetailsDialog(Contact contact) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View dialogView = inflater.inflate(R.layout.dialog_contact_details, null);
//        builder.setView(dialogView);
//
//        TextView textViewContactName = dialogView.findViewById(R.id.textViewContactName);
//        TextView textViewContactPhone = dialogView.findViewById(R.id.textViewContactPhone);
//        Button buttonCallContact = dialogView.findViewById(R.id.buttonCallContact);
//        Button buttonCloseDialog = dialogView.findViewById(R.id.buttonCloseDialog);
//
//        textViewContactName.setText(contact.getName());
//        textViewContactPhone.setText(contact.getPhoneNumber());
//
//        AlertDialog dialog = builder.create();
//
//        buttonCallContact.setOnClickListener(v -> {
//            String phone = contact.getPhoneNumber();
//            Intent intent = new Intent(Intent.ACTION_CALL);
//            intent.setData(Uri.parse("tel:" + phone));
//            startActivity(intent);
//        });
//
//        buttonCloseDialog.setOnClickListener(v -> dialog.dismiss());
//
//        dialog.show();
//    }
//}
package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private List<Contact> favoriteList;

    private List<Contact> allContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        ImageButton menu = findViewById(R.id.menu_button);

        favoriteList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("contact_list")) {
            allContacts = (List<Contact>) intent.getSerializableExtra("contact_list");
            for (Contact contact : allContacts) {
                if (contact.isFavorite()) {
                    Log.w("FFFFFFFF","FFFFFFFF");
                    favoriteList.add(contact);
                }
            }
        }
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FavoritesActivity.this, MainActivity5.class);

                startActivity(intent);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerViewFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        contactAdapter = new ContactAdapter(favoriteList, new ContactAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Contact contact) {
                showContactDetailsDialog(contact);
            }

            @Override
            public void onDeleteClick(int position) {
                favoriteList.remove(position);
                contactAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onFavoriteClick(int position) {
                Contact contact = favoriteList.get(position);
                contact.setFavorite(!contact.isFavorite());
                favoriteList.remove(position);
                contactAdapter.notifyItemRemoved(position);
            }
        });

        recyclerView.setAdapter(contactAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        findViewById(R.id.btnContacts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FavoritesActivity.this, ContactListActivity.class);
                intent.putExtra("contact_list", new ArrayList<>(allContacts));
                startActivity(intent);
            }
        });

        findViewById(R.id.btnFavorites).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {}
        });

        findViewById(R.id.btnKeypad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FavoritesActivity.this, KeypadActivity.class);
                intent.putExtra("contact_list", new ArrayList<>(allContacts));
                startActivity(intent);
            }
        });
    }

    private void showContactDetailsDialog(Contact contact) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_contact_details, null);
        builder.setView(dialogView);

        TextView textViewContactName = dialogView.findViewById(R.id.textViewContactName);
        TextView textViewContactPhone = dialogView.findViewById(R.id.textViewContactPhone);
        Button buttonCallContact = dialogView.findViewById(R.id.buttonCallContact);
        Button buttonCloseDialog = dialogView.findViewById(R.id.buttonCloseDialog);

        textViewContactName.setText(contact.getName());
        textViewContactPhone.setText(contact.getPhoneNumber());

        AlertDialog dialog = builder.create();

        buttonCallContact.setOnClickListener(v -> {
            String phone = contact.getPhoneNumber();
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phone));
            startActivity(intent);
        });

        buttonCloseDialog.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

}





