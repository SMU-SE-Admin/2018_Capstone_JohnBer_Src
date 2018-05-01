package smu.ac.kr.johnber.account;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import smu.ac.kr.johnber.R;
import smu.ac.kr.johnber.util.LogUtils;



public class loginActivity extends AppCompatActivity {
    private static final String TAG = LogUtils.makeLogTag(loginActivity.class);
    private EditText mId;
    private EditText mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mId = findViewById(R.id.id_input);
        mId.getText();
        Toast.makeText(this, mId.toString(), Toast.LENGTH_SHORT).show();
    }
}


