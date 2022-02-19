package com.example.meghaprototype1;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.meghaprototype1.activity.Homepage;
import com.example.meghaprototype1.auth.AuthActivity;
import com.google.firebase.auth.FirebaseAuth;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.enterAppBtn).setOnClickListener(view -> {
            if (mAuth.getCurrentUser() != null) {
                startActivity(new Intent(SplashScreen.this, Homepage.class));
            }else {
                startActivity(new Intent(SplashScreen.this, AuthActivity.class));
            }
            finish();

        });


    }
}