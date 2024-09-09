package com.example.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ContactListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private List<Contact> contactList;
    private FloatingActionButton fabAdd, fabDeleteMode;
    private boolean deleteMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        contactList = new ArrayList<>();

        // Hardcode contacts for demo
        contactList.add(new Contact("Dimitris", "0000000000"));
        contactList.add(new Contact("Anna", "1234567890"));
        contactList.add(new Contact("Mixalis", "9876543210"));

        recyclerView = findViewById(R.id.recyclerView);
        fabAdd = findViewById(R.id.fab_add);
        fabDeleteMode = findViewById(R.id.fab_delete_mode);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("contact_list")) {
            contactList = (List<Contact>) intent.getSerializableExtra("contact_list");
        }

        contactAdapter = new ContactAdapter(contactList, new ContactAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Contact contact) {
                showContactDetailsDialog(contact);
            }

            @Override
            public void onDeleteClick(int position) {
                contactList.remove(position);
                contactAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onFavoriteClick(int position) {
                Contact contact = contactList.get(position);
                contact.setFavorite(!contact.isFavorite());
                contactAdapter.notifyItemChanged(position);
                Toast.makeText(ContactListActivity.this, contact.isFavorite() ? "Added to favorites!" : "Removed from favorites!", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(contactAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddContactDialog();
            }
        });

        fabDeleteMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMode = !deleteMode;
                contactAdapter.setDeleteMode(deleteMode);
                if (deleteMode) {
                    fabDeleteMode.setImageResource(R.drawable.ic_delete);
                    fabAdd.hide();
                } else {
                    fabDeleteMode.setImageResource(R.drawable.ic_delete);
                    fabAdd.show();
                }
            }
        });

        setupBottomNavigation();
    }

    private void showAddContactDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_contact, null);
        dialogBuilder.setView(dialogView);

        EditText editTextName = dialogView.findViewById(R.id.edit_text_name);
        EditText editTextNumber = dialogView.findViewById(R.id.edit_text_number);

        dialogBuilder.setTitle("Add Contact");
        dialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String name = editTextName.getText().toString().trim();
                String number = editTextNumber.getText().toString().trim();

                if (!name.isEmpty() && !number.isEmpty() && number.length() == 10) {
                    contactList.add(new Contact(name, number));
                    contactAdapter.notifyItemInserted(contactList.size() - 1);
                } else {
                    Toast.makeText(ContactListActivity.this, "Please enter a 10-digit number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialogBuilder.setNegativeButton("Cancel", null);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
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

        AlertDialog dialog = builder.create();  // Declare and initialize the dialog before using it

        buttonCallContact.setOnClickListener(v -> {
            String phone = contact.getPhoneNumber();
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phone));
            startActivity(intent);
        });

        buttonCloseDialog.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }




    private void setupBottomNavigation() {
        findViewById(R.id.btnContacts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        findViewById(R.id.btnFavorites).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactListActivity.this, FavoritesActivity.class);
                intent.putExtra("contact_list", new ArrayList<>(contactList));
                startActivity(intent);
            }
        });

        findViewById(R.id.btnKeypad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactListActivity.this, KeypadActivity.class);
                intent.putExtra("contact_list", new ArrayList<>(contactList));
                startActivity(intent);
            }
        });
    }
}
