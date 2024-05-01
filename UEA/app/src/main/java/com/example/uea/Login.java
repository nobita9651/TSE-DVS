package com.example.uea;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    Button callSignUp,login_btn;
    ImageView logoImage;
    TextView signInText,welcomeText;
    TextInputLayout username,password;

    FirebaseAuth mAuth;
    private static final String TAG = "Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        logoImage = findViewById(R.id.topLogo);
        welcomeText = findViewById(R.id.wcl_txt);
        signInText = findViewById(R.id.sign_in_text);
        username = findViewById(R.id.username_txt);
        password = findViewById(R.id.password_txt);
        login_btn = findViewById(R.id.login_btn);
        callSignUp = findViewById(R.id.signUp);

        callSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this,SignUp.class);

                Pair[] pairs = new Pair[7];
                pairs[0] = new Pair<View,String>(logoImage,"logo_image");
                pairs[1] = new Pair<View,String>(welcomeText,"logo_text");
                pairs[2] = new Pair<View,String>(signInText,"signup_trn");
                pairs[3] = new Pair<View,String>(username,"username_trn");
                pairs[4] = new Pair<View,String>(password,"password_trn");
                pairs[5] = new Pair<View,String>(login_btn,"lg_su_trn");
                pairs[6] = new Pair<View,String>(callSignUp,"lg_su_txt_trn");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login.this,pairs);
                startActivity(intent,options.toBundle());

            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        String usrname = username.getEditText().getText().toString();
        String passwrd = password.getEditText().getText().toString();

        mAuth.signInWithEmailAndPassword(usrname,passwrd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(Login.this,Dashboard.class);
                    startActivity(intent);
                }
                else{
                    Log.d(TAG, "Wrong username or password");
                }
            }
        });
    }
}