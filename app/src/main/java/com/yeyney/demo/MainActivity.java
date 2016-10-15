package com.yeyney.demo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;


public class MainActivity extends AuthActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED) {
                Log.d(TAG, "Requesting permission for SMS");
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 256);
            } else if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
                Log.d(TAG, "Requesting permission for SMS");
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 256);
            } else {
                gotoNextActivity();
            }
        }
    }

    private void gotoNextActivity() {
        if (isSignedIn) {
            startActivity(new Intent(MainActivity.this, YeyNeyActivity.class));
        } else {
            startActivity(new Intent(MainActivity.this, GoogleSignInActivity.class));
        }
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        gotoNextActivity();
    }
}
