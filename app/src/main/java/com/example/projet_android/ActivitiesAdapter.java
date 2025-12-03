package com.example.projet_android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ActivitiesAdapter extends RecyclerView.Adapter<ActivitiesAdapter.ViewHolder> {
    
    private List<String> activities;
    
    public ActivitiesAdapter(List<String> activities) {
        this.activities = activities;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activity, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String activity = activities.get(position);
        holder.activityTextView.setText(activity);
    }
    
    @Override
    public int getItemCount() {
        return activities.size();
    }
    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView activityTextView;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            activityTextView = itemView.findViewById(R.id.tv_activity_item);
        }
    }
}
