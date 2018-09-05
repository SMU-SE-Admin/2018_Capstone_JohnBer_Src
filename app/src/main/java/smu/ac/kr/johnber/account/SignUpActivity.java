package smu.ac.kr.johnber.account;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import smu.ac.kr.johnber.R;

public class SignUpActivity extends AppCompatActivity {

    // [START declare_auth]
    private FirebaseAuth mAuth;
    private Spinner gender;
    private EditText weight;
    private EditText height;
    private EditText text_id;
    private EditText text_nickname;
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
        text_nickname = findViewById(R.id.ed_nickname);
        weight = findViewById(R.id.weightt_input);
        height = findViewById(R.id.height_input);
        gender = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.signup_gender_spinner, new String[]{"남성", "여성"});
        gender.setAdapter(adapter);
        gender.setSelection(0);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser(text_id.getText().toString()
                        ,text_password.getText().toString()
                        ,text_nickname.getText().toString()
                        ,Double.parseDouble(weight.getText().toString())
                        ,Double.parseDouble(height.getText().toString())
                        ,gender.getSelectedItem().toString());
            }
        });
    }

    //사용자 회원가입
    private void createUser(String email, String password, final String nickname, final double weight, final double height, final String gender){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
//                            .setPhotoUri(Uri.parse(null)))    //  프로필 사진 세팅
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            user.updateProfile(new UserProfileChangeRequest.Builder()
                            .setDisplayName(nickname).build());

                            updateUI(user);
                            finish();
                            // /userProfile 신상 정보 저장
                            // Write data to the database
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference();
                            UserProfile userProfile = new UserProfile(user.getUid(), weight, height, gender);
                            myRef.child(user.getUid()).child("userProfile").setValue(userProfile);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
    }
}
