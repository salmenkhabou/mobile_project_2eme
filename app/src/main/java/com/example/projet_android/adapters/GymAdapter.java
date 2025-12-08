package com.example.projet_android.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projet_android.R;
import com.example.projet_android.models.Gym;
import java.util.List;

public class GymAdapter extends RecyclerView.Adapter<GymAdapter.GymViewHolder> {
    
    private Context context;
    private List<Gym> gyms;
    private OnGymClickListener onGymClickListener;
    
    public interface OnGymClickListener {
        void onGymClick(Gym gym);
        void onDirectionsClick(Gym gym);
        void onCallClick(Gym gym);
    }
    
    public GymAdapter(Context context, List<Gym> gyms) {
        this.context = context;
        this.gyms = gyms;
    }
    
    public void setOnGymClickListener(OnGymClickListener listener) {
        this.onGymClickListener = listener;
    }
    
    @NonNull
    @Override
    public GymViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gym, parent, false);
        return new GymViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull GymViewHolder holder, int position) {
        Gym gym = gyms.get(position);
        
        holder.tvGymName.setText(gym.getName());
        holder.tvGymAddress.setText(gym.getAddress());
        holder.tvGymDistance.setText(gym.getFormattedDistance());
        holder.tvGymRating.setText(String.format("%.1f", gym.getRating()));
        holder.tvGymType.setText(gym.getGymType());
        holder.tvGymPrice.setText(String.format("%.0f€/mois", gym.getMonthlyPrice()));
        
        // Set open/closed status
        if (gym.isOpen()) {
            holder.tvGymStatus.setText("Ouvert");
            holder.tvGymStatus.setTextColor(ContextCompat.getColor(context, R.color.primary_green));
        } else {
            holder.tvGymStatus.setText("Fermé");
            holder.tvGymStatus.setTextColor(ContextCompat.getColor(context, R.color.error_red));
        }
        
        // Set gym type icon and color
        setGymTypeIcon(holder.imgGymIcon, gym.getGymType());
        
        // Set rating stars background color
        if (gym.getRating() >= 4.5) {
            holder.tvGymRating.setBackgroundTintList(
                ContextCompat.getColorStateList(context, R.color.primary_green));
        } else if (gym.getRating() >= 4.0) {
            holder.tvGymRating.setBackgroundTintList(
                ContextCompat.getColorStateList(context, R.color.wellness_accent));
        } else {
            holder.tvGymRating.setBackgroundTintList(
                ContextCompat.getColorStateList(context, R.color.text_secondary));
        }
        
        // Set amenities (show first 2)
        String[] amenities = gym.getAmenities();
        if (amenities != null && amenities.length > 0) {
            StringBuilder amenitiesText = new StringBuilder();
            int count = Math.min(2, amenities.length);
            for (int i = 0; i < count; i++) {
                amenitiesText.append(amenities[i]);
                if (i < count - 1) amenitiesText.append(", ");
            }
            if (amenities.length > 2) {
                amenitiesText.append("...");
            }
            holder.tvGymAmenities.setText(amenitiesText.toString());
        } else {
            holder.tvGymAmenities.setText("Équipements non spécifiés");
        }
        
        // Click listeners
        holder.cardGym.setOnClickListener(v -> {
            if (onGymClickListener != null) {
                onGymClickListener.onGymClick(gym);
            }
        });
        
        holder.btnDirections.setOnClickListener(v -> {
            if (onGymClickListener != null) {
                onGymClickListener.onDirectionsClick(gym);
            }
        });
        
        holder.btnCall.setOnClickListener(v -> {
            if (onGymClickListener != null) {
                onGymClickListener.onCallClick(gym);
            }
        });
    }
    
    private void setGymTypeIcon(ImageView iconView, String gymType) {
        switch (gymType.toLowerCase()) {
            case "public":
                iconView.setImageResource(R.drawable.ic_gym_public);
                iconView.setColorFilter(ContextCompat.getColor(context, R.color.primary_blue));
                break;
            case "private":
                iconView.setImageResource(R.drawable.ic_gym_private);
                iconView.setColorFilter(ContextCompat.getColor(context, R.color.primary_purple));
                break;
            case "chain":
                iconView.setImageResource(R.drawable.ic_gym_chain);
                iconView.setColorFilter(ContextCompat.getColor(context, R.color.wellness_accent));
                break;
            case "boutique":
                iconView.setImageResource(R.drawable.ic_gym_boutique);
                iconView.setColorFilter(ContextCompat.getColor(context, R.color.primary_green));
                break;
            default:
                iconView.setImageResource(R.drawable.ic_gym_default);
                iconView.setColorFilter(ContextCompat.getColor(context, R.color.text_secondary));
                break;
        }
    }
    
    @Override
    public int getItemCount() {
        return gyms != null ? gyms.size() : 0;
    }
    
    public void updateGyms(List<Gym> newGyms) {
        this.gyms = newGyms;
        notifyDataSetChanged();
    }
    
    public static class GymViewHolder extends RecyclerView.ViewHolder {
        CardView cardGym;
        ImageView imgGymIcon;
        TextView tvGymName;
        TextView tvGymAddress;
        TextView tvGymDistance;
        TextView tvGymRating;
        TextView tvGymType;
        TextView tvGymPrice;
        TextView tvGymStatus;
        TextView tvGymAmenities;
        ImageView btnDirections;
        ImageView btnCall;
        
        public GymViewHolder(@NonNull View itemView) {
            super(itemView);
            
            cardGym = itemView.findViewById(R.id.cardGym);
            imgGymIcon = itemView.findViewById(R.id.imgGymIcon);
            tvGymName = itemView.findViewById(R.id.tvGymName);
            tvGymAddress = itemView.findViewById(R.id.tvGymAddress);
            tvGymDistance = itemView.findViewById(R.id.tvGymDistance);
            tvGymRating = itemView.findViewById(R.id.tvGymRating);
            tvGymType = itemView.findViewById(R.id.tvGymType);
            tvGymPrice = itemView.findViewById(R.id.tvGymPrice);
            tvGymStatus = itemView.findViewById(R.id.tvGymStatus);
            tvGymAmenities = itemView.findViewById(R.id.tvGymAmenities);
            btnDirections = itemView.findViewById(R.id.btnDirections);
            btnCall = itemView.findViewById(R.id.btnCall);
        }
    }
}
