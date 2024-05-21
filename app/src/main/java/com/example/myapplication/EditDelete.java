package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.adapter.ManageBooking;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class EditDelete extends AppCompatActivity {
    EditText editTextUserName ;
    EditText editTextUserEmail;
    EditText editTextUserRole;
    EditText editTextUserAge;
    String userId;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_delete);

        editTextUserName = findViewById(R.id.editUserName);
        editTextUserEmail = findViewById(R.id.editUserEmail);
        editTextUserRole = findViewById(R.id.editUserRole);
        editTextUserAge = findViewById(R.id.editUserAge);


        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        // Assuming user data is received from the adapter
        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getStringExtra("userId");
            String username = intent.getStringExtra("UserName");
            String email = intent.getStringExtra("email");
            String role = intent.getStringExtra("role");
            String age = intent.getStringExtra("age");


            // Set the text of TextViews with the user data
            editTextUserName.setText(username);
            editTextUserEmail.setText(email);
            editTextUserRole.setText(role);
            editTextUserAge.setText(age); // Convert age to string before setting

        }

        // Set click listeners for Edit and Delete buttons
        Button btnEditUser = findViewById(R.id.btnEditUser);
        Button btnDeleteUser = findViewById(R.id.btnDeleteUser);

        btnEditUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editBooking();
            }
        });

        btnDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser();
            }
        });
    }

    private void editBooking() {

        String username  = editTextUserName.getText().toString();
        String email = editTextUserEmail.getText().toString();
        String role = editTextUserRole.getText().toString();
        String age = editTextUserAge.getText().toString();


        // Validate input fields
        if (username.isEmpty() || email.isEmpty() || role.isEmpty() ||
                age.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a HashMap to hold the updated data
        HashMap<String, Object> updates = new HashMap<>();
        updates.put("username", username);
        updates.put("email", email);
        updates.put("role", role);
        updates.put("age", age);

        // Update specific fields within the booking in Firebase Realtime Database
        mDatabase.child(userId).updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(EditDelete.this, "User Updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditDelete.this, "Failed To Update", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void deleteUser() {

        AlertDialog.Builder builder = new AlertDialog.Builder(EditDelete.this);
        builder.setTitle("Are you Sure ?");
        builder.setMessage("Deleted User data cannot be undone");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Call your actual delete logic here (using the User key or other identifier)
                mDatabase.child(userId).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(EditDelete.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(EditDelete.this, "Failed to delete User: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(EditDelete.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        builder.create().show();
    }

}

