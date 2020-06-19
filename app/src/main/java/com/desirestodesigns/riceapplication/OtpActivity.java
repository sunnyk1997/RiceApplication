package com.desirestodesigns.riceapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class OtpActivity extends AppCompatActivity {
    private static final String TAG ="TAG" ;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    // We will receive or Auth credentials in the following variable
    private String mAuthVerificationId;

    private EditText mOtpText;
    private Button mVerifyButton;
    private ProgressBar mOtpProgress;
    private TextView mOtpFeedback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        mAuthVerificationId = getIntent().getStringExtra("AuthCredentials");

        mOtpText = findViewById(R.id.ed1);
        mOtpFeedback = findViewById(R.id.otp_form_feedback);
        mOtpProgress = findViewById(R.id.otp_progress);
        mVerifyButton = findViewById(R.id.otp_btn);

        mVerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp = mOtpText.getText().toString();
                if(otp.isEmpty()){
                    mOtpFeedback.setVisibility(View.VISIBLE);
                    mOtpFeedback.setText("Please fill in the OTP to complete verification");
                }
                else{

                    mOtpProgress.setVisibility(View.VISIBLE);
                    mVerifyButton.setEnabled(false);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mAuthVerificationId,otp);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(OtpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");

//                            FirebaseUser user = task.getResult().getUser();
//                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                //The verification code entered was invalid
                                mOtpFeedback.setVisibility(View.VISIBLE);
                                mOtpFeedback.setText("There was an error verifying OTP");
                            }
                        }
                        mOtpProgress.setVisibility(View.INVISIBLE);
                        mVerifyButton.setEnabled(true);
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCurrentUser != null) {
            sendUserToHome();
        }

    }

    private void sendUserToHome() {
        Intent homeIntent = new Intent(OtpActivity.this, RegistrationActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish();
    }

}
