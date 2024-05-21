package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.EditDelete;
import com.example.myapplication.HomeActivity;
import com.example.myapplication.Model.PaymentOb;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Payment extends AppCompatActivity {
    private EditText etCardNumber;
    private EditText etExpiryDate;
    private EditText etCVV;
    private TextView priceTv;
    private Button payBtn;
    private DatabaseReference mDatabase;
    private String bookingId;
    private String price;
    private String cardNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Initialize views
        etCardNumber = findViewById(R.id.etCardNumber);
        etExpiryDate = findViewById(R.id.etExpiryDate);
        etCVV = findViewById(R.id.etCVV);
        priceTv = findViewById(R.id.tvPrice);
        payBtn = findViewById(R.id.btnPayNow);

        // Initialize Firebase database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get data from intent
        bookingId = getIntent().getStringExtra("bookingId");
        price = getIntent().getStringExtra("price");

        // Set price text
        priceTv.setText("Price: $" + price);

        // Set click listener for pay button
        payBtn.setOnClickListener(v -> payment());
    }

    private void payment() {
        try {
            cardNumber = etCardNumber.getText().toString().trim();
            String expiryDate = etExpiryDate.getText().toString().trim();
            String CVV = etCVV.getText().toString().trim();

            // Validate input fields
            if (cardNumber.isEmpty() || expiryDate.isEmpty() || CVV.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate card number length
            if (!isValidCardNumberLength(cardNumber)) {
                Toast.makeText(this, "Invalid card number length", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate CVV length
            if (!isValidCVVLength(CVV)) {
                Toast.makeText(this, "Invalid CVV length", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create PaymentOb object
            PaymentOb payment = new PaymentOb();
            payment.setBookingId(bookingId);
            payment.setCardNumber(cardNumber);
            payment.setExpiryDate(expiryDate);
            payment.setCVV(CVV);
            payment.setAmount(price);

            // Add payment to Firebase database
            mDatabase.child("Payments").push().setValue(payment)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Payment successful", Toast.LENGTH_SHORT).show();
                        generateReceipt(payment);
                        showReceiptPopup(payment);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to make payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            Log.e("Payment Error", e.getMessage(), e);
            Toast.makeText(this, "Error processing payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Helper method to validate card number length
    private boolean isValidCardNumberLength(String cardNumber) {
        return cardNumber.length() == 16; // Assuming standard card number length
    }

    // Helper method to validate CVV length
    private boolean isValidCVVLength(String CVV) {
        return CVV.length() == 3; // Assuming standard CVV length
    }

    // Method to generate receipt as PDF
    private void generateReceipt(PaymentOb payment) {
        try {
            // Create PDF document
            File pdfFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "receipt.pdf");
            PdfWriter writer = new PdfWriter(new FileOutputStream(pdfFile));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Add content to the PDF
            document.add(new Paragraph("Payment Receipt"));
            document.add(new Paragraph("Booking ID: " + payment.getBookingId()));
            document.add(new Paragraph("Card Number: " + payment.getCardNumber()));
            document.add(new Paragraph("Amount: $" + payment.getAmount()));
            document.add(new Paragraph("Date & Time: " + getCurrentDateTime()));

            // Close the document
            document.close();

            // Display PDF path
            Toast.makeText(this, "Receipt saved to: " + pdfFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("PDF Generation Error", e.getMessage(), e);
            Toast.makeText(this, "Error generating PDF receipt: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Method to get current date and time
    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    // Method to show receipt in a pop-up
    private void showReceiptPopup(PaymentOb payment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Payment Receipt"+"\n");
        builder.setMessage(
                "Booking ID: " + payment.getBookingId() + "\n\n" +
                        "Card Number: " + payment.getCardNumber() + "\n\n" +
                        "Amount: $" + payment.getAmount() + "\n\n" +
                        "Date & Time: " + getCurrentDateTime() + "\n\n" +
                        "Receipt saved to: " + getPdfFilePath()
        );

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Payment.this, HomeActivity.class);
                startActivity(intent);
                finish();

            }
        });

        builder.show();
    }

    // Method to get PDF file path
    private String getPdfFilePath() {
        // Specify the directory path on your local laptop
        String directoryPath = "C:/Users/Lenovo/Documents/MyAppReceipts";

        // Create the directory if it doesn't exist
        File dir = new File(directoryPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Specify the PDF file path within the directory
        return directoryPath + "/receipt.pdf";
    }

}
