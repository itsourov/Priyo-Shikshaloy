package com.example.meghaprototype1.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meghaprototype1.R;
import com.example.meghaprototype1.Sourov;
import com.example.meghaprototype1.activity.Homepage;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.annotations.NotNull;
import com.hbb20.CountryCodePicker;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class AuthActivity extends AppCompatActivity {

    CountryCodePicker ccp;
    TextView countryCodeTextOnAuth;
    EditText inputNumberOnAuth, inputCodeOnVerify;

    FirebaseAuth mAuth;
    private String mVerificationId;
    CardView verifyContainer;

    Sourov sourov;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mAuth = FirebaseAuth.getInstance();

        sourov = new Sourov(AuthActivity.this);

        ccp = findViewById(R.id.countryCodePickerOnAuth);
        countryCodeTextOnAuth = findViewById(R.id.countryCodeTextOnAuth);
        inputNumberOnAuth = findViewById(R.id.inputNumberOnAuth);

        verifyContainer = findViewById(R.id.verifyContainer);
        inputCodeOnVerify = findViewById(R.id.inputCodeOnVerify);

        ccp.registerCarrierNumberEditText(inputNumberOnAuth);
        ccp.setPhoneNumberValidityChangeListener(isValidNumber -> {
            String str = ccp.getSelectedCountryCodeWithPlus();


            countryCodeTextOnAuth.setText(str);

        });

        findViewById(R.id.nextStepBtnOnAA).setOnClickListener(view -> sendCodeToUser(ccp.getFullNumberWithPlus()));

    }

    private void sendCodeToUser(String fullNumber) {
        sourov.spinner().show();
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onCodeSent(@NonNull @NotNull String s, @NonNull @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                createPopupDialog();
                sourov.spinner().dismiss();
                mVerificationId = s;
                createPopupDialog();


            }

            @Override
            public void onVerificationCompleted(@NonNull @NotNull PhoneAuthCredential phoneAuthCredential) {

                final String code = phoneAuthCredential.getSmsCode();
                if (code != null) {
                    inputCodeOnVerify.setText(code);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                    signInWithPhoneAuthCredential(credential);
                }

            }

            @Override
            public void onVerificationFailed(@NonNull @NotNull FirebaseException e) {

                Toast.makeText(AuthActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        };

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(fullNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void createPopupDialog() {
        verifyContainer.setVisibility(View.VISIBLE);
        Animation sgAnimation = AnimationUtils.loadAnimation(AuthActivity.this, R.anim.slide_in_up);
        verifyContainer.setAnimation(sgAnimation);

        findViewById(R.id.cancelPopupBtnOnVerify).setOnClickListener(v -> {
            Animation slide_out_up = AnimationUtils.loadAnimation(AuthActivity.this, R.anim.slide_out_up);
            verifyContainer.setAnimation(slide_out_up);
            verifyContainer.setVisibility(View.GONE);
        });
        findViewById(R.id.buttonOnVerify).setOnClickListener(v -> {
            String codeByUser = inputCodeOnVerify.getText().toString().trim();
            if (codeByUser.length() == 6) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, codeByUser);
                signInWithPhoneAuthCredential(credential);
            } else {
                Toast.makeText(this, "Code must be 6 digit long", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        sourov.spinner().show();

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    sourov.spinner().dismiss();
                    if (task.isSuccessful()) {

                        startActivity(new Intent(AuthActivity.this, Homepage.class));
                        finish();


                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(AuthActivity.this, "code invalid", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AuthActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }

                });
    }
}