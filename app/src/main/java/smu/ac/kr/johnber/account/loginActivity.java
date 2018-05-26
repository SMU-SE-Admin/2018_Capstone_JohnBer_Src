package smu.ac.kr.johnber.account;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import smu.ac.kr.johnber.R;
import smu.ac.kr.johnber.run.MainActivity;
import smu.ac.kr.johnber.util.LogUtils;

import static smu.ac.kr.johnber.util.LogUtils.LOGD;


public class loginActivity extends AppCompatActivity {
        private static final String TAG = LogUtils.makeLogTag(loginActivity.class);
        private EditText text_id;
        private EditText text_password;
        private Button button_login;
        private Button button_sign;

        // [START declare_auth]
        private FirebaseAuth mAuth;


    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);
            LOGD(TAG, "메시지");

            mAuth = FirebaseAuth.getInstance();

            text_id = (EditText)findViewById(R.id.login_id);
            text_password = (EditText)findViewById(R.id.login_password);
            button_login = (Button)findViewById(R.id.btn_login);
            button_sign = (Button)findViewById(R.id.btn_signup);

            button_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loginUser(text_id.getText().toString(), text_password.getText().toString());
                }
            });

            button_sign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                    startActivity(intent);
                }
            });


        }


        private void loginUser(String email, String password){
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(loginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });
        }







}


