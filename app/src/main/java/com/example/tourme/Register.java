package com.example.tourme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextWatcher;
import android.text.NoCopySpan;


import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class Register extends AppCompatActivity {

    EditText mEmail, mPassword, mConfirmPassword;
    Button registerButton;
    FirebaseAuth fAuth;
    //TextView passwordNeedText;

    String patternForEmail = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";

    boolean didFindError = false;

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

    void createAccount(String email, String password){
        fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Register.this,"Uspesno ste kreirali nalog",Toast.LENGTH_LONG).show();
                }
                else{
                    String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();

                    if(errorCode.equals("ERROR_EMAIL_ALREADY_IN_USE")){
                        setEmailError("Vec postoji nalog sa ovim Email-om");
                    }else {
                        Toast.makeText(Register.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mConfirmPassword = findViewById(R.id.confirm_password);

        registerButton = findViewById(R.id.register_dugme);

        fAuth = FirebaseAuth.getInstance();

        //luka ovo su nove izmene
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        /*

        OVO CE NAM KASNIJE TREBA ZA CUVANEJ SLIKE
        StorageReference storageRef = storage.getReference();

        NJIHOV DEFAULT KOD PRIMER
        * // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("first", "Ada");
        user.put("last", "Lovelace");
        user.put("born", 1815);

        // Add a new document with a generated ID
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error adding document", e);
            }
        });
        *
        * */

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String confirm_password = mConfirmPassword.getText().toString().trim();

                didFindError = false;

                if(TextUtils.isEmpty(email)){
                    setEmailError("Unesite Email");
                }else{
                    java.util.regex.Pattern p = java.util.regex.Pattern.compile(patternForEmail);
                    java.util.regex.Matcher m = p.matcher(email);

                    if(!m.matches()) {
                        setEmailError("Email nije pravog formata");
                    }
                }

                int len = password.length();
                if(len >= 1 && len < 6){
                    setPasswordError("Sifra mora imati vise 6 - 12 karaktera");
                    mConfirmPassword.setError(null);
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
                    createAccount(email, password);
                }

            }
        });

    }
}