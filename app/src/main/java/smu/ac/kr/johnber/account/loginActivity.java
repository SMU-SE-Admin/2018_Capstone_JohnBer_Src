package smu.ac.kr.johnber.account;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

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
