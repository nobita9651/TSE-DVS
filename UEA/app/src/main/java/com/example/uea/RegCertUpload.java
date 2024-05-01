package com.example.uea;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RegCertUpload extends AppCompatActivity {

    private Button regCertSelect;
    private Button regCertUpload;
    RegCertClass regCertObj = new RegCertClass();
    String resUrl =  "";
    private static final int IMAGE_SELECT = 2;
    byte[] imageData;
    private String currentPhotoPath;
    private static final int MY_CAMERA_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_cert_upload);

        RequestQueue queue = Volley.newRequestQueue(this);
        regCertSelect = findViewById(R.id.rcSelectBtn);
        regCertUpload = findViewById(R.id.rcUploadBtn);

        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }

        regCertSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFile();
            }
        });

        regCertUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regCertObj.postData(new RegCertAsyncPost() {
                    @Override
                    public void processFinished(String opLoc) {
                        Log.d("OperationLocation", " " + opLoc);
                        resUrl = opLoc;
                    }
                    @Override
                    public void processFailed(VolleyError error) {
                        Log.d("PostRequest", "processFailed: " + error.getMessage());
                    }
                },queue,imageData);

                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        regCertObj.getData(resUrl, new RegCertAsyncGet() {
                            @Override
                            public void processFinished(RegCertInfo regCertInfo) {
                                storeData(regCertInfo);
                            }

                            @Override
                            public void processFailed(VolleyError error) {
                                Log.d("Result", " " + error.getMessage());
                            }
                        } , queue);
                    }
                },15000);
            }
        });
    }

    private void openFile() {
        Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickIntent.setType("image/*");

        Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d("FileError", "openFile: " + ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.projectapp.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            }
        }
        String pickTitle = "Select or take a new picture";
        Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);

        chooserIntent.putExtra
                (
                        Intent.EXTRA_INITIAL_INTENTS,
                        new Intent[] { takePictureIntent }
                );
        startActivityForResult(chooserIntent, IMAGE_SELECT);
    }

    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        Log.d("ImagePath", "createImageFile: " + currentPhotoPath);
        return image;
    }

    private void storeData(RegCertInfo regCertInfo) {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        FirebaseDatabase.getInstance().getReference("RegistrationCertificate")
                                .child(user.registrationNumber).setValue(regCertInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(RegCertUpload.this, "RC Data Uploaded", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(RegCertUpload.this,DocumentUpload.class));
                                            finish();
                                        }
                                        else{
                                            Toast.makeText(RegCertUpload.this, "Upload Failed! Try Again", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("RegCertUpload", "onCancelled: "  + "Error");
                    }
                });
    }
}