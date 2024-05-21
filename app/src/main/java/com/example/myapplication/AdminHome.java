package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Model.AddTrainActivity;
import com.google.android.material.navigation.NavigationView;

public class AdminHome extends AppCompatActivity {

    private NavigationView navigationView;

    // Define constants for menu item IDs
    private Button users;
    private Button tickets;
    private Button train;
    private Button logout;

    private SharedPreferences sharedPreferences;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_home);

        users = findViewById(R.id.usersButton);
        tickets = findViewById(R.id.ticketsButton);
        train = findViewById(R.id.trainButton);
        logout = findViewById(R.id.logoutButton);

        users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHome.this,manage_users.class);
                startActivity(intent);
            }
        });
        tickets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHome.this, ManageTickets.class);
                startActivity(intent);
            }
        });
        train.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHome.this, AddTrainActivity.class);
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeUserIdFromPreferences();
                Intent intent = new Intent(AdminHome.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    private void removeUserIdFromPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("user_id");
        editor.apply();
    }
}
