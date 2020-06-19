package com.desirestodesigns.riceapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RegistrationActivity extends AppCompatActivity {
    private static final String TAG = "REGISTRATION ACTIVITY";
    EditText mFirstName, mLastName, mMobileNumber, mAddress;
    Button mSave;
    String firstName, lastName, mobileNumber, address,createdDate,documentId;
    RegistrationForm registrationForm;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference collectionReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initializeUi();
    }

    private void initializeUi() {
        mFirstName = findViewById(R.id.first_name);
        mLastName = findViewById(R.id.last_name);
        mMobileNumber = findViewById(R.id.mobile_number);
        mAddress = findViewById(R.id.address);
        mSave = findViewById(R.id.save_btn);
        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("Customers");
    }

    public void onClickSave(View view) {
        firstName = mFirstName.getText().toString();
        lastName = mLastName.getText().toString();
        mobileNumber = mMobileNumber.getText().toString();
        address = mAddress.getText().toString();
        if ((!TextUtils.isEmpty(firstName)) && (!TextUtils.isEmpty(lastName))
                && (!TextUtils.isEmpty(mobileNumber)) || (!TextUtils.isEmpty(address))) {
            Log.i(TAG, "Entered in to IF loop");
            registrationObject();
        } else {
            Log.i(TAG, "Entered into ELSE loop");
            Toast.makeText(this, "Please enter appropriate data", Toast.LENGTH_LONG).show();
        }
    }

    private void registrationObject() {
        registrationForm = new RegistrationForm();
        registrationForm.setFirstName(firstName);
        registrationForm.setLastName(lastName);
        registrationForm.setMobileNumber(mobileNumber);
        registrationForm.setAddress(address);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy  hh:mm:ss");
        createdDate = simpleDateFormat.format(new Date());
        Log.i(TAG, "Current Timestamp: " + createdDate);
        registrationForm.setCreatedDate(createdDate);
        DocumentReference ref = firebaseFirestore.collection("Customers").document();
        documentId = ref.getId();
        registrationForm.setDocumentId(documentId);
        sendToDb(documentId);
    }
    private void sendToDb(final String documentId){
        Log.i(TAG, "sendToDb method is invoked");
        collectionReference.document(documentId)
                .set(registrationForm).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                clearUi();
                Log.i(TAG, "clearUi method is executed");
                finish();
                Log.i(TAG, "Add Employee Activity is closed and returns to Main Activity");
                Toast.makeText(RegistrationActivity.this, "Data successfully added", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegistrationActivity.this, "Issue while adding data", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void clearUi() {
        mFirstName.setText("");
        mLastName.setText("");
        mMobileNumber.setText("");
        mAddress.setText("");
        Intent homeIntent = new Intent(RegistrationActivity.this,MainActivity.class);
        startActivity(homeIntent);
    }
}
