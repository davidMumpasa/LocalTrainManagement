package com.example.myapplication.Model;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.HomeActivity;
import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class AddTrainActivity extends AppCompatActivity {
    private EditText editTextTrainId;
    private EditText editTextTrainName;
    private Spinner editTextOriginId;
    private Spinner editTextDestId;
    private EditText editTextDepartureTime;
    private EditText editTextArrivalTime;
    private EditText editTextCapacity;
    private EditText editTextPrice;
    private Button buttonAddTrain;
    private int hour, minute;

    private DatabaseReference mDatabase;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_train);

        editTextTrainName = findViewById(R.id.editTextTrainName);
        editTextOriginId = findViewById(R.id.spinnerOriginId);
        editTextDestId = findViewById(R.id.SpinnerDestId);
        editTextDepartureTime = findViewById(R.id.editTextDepartureTime);
        editTextArrivalTime = findViewById(R.id.editTextArrivalTime);
        editTextCapacity = findViewById(R.id.editTextCapacity);
        editTextPrice = findViewById(R.id.editTextPrice);
        buttonAddTrain = findViewById(R.id.buttonAddTrain);

        // Initialize Firebase database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        editTextDepartureTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get current time
                final Calendar c = Calendar.getInstance();
                hour = c.get(Calendar.HOUR_OF_DAY);
                minute = c.get(Calendar.MINUTE);

                // Create a time picker dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddTrainActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // Set selected time to the EditText
                                editTextDepartureTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                            }
                        }, hour, minute, false); // false for 12-hour format

                // Show the time picker dialog
                timePickerDialog.show();
            }
        });
        editTextArrivalTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get current time
                final Calendar c = Calendar.getInstance();
                hour = c.get(Calendar.HOUR_OF_DAY);
                minute = c.get(Calendar.MINUTE);

                // Create a time picker dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddTrainActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // Set selected time to the EditText
                                editTextArrivalTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                            }
                        }, hour, minute, false); // false for 12-hour format

                // Show the time picker dialog
                timePickerDialog.show();
            }
        });

        // Set click listener for Add Train button
        buttonAddTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTrain();
            }
        });
    }

    private void addTrain() {
        String trainName = editTextTrainName.getText().toString();
        String originName = String.valueOf(editTextOriginId.getSelectedItemPosition());
        String destName = String.valueOf(editTextDestId.getSelectedItemPosition());
        String departureTime = editTextDepartureTime.getText().toString();
        String arrivalTime = editTextArrivalTime.getText().toString();
        String capacity = editTextCapacity.getText().toString();
        String price = editTextPrice.getText().toString();

        if (trainName.isEmpty() || originName.isEmpty() || destName.isEmpty() ||
                departureTime.isEmpty() || arrivalTime.isEmpty() || capacity.isEmpty() || price.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the train already exists
        Query query = mDatabase.child("Trains").orderByChild("trainName").equalTo(trainName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Train with the same name already exists
                    Toast.makeText(AddTrainActivity.this, "Train already exists", Toast.LENGTH_SHORT).show();
                } else {
                    // Train does not exist, proceed with adding it to the database
                    Train train = new Train();
                    train.setTrainName(trainName);
                    train.setOriginStation(originName);
                    train.setDestinationStation(destName);
                    train.setDepartureTime(departureTime);
                    train.setArrivalTime(arrivalTime);
                    train.setCapacity(Integer.parseInt(capacity));
                    train.setPrice(price);


                    // Add train to Firebase database
                    mDatabase.child("Trains").push().setValue(train)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(AddTrainActivity.this, "Train added successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(AddTrainActivity.this, "Failed to add train: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddTrainActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
