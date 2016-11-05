package com.yeyney.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.GoogleAuthProvider;

public class GoogleSignInActivity extends AuthActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "GoogleSignInActivity";
    private static final int RC_SIGN_IN = 256;

    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        OptionalPendingResult<GoogleSignInResult> pendingResult = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (pendingResult.isDone()) {
            Log.d(TAG, "There's immediate result available.");
            gotGoogleSignInResult(pendingResult.get());
        } else {
            showProgressIndicator();
            pendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult result) {
                    gotGoogleSignInResult(result);
                }
            });
        }
    }

    private void gotGoogleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = result.getSignInAccount();
            firebaseAuthWithGoogle(account);
        } else {
            int statusCode = result.getStatus().getStatusCode();
            if (statusCode == GoogleSignInStatusCodes.SIGN_IN_REQUIRED) {
                Log.d(TAG, "Sign in is required");
                Toast.makeText(GoogleSignInActivity.this, "Please sign in with you Google account", Toast.LENGTH_SHORT).show();
                signIn();
                return;
            } else if (statusCode == GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
                // TODO: Explain to the user that sign in is required, and ask them to try again
                Log.d(TAG, "User cancelled sign in");
            } else if (statusCode == GoogleSignInStatusCodes.SIGN_IN_FAILED) {
                // TODO: Ask user to try again
                Log.d(TAG, "Sign in failed");
            }

            Log.d(TAG, result.getStatus().toString());
            Log.d(TAG, result.getStatus().getStatusMessage());
            Toast.makeText(this, "You where not logged in with Google", Toast.LENGTH_SHORT).show();
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                gotGoogleSignInResult(Auth.GoogleSignInApi.getSignInResultFromIntent(data));
            } else {
                Log.d(TAG, "Something went wrong with RC_SIGN_IN. Result code: " + resultCode);
                Toast.makeText(this, "Something went wrong, please restart the app", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            dismissProgressIndicator();
                            startActivity(new Intent(GoogleSignInActivity.this, YeyNeyActivity.class));
                            finish();
                        } else {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(GoogleSignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
}
