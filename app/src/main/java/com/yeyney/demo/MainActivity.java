package com.yeyney.demo;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AuthActivity {

    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST = 256;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] missingPermissions = checkRequiredPermissions();
            if (missingPermissions.length > 0) {
                requestPermissions(missingPermissions, PERMISSION_REQUEST);
                return;
            }
        }

        gotoNextActivity();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private String[] checkRequiredPermissions() {
        List<String> missingPermissions = new ArrayList<>();
        if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED) {
            Log.d(TAG, "Missing permission for SMS");
            missingPermissions.add(Manifest.permission.SEND_SMS);
        } else if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
            Log.d(TAG, "Missing permission for SMS");
            missingPermissions.add(Manifest.permission.READ_CONTACTS);
        }
        return missingPermissions.toArray(new String[missingPermissions.size()]);
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
        if (requestCode == PERMISSION_REQUEST) {
            gotoNextActivity();
        }
    }
}
