package com.example.projet_android;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.projet_android.adapters.WellnessTipAdapter;
import com.example.projet_android.adapters.ChallengeAdapter;
import com.example.projet_android.models.WellnessTip;
import com.example.projet_android.models.Challenge;
import com.example.projet_android.utils.PreferencesManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Hub de bien-être avec conseils quotidiens, défis, méditation, et plus
 */
public class WellnessHubActivity extends AppCompatActivity {
    
    private TextView dailyMotivationTextView;
    private TextView wellnessScoreTextView;
    private TextView streakCountTextView;
    private RecyclerView tipsRecyclerView;
    private RecyclerView challengesRecyclerView;
    private CardView weatherCardView;
    private CardView meditationCardView;
    private CardView hydrationCardView;
    private CardView sleepAnalysisCardView;
    private Button moodTrackerButton;
    private Button bodyAnalysisButton;
    
    private PreferencesManager preferencesManager;
    private WellnessTipAdapter tipAdapter;
    private ChallengeAdapter challengeAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wellness_hub);
        
        preferencesManager = new PreferencesManager(this);
        
        initViews();
        setupRecyclerViews();
        loadWellnessData();
        setupClickListeners();
        updateDailyContent();
    }
    
    private void initViews() {
        dailyMotivationTextView = findViewById(R.id.tv_daily_motivation);
        wellnessScoreTextView = findViewById(R.id.tv_wellness_score);
        streakCountTextView = findViewById(R.id.tv_streak_count);
        tipsRecyclerView = findViewById(R.id.rv_wellness_tips);
        challengesRecyclerView = findViewById(R.id.rv_challenges);
        weatherCardView = findViewById(R.id.card_weather);
        meditationCardView = findViewById(R.id.card_meditation);
        hydrationCardView = findViewById(R.id.card_hydration);
        sleepAnalysisCardView = findViewById(R.id.card_sleep_analysis);
        moodTrackerButton = findViewById(R.id.btn_mood_tracker);
        bodyAnalysisButton = findViewById(R.id.btn_body_analysis);
    }
    
    private void setupRecyclerViews() {
        // Configuration RecyclerView conseils
        tipsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        tipAdapter = new WellnessTipAdapter(new ArrayList<>());
        tipsRecyclerView.setAdapter(tipAdapter);
          // Configuration RecyclerView défis
        challengesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        challengeAdapter = new ChallengeAdapter(new ArrayList<>(), new ChallengeAdapter.OnChallengeActionListener() {
            @Override
            public void onChallengeCompleted(Challenge challenge) {
                WellnessHubActivity.this.onChallengeCompleted(challenge);
            }
            
            @Override
            public void onChallengeClicked(Challenge challenge) {
                WellnessHubActivity.this.onChallengeClicked(challenge);
            }
        });
        challengesRecyclerView.setAdapter(challengeAdapter);
    }
    
    private void loadWellnessData() {
        // Charger les conseils wellness du jour
        List<WellnessTip> dailyTips = generateDailyWellnessTips();
        tipAdapter.updateTips(dailyTips);
        
        // Charger les défis actifs
        List<Challenge> activeChallenges = generateActiveChallenges();
        challengeAdapter.updateChallenges(activeChallenges);
        
        // Calculer le score de bien-être
        updateWellnessScore();
        
        // Mettre à jour la série (streak)
        updateStreak();
    }
    
    private void setupClickListeners() {
        weatherCardView.setOnClickListener(v -> {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
        });
        
        meditationCardView.setOnClickListener(v -> {
            Intent intent = new Intent(this, MeditationActivity.class);
            startActivity(intent);
        });
        
        hydrationCardView.setOnClickListener(v -> {
            Intent intent = new Intent(this, HydrationTrackerActivity.class);
            startActivity(intent);
        });
        
        sleepAnalysisCardView.setOnClickListener(v -> {
            Intent intent = new Intent(this, SleepAnalysisActivity.class);
            startActivity(intent);
        });
        
        moodTrackerButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MoodTrackerActivity.class);
            startActivity(intent);
        });
        
        bodyAnalysisButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, BodyAnalysisActivity.class);
            startActivity(intent);
        });
    }
    
    private void updateDailyContent() {
        // Update daily motivation
        String[] motivations = {
            "Every small step counts towards your wellness journey! 💪",
            "Today is a new opportunity to take care of yourself 🌟",
            "Your health is your wealth - invest in it today! 🏆",
            "Progress, not perfection - you've got this! 🚀",
            "Small healthy choices lead to big transformations 🌱"
        };
        int dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        dailyMotivationTextView.setText(motivations[dayOfYear % motivations.length]);
        
        // Update wellness score
        int wellnessScore = calculateWellnessScore();
        wellnessScoreTextView.setText(wellnessScore + "/100");
        
        // Update streak
        int currentStreak = preferencesManager.getWellnessStreak();
        streakCountTextView.setText(currentStreak + " days");
        
        // Check if today's activities are completed
        checkAndUpdateStreak();
    }
    
    private List<WellnessTip> generateDailyWellnessTips() {
        List<WellnessTip> tips = new ArrayList<>();
        
        Calendar cal = Calendar.getInstance();
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                tips.add(new WellnessTip("💧 Hydratation", "Commencez la semaine en buvant un grand verre d'eau au réveil", "hydration"));
                tips.add(new WellnessTip("🧘 Méditation", "5 minutes de méditation matinale pour clarifier vos intentions", "meditation"));
                tips.add(new WellnessTip("🥗 Nutrition", "Intégrez des légumes verts à votre premier repas", "nutrition"));
                break;
                
            case Calendar.TUESDAY:
                tips.add(new WellnessTip("🏃 Activité", "Marchez 10 minutes de plus qu'hier", "activity"));
                tips.add(new WellnessTip("😴 Sommeil", "Préparez votre routine du soir dès maintenant", "sleep"));
                tips.add(new WellnessTip("🌞 Vitamine D", "Sortez 15 minutes au soleil aujourd'hui", "sun"));
                break;
                
            case Calendar.WEDNESDAY:
                tips.add(new WellnessTip("🍎 Collation", "Choisissez un fruit plutôt qu'un snack industriel", "nutrition"));
                tips.add(new WellnessTip("💪 Force", "Faites 10 pompes ou squats pendant une pause", "strength"));
                tips.add(new WellnessTip("🧠 Mental", "Pratiquez la gratitude: notez 3 choses positives", "mental"));
                break;
                
            case Calendar.THURSDAY:
                tips.add(new WellnessTip("🚶 Mobilité", "Étirez-vous toutes les 2 heures si vous êtes assis", "mobility"));
                tips.add(new WellnessTip("💚 Respiration", "Pratiquez la respiration 4-7-8 pour vous détendre", "breathing"));
                tips.add(new WellnessTip("🥛 Protéines", "Incluez des protéines à chaque repas", "nutrition"));
                break;
                
            case Calendar.FRIDAY:
                tips.add(new WellnessTip("🎉 Récompense", "Célébrez vos progrès de la semaine !", "motivation"));
                tips.add(new WellnessTip("🛀 Détente", "Planifiez un moment de détente ce soir", "relaxation"));
                tips.add(new WellnessTip("📱 Digital", "Réduisez le temps d'écran avant le coucher", "digital_wellness"));
                break;
                
            case Calendar.SATURDAY:
                tips.add(new WellnessTip("🌿 Nature", "Passez du temps dans la nature aujourd'hui", "nature"));
                tips.add(new WellnessTip("👥 Social", "Connectez-vous avec un proche", "social"));
                tips.add(new WellnessTip("🍳 Cuisine", "Préparez un repas sain et coloré", "cooking"));
                break;
                
            case Calendar.SUNDAY:
                tips.add(new WellnessTip("📝 Planning", "Planifiez vos objectifs wellness pour la semaine", "planning"));
                tips.add(new WellnessTip("🧹 Organisation", "Organisez votre espace pour la semaine", "organization"));
                tips.add(new WellnessTip("😌 Bilan", "Réfléchissez aux leçons de cette semaine", "reflection"));
                break;
        }
        
        return tips;
    }
    
    private List<Challenge> generateActiveChallenges() {
        List<Challenge> challenges = new ArrayList<>();
        
        // Défis quotidiens
        challenges.add(new Challenge(
            "💧 Défi Hydratation", 
            "Buvez 8 verres d'eau aujourd'hui",
            "daily",
            false,
            50 // points
        ));
        
        challenges.add(new Challenge(
            "🚶 10,000 Pas", 
            "Atteignez 10,000 pas aujourd'hui",
            "daily",
            preferencesManager.isChallengeCompleted("steps_10k_today"),
            100
        ));
        
        challenges.add(new Challenge(
            "🧘 Méditation 10min", 
            "Méditez pendant 10 minutes",
            "daily",
            preferencesManager.isChallengeCompleted("meditation_10min_today"),
            75
        ));
        
        // Défis hebdomadaires
        challenges.add(new Challenge(
            "🌿 7 Jours Nature", 
            "Sortez dans la nature 7 jours consécutifs",
            "weekly",
            false,
            200
        ));
        
        challenges.add(new Challenge(
            "💪 Séance Sport 3x", 
            "Faites 3 séances de sport cette semaine",
            "weekly",
            preferencesManager.getWeeklyWorkouts() >= 3,
            150
        ));
        
        // Défis mensuels
        challenges.add(new Challenge(
            "📱 Digital Detox", 
            "Réduisez votre temps d'écran de 30% ce mois",
            "monthly",
            false,
            300
        ));
        
        return challenges;
    }
    
    private void updateWellnessScore() {
        int score = calculateWellnessScore();
        wellnessScoreTextView.setText(score + "/100");
        
        // Changer la couleur selon le score
        if (score >= 80) {
            wellnessScoreTextView.setTextColor(getColor(R.color.green_primary));
        } else if (score >= 60) {
            wellnessScoreTextView.setTextColor(getColor(R.color.orange_primary));
        } else {
            wellnessScoreTextView.setTextColor(getColor(R.color.red_primary));
        }
    }
    
    private int calculateWellnessScore() {
        int score = 0;
        
        // Hydration (20 points)
        int dailyWater = preferencesManager.getDailyWaterIntake();
        int waterGoal = preferencesManager.getWaterGoal();
        if (waterGoal > 0) {
            score += Math.min(20, (dailyWater * 20) / waterGoal);
        }
        
        // Steps (20 points)
        int dailySteps = preferencesManager.getDailySteps();
        int stepsGoal = preferencesManager.getStepsGoal();
        if (stepsGoal > 0) {
            score += Math.min(20, (dailySteps * 20) / stepsGoal);
        }
        
        // Sleep (20 points)
        int sleepHours = preferencesManager.getLastNightSleep();
        if (sleepHours >= 7 && sleepHours <= 9) {
            score += 20;
        } else if (sleepHours >= 6 && sleepHours <= 10) {
            score += 15;
        } else if (sleepHours >= 5) {
            score += 10;
        }
        
        // Meditation (15 points)
        if (preferencesManager.hasMeditatedToday()) {
            score += 15;
        }
        
        // Mood tracking (10 points)
        if (preferencesManager.hasLoggedMoodToday()) {
            score += 10;
        }
        
        // Body measurements (15 points)
        long lastBodyUpdate = preferencesManager.getLastBodyAnalysisUpdate();
        long daysSinceUpdate = (System.currentTimeMillis() - lastBodyUpdate) / (24 * 60 * 60 * 1000);
        if (daysSinceUpdate <= 7) {
            score += 15;
        } else if (daysSinceUpdate <= 14) {
            score += 10;
        } else if (daysSinceUpdate <= 30) {
            score += 5;
        }
        
        return Math.min(100, score);
    }
    
    private void updateStreak() {
        int currentStreak = preferencesManager.getCurrentWellnessStreak();
        streakCountTextView.setText(currentStreak + " jours");
        
        // Animation ou couleur selon la série
        if (currentStreak >= 7) {
            streakCountTextView.setTextColor(getColor(R.color.gold));
        } else if (currentStreak >= 3) {
            streakCountTextView.setTextColor(getColor(R.color.green_primary));
        } else {
            streakCountTextView.setTextColor(getColor(R.color.text_secondary));
        }
    }
    
    private void checkAndUpdateStreak() {
        // Check if minimum daily activities are completed
        boolean hasCompletedMinimum = 
            preferencesManager.getDailyWaterIntake() >= (preferencesManager.getWaterGoal() * 0.7) ||
            preferencesManager.getDailySteps() >= (preferencesManager.getStepsGoal() * 0.7) ||
            preferencesManager.hasLoggedMoodToday();
        
        if (hasCompletedMinimum && !preferencesManager.hasStreakBeenUpdatedToday()) {
            int currentStreak = preferencesManager.getWellnessStreak();
            preferencesManager.setWellnessStreak(currentStreak + 1);
            preferencesManager.setStreakUpdatedToday(true);
            
            // Award bonus points for streaks
            if ((currentStreak + 1) % 7 == 0) {
                preferencesManager.addWellnessPoints(50); // Weekly streak bonus
                showStreakCelebration(currentStreak + 1);
            }
        }
    }
    
    private void showStreakCelebration(int streak) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("🎉 Streak Milestone!");
        builder.setMessage("Congratulations! You've maintained your wellness routine for " + 
                          streak + " days straight!\n\n+50 bonus points awarded! 🏆");
        builder.setPositiveButton("Amazing!", null);
        builder.show();
    }
    
    private void onChallengeCompleted(Challenge challenge) {
        // Marquer le défi comme terminé
        preferencesManager.markChallengeCompleted(challenge.getId());
        
        // Ajouter des points
        int currentPoints = preferencesManager.getWellnessPoints();
        preferencesManager.addWellnessPoints(challenge.getPoints());
        
        // Afficher une notification de succès
        showChallengeCompletionDialog(challenge);
        
        // Mettre à jour l'affichage
        updateWellnessScore();
        loadWellnessData();
    }
    
    private void onChallengeClicked(Challenge challenge) {
        // Afficher les détails du défi ou permettre de l'accepter
        showChallengeDetailsDialog(challenge);
    }
    
    private void showChallengeCompletionDialog(Challenge challenge) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Challenge Completed! 🎉");
        builder.setMessage("Great job completing: " + challenge.getTitle() + 
                          "\n\nYou earned " + challenge.getPoints() + " wellness points!");
        builder.setPositiveButton("Awesome!", null);
        builder.show();
    }
    
    private void showChallengeDetailsDialog(Challenge challenge) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(challenge.getTitle());
        builder.setMessage(challenge.getDescription() + 
                          "\n\nReward: " + challenge.getPoints() + " points");
        
        if (!challenge.isCompleted()) {
            builder.setPositiveButton("Start Challenge", (dialog, which) -> {
                startChallengeActivity(challenge);
            });
        }
        builder.setNegativeButton("Close", null);
        builder.show();
    }
    
    private void startChallengeActivity(Challenge challenge) {
        Intent intent;
        switch (challenge.getType()) {
            case "meditation":
                intent = new Intent(this, MeditationActivity.class);
                break;
            case "hydration":
                intent = new Intent(this, HydrationTrackerActivity.class);
                break;
            case "mood":
                intent = new Intent(this, MoodTrackerActivity.class);
                break;
            case "body":
                intent = new Intent(this, BodyAnalysisActivity.class);
                break;
            case "sleep":
                intent = new Intent(this, SleepAnalysisActivity.class);
                break;
            default:
                return;
        }
        startActivity(intent);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Rafraîchir les données quand on revient sur l'activité
        loadWellnessData();
        updateWellnessScore();
    }
}
