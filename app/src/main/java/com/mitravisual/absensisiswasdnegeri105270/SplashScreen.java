package com.mitravisual.absensisiswasdnegeri105270;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.mitravisual.absensisiswasdnegeri105270.Guru.GuruActivity;
import com.mitravisual.absensisiswasdnegeri105270.Login.Login;

public class SplashScreen extends AppCompatActivity {

    private TextView tvLogo;
    private ImageView ivLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        tvLogo = findViewById(R.id.tvLogo);
        ivLogo = findViewById(R.id.ivLogo);

        Animation animation_naik = AnimationUtils.loadAnimation(this, R.anim.transisi_naik);
        Animation animation_turun = AnimationUtils.loadAnimation(this, R.anim.transisi_turun);

        tvLogo.setAnimation(animation_turun);
        ivLogo.setAnimation(animation_naik);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(decorView.SYSTEM_UI_FLAG_FULLSCREEN);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences(Login.SHARED_PREF_NAME, 0);
                boolean hasLoggedIn = sharedPreferences.getBoolean("hasLoggedIn", false);

                if (hasLoggedIn){
                    startActivity(new Intent(getApplicationContext(), GuruActivity.class));
                    finish();
                }else{
                    startActivity(new Intent(getApplicationContext(), Login.class));
                    finish();
                }

            }
        }, 3000);
    }
}