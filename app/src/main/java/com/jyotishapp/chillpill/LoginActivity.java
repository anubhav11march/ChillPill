package com.jyotishapp.chillpill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private static String VERIFICATION_ID;
    FirebaseAuth auth;

    TextInputLayout phone, otp;
    Button login, getCode;
    LottieAnimationView heart;

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();
        getSupportActionBar().hide();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        imageView = findViewById(R.id.image_view);
        phone = findViewById(R.id.phone);
        otp = findViewById(R.id.otp);
        getCode = findViewById(R.id.getCode);
        login = findViewById(R.id.login_btn);
        login.setVisibility(View.INVISIBLE);

        heart = findViewById(R.id.heart);
        heart.setVisibility(View.INVISIBLE);

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.edittext);
        otp.setAnimation(anim);
        phone.setAnimation(anim);

        getCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = phone.getEditText().getText().toString().trim();

                if (!TextUtils.isEmpty(number) && number.length() > 9) {

                    getVerificationCode('+' + "91" + number);

                    heart.setVisibility(View.VISIBLE);
                    login.setVisibility(View.VISIBLE);
                    getCode.setVisibility(View.INVISIBLE);
                }
                else {
                    phone.setError("Phone Number Required");
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(otp.getEditText().getText())){
                    otp.setError("Can't Be Empty");
                    return;
                }
                Toast.makeText(LoginActivity.this, "Logging in...", Toast.LENGTH_SHORT).show();
                String userCode = otp.getEditText().getText().toString().trim();
                if (!TextUtils.isEmpty(userCode))
                    verifyCode(userCode);
            }
        } );


    }

    private void verifyCode(String userCode) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VERIFICATION_ID, userCode);
        signInWith(credential);

    }

    private void signInWith(PhoneAuthCredential credential) {

        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();

                        } else
                            Toast.makeText(LoginActivity.this, "ERROR OCCURRED", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void getVerificationCode(String phoneNumber) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks
            = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            String userCode = credential.getSmsCode();
            if (userCode != null) {
                otp.getEditText().setText(userCode);
                verifyCode(userCode);
            }

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            VERIFICATION_ID = s;

        }


    };

}

