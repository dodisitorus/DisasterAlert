package com.dodi.disasteralert;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.dodi.disasteralert.utils.UserPreferences;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (UserPreferences.getDataBool(UserPreferences.loginKey, getApplicationContext())) {
            startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
        } else {
            startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
        }

        finish();
    }
}
