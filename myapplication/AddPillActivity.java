package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.Models.Pill;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddPillActivity extends AppCompatActivity {

    private EditText pillNameEditText;
    private EditText timesPerDayEditText;
    private LinearLayout hoursContainer;
    private TextView errorMessage;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pill);

        pillNameEditText = findViewById(R.id.pill_name);
        timesPerDayEditText = findViewById(R.id.times_per_day);
        hoursContainer = findViewById(R.id.hours_container);
        errorMessage = findViewById(R.id.error_message);
        submitButton = findViewById(R.id.submit_button);
        Button cancelButton = findViewById(R.id.cancel_button);

        timesPerDayEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validateTimesPerDay();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSubmit();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // Close the activity
            }
        });
    }

    private void validateTimesPerDay() { // To keep the user from entering an unreasonable amount of pills for a day
        hoursContainer.removeAllViews();
        String timesPerDayStr = timesPerDayEditText.getText().toString();
        if (!timesPerDayStr.isEmpty()) {
            int timesPerDay = Integer.parseInt(timesPerDayStr);
            if (timesPerDay > 5) {
                showError("The number of times per day cannot exceed 5.");
                submitButton.setEnabled(false);
            } else {
                errorMessage.setVisibility(View.GONE);
                submitButton.setEnabled(true);
                updateHourFields(timesPerDay);
            }
        } else {
            errorMessage.setVisibility(View.GONE);
            submitButton.setEnabled(false);
        }
    }

    private void updateHourFields(int timesPerDay) { // To provide the user with the wanted amount of hours to fill in
        for (int i = 0; i < timesPerDay; i++) {
            EditText hourEditText = new EditText(this);
            hourEditText.setHint("Hour " + (i + 1));
            hourEditText.setFocusable(false);
            hourEditText.setClickable(true);
            hourEditText.setBackgroundColor(Color.parseColor("#E8FFFE"));
            hourEditText.setHintTextColor(Color.parseColor("#000000"));
            hourEditText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            hourEditText.setTextSize(40);
            hourEditText.setTextColor(Color.parseColor("#000000"));
            hourEditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showTimePickerDialog((EditText) view);
                }
            });
            hoursContainer.addView(hourEditText);
        }
    }

    private void showTimePickerDialog(final EditText editText) { // To fill in the hour
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        @SuppressLint("DefaultLocale") TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute1) -> editText.setText(String.format("%02d:%02d", hourOfDay, minute1)),
                hour, minute, true);
        timePickerDialog.show();
    }

    private void showError(String message) { // In case an error occurs during the creation of a pill event
        errorMessage.setText(message);
        errorMessage.setVisibility(View.VISIBLE);
    }

    private void handleSubmit() { // The actions to validate the information and pass it to the main activity to be added in the lists
        String pillName = pillNameEditText.getText().toString();
        String timesPerDayStr = timesPerDayEditText.getText().toString();
        if (pillName.isEmpty()) {
            showError("Pill name cannot be empty.");
            return;
        }
        if (timesPerDayStr.isEmpty()) {
            showError("Please enter the number of times per day.");
            return;
        }

        int timesPerDay = Integer.parseInt(timesPerDayStr);
        if (timesPerDay > 5) {
            showError("The number of times per day cannot exceed 5.");
            return;
        }

        List<Pill> morningPills = new ArrayList<>();
        List<Pill> afternoonPills = new ArrayList<>();
        List<Pill> eveningPills = new ArrayList<>();

        for(int i = 0; i < hoursContainer.getChildCount(); i++){
            EditText timeEditText = (EditText) hoursContainer.getChildAt(i);
            String time = timeEditText.getText().toString();
            if (time.isEmpty()) {
                showError("All time fields must be filled.");
                return;
            }
        }

        for (int i = 0; i < hoursContainer.getChildCount(); i++) {
            EditText timeEditText = (EditText) hoursContainer.getChildAt(i);
            String time = timeEditText.getText().toString();

            int hour = Integer.parseInt(time.split(":")[0]);

            if (hour >= 5 && hour < 12) {
                morningPills.add(new Pill(pillName,time));
            } else if (hour >= 12 && hour < 18) {
                afternoonPills.add(new Pill(pillName,time));
            } else {
                eveningPills.add(new Pill(pillName,time));
            }
        }

        // Pass the data back to MainActivity
        Intent resultIntent = new Intent();
        resultIntent.putParcelableArrayListExtra("morningPills", new ArrayList<>(morningPills));
        resultIntent.putParcelableArrayListExtra("afternoonPills", new ArrayList<>(afternoonPills));
        resultIntent.putParcelableArrayListExtra("eveningPills", new ArrayList<>(eveningPills));
        setResult(RESULT_OK, resultIntent);
        finish(); // Close the activity
    }
}