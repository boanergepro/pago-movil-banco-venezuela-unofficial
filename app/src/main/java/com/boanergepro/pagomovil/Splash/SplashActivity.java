package com.boanergepro.pagomovil.Splash;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;

import com.boanergepro.pagomovil.R;
import com.boanergepro.pagomovil.activities.FormActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceSatte) {
        super.onCreate(savedInstanceSatte);

        Intent intent = new Intent(this, FormActivity.class);
        startActivity(intent);
    }
}
