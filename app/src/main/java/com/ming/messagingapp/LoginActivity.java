package com.ming.messagingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CCPCountry;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText phoneNumberEditText, verifiyCodeText;
    private Button btnSendOpt, btnResendOpt, btnVerifyOpt;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private String verificationId;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private CountryCodePicker countryCodePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();


        userisLoggedin();
        phoneNumberEditText = findViewById(R.id.et_phone_number);
        countryCodePicker = findViewById(R.id.ccp);
        countryCodePicker.registerCarrierNumberEditText(phoneNumberEditText);
        verifiyCodeText = findViewById(R.id.et_otp);
        btnSendOpt = findViewById(R.id.btn_send_otp);
        btnResendOpt = findViewById(R.id.btn_resend_otp);
        btnVerifyOpt = findViewById(R.id.btn_verify_otp);

        btnSendOpt.setOnClickListener(this);
        btnVerifyOpt.setOnClickListener(this);
        btnResendOpt.setOnClickListener(this);
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(LoginActivity.this, "verification complete", Toast.LENGTH_SHORT).show();
                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // SMS quota exceeded
                    Toast.makeText(LoginActivity.this, "SMS Quota exceeded", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Log.d("LoginActivity", "onCodeSent:" + verificationId);

                verificationId = s;

            }
        };


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        firebaseAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    currentUser = firebaseAuth.getCurrentUser();

                    if (currentUser != null) {
                        final DatabaseReference mDb = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
                        mDb.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    Map<String, Object> userMap = new HashMap<>();
                                    userMap.put("phone", currentUser.getPhoneNumber());
                                    userMap.put("name", currentUser.getPhoneNumber());
                                    mDb.updateChildren(userMap);


                                }
                                userisLoggedin();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Verification Code is wrong, try again", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    private void userisLoggedin() {
        if (currentUser != null) {
            startActivity(new Intent(LoginActivity.this, UserDetailActivity.class));
            finish();
        }

    }

    private void sendOpt() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                "+19176211062",
                countryCodePicker.getFullNumberWithPlus(),
                60,
                TimeUnit.SECONDS,
                this,
                mCallback
        );
        Toast.makeText(this, "Sending OPT", Toast.LENGTH_SHORT).show();
    }

    private void verifyOpt() {
        String code = verifiyCodeText.getText().toString();
        if (!TextUtils.isEmpty(code)) {
            try {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
                signInWithPhoneAuthCredential(credential);
            } catch (Exception e) {
                Toast toast = Toast.makeText(getApplicationContext(), "Verification Code is wrong, try again", Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            Toast.makeText(this, "no code entered", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send_otp:
                sendOpt();
                break;
            case R.id.btn_verify_otp:
                verifyOpt();


        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        userisLoggedin();


    }
}
