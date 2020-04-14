package com.hrishi3331studio.template_informal.General;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hrishi3331studio.template_informal.R;
import com.hrishi3331studio.template_informal.UserAuth.Authentication;

public class Splash extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (mUser == null){
                    intent = new Intent(Splash.this, Authentication.class);
                }
                else {
                    intent = new Intent(Splash.this, MainActivity.class);
                }
                startActivity(intent);
                finish();
            }
        };

        handler.postDelayed(runnable, 1500);
    }
}
