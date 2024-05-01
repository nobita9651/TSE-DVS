package com.example.pea;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private static final String TAG = "Login";
    TextInputLayout username,password;
    Button login_btn;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        username = findViewById(R.id.username_txt);
        password = findViewById(R.id.password_txt);
        login_btn = findViewById(R.id.login_btn);

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
                    Intent intent = new Intent(Login.this,MainActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(Login.this, "Wrong username or password", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Wrong username or password");
                }
            }
        });
    }
}