package com.example.projet_android.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet_android.R;
import com.example.projet_android.models.Challenge;

import java.util.List;

/**
 * Adapter pour afficher les défis wellness dans un RecyclerView
 */
public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.ChallengeViewHolder> {
    
    private List<Challenge> challenges;
    private OnChallengeActionListener listener;
    
    public interface OnChallengeActionListener {
        void onChallengeCompleted(Challenge challenge);
        void onChallengeClicked(Challenge challenge);
    }
    
    public ChallengeAdapter(List<Challenge> challenges, OnChallengeActionListener listener) {
        this.challenges = challenges;
        this.listener = listener;
    }
    
    public void updateChallenges(List<Challenge> newChallenges) {
        this.challenges = newChallenges;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public ChallengeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_challenge, parent, false);
        return new ChallengeViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ChallengeViewHolder holder, int position) {
        Challenge challenge = challenges.get(position);
        holder.bind(challenge);
    }
    
    @Override
    public int getItemCount() {
        return challenges != null ? challenges.size() : 0;
    }
    
    class ChallengeViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView titleTextView;
        private TextView descriptionTextView;
        private TextView typeTextView;
        private TextView pointsTextView;
        private ProgressBar progressBar;
        private TextView progressTextView;
        private Button actionButton;
        
        public ChallengeViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_challenge);
            titleTextView = itemView.findViewById(R.id.tv_challenge_title);
            descriptionTextView = itemView.findViewById(R.id.tv_challenge_description);
            typeTextView = itemView.findViewById(R.id.tv_challenge_type);
            pointsTextView = itemView.findViewById(R.id.tv_challenge_points);
            progressBar = itemView.findViewById(R.id.pb_challenge_progress);
            progressTextView = itemView.findViewById(R.id.tv_challenge_progress);
            actionButton = itemView.findViewById(R.id.btn_challenge_action);
        }
        
        public void bind(Challenge challenge) {
            titleTextView.setText(challenge.getTitle());
            descriptionTextView.setText(challenge.getDescription());
            typeTextView.setText(challenge.getTypeDisplayName());
            pointsTextView.setText(challenge.getPointsText());
            
            // Configuration de la barre de progrès
            if (challenge.getMaxProgress() > 1) {
                progressBar.setVisibility(View.VISIBLE);
                progressTextView.setVisibility(View.VISIBLE);
                progressBar.setMax(challenge.getMaxProgress());
                progressBar.setProgress(challenge.getProgress());
                progressTextView.setText(challenge.getProgress() + "/" + challenge.getMaxProgress());
            } else {
                progressBar.setVisibility(View.GONE);
                progressTextView.setVisibility(View.GONE);
            }
            
            // Configuration du bouton d'action
            if (challenge.isCompleted()) {
                actionButton.setText("✅ Terminé");
                actionButton.setEnabled(false);
                actionButton.setBackgroundColor(0xFF4CAF50); // Vert
                cardView.setAlpha(0.7f);
            } else {
                actionButton.setText("Compléter");
                actionButton.setEnabled(true);
                actionButton.setBackgroundColor(0xFF2196F3); // Bleu
                cardView.setAlpha(1.0f);
            }
            
            // Couleur de la card selon le type
            int backgroundColor = getTypeColor(challenge.getType());
            cardView.setCardBackgroundColor(backgroundColor);
            
            // Listeners
            actionButton.setOnClickListener(v -> {
                if (listener != null && !challenge.isCompleted()) {
                    challenge.setCompleted(true);
                    listener.onChallengeCompleted(challenge);
                    notifyItemChanged(getAdapterPosition());
                }
            });
            
            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onChallengeClicked(challenge);
                }
            });
        }
        
        private int getTypeColor(String type) {
            switch (type.toLowerCase()) {
                case "daily": return 0xFFE8F5E8; // Vert très clair
                case "weekly": return 0xFFE3F2FD; // Bleu très clair
                case "monthly": return 0xFFFCE4EC; // Rose très clair
                default: return 0xFFF5F5F5; // Gris très clair
            }
        }
    }
}