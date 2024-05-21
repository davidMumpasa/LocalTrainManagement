package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.Model.AddTrainActivity;
import com.example.myapplication.Model.Booking;
import com.example.myapplication.Model.Train;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class BookingActivity extends AppCompatActivity {
    private EditText editTextSelectDate;
    private EditText editTextSelectTime;
    private EditText editTextPassengerName;
    private Spinner spinnerSelectSeat;
    private Spinner spinnerSelectTrain;
    private Button btnConfirmBooking;
    String userId;
    private int hour, minute;
    private String price;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking);

        editTextPassengerName = findViewById(R.id.editTextPassengerName);
        spinnerSelectSeat = findViewById(R.id.spinnerSelectSeat);
        spinnerSelectTrain = findViewById(R.id.spinnerSelectTrain);
        editTextSelectDate = findViewById(R.id.editTextSelectDate);
        editTextSelectTime = findViewById(R.id.editTextSelectTime);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        String trainName = getIntent().getStringExtra("trainName");
        price = getIntent().getStringExtra("price");
        if (trainName != null) {
            // Set the train name to the Spinner
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{trainName});
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSelectTrain.setAdapter(adapter);

            // Disable the Spinner
            spinnerSelectTrain.setEnabled(false);
        }


        // Set an OnClickListener on the EditText to show DatePickerDialog when clicked
        editTextSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        editTextSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get current time
                final Calendar c = Calendar.getInstance();
                hour = c.get(Calendar.HOUR_OF_DAY);
                minute = c.get(Calendar.MINUTE);

                // Create a time picker dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(BookingActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // Set selected time to the EditText
                                editTextSelectTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                            }
                        }, hour, minute, false); // false for 12-hour format

                // Show the time picker dialog
                timePickerDialog.show();
            }
        });
        userId = getUserIdFromPreferences();
        btnConfirmBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                book();
            }
        });


    }


    private void showDatePickerDialog() {
        // Get current date from Calendar
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a DatePickerDialog and set its listener
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(android.widget.DatePicker view, int selectedYear, int selectedMonth, int selectedDayOfMonth) {
                        // Update the EditText with the selected date
                        String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDayOfMonth;
                        editTextSelectDate.setText(selectedDate);
                    }
                }, year, month, dayOfMonth);

        // Show the DatePickerDialog
        datePickerDialog.show();
    }
    private void book() {
        try {

            String passName = editTextPassengerName.getText().toString();
            String seat = String.valueOf(spinnerSelectSeat.getSelectedItem());
            String trainName = String.valueOf(spinnerSelectTrain.getSelectedItem());
            String selectedData = editTextSelectDate.getText().toString();
            String selectedTime = editTextSelectTime.getText().toString();

            if (trainName.isEmpty() || passName.isEmpty() || seat.isEmpty() ||
                    selectedData.isEmpty() || selectedTime.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get the current date and time
            Calendar calendar = Calendar.getInstance();
            Date currentDate = calendar.getTime();

            // Parse the selected date and time
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date selectedDateTime = sdf.parse(selectedData + " " + selectedTime);

            // Check if the selected date and time are in the future
            if (selectedDateTime != null && selectedDateTime.after(currentDate)) {
                // Check if the Booking already exists


                Query query = mDatabase.child("Bookings")
                        .orderByChild("trainName").equalTo(trainName)
                        .limitToFirst(1); // Limit the query to just 1 result

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Train with the same name already exists
                            Toast.makeText(BookingActivity.this, "Booking already exists", Toast.LENGTH_SHORT).show();
                        } else {
                            String bookingKey = mDatabase.child("Bookings").push().getKey();
                            // Train does not exist, proceed with adding it to the database
                            Booking booking = new Booking();
                            booking.setBookingId(bookingKey);
                            booking.setUserId(userId);
                            booking.setPassName(passName);
                            booking.setSeat(seat);
                            booking.setTrain(trainName);
                            booking.setBookingDate(selectedData);
                            booking.setBookingTime(selectedTime);

                            // Add train to Firebase database
                            mDatabase.child("Bookings").push().setValue(booking)
                                    .addOnSuccessListener(aVoid -> {
                                        Intent intent = new Intent(BookingActivity.this, Payment.class);
                                        intent.putExtra("bookingId", bookingKey);
                                        intent.putExtra("price", price);
                                        startActivity(intent);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(BookingActivity.this, "Failed to book: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(BookingActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Selected date and time must be in the future", Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            Log.d("Booking Error", e.getMessage());
            Toast.makeText(this, "Error parsing date and time", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.d("Booking Error", e.getMessage());
            Toast.makeText(this, "Database error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public String getUserIdFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("user_id", "");
    }

}