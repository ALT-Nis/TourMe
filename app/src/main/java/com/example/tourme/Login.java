package com.example.tourme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {

    EditText mEmail, mPassword;
    Button login_dugme, buttonShowHidePassword;
    TextView registerButton;
    FirebaseAuth fAuth;

    boolean didFindError = false;
    boolean isPasswordHidden = true;

    private DatabaseReference mDatabase;

    String patternForEmail = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";

    void setEmailError(String errorText){
        mEmail.setError(errorText);
        didFindError = true;
    }

    void setPasswordError(String errorText){
        mPassword.setError(errorText);
        didFindError = true;
    }

    void resetErrors(){
        mEmail.setError(null);
        mPassword.setError(null);
    }

    void finishSigningIn(String email, String password){
        fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Login.this,"Uspesno",Toast.LENGTH_LONG).show();
                    Intent i = new Intent(Login.this, Glavni_ekran.class);
                    startActivity(i);
                }
                else{
                    String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                    Log.e("gf", errorCode);
                    if(errorCode.equals("ERROR_USER_NOT_FOUND")){
                        setEmailError("Ne postoji nalog sa ovim email-om");
                    }else if(errorCode.equals("ERROR_WRONG_PASSWORD")){
                        setPasswordError("Pogresna sifra");
                    }else{
                        Toast.makeText(Login.this,"Greska" + task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    void continueSignIn(String id, String password){
        mDatabase.child("users").child(id).child("email").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("Firebase", "Error getting data", task.getException());
                }
                else {
                    String emailFromDatabase = String.valueOf(task.getResult().getValue());
                    if(!emailFromDatabase.equals("null")){
                        finishSigningIn(emailFromDatabase, password);
                    }else{
                        setEmailError("Ne postoji nalog sa ovim ID-em");
                    }
                }
            }
        });
    }

    void startSigningIn(String email, String password){
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(patternForEmail);
        java.util.regex.Matcher m = p.matcher(email);


        if(!m.matches()) {
            //nije email nego je username
            String username = email;
            mDatabase.child("usersID").child(username).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("Firebase", "Error getting data", task.getException());
                    }
                    else {
                        String IDFromDatabase = String.valueOf(task.getResult().getValue());
                        if(!IDFromDatabase.equals("null")){
                            continueSignIn(IDFromDatabase, password);
                        }else{
                            setEmailError("Ne postoji ovakvo korisnicko ime");
                        }
                    }
                }
            });
        }else{
            finishSigningIn(email, password);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.email_login);
        mPassword = findViewById(R.id.password_login);

        fAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        registerButton = (TextView)findViewById(R.id.goToRegister);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this, Register.class);
                startActivity(i);
            }
        });

        buttonShowHidePassword = findViewById(R.id.buttonForShowingPasswordLogin);
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

        login_dugme = findViewById(R.id.login_dugme);

        login_dugme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                didFindError = false;
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                resetErrors();

                if(TextUtils.isEmpty(email)){
                    setEmailError("Unesite email");
                }

                if(TextUtils.isEmpty(password)){
                    setPasswordError("Unesite sifru");
                }
                if(!didFindError){
                    startSigningIn(email, password);
                }

            }
        });
    }
}