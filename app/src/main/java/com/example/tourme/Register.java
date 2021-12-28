package com.example.tourme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.tourme.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    EditText mEmail, mUserName, mPassword, mConfirmPassword;

    Button registerButton, buttonShowHidePassword, buttonShowHideConfirmPassword;
    Button tryAgainButton;
    TextView loginButton;

    FirebaseAuth fAuth;

    ConstraintLayout constraintLayout;

    View viewNoInternet;

    private DatabaseReference mDatabase;

    String patternForEmail = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
    String patternForUsername = "^[a-zA-Z0-9]+$";
    String email, username, password, confirm_password;

    boolean didFindError = false;
    boolean isPasswordHidden = true;
    boolean isConfirmPasswordHidden = true;


    void setEmailError(String errorText){
        mEmail.setError(errorText);
        didFindError = true;
    }

    void setPasswordError(String errorText){
        mPassword.setError(errorText);
        didFindError = true;
    }

    void setConfirmPassword(String errorText){
        mConfirmPassword.setError(errorText);
        didFindError = true;
    }

    void setUsernameError(String errorText){
        mUserName.setError(errorText);
        didFindError = true;
    }

    void resetErrors(){
        mEmail.setError(null);
        mUserName.setError(null);
        mPassword.setError(null);
        mConfirmPassword.setError(null);
    }

    void HideEverything(){
        for (int i=0;i<constraintLayout.getChildCount();i++) {
            View v1=constraintLayout.getChildAt(i);
            v1.setVisibility(View.GONE);
        }

        viewNoInternet.setVisibility(View.VISIBLE);
    }

    void ShowEverything(){
        for (int i=0;i<constraintLayout.getChildCount();i++) {
            View v1=constraintLayout.getChildAt(i);
            v1.setVisibility(View.VISIBLE);
        }

        viewNoInternet.setVisibility(View.GONE);
    }

    Boolean IsConnectedToInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }

    void finishCreatingAccount(String AccountEmail, String AccountPassword) {
        if(IsConnectedToInternet()){
            fAuth.createUserWithEmailAndPassword(AccountEmail,AccountPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(Register.this,"Uspesno ste kreirali nalog",Toast.LENGTH_LONG).show();

                        String userId = fAuth.getCurrentUser().getUid();
                        User user = new User(userId, AccountEmail, username, "default", "0", "offline");
                        mDatabase.child("users").child(userId).setValue(user);

                        mDatabase.child("usersID").child(username).setValue(userId);
                    }
                    else{
                        String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();

                        if(errorCode.equals("ERROR_EMAIL_ALREADY_IN_USE")){
                            setEmailError("Vec postoji nalog sa ovim Email-om");
                        }else {
                            HideEverything();
                        }
                    }
                }
            });
        }else{
            HideEverything();
        }

    }
    void startCreatingAccount(String AccountEmail, String AccountPassword){
        if(IsConnectedToInternet()){
            mDatabase.child("usersID").child(username).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        HideEverything();
                    }
                    else {
                        String dataFromDatabase = String.valueOf(task.getResult().getValue());
                        if(!dataFromDatabase.equals("null")){
                            setUsernameError("Vec postoji nalog sa ovim korisnickim imenom");
                        }else{
                            finishCreatingAccount(AccountEmail, AccountPassword);
                        }
                    }
                }
            });
        }else{
            HideEverything();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmail = findViewById(R.id.email);
        mUserName = findViewById(R.id.CustomName);
        mPassword = findViewById(R.id.password);
        mConfirmPassword = findViewById(R.id.confirm_password);

        viewNoInternet = (View) findViewById(R.id.nemaInternet);

        fAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        constraintLayout = findViewById(R.id.RegisterActivity);

        buttonShowHidePassword = findViewById(R.id.buttonForShowingPassword);
        buttonShowHidePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPasswordHidden){
                    mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    buttonShowHidePassword.setText("Hide");
                }else{
                    mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    buttonShowHidePassword.setText("Show");
                }
                isPasswordHidden = !isPasswordHidden;
                mPassword.setSelection(mPassword.length());
            }
        });

        buttonShowHideConfirmPassword = findViewById(R.id.buttonForShowingConfirmPassword);
        buttonShowHideConfirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isConfirmPasswordHidden){
                    mConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    buttonShowHideConfirmPassword.setText("Hide");
                }else{
                    mConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    buttonShowHideConfirmPassword.setText("Show");
                }
                isConfirmPasswordHidden = !isConfirmPasswordHidden;
                mConfirmPassword.setSelection(mConfirmPassword.length());
            }
        });

        loginButton = (TextView)findViewById(R.id.goToSignIn);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Register.this, Login.class);
                startActivity(i);
            }
        });

        tryAgainButton = viewNoInternet.findViewById(R.id.TryAgainButton);
        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowEverything();
            }
        });

        registerButton = findViewById(R.id.register_dugme);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mEmail.getText().toString().trim();
                username = mUserName.getText().toString().trim();
                password = mPassword.getText().toString().trim();
                confirm_password = mConfirmPassword.getText().toString().trim();

                didFindError = false;

                resetErrors();

                if(TextUtils.isEmpty(email)){
                    setEmailError("Unesite Email");
                }else{
                    java.util.regex.Pattern p = java.util.regex.Pattern.compile(patternForEmail);
                    java.util.regex.Matcher m = p.matcher(email);

                    if(!m.matches()) {
                        setEmailError("Email nije pravog formata");
                    }
                }

                if(TextUtils.isEmpty(username)){
                    setUsernameError("Unesti korisnicko ime");
                }else{
                    java.util.regex.Pattern p = java.util.regex.Pattern.compile(patternForUsername);
                    java.util.regex.Matcher m = p.matcher(username);

                    if(!m.matches()) {
                        setUsernameError("Korisnicko ime mora da se sastoji samo od slova i brojeva");
                    }
                }

                int len = password.length();
                if(len >= 1 && len < 6){
                    setPasswordError("Sifra mora imati vise 6 - 12 karaktera");
                }else{
                    if(TextUtils.isEmpty(password)) {
                        setPasswordError("Unesite Sifru");
                    }

                    if(TextUtils.isEmpty(confirm_password)){
                        setConfirmPassword("Potvrdite Sifru");
                    }else if(!TextUtils.equals(confirm_password,password)){
                        setConfirmPassword("Sifre nisu iste");
                    }
                }


                if(!didFindError){
                    startCreatingAccount(email, password);
                }

            }
        });

    }

}

