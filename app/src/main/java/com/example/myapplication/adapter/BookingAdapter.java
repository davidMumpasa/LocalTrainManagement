package com.example.myapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.BookingActivity;
import com.example.myapplication.Model.Booking;
import com.example.myapplication.Model.User;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private ArrayList<Booking> bookingsList;
    private Context context;

    public BookingAdapter(ArrayList<Booking> bookingsList, Context context) {
        this.bookingsList = bookingsList;
        this.context = context;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingsList.get(position);
        holder.bind(booking);
        holder.itemView.setOnClickListener(v -> {
            // Navigate to the book activity
            Intent intent = new Intent(context, ManageBooking.class);
            // Pass the train name to the BookingActivity using intent extras
            intent.putExtra("bookingId", booking.getBookingId());
            intent.putExtra("passName", booking.getPassName());
            intent.putExtra("bookingDate", booking.getBookingDate());
            intent.putExtra("bookingTime", booking.getBookingTime());
            intent.putExtra("train", booking.getTrain());
            intent.putExtra("seat", booking.getSeat());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return bookingsList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateBookingList(List<Booking> filteredList) {
        this.bookingsList = (ArrayList<Booking>) filteredList;
        notifyDataSetChanged();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        private TextView passName;
        private TextView textBookingDate;
        private TextView textBookingTime;
        private TextView textBookingDetails;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            passName = itemView.findViewById(R.id.passName);
            textBookingDate = itemView.findViewById(R.id.textBookingDate);
            textBookingTime = itemView.findViewById(R.id.textBookingTime);
            textBookingDetails = itemView.findViewById(R.id.textBookingDetails);
        }

        @SuppressLint("SetTextI18n")
        public void bind(Booking booking) {

            passName.setText("Name: " + booking.getPassName());
            textBookingDate.setText("Date: " + booking.getBookingDate());
            textBookingTime.setText("Time: " + booking.getBookingTime());
            textBookingDetails.setText(booking.getTrain()+" "+ booking.getSeat());
        }
    }
}