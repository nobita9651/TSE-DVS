package com.example.uea;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DocumentDisplay extends AppCompatActivity {

    ProgressBar docRetriveProgressBar;
    TextView regCertExpiryText;
    TextView polCertExpiryText;
    String registrationNum;
    String regCertExpiryDate;
    String polCertExpiryDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_display);

        docRetriveProgressBar = findViewById(R.id.docFetchProgress);
        regCertExpiryText = findViewById(R.id.rcValidDate);
        polCertExpiryText = findViewById(R.id.pucValidDate);

        docRetriveProgressBar.setVisibility(View.VISIBLE);
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        registrationNum = user.registrationNumber;

                        FirebaseDatabase.getInstance().getReference("RegistrationCertificate")
                                .child(registrationNum).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        RegCertInfo regCertInfo = snapshot.getValue(RegCertInfo.class);
                                        regCertExpiryDate = regCertInfo.rcExpiryDate;
                                        regCertExpiryText.setText("Valid Upto : " + regCertExpiryDate);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(DocumentDisplay.this, "Error retrieving data!", Toast.LENGTH_SHORT).show();
                                    }
                                });

                        FirebaseDatabase.getInstance().getReference("PollutionCertificate")
                                .child(registrationNum).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        PoluCertInfo poluCertInfo = snapshot.getValue(PoluCertInfo.class);
                                        polCertExpiryDate = poluCertInfo.polCertExpiry;
                                        polCertExpiryText.setText("Valid Upto : " + polCertExpiryDate);
                                        docRetriveProgressBar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(DocumentDisplay.this, "Error retrieving data!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(DocumentDisplay.this, "Error retrieving data!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}