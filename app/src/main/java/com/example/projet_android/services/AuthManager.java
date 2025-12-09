package com.example.projet_android.services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.Map;

/**
 * =======================================
 * GESTIONNAIRE D'AUTHENTIFICATION HEALTH TRACKER
 * =======================================
 * 
 * Service central pour la gestion de l'authentification utilisateur dans l'application Health Tracker.
 * 
 * FONCTIONNALITÉS PRINCIPALES :
 * • Authentification par email/mot de passe avec simulation de base de données locale
 * • Intégration complète avec Google Sign-In pour l'authentification sociale
 * • Système d'inscription avec validation et gestion des doublons
 * • Réinitialisation de mot de passe avec vérification email
 * • Gestion des sessions utilisateur et déconnexion sécurisée
 * • Interface callback pour la communication asynchrone avec les activités
 * 
 * MÉTHODES D'AUTHENTIFICATION SUPPORTÉES :
 * • 📧 Connexion traditionnelle (email + mot de passe)
 * • 🔍 Connexion Google (OAuth2 avec Google Sign-In API)
 * • 📝 Inscription nouveau compte avec validation
 * • 🔒 Réinitialisation mot de passe
 * 
 * GESTION DES DONNÉES :
 * • Simulation locale des comptes utilisateurs (HashMap)
 * • Génération automatique d'IDs utilisateur uniques
 * • Stockage temporaire pour tests et développement
 * • Interface prête pour intégration Firebase/backend réel
 * 
 * @version 1.0
 * @author Équipe Health Tracker
 */
public class AuthManager {
      // ============ CONSTANTES DE CONFIGURATION ============
    private static final String TAG = "AuthManager";  // Tag pour les logs de débogage
    private static final int RC_SIGN_IN = 9001;  // Code de requête pour Google Sign-In
    
    // ============ VARIABLES MEMBRES ============
    private Context context;  // Contexte de l'application
    private GoogleSignInClient googleSignInClient;  // Client Google Sign-In configuré
    
    // Simulation d'une base de données locale pour les comptes utilisateurs
    // En production, ceci serait remplacé par une base de données réelle (Firebase, SQLite, etc.)
    private static Map<String, UserAccount> localAccounts = new HashMap<>();
    
    // ============ INTERFACES CALLBACK ============
    
    /**
     * Interface pour les callbacks d'authentification
     * Permet la communication asynchrone entre le service et les activités
     */
    public interface AuthListener {
        void onAuthSuccess(String userId, String email, String displayName);
        void onAuthError(String error);
    }
    
    /**
     * Interface pour les callbacks de réinitialisation de mot de passe
     * Gère les retours de la fonctionnalité "Mot de passe oublié"
     */
    public interface ResetPasswordListener {
        void onResetPasswordSuccess();
        void onResetPasswordError(String error);
    }
    
    // ============ CLASSE INTERNE POUR LES COMPTES UTILISATEUR ============
    
    /**
     * Modèle de données pour représenter un compte utilisateur
     * Structure simple pour la simulation locale des comptes
     */
    private static class UserAccount {
        String email;        // Adresse email de l'utilisateur
        String password;     // Mot de passe (hashé en production)
        String displayName;  // Nom d'affichage de l'utilisateur
        String userId;       // Identifiant unique généré automatiquement
        
        /**
         * Constructeur pour créer un nouveau compte utilisateur
         * Génère automatiquement un ID unique basé sur le timestamp
         */
        UserAccount(String email, String password, String displayName) {
            this.email = email;
            this.password = password;
            this.displayName = displayName;
            this.userId = "user_" + System.currentTimeMillis();  // ID unique basé sur le temps
        }
    }
      // ============ CONSTRUCTEUR ET INITIALISATION ============
    
    /**
     * Constructeur principal du gestionnaire d'authentification
     * Initialise le contexte et configure les services d'authentification
     * 
     * @param context Contexte de l'application Android
     */
    public AuthManager(Context context) {
        this.context = context;
        setupGoogleSignIn();
    }
    
    /**
     * Configuration du client Google Sign-In
     * Définit les permissions et scopes requis pour l'authentification Google
     */
    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()     // Demander l'accès à l'email
                .requestProfile()   // Demander l'accès au profil (nom, photo)
                .build();
        
