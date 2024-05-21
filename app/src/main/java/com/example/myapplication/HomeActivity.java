package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.Train;
import com.example.myapplication.adapter.TrainAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView trainRecyclerView;
    private TrainAdapter trainAdapter;
    private List<Train> trainList;
    private FirebaseFirestore db;
    private DatabaseReference reference;
    Spinner fromSpinner;
    Spinner toSpinner;
    EditText departureTimeEditText;
    EditText arrivalTimeEditText;
    Button searchButton;
    private SharedPreferences sharedPreferences;
    private int hour, minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

        trainList = new ArrayList<>();
        trainAdapter = new TrainAdapter(trainList, this);

        trainRecyclerView = findViewById(R.id.trainRecyclerView);
        fromSpinner = findViewById(R.id.from_Spinner);
        toSpinner = findViewById(R.id.to_Spinner);
         departureTimeEditText = findViewById(R.id.departure_edit_text);
         arrivalTimeEditText = findViewById(R.id.arrival_edit_text);
         searchButton = findViewById(R.id.seach_btn);
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        trainRecyclerView.setLayoutManager(layoutManager);
        trainRecyclerView.setAdapter(trainAdapter);


        searchButton.setOnClickListener(view -> searchTrains());
        // Fetch train data from Firestore


        departureTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get current time
                final Calendar c = Calendar.getInstance();
                hour = c.get(Calendar.HOUR_OF_DAY);
                minute = c.get(Calendar.MINUTE);

                // Create a time picker dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(HomeActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // Set selected time to the EditText
                                departureTimeEditText.setText(String.format("%02d:%02d", hourOfDay, minute));
                            }
                        }, hour, minute, false); // false for 12-hour format

                // Show the time picker dialog
                timePickerDialog.show();
            }
        });

        arrivalTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get current time
                final Calendar c = Calendar.getInstance();
                hour = c.get(Calendar.HOUR_OF_DAY);
                minute = c.get(Calendar.MINUTE);

                // Create a time picker dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(HomeActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // Set selected time to the EditText
                                arrivalTimeEditText.setText(String.format("%02d:%02d", hourOfDay, minute));
                            }
                        }, hour, minute, false); // false for 12-hour format

                // Show the time picker dialog
                timePickerDialog.show();
            }
        });


        fetchTrainData();
    }


    private void searchTrains() {
        try {
            // Get the selected items from the Spinners
            String fromStation = String.valueOf(fromSpinner.getSelectedItemPosition());
            String toStation = String.valueOf(toSpinner.getSelectedItemPosition());
            String departureTime = departureTimeEditText.getText().toString().trim();
            String arrivalTime = arrivalTimeEditText.getText().toString().trim();

            List<Train> filteredList = new ArrayList<>();
            for (Train train : trainList) {
                // Implement your filtering logic here
                // This example filters trains based on all 4 criteria
                if (Objects.equals(train.getOriginStation(), fromStation) ||
                        Objects.equals(train.getDestinationStation(), toStation) ||
                        train.getDepartureTime().equals(departureTime) ||
                        train.getArrivalTime().equals(arrivalTime)) {
                    filteredList.add(train);
                }
            }
            trainAdapter.updateTrainList(filteredList);
            if (filteredList.isEmpty()) {
                Toast.makeText(HomeActivity.this, "No trains found matching your search criteria", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception exception) {
            Log.d("Train Error: ______________________>", exception.toString());
        }
    }
    private void fetchTrainData() {
        reference.child("Trains").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clear the train list before populating it with new data
                trainList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot trainSnapshot : snapshot.getChildren()) {
                        Train train = trainSnapshot.getValue(Train.class);
                        if (train != null) {
                            trainList.add(train);
                        }
                    }
                    // Update the adapter after data is retrieved
                    trainAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(HomeActivity.this, "No train data found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Error fetching train data: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showMenu(View view) {
        // Create a PopupMenu
        PopupMenu popupMenu = new PopupMenu(this, view);

        // Inflate the menu from XML
        popupMenu.getMenuInflater().inflate(R.menu.menu_main, popupMenu.getMenu());

        // Set item click listener for the menu items
        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_profile) {
                // Handle menu item 1 click
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                return true;
            } else if (itemId == R.id.menu_viewBookings) {
                // Handle menu item 2 click
                startActivity(new Intent(HomeActivity.this, view_booking.class));
                return true;
            } else if (itemId == R.id.menu_logout) {
                removeUserIdFromPreferences();
                // Handle logout click
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                return true;
            } else if (itemId == R.id.menu_about) {
                // Start AboutActivity when "About" menu item is clicked
                startActivity(new Intent(HomeActivity.this, AboutActivity.class));
                return true;
            } else {
                return false;
            }
        });
        // Show the popup menu
        popupMenu.show();
    }
    private String getUserIdFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("user_id", "");
    }
    private void removeUserIdFromPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("user_id");
        editor.apply();
    }
}
