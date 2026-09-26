package com.example.semesterproject.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.semesterproject.databinding.ActivityLoginBinding;

public class LoginActivity extends BaseActivity {

    ActivityLoginBinding binding;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        setVariable();
    }

    private void setVariable() {
        binding.loginBtn.setOnClickListener(v -> {
            String email = binding.userEdt.getText().toString();
            String password = binding.passEdt.getText().toString();

            if (!email.isEmpty() && !password.isEmpty()) {
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, task -> {
                    if (task.isSuccessful()) {
                        // Save email and password to SharedPreferences
                        saveToSharedPreferences(email, password);

                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(LoginActivity.this, "Please Fill username and password", Toast.LENGTH_SHORT).show();
            }
        });

        binding.sigTxt.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, SignupActivity.class)));
    }

    private void saveToSharedPreferences(String email, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.apply(); // Save asynchronously
        Toast.makeText(this, "Credentials saved locally", Toast.LENGTH_SHORT).show();
    }
}
