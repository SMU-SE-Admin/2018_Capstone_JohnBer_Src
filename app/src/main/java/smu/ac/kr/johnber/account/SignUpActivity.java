package smu.ac.kr.johnber.account;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class SignUpActivity extends AppCompatActivity {

    // [START declare_auth]
    private FirebaseAuth mAuth;

    private EditText text_id;
    private EditText text_password;
    private Button button;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        text_id = (EditText)findViewById(R.id.signup_id);
        text_password = (EditText)findViewById(R.id.signup_password);
        button = (Button)findViewById(R.id.signup);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser(text_id.getText().toString(), text_password.getText().toString());
            }
        });

    }

    //사용자 회원가입
    private void createUser(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    //???
    private void updateUI(FirebaseUser user) {
    }


}
