package com.example.uea;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.card.MaterialCardView;

public class DocumentUpload extends AppCompatActivity {

    MaterialCardView regCertUpload,puCertUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_upload);

        regCertUpload = findViewById(R.id.regCertBox);
        puCertUpload = findViewById(R.id.puCertBox);

        regCertUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DocumentUpload.this,RegCertUpload.class);
                startActivity(intent);
            }
        });
    }
}