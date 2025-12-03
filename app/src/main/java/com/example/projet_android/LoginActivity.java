package com.example.projet_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projet_android.services.AuthManager;
import com.example.projet_android.utils.PreferencesManager;

public class LoginActivity extends AppCompatActivity implements AuthManager.AuthListener {
    
    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnGoogleLogin;
    private TextView tvRegisterLink;
    private TextView tvForgotPassword;
    private ProgressBar progressBar;
    
    private AuthManager authManager;
    private PreferencesManager preferencesManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        authManager = new AuthManager(this);
        preferencesManager = new PreferencesManager(this);
        
        initViews();
        setupClickListeners();
    }
    
    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnGoogleLogin = findViewById(R.id.btn_google_login);
        tvRegisterLink = findViewById(R.id.tv_register_link);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        progressBar = findViewById(R.id.progress_bar_login);
    }
    
    private void setupClickListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
        
        btnGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithGoogle();
            }
        });
        
        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }
    
    private void attemptLogin() {
        // Reset errors
        etEmail.setError(null);
        etPassword.setError(null);
        
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        
        boolean cancel = false;
        View focusView = null;
        
        // Vérifier le mot de passe
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Ce champ est requis");
            focusView = etPassword;
            cancel = true;
        } else if (password.length() < 6) {
            etPassword.setError("Le mot de passe doit contenir au moins 6 caractères");
            focusView = etPassword;
            cancel = true;
        }
        
        // Vérifier l'email
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Ce champ est requis");
            focusView = etEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            etEmail.setError("Adresse email invalide");
            focusView = etEmail;
            cancel = true;
        }
        
        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            authManager.loginWithEmail(email, password, this);
        }
    }
    
    private void loginWithGoogle() {
        showProgress(true);
        authManager.loginWithGoogle(this, this);
    }
    
    private void resetPassword() {
        String email = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Entrez votre adresse email pour la réinitialisation");
            etEmail.requestFocus();
            return;
        }
        
        authManager.resetPassword(email, new AuthManager.ResetPasswordListener() {
            @Override
            public void onResetPasswordSuccess() {
                Toast.makeText(LoginActivity.this, 
                              "Instructions de réinitialisation envoyées par email", 
                              Toast.LENGTH_LONG).show();
            }
            
            @Override
            public void onResetPasswordError(String error) {
                Toast.makeText(LoginActivity.this, "Erreur: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
    
    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    
    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!show);
        btnGoogleLogin.setEnabled(!show);
    }
    
    @Override
    public void onAuthSuccess(String userId, String email, String displayName) {
        showProgress(false);
        
        // Sauvegarder les informations utilisateur
        preferencesManager.setUserLoggedIn(true);
        preferencesManager.setUserId(userId);
        preferencesManager.setUserEmail(email);
        if (displayName != null && !displayName.isEmpty()) {
            preferencesManager.setUserName(displayName);
        }
        
        Toast.makeText(this, "Connexion réussie !", Toast.LENGTH_SHORT).show();
        
        // Naviguer vers le dashboard
        Intent intent = new Intent(LoginActivity.this, MainActivity2.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    @Override
    public void onAuthError(String error) {
        showProgress(false);
        Toast.makeText(this, "Erreur de connexion: " + error, Toast.LENGTH_LONG).show();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        authManager.handleActivityResult(requestCode, resultCode, data);
    }
}
