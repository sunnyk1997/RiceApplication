package com.desirestodesigns.riceapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private EditText mPno;
    private EditText mCountryCode;
    private Button mGenerateOtp;
    private ProgressBar mloginProgress;
    private TextView mFeedback;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        mPno = findViewById(R.id.phone_number);
        mCountryCode = findViewById(R.id.country_code_text);
        mGenerateOtp = findViewById(R.id.btn);
        mloginProgress = findViewById(R.id.progress_bar);
        mFeedback = findViewById(R.id.login_form_feedback);

        mGenerateOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone_number = mPno.getText().toString();
                String country_code = mCountryCode.getText().toString();
                String complete_phone_number = "+" + country_code + "" + phone_number;

                if (phone_number.isEmpty() || country_code.isEmpty()) {
                    mFeedback.setText("Please fill in the form to continue");
                    mFeedback.setVisibility(View.VISIBLE);
                } else {
                    mloginProgress.setVisibility(View.VISIBLE);
                    mGenerateOtp.setEnabled(false);

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            complete_phone_number,
                            60,
                            TimeUnit.SECONDS,
                            LoginActivity.this,
                            mCallbacks
                    );
                }

            }
        });
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:" + phoneAuthCredential);
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (e == null) {
                    mFeedback.setText("Verification failed, please try again");
                    mFeedback.setVisibility(View.VISIBLE);
                    mloginProgress.setVisibility(View.INVISIBLE);
                    mGenerateOtp.setEnabled(true);
                }
            }

            //After the code is sent to the user the user will be redirected to otp verification activity using Intent
            @Override
            public void onCodeSent(final String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                //to auto verify otp
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                Intent otpIntent = new Intent(LoginActivity.this, OtpActivity.class);
                                otpIntent.putExtra("AuthCredentials", s);
                                startActivity(otpIntent);
                            }
                        }, 10000);
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCurrentUser != null) {
            Intent homeIntent = new Intent(LoginActivity.this, MainActivity.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(homeIntent);
            finish();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");

//                            FirebaseUser user = task.getResult().getUser();
//                            // ...
                            sendUserToHome();
                        } else {
                            // Sign in failed, display a message and update the UI
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                //The verification code entered was invalid
                                mFeedback.setVisibility(View.VISIBLE);
                                mFeedback.setText("There was an error verifying OTP");
                            }
                        }
                        mloginProgress.setVisibility(View.INVISIBLE);
                        mGenerateOtp.setEnabled(true);
                    }
                });
    }

    private void sendUserToHome() {
        Intent homeIntent = new Intent(LoginActivity.this, MainActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish();
    }
}