        googleSignInClient = GoogleSignIn.getClient(context, gso);
    }
    
    // ============ MÉTHODES D'AUTHENTIFICATION ============
    
    /**
     * Authentification par email et mot de passe
     * Simule une authentification réseau avec vérification locale des identifiants
     * 
     * @param email Adresse email de l'utilisateur
     * @param password Mot de passe de l'utilisateur
     * @param listener Callback pour recevoir le résultat de l'authentification
     */
    public void loginWithEmail(String email, String password, AuthListener listener) {
        // Exécution asynchrone pour simuler une requête réseau
        new Thread(() -> {
            try {
                // Simulation d'un délai réseau réaliste (1.5 secondes)
                Thread.sleep(1500);
                
                // Vérification des identifiants dans la base de données locale
                UserAccount account = localAccounts.get(email);
                if (account != null && account.password.equals(password)) {
                    // Authentification réussie - retour sur le thread UI
                    ((Activity) context).runOnUiThread(() -> 
                        listener.onAuthSuccess(account.userId, account.email, account.displayName));
                } else {
                    // Identifiants incorrects - retour d'erreur
                    ((Activity) context).runOnUiThread(() -> 
                        listener.onAuthError("Email ou mot de passe incorrect"));
                }
                
            } catch (InterruptedException e) {
                ((Activity) context).runOnUiThread(() -> 
                    listener.onAuthError("Erreur de connexion"));
            }
        }).start();
    }
    
    public void registerWithEmail(String email, String password, String displayName, AuthListener listener) {
        // Simulation d'une inscription réseau
        new Thread(() -> {
            try {
                // Simuler un délai réseau
                Thread.sleep(2000);
                
                // Vérifier si l'email existe déjà
                if (localAccounts.containsKey(email)) {
                    ((Activity) context).runOnUiThread(() -> 
                        listener.onAuthError("Un compte avec cet email existe déjà"));
                    return;
                }
                
                // Créer un nouveau compte
                UserAccount newAccount = new UserAccount(email, password, displayName);
                localAccounts.put(email, newAccount);
                
                Log.d(TAG, "Nouveau compte créé: " + email);
                
                // Succès
                ((Activity) context).runOnUiThread(() -> 
                    listener.onAuthSuccess(newAccount.userId, newAccount.email, newAccount.displayName));
                
            } catch (InterruptedException e) {
                ((Activity) context).runOnUiThread(() -> 
                    listener.onAuthError("Erreur lors de l'inscription"));
            }
        }).start();
    }
    
    public void loginWithGoogle(Activity activity, AuthListener listener) {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
        
        // Stocker le listener pour l'utiliser dans handleActivityResult
        this.currentAuthListener = listener;
    }
    
    private AuthListener currentAuthListener;
    
    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null && currentAuthListener != null) {
                    String userId = "google_" + account.getId();
                    currentAuthListener.onAuthSuccess(userId, account.getEmail(), account.getDisplayName());
                }
            } catch (ApiException e) {
                Log.w(TAG, "Échec de la connexion Google", e);
                if (currentAuthListener != null) {
                    currentAuthListener.onAuthError("Échec de la connexion Google");
                }
            }
        }
    }
    
    public void resetPassword(String email, ResetPasswordListener listener) {
        // Simulation d'une réinitialisation de mot de passe
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                
                if (localAccounts.containsKey(email)) {
                    ((Activity) context).runOnUiThread(listener::onResetPasswordSuccess);
                } else {
                    ((Activity) context).runOnUiThread(() -> 
                        listener.onResetPasswordError("Aucun compte trouvé avec cet email"));
                }
                
            } catch (InterruptedException e) {
                ((Activity) context).runOnUiThread(() -> 
                    listener.onResetPasswordError("Erreur lors de la réinitialisation"));
            }
        }).start();
    }
    
    public void logout() {
        // Déconnexion Google
        googleSignInClient.signOut();
        
        Log.d(TAG, "Utilisateur déconnecté");
    }
    
    // Méthodes utilitaires pour les tests
    public static void addTestAccount() {
        localAccounts.put("test@example.com", 
            new UserAccount("test@example.com", "123456", "Utilisateur Test"));
    }
    
    public static void clearAllAccounts() {
        localAccounts.clear();
    }
    
    static {
        // Ajouter un compte de test par défaut
        addTestAccount();
    }
}
