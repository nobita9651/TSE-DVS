package com.example.uea;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    TextInputLayout regFullName,regRegistrationNumber,regEmail,regPhone,regPassword;
    Button signUp;
    private FirebaseAuth mAuth;
    public static final String TAG = "SignUp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        regFullName = findViewById(R.id.name);
        regRegistrationNumber = findViewById(R.id.regNum);
        regPhone = findViewById(R.id.phoneNo);
        regEmail = findViewById(R.id.email);
        regPassword = findViewById(R.id.password);

        mAuth = FirebaseAuth.getInstance();

        signUp = findViewById(R.id.signUpButton);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
                Intent intent = new Intent(SignUp.this,DocumentUpload.class);
                startActivity(intent);
            }
        });
    }

    private Boolean validateName(){
        String val = regFullName.getEditText().getText().toString();
        if(val.isEmpty()){
            regFullName.setError("Field cannot be empty");
            return false;
        }
        else{
            regFullName.setError(null);
            regFullName.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateRegistrationNumber(){
        String val = regRegistrationNumber.getEditText().getText().toString();
        if(val.length() < 10){
            regRegistrationNumber.setError("Invalid Registration Number");
            return false;
        }
        else{
            regRegistrationNumber.setError(null);
            regRegistrationNumber.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateEmail(){
        String val = regEmail.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._]+@[a-z]+\\.+[a-z]+";
        if(val.isEmpty()){
            regEmail.setError("Field cannot be empty");
            return false;
        }
        else if(!val.matches(emailPattern)){
            regEmail.setError("Invalid Email address");
            return false;
        }
        else{
            regEmail.setError(null);
            regEmail.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePhoneNo(){
        String val = regPhone.getEditText().getText().toString();
        if(val.isEmpty()){
            regPhone.setError("Field cannot be empty");
            return false;
        }
        else{
            regPhone.setError(null);
            regPhone.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword(){
        String val = regPassword.getEditText().getText().toString();
        String passwordVal = "^" +
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=])" +    //at least one special character
                "(?=\\S+$)" +           //no white spaces
                ".{4,}" +               //at least four characters
                "$";
        if(val.isEmpty()){
            regPassword.setError("Field cannot be empty");
            return false;
        }
        else if(!val.matches(passwordVal)){
            regPassword.setError("Password is too weak");
            return false;
        }
        else{
            regPassword.setError(null);
            regPassword.setErrorEnabled(false);
            return true;
        }
    }

    public void registerUser(){

        if(!validateName() | !validateRegistrationNumber() | !validateEmail() | !validatePhoneNo() | !validatePassword()){
            return;
        }

        String name = regFullName.getEditText().getText().toString();
        String registrationNumber = regRegistrationNumber.getEditText().getText().toString();
        String phoneNo = regPhone.getEditText().getText().toString();
        String email = regEmail.getEditText().getText().toString();
        String password = regPassword.getEditText().getText().toString();

        User user = new User(name,registrationNumber,phoneNo,email);
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Log.d(TAG, "User added and registered");
                                    }
                                    else{
                                        Log.d(TAG, "Failed to add user");
                                    }
                                }
                            });
                }
                else{
                    Log.d(TAG, "Failed to create user");
                }
            }
        });
    }
}