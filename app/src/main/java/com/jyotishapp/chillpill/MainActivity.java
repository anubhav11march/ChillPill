package com.jyotishapp.chillpill;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        ImageView pis = findViewById(R.id.pist);
        ImageView dr = findViewById(R.id.drop);
        pis.startAnimation(AnimationUtils.loadAnimation(this, R.anim.piston));
        dr.startAnimation(AnimationUtils.loadAnimation(this, R.anim.drop));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        }, 1000);
    }
}
