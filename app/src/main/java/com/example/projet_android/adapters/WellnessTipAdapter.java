package com.example.projet_android.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet_android.R;
import com.example.projet_android.models.WellnessTip;

import java.util.List;

/**
 * Adapter pour afficher les conseils de bien-être dans un RecyclerView horizontal
 */
public class WellnessTipAdapter extends RecyclerView.Adapter<WellnessTipAdapter.TipViewHolder> {
    
    private List<WellnessTip> tips;
    private OnTipClickListener listener;
    
    public interface OnTipClickListener {
        void onTipClick(WellnessTip tip);
    }
    
    public WellnessTipAdapter(List<WellnessTip> tips) {
        this.tips = tips;
    }
    
    public void setOnTipClickListener(OnTipClickListener listener) {
        this.listener = listener;
    }
    
    public void updateTips(List<WellnessTip> newTips) {
        this.tips = newTips;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public TipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wellness_tip, parent, false);
        return new TipViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull TipViewHolder holder, int position) {
        WellnessTip tip = tips.get(position);
        holder.bind(tip);
    }
    
    @Override
    public int getItemCount() {
        return tips != null ? tips.size() : 0;
    }
    
    class TipViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView titleTextView;
        private TextView descriptionTextView;
        private TextView categoryTextView;
        
        public TipViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_tip);
            titleTextView = itemView.findViewById(R.id.tv_tip_title);
            descriptionTextView = itemView.findViewById(R.id.tv_tip_description);
            categoryTextView = itemView.findViewById(R.id.tv_tip_category);
        }
        
        public void bind(WellnessTip tip) {
            titleTextView.setText(tip.getTitle());
            descriptionTextView.setText(tip.getDescription());
            categoryTextView.setText(tip.getIconEmoji());
            
            // Changer l'apparence si le conseil a été lu
            if (tip.isRead()) {
                cardView.setAlpha(0.7f);
            } else {
                cardView.setAlpha(1.0f);
            }
            
            // Couleur de fond selon la catégorie
            int backgroundTint = getCategoryColor(tip.getCategory());
            cardView.setCardBackgroundColor(backgroundTint);
            
            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTipClick(tip);
                }
                tip.setRead(true);
                notifyItemChanged(getAdapterPosition());
            });
        }
        
        private int getCategoryColor(String category) {
            // Retourner des couleurs différentes selon la catégorie
            switch (category.toLowerCase()) {
                case "hydration": return 0xFF81D4FA; // Bleu clair
                case "meditation": return 0xFFCE93D8; // Violet clair
                case "nutrition": return 0xFFA5D6A7; // Vert clair
                case "activity": return 0xFFFFB74D; // Orange clair
                case "sleep": return 0xFF90CAF9; // Bleu sommeil
                case "mental": return 0xFFF8BBD9; // Rose mental
                default: return 0xFFE1F5FE; // Bleu très clair par défaut
            }
        }
    }
}