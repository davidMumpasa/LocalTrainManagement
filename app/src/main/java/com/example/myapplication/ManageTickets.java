package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.Booking;
import com.example.myapplication.Model.Ticket;
import com.example.myapplication.Model.User;
import com.example.myapplication.adapter.BookingAdapter;
import com.example.myapplication.adapter.TicketAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManageTickets extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TicketAdapter ticketAdapter;
    private ArrayList<Ticket> ticketList;
    private DatabaseReference reference;
    private EditText etSearch;
    private Handler handler;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_tickets);

        etSearch = findViewById(R.id.etSearch);
        recyclerView = findViewById(R.id.recyclerViewTickets);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reference = FirebaseDatabase.getInstance().getReference();
        ticketList = new ArrayList<>(); // Initialize bookingsList
        ticketAdapter = new TicketAdapter(ticketList,ManageTickets.this);
        recyclerView.setAdapter(ticketAdapter);
        handler = new Handler();

        // Check if bookings list is empty to show/hide empty state TextView
        TextView textEmptyState = findViewById(R.id.textEmptyState);

        // Runnable to check for empty bookings and update text view
        Runnable checkEmptyBookings = new Runnable() {
            @Override
            public void run() {
                if (ticketList.isEmpty()) {
                    textEmptyState.setVisibility(View.VISIBLE);
                } else {
                    textEmptyState.setVisibility(View.GONE);
                }
            }
        };

        // Post the Runnable with a 10-second delay
        handler.postDelayed(checkEmptyBookings, 10000);
        fetchBookings();
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchTicket();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void fetchBookings() {
        reference.child("Bookings").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ticketList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot bookingSnapshot : snapshot.getChildren()) {
                        String bookingId = bookingSnapshot.getKey(); // Get the key of the current child
                        Ticket booking = bookingSnapshot.getValue(Ticket.class);
                        if (booking != null) {
                            booking.setBookingId(bookingId); // Set the booking ID in the Booking object (assuming a setter exists)
                            ticketList.add(booking);
                        }
                    }
                    ticketAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ManageTickets.this, "No Booking data found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageTickets.this, "Error fetching Booking data: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchTicket() {
        try {
            // Get the search query from the EditText
            String search = etSearch.getText().toString().trim().toLowerCase();

            // Create a filtered list to store the matching users
            List<Ticket> filteredList = new ArrayList<>();

            // Loop through the userList to find matching users
            for (Ticket ticket : ticketList) {
                // Convert the user data to lowercase for case-insensitive search
                String passName = ticket.getPassName().toLowerCase();
                String date = ticket.getBookingDate().toLowerCase();
                String time = ticket.getBookingTime().toLowerCase();
                String seat = ticket.getSeat().toLowerCase();
                String train = ticket.getTrain().toLowerCase();

                // Check if any field matches the search query
                if (passName.contains(search) || date.contains(search) || time.contains(search)|| time.contains(seat)|| time.contains(train)) {
                    filteredList.add(ticket);
                }
            }

            // Update the userAdapter with the filtered list
            ticketAdapter.updateTicketList(filteredList);

            // Show or hide empty state TextView based on filtered list size
            TextView textEmptyState = findViewById(R.id.textEmptyState);
            if (filteredList.isEmpty()) {
                textEmptyState.setVisibility(View.VISIBLE);
            } else {
                textEmptyState.setVisibility(View.GONE);
            }

        } catch (Exception exception) {
            Log.d("Ticket Error", exception.toString());
        }
    }
}