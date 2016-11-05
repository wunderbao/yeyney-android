package com.yeyney.demo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yeyney.demo.model.Contact;

import java.io.File;
import java.util.ArrayList;

public class ShareActivity extends AuthActivity {

    private static final String TAG = "ShareActivity";

    private FirebaseStorage storage;
    private ImageView imageView;
    private EditText commentView, valueView;
    private String currentPhotoPath;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        storage = FirebaseStorage.getInstance();

        Bundle extras = getIntent().getExtras();
        currentPhotoPath = extras.getString("PHOTO_PATH");
        imageView = (ImageView) findViewById(R.id.imageView_share_photo);
        imageView.setImageURI(Uri.fromFile(new File(currentPhotoPath)));

        commentView = (EditText) findViewById(R.id.editText_share_comment);
        valueView = (EditText) findViewById(R.id.editText_share_value);

        Button sendSms = (Button) findViewById(R.id.button_share_send_sms);
        sendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(ShareActivity.this, ContactsActivity.class), ContactsActivity.SEND_SMS_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ContactsActivity.SEND_SMS_REQUEST) {
            if (resultCode == RESULT_OK) {
                showProgressIndicator();
                final ArrayList<Contact> recipients = (ArrayList<Contact>) data.getSerializableExtra("recipients");
                final String recipientsAsString = TextUtils.join(",", recipients);
                // TODO: Recipients needs to be sent to the server, for statistics

                StorageReference storageRef = storage.getReferenceFromUrl("gs://yeyney-demo.appspot.com");
                Uri file = Uri.fromFile(new File(currentPhotoPath));
                final StorageReference imagesRef = storageRef.child("test-images/" + file.getLastPathSegment());
                UploadTask uploadTask = imagesRef.putFile(file);

                // Observe state change events such as progress, pause, and resume
                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        Log.d(TAG, "Upload is " + progress + "% done");
                    }
                }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "Upload is paused");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Upload failed");
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "Upload success");
                        Log.d(TAG, "Name: " + imagesRef.getName());
                        Log.d(TAG, "Path: " + imagesRef.getPath());
                        Log.d(TAG, "Task-downloadUrl: " + taskSnapshot.getDownloadUrl());
                        SMSApi.generateAndSendSMS(auth.getCurrentUser().getUid(), recipients, taskSnapshot.getDownloadUrl().toString(), commentView.getText().toString(), valueView.getText().toString());
                        dismissProgressIndicator();
                        finish();
                    }
                });
            }
        }
    }

    protected void showProgressIndicator() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(true);
        }

        progressDialog.show();
    }

    protected void dismissProgressIndicator() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
