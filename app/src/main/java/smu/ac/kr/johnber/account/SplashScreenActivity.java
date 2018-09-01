package smu.ac.kr.johnber.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import smu.ac.kr.johnber.R;
import smu.ac.kr.johnber.run.RunningActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash_screen);

        Intent intent = new Intent(getApplicationContext(), RunningActivity.class);
        startActivity(intent);
        finish();

    }
}
