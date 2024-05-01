package com.example.uea;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Dashboard extends AppCompatActivity {

    MaterialCardView documentCard;
    TextView dashboardUsernameText,fineAmountTextView;
    ProgressBar dashboardProgressBar;
    String username;
    String regNum;
    int fineAmt;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        documentCard = findViewById(R.id.documentCardView);
        dashboardProgressBar = findViewById(R.id.dashboardProgress);
        dashboardUsernameText = findViewById(R.id.dashboardUsername);
        fineAmountTextView = findViewById(R.id.fineAmountText);

        dashboardProgressBar.setVisibility(View.VISIBLE);
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        username = user.name;
                        regNum = user.registrationNumber;
                        dashboardUsernameText.setText(username);
                        dashboardProgressBar.setVisibility(View.GONE);

                        FirebaseDatabase.getInstance().getReference("FineCount")
                                .child(regNum).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        FineCount fineCount = snapshot.getValue(FineCount.class);
                                        fineAmt = fineCount.fineCounter;
                                        fineAmt  = fineAmt * 200;
                                        fineAmountTextView.setText("Rs : " + fineAmt);
                                        dashboardProgressBar.setVisibility(View.GONE);
                                    }

                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(Dashboard.this, "Error retrieving data!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Dashboard.this, "Error retrieving data!", Toast.LENGTH_SHORT).show();
                    }
                });

        documentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this,DocumentDisplay.class);
                startActivity(intent);
            }
        });
    }
}