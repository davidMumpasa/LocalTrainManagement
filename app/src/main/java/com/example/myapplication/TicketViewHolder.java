package com.example.myapplication;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.Ticket;

public class TicketViewHolder extends RecyclerView.ViewHolder {
    private TextView passName;
    private TextView textBookingDate;
    private TextView textBookingTime;
    private TextView textBookingDetails;

    public TicketViewHolder(@NonNull View itemView) {
        super(itemView);
        passName = itemView.findViewById(R.id.passName);
        textBookingDate = itemView.findViewById(R.id.textBookingDate);
        textBookingTime = itemView.findViewById(R.id.textBookingTime);
        textBookingDetails = itemView.findViewById(R.id.textBookingDetails);
    }

    public void bindTicket(Ticket ticket) {
        passName.setText(ticket.getPassName());
        textBookingDate.setText(ticket.getBookingDate());
        textBookingTime.setText(ticket.getBookingTime());
        textBookingDetails.setText(ticket.getTrain());
    }
}
