package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.Booking;
import com.example.myapplication.Model.Train;
import com.example.myapplication.Model.User;
import com.example.myapplication.adapter.UserAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class manage_users extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    private DatabaseReference reference;
    private EditText etSearch;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        reference = FirebaseDatabase.getInstance().getReference();
        // Initialize RecyclerView and userList
        recyclerView = findViewById(R.id.rvUserList);
        etSearch =  findViewById(R.id.etSearch);
        userList = new ArrayList<>();

        userAdapter = new UserAdapter(userList,manage_users.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userAdapter);
        displayNoTextMsg();
        fetchUsersFromDatabase();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void searchUsers() {
        try {
            // Get the search query from the EditText
            String search = etSearch.getText().toString().trim().toLowerCase();

            // Create a filtered list to store the matching users
            List<User> filteredList = new ArrayList<>();

            // Loop through the userList to find matching users
            for (User user : userList) {
                // Convert the user data to lowercase for case-insensitive search
                String username = user.getUsername().toLowerCase();
                String email = user.getEmail().toLowerCase();
                String role = user.getRole().toLowerCase();

                // Check if any field matches the search query
                if (username.contains(search) || email.contains(search) || role.contains(search)) {
                    filteredList.add(user);
                }
            }

            // Update the userAdapter with the filtered list
            userAdapter.updateUserList(filteredList);

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


    private void fetchUsersFromDatabase() {
        try{
            reference.child("Users").addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    userList.clear();
                    if (snapshot.exists()) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            String userId = userSnapshot.getKey();
                            User user = userSnapshot.getValue(User.class);
                            if (user != null) {
                                user.setUserId(userId);
                                userList.add(user);
                            }
                        }
                        userAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(manage_users.this, "No User data found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(manage_users.this, "Error fetching User data: " + error.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception exception){
            Toast.makeText(manage_users.this, "Error fetching User data: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void displayNoTextMsg(){
        handler = new Handler();
        // Check if bookings list is empty to show/hide empty state TextView
        TextView textEmptyState = findViewById(R.id.textEmptyState);

        // Runnable to check for empty bookings and update text view
        Runnable checkEmptyBookings = new Runnable() {
            @Override
            public void run() {
                if (userList.isEmpty()) {
                    textEmptyState.setVisibility(View.VISIBLE);
                } else {
                    textEmptyState.setVisibility(View.GONE);
                }
            }
        };

        // Post the Runnable with a 10-second delay
        handler.postDelayed(checkEmptyBookings, 10000);

    }
}
