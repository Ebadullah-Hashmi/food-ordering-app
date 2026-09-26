package com.example.semesterproject.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.semesterproject.R;
import com.example.semesterproject.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class SignupActivity extends BaseActivity {
ActivitySignupBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setVariable();

    }

    private void setVariable() {

        binding.signBtn.setOnClickListener(v -> {
            String email=binding.userEdt.getText().toString();
            String pass=binding.passEdt.getText().toString();

            if(pass.length()<6){
                Toast.makeText(SignupActivity.this, "Your Password must be 6 characters", Toast.LENGTH_SHORT).show();
            return;
            }

            mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(SignupActivity.this, task -> {
                if(task.isSuccessful())
                {
                    Log.i(TAG, "onComplete: ");
                    startActivity(new Intent(SignupActivity.this,MainActivity.class));

                }
                else
                {
                    Log.i(TAG, "Failed: ",task.getException());
                    Toast.makeText(SignupActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                }
            });

        });
        binding.loginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this,LoginActivity.class));
            }
        });
    }
}