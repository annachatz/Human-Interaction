package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import java.io.FileInputStream;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.InputStreamReader;
import com.example.myapplication.Models.Pill;
import com.google.gson.Gson;
import java.lang.reflect.Type;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.io.BufferedReader;
import com.google.gson.reflect.TypeToken;
public class ManagePills extends AppCompatActivity {
    private static final String FILE_NAME = "pills_data.txt";
    private static final int ADD_PILL_REQUEST = 1;
    private static final int DELETE_PILL_REQUEST = 2;
    private RecyclerView recyclerView1, recyclerView2, recyclerView3;
    private ListAdapter adapter1, adapter2, adapter3;
    private Button addPillButton, deleteButton;
    private ImageButton home;

    List<Pill> Morning = new ArrayList<>();
    List<Pill> Lunch = new ArrayList<>();
    List<Pill> Dinner = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_pills);
        loadPillsFromFile();
        recyclerView1 = findViewById(R.id.recyclerView1);
        recyclerView2 = findViewById(R.id.recyclerView2);
        recyclerView3 = findViewById(R.id.recyclerView3);
        addPillButton = findViewById(R.id.addPillButton);
        deleteButton = findViewById(R.id.deleteButton);
        home = findViewById(R.id.menu_button);

        // Set up adapters and layout managers
        adapter1 = new ListAdapter(Morning);
        adapter2 = new ListAdapter(Lunch);
        adapter3 = new ListAdapter(Dinner);

        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        recyclerView3.setLayoutManager(new LinearLayoutManager(this));

        recyclerView1.setAdapter(adapter1);
        recyclerView2.setAdapter(adapter2);
        recyclerView3.setAdapter(adapter3);

        addPillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManagePills.this, AddPillActivity.class);
                startActivityForResult(intent, ADD_PILL_REQUEST);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManagePills.this, MainActivity5.class);
                startActivityForResult(intent, ADD_PILL_REQUEST);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagePills.this, DeletePillActivity.class);
                ArrayList<Pill> pills = new ArrayList<Pill>();
                pills.addAll(Morning);
                pills.addAll(Lunch);
                pills.addAll(Dinner);
                intent.putParcelableArrayListExtra("pills", pills);
                startActivityForResult(intent, DELETE_PILL_REQUEST);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!getSystemService(AlarmManager.class).canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }

    }
    protected void onPause() {
        super.onPause();
        // Save pills to file
        savePillsToFile();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_PILL_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                ArrayList<Pill> newMorningPills = data.getParcelableArrayListExtra("morningPills");
                ArrayList<Pill> newAfternoonPills = data.getParcelableArrayListExtra("afternoonPills");
                ArrayList<Pill> newEveningPills = data.getParcelableArrayListExtra("eveningPills");

                if (newMorningPills != null) {
                    Morning.addAll(newMorningPills);
                    removeDuplicatesAndSort(Morning);
                    for (Pill p : Morning) {
                        setPillAlarms(p.getPillName(), p.getTime(), p.getId());
                    }
                    adapter1.notifyDataSetChanged();
                }
                if (newAfternoonPills != null) {
                    Lunch.addAll(newAfternoonPills);
                    removeDuplicatesAndSort(Lunch);
                    for (Pill p : Lunch) {
                        setPillAlarms(p.getPillName(), p.getTime(), p.getId());
                    }
                    adapter2.notifyDataSetChanged();
                }
                if (newEveningPills != null) {
                    Dinner.addAll(newEveningPills);
                    removeDuplicatesAndSort(Dinner);
                    for (Pill p : Dinner) {
                        setPillAlarms(p.getPillName(), p.getTime(), p.getId());
                    }
                    adapter3.notifyDataSetChanged();
                }
            }
        } else if (requestCode == DELETE_PILL_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                ArrayList<Pill> deletedPills = data.getParcelableArrayListExtra("selectedPills");
                if (deletedPills != null) {
                    for (Pill pill : deletedPills) {
                        cancelPillAlarm(pill.getId());
                    }

                    Morning.removeAll(deletedPills);
                    Lunch.removeAll(deletedPills);
                    Dinner.removeAll(deletedPills);
                    adapter1.notifyDataSetChanged();
                    adapter2.notifyDataSetChanged();
                    adapter3.notifyDataSetChanged();

                }
            }
        }
    }
    private void savePillsToFile() {
        try {
            FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            Gson gson = new Gson();

            String morningPillsJson = gson.toJson(Morning);
            String afternoonPillsJson = gson.toJson(Lunch);
            String eveningPillsJson = gson.toJson(Dinner);

            String fileContent = "morningPills:" + morningPillsJson + "\n"
                    + "afternoonPills:" + afternoonPillsJson + "\n"
                    + "eveningPills:" + eveningPillsJson;

            fos.write(fileContent.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void loadPillsFromFile() {
        try {
            FileInputStream fis = openFileInput(FILE_NAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            Gson gson = new Gson();

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("morningPills:")) {
                    String morningPillsJson = line.substring("morningPills:".length());
                    Type type = new TypeToken<ArrayList<Pill>>() {}.getType();
                    Morning = gson.fromJson(morningPillsJson, type);
                } else if (line.startsWith("afternoonPills:")) {
                    String afternoonPillsJson = line.substring("afternoonPills:".length());
                    Type type = new TypeToken<ArrayList<Pill>>() {}.getType();
                    Lunch = gson.fromJson(afternoonPillsJson, type);
                } else if (line.startsWith("eveningPills:")) {
                    String eveningPillsJson = line.substring("eveningPills:".length());
                    Type type = new TypeToken<ArrayList<Pill>>() {}.getType();
                    Dinner = gson.fromJson(eveningPillsJson, type);
                }
            }
            adapter1.notifyDataSetChanged();
            reader.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeDuplicatesAndSort(List<Pill> pills) {
        Set<String> uniquePills = new HashSet<>();
        List<Pill> uniquePillList = new ArrayList<>();

        for (Pill pill : pills) {
            String identifier = pill.getPillName() + pill.getTime();
            if (uniquePills.add(identifier)) {
                uniquePillList.add(pill);
            }
        }

        uniquePillList.sort(new Comparator<Pill>() {
            @Override
            public int compare(Pill p1, Pill p2) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                try {
                    return Objects.requireNonNull(sdf.parse(p1.getTime())).compareTo(sdf.parse(p2.getTime()));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });

        pills.clear();
        pills.addAll(uniquePillList);
    }

    @SuppressLint("ScheduleExactAlarm")
    private void setPillAlarms(String pillName, String time, int id) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(this, "Permission to schedule exact alarms is required", Toast.LENGTH_LONG).show();
                return;
            }
        }

        Calendar calendar = Calendar.getInstance();
        String[] timeParts = time.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        // Set the calendar to the alarm time
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // If the set time has already passed, set the alarm for the next day
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        Intent intent = new Intent(this, PillAlarmReceiver.class);
        intent.putExtra("pillName", pillName);
        intent.putExtra("time", time);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void cancelPillAlarm(int id) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, PillAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.cancel(pendingIntent);
    }

}