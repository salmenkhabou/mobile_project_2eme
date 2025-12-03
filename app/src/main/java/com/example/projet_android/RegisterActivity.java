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

public class RegisterActivity extends AppCompatActivity implements AuthManager.AuthListener {
    
    private EditText etFullName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Button btnRegister;
    private TextView tvLoginLink;
    private ProgressBar progressBar;
    
    private AuthManager authManager;
    private PreferencesManager preferencesManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        authManager = new AuthManager(this);
        preferencesManager = new PreferencesManager(this);
        
        initViews();
        setupClickListeners();
    }
    
    private void initViews() {
        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        tvLoginLink = findViewById(R.id.tv_login_link);
        progressBar = findViewById(R.id.progress_bar_register);
    }
    
    private void setupClickListeners() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });
        
        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Retourner à LoginActivity
            }
        });
    }
    
    private void attemptRegister() {
        // Reset errors
        etFullName.setError(null);
        etEmail.setError(null);
        etPassword.setError(null);
        etConfirmPassword.setError(null);
        
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        
        boolean cancel = false;
        View focusView = null;
        
        // Vérifier la confirmation du mot de passe
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Les mots de passe ne correspondent pas");
            focusView = etConfirmPassword;
            cancel = true;
        }
        
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
        
        // Vérifier le nom complet
        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Ce champ est requis");
            focusView = etFullName;
            cancel = true;
        } else if (fullName.length() < 2) {
            etFullName.setError("Le nom doit contenir au moins 2 caractères");
            focusView = etFullName;
            cancel = true;
        }
        
        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            authManager.registerWithEmail(email, password, fullName, this);
        }
    }
    
    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    
    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!show);
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
        
        Toast.makeText(this, "Inscription réussie !", Toast.LENGTH_SHORT).show();
        
        // Naviguer vers le dashboard
        Intent intent = new Intent(RegisterActivity.this, MainActivity2.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    @Override
    public void onAuthError(String error) {
        showProgress(false);
        Toast.makeText(this, "Erreur d'inscription: " + error, Toast.LENGTH_LONG).show();
    }
}
