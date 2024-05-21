package com.example.myapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.BookingActivity;
import com.example.myapplication.Model.Booking;
import com.example.myapplication.Model.Train;
import com.example.myapplication.R;


import java.util.List;

public class TrainAdapter extends RecyclerView.Adapter<TrainAdapter.ViewHolder> {
    private List<Train> trainList;
    private Context context;

    public TrainAdapter(List<Train> trainList, Context context) {
        this.trainList = trainList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.train_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Train train = trainList.get(position);
        holder.trainNameTextView.setText(train.getTrainName());
        holder.priceBtn.setText(train.getPrice());

        // Set click listener for the train item
        holder.itemView.setOnClickListener(v -> {
            // Navigate to the book activity
            Intent intent = new Intent(context, BookingActivity.class);
            // Pass the train name to the BookingActivity using intent extras
            intent.putExtra("trainName", train.getTrainName());
            intent.putExtra("price", train.getPrice());

            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return trainList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView trainNameTextView;
        Button priceBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            trainNameTextView = itemView.findViewById(R.id.timeTextView);
            priceBtn = itemView.findViewById(R.id.PriceBtn);

            // Initialize other TextViews here
        }

    }
    @SuppressLint("NotifyDataSetChanged")
    public void updateTrainList(List<Train> filteredList) {
        this.trainList = filteredList;
        notifyDataSetChanged();
    }
}
