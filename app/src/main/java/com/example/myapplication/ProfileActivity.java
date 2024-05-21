package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {
    private EditText edtUserName;
    private EditText edtEmail;
    private EditText edtAge;
    private Button saveBtn;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextInputLayout tilPassengerName = findViewById(R.id.usernameLayout);
        TextInputLayout tilEmailLayout = findViewById(R.id.emailLayout);
        TextInputLayout ageLayout = findViewById(R.id.ageLayout);
        saveBtn = findViewById(R.id.btnSaveProfile);

        edtUserName = tilPassengerName.getEditText();
        edtEmail = tilEmailLayout.getEditText();
        edtAge = ageLayout.getEditText();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        getUser();
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfile();
            }
        });

    }

    private void getUser() {
        String userId = getUserIdFromPreferences();

        mDatabase.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        // Populate user details into TextInputEditTexts
                        edtUserName.setText(user.getUsername());
                        edtEmail.setText(user.getEmail());
//                        edtPhoneNumber.setText(user.getPhoneNumber());
                        edtAge.setText(String.valueOf(user.getAge()));
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Error fetching User data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editProfile() {
        String userId = getUserIdFromPreferences();
        String username  = edtUserName.getText().toString();
        String email = edtEmail.getText().toString();
        String age = edtAge.getText().toString();


        // Validate input fields
        if (username.isEmpty() || email.isEmpty() || age.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a HashMap to hold the updated data
        HashMap<String, Object> updates = new HashMap<>();
        updates.put("username", username);
        updates.put("email", email);
        updates.put("age", age);

        // Update specific fields within the booking in Firebase Realtime Database
        mDatabase.child("Users").child(userId).updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ProfileActivity.this, "User Updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed To Update", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getUserIdFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("user_id", "");
    }
}
