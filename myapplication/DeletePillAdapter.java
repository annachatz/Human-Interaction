package com.example.myapplication;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Models.Pill;

import java.util.List;


public class DeletePillAdapter extends RecyclerView.Adapter<DeletePillAdapter.ViewHolder> {

    private List<Pill> pillList;
    private List<Pill> selectedPills;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Pill pill);
    }

    public DeletePillAdapter(List<Pill> pillList, List<Pill> selectedPills, OnItemClickListener onItemClickListener) {
        this.pillList = pillList;
        this.selectedPills = selectedPills;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pill_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pill pill = pillList.get(position);
        holder.pillNameTextView.setText(pill.getPillName());
        holder.pillTimeTextView.setText(pill.getTime());

        if (selectedPills.contains(pill)) {
            holder.itemView.setBackgroundColor(Color.RED);
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(pill);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return pillList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView pillNameTextView;
        public TextView pillTimeTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            pillNameTextView = itemView.findViewById(R.id.pill_name);
            pillTimeTextView = itemView.findViewById(R.id.pill_time);
        }
    }
}