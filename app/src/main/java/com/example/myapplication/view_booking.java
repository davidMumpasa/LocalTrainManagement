package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.Booking;
import com.example.myapplication.adapter.BookingAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class view_booking extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BookingAdapter bookingAdapter;
    private ArrayList<Booking> bookingsList;
    private DatabaseReference reference;
    private EditText searchBtn;
    private Handler handler;
    private String userId;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_booking);

        recyclerView = findViewById(R.id.recyclerViewBookings);
        searchBtn = findViewById(R.id.etSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reference = FirebaseDatabase.getInstance().getReference();
        bookingsList = new ArrayList<>(); // Initialize bookingsList
        bookingAdapter = new BookingAdapter(bookingsList,view_booking.this);
        recyclerView.setAdapter(bookingAdapter);
        handler = new Handler();

        // Check if bookings list is empty to show/hide empty state TextView
        TextView textEmptyState = findViewById(R.id.textEmptyState);

        // Runnable to check for empty bookings and update text view
        Runnable checkEmptyBookings = new Runnable() {
            @Override
            public void run() {
                if (bookingsList.isEmpty()) {
                    textEmptyState.setVisibility(View.VISIBLE);
                } else {
                    textEmptyState.setVisibility(View.GONE);
                }
            }
        };

        // Post the Runnable with a 10-second delay
        handler.postDelayed(checkEmptyBookings, 10000);
        userId = getUserIdFromPreferences();
        fetchBookings();

        searchBtn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchBooking();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void searchBooking() {
        try {
            // Get the search query from the EditText
            String search = searchBtn.getText().toString().trim().toLowerCase();

            // Create a filtered list to store the matching users
            List<Booking> filteredList = new ArrayList<>();

            // Loop through the userList to find matching users
            for (Booking booking : bookingsList) {
                // Convert the user data to lowercase for case-insensitive search
                String passName = booking.getPassName().toLowerCase();
                String seat = booking.getSeat().toLowerCase();
                String train = booking.getTrain().toLowerCase();

                // Check if any field matches the search query
                if (passName.contains(search) || seat.contains(search) || train.contains(search)) {
                    filteredList.add(booking);
                }
            }

            // Update the userAdapter with the filtered list
            bookingAdapter.updateBookingList(filteredList);

            // Show or hide empty state TextView based on filtered list size
            TextView textEmptyState = findViewById(R.id.textEmptyState);
            if (filteredList.isEmpty()) {
                textEmptyState.setVisibility(View.VISIBLE);
            } else {
                textEmptyState.setVisibility(View.GONE);
            }

        } catch (Exception exception) {
            Log.d("User Error", exception.toString());
        }
    }

    private void fetchBookings() {
        reference.child("Bookings").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookingsList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot bookingSnapshot : snapshot.getChildren()) {
                        String bookingId = bookingSnapshot.getKey();
                        Booking booking = bookingSnapshot.getValue(Booking.class);

                        if (booking != null) {
                            if(Objects.equals(booking.getUserId(), userId)){
                                booking.setBookingId(bookingId); // Set the booking ID in the Booking object (assuming a setter exists)
                                bookingsList.add(booking);
                            }
                        }
                        bookingAdapter.notifyDataSetChanged();
                    }

                } else {
                    Toast.makeText(view_booking.this, "No Booking data found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(view_booking.this, "Error fetching Booking data: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getUserIdFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("user_id", "");
    }

}