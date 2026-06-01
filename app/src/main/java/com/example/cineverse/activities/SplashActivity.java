package com.example.cineverse.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;  // ✅ ImageView au lieu de TextView
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cineverse.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView imageLogo  = findViewById(R.id.imageLogo);   // ✅ corrigé
        TextView textAppName = findViewById(R.id.textAppName);
        TextView textTagline = findViewById(R.id.textTagline);
        View     progressBar = findViewById(R.id.progressBar);

        // Départ invisible
        imageLogo.setAlpha(0f);                                 // ✅ corrigé
        textAppName.setAlpha(0f);
        textTagline.setAlpha(0f);
        progressBar.setAlpha(0f);
        imageLogo.setTranslationY(-40f);                        // ✅ corrigé
        textAppName.setTranslationY(30f);
        textTagline.setTranslationY(30f);

        // Animation logo
        ObjectAnimator logoAlpha = ObjectAnimator
                .ofFloat(imageLogo, "alpha", 0f, 1f)            // ✅ corrigé
                .setDuration(500);
        ObjectAnimator logoY = ObjectAnimator
                .ofFloat(imageLogo, "translationY", -40f, 0f)   // ✅ corrigé
                .setDuration(500);

        // Animation nom app
        ObjectAnimator nameAlpha = ObjectAnimator
                .ofFloat(textAppName, "alpha", 0f, 1f)
                .setDuration(500);
        ObjectAnimator nameY = ObjectAnimator
                .ofFloat(textAppName, "translationY", 30f, 0f)
                .setDuration(500);

        // Animation tagline
        ObjectAnimator tagAlpha = ObjectAnimator
                .ofFloat(textTagline, "alpha", 0f, 1f)
                .setDuration(400);
        ObjectAnimator tagY = ObjectAnimator
                .ofFloat(textTagline, "translationY", 30f, 0f)
                .setDuration(400);

        // Animation progressBar
        ObjectAnimator progressAlpha = ObjectAnimator
                .ofFloat(progressBar, "alpha", 0f, 1f)
                .setDuration(300);

        // Jouer dans l'ordre
        AnimatorSet set = new AnimatorSet();
        set.play(logoAlpha).with(logoY);
        set.play(nameAlpha).with(nameY).after(200);
        set.play(tagAlpha).with(tagY).after(400);
        set.play(progressAlpha).after(600);
        set.start();

        // Aller à CollectionActivity après 2.5s
        new Handler(Looper.getMainLooper())
                .postDelayed(() -> {
                    startActivity(new Intent(
                            SplashActivity.this,
                            CollectionActivity.class
                    ));
                    overridePendingTransition(
                            android.R.anim.fade_in,
                            android.R.anim.fade_out
                    );
                    finish();
                }, 2500);
    }
}