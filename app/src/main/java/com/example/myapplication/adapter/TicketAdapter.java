package com.example.myapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.Ticket;
import com.example.myapplication.Model.User;
import com.example.myapplication.R;
import com.example.myapplication.TicketViewHolder;

import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketViewHolder> {
    private List<Ticket> ticketList;
    private Context context;

    public TicketAdapter(List<Ticket> ticketList, Context context) {
        this.ticketList = ticketList;
        this.context = context;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ticket_item, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = ticketList.get(position);
        holder.bindTicket(ticket);
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateTicketList(List<Ticket> filteredList) {
        this.ticketList = filteredList;
        notifyDataSetChanged();
    }
}
