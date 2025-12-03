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

public class AuthManager {
    
    private static final String TAG = "AuthManager";
    private static final int RC_SIGN_IN = 9001;
    
    private Context context;
    private GoogleSignInClient googleSignInClient;
    
    // Simulation d'une base de données locale pour les comptes
    private static Map<String, UserAccount> localAccounts = new HashMap<>();
    
    public interface AuthListener {
        void onAuthSuccess(String userId, String email, String displayName);
        void onAuthError(String error);
    }
    
    public interface ResetPasswordListener {
        void onResetPasswordSuccess();
        void onResetPasswordError(String error);
    }
    
    private static class UserAccount {
        String email;
        String password;
        String displayName;
        String userId;
        
        UserAccount(String email, String password, String displayName) {
            this.email = email;
            this.password = password;
            this.displayName = displayName;
            this.userId = "user_" + System.currentTimeMillis();
        }
    }
    
    public AuthManager(Context context) {
        this.context = context;
        setupGoogleSignIn();
    }
    
    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        
        googleSignInClient = GoogleSignIn.getClient(context, gso);
    }
    
    public void loginWithEmail(String email, String password, AuthListener listener) {
        // Simulation d'une authentification réseau
        new Thread(() -> {
            try {
                // Simuler un délai réseau
                Thread.sleep(1500);
                
                // Vérifier les identifiants
                UserAccount account = localAccounts.get(email);
                if (account != null && account.password.equals(password)) {
                    // Succès
                    ((Activity) context).runOnUiThread(() -> 
                        listener.onAuthSuccess(account.userId, account.email, account.displayName));
                } else {
                    // Échec
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
