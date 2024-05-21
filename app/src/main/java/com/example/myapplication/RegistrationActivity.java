package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Model.Booking;
import com.example.myapplication.Model.User;
import com.google.firebase.auth.FirebaseAuth;
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

public class RegistrationActivity extends AppCompatActivity {
    EditText editTextUsername;
    EditText editTextEmail;
    EditText editPhoneNum;
    EditText editTextPassword;
    Button signUpBtn;
    User user;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editPhoneNum = findViewById(R.id.editPhoneNum);
        editTextPassword = findViewById(R.id.editTextPassword);
        signUpBtn = findViewById(R.id.buttonSignUp);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        TextView textView = findViewById(R.id.textViewLogin);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void register() {
        try {

            String username = editTextUsername.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String phoneNum = editPhoneNum.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            // Validate input fields
            if (username.isEmpty() || email.isEmpty() || phoneNum.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegistrationActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (checkPasswordStrength(password)){
                // Check if the Booking already exists
                Query query = mDatabase.child("Users")
                        .orderByChild("username").equalTo(username)
                        .limitToFirst(1); // Limit the query to just 1 result

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Train with the same name already exists
                            Toast.makeText(RegistrationActivity.this, "User already exists", Toast.LENGTH_SHORT).show();
                        } else {
                            // Train does not exist, proceed with adding it to the database
                            User user = new User();

                            user.setUsername(username);
                            user.setEmail(email);
                            user.setRole("user");
                            user.setPassword(password);
                            user.setAge("0");


                            // Add train to Firebase database
                            mDatabase.child("Users").push().setValue(user)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(RegistrationActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(RegistrationActivity.this, "Failed to Register: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(RegistrationActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }



        } catch (Exception e) {
            Log.d("Booking Error", e.getMessage());
            Toast.makeText(this, "Database error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }




    public boolean checkPasswordStrength(String password) {

        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;
        boolean isStrong = false;

        for (char ch : password.toCharArray()) {
            if (Character.isUpperCase(ch)) {
                hasUpperCase = true;
            } else if (Character.isLowerCase(ch)) {
                hasLowerCase = true;
            } else if (Character.isDigit(ch)) {
                hasDigit = true;
            } else if (isSpecialChar(ch)) {
                hasSpecialChar = true;
            }
        }

        int strengthScore = 0;
        if (hasUpperCase) strengthScore++;
        if (hasLowerCase) strengthScore++;
        if (hasDigit) strengthScore++;
        if (hasSpecialChar) strengthScore++;

        String strengthMessage;
        if (password.length() < 8) {
            Toast.makeText(this, "Weak (password length should be at least 8 characters)", Toast.LENGTH_SHORT).show();
        } else if (strengthScore < 3) {
            Toast.makeText(this, "Medium (include at least 3 of uppercase, lowercase, digit, and special character)", Toast.LENGTH_SHORT).show();
        } else {
            isStrong = true;
        }
        return isStrong;
    }

    public boolean isSpecialChar(char ch) {
        String specialChars = "!@#$%^&*()_+[]{}|;:,.<>?/";
        return specialChars.contains(String.valueOf(ch));
    }
}
