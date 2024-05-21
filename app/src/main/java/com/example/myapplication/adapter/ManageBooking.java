package com.example.myapplication.adapter;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Model.Booking;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

public class ManageBooking extends AppCompatActivity {

    private EditText editTextPassName;
    private EditText editTextBookingDate;
    private EditText editTextBookingTime;
    private EditText editTextTrain;
    private EditText editTextSeat;
    private Button editButton;
    private DatabaseReference mDatabase;
    private Button deleteButton;
    String bookingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_booking);

        // Initialize EditText fields and buttons
        editTextPassName = findViewById(R.id.editTextPassName);
        editTextBookingDate = findViewById(R.id.editTextBookingDate);
        editTextBookingTime = findViewById(R.id.editTextBookingTime);
        editTextTrain = findViewById(R.id.editTextTrain);
        editTextSeat = findViewById(R.id.editTextSeat);
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);

        mDatabase = FirebaseDatabase.getInstance().getReference("Bookings");

        // Get booking data from intent extras
        Intent intent = getIntent();
        if (intent != null) {
            bookingId = intent.getStringExtra("bookingId");
            String passName = intent.getStringExtra("passName");
            String bookingDate = intent.getStringExtra("bookingDate");
            String bookingTime = intent.getStringExtra("bookingTime");
            String train = intent.getStringExtra("train");
            String seat = intent.getStringExtra("seat");

            // Populate EditText fields with booking data
            editTextPassName.setText(passName);
            editTextBookingDate.setText(bookingDate);
            editTextBookingTime.setText(bookingTime);
            editTextTrain.setText(train);
            editTextSeat.setText(seat);
        }

        // Set click listeners for edit and delete buttons
        editButton.setOnClickListener(v -> editBooking());
        deleteButton.setOnClickListener(v -> deleteBooking());
    }

    private void editBooking() {
        String updatedPassName = editTextPassName.getText().toString().trim();
        String updatedBookingDate = editTextBookingDate.getText().toString().trim();
        String updatedBookingTime = editTextBookingTime.getText().toString().trim();
        String updatedTrainName = editTextTrain.getText().toString().trim();
        String updatedSeat = editTextSeat.getText().toString().trim();

        // Validate input fields
        if (updatedPassName.isEmpty() || updatedBookingDate.isEmpty() || updatedBookingTime.isEmpty() ||
                updatedTrainName.isEmpty() || updatedSeat.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a HashMap to hold the updated data
        HashMap<String, Object> updates = new HashMap<>();
        updates.put("bookingId", bookingId);
        updates.put("passName", updatedPassName);
        updates.put("bookingDate", updatedBookingDate);
        updates.put("bookingTime", updatedBookingTime);
        updates.put("train", updatedTrainName);
        updates.put("seat", updatedSeat);

        // Update specific fields within the booking in Firebase Realtime Database
        mDatabase.child(bookingId).updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ManageBooking.this, "Booking Updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ManageBooking.this, "Failed To Update", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void deleteBooking() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ManageBooking.this);
        builder.setTitle("Are you Sure ?");
        builder.setMessage("Deleted booking data cannot be undone");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Call your actual delete logic here (using the booking key or other identifier)
                mDatabase.child(bookingId).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(ManageBooking.this, "Booking deleted successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(ManageBooking.this, "Failed to delete booking: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ManageBooking.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        builder.create().show();
    }
}