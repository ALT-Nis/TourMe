package com.example.tourme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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

import java.util.HashMap;

public class Register extends AppCompatActivity {

    EditText mEmail, mUserName, mPassword, mConfirmPassword;
    Button registerButton;
    TextView loginButton;
    FirebaseAuth fAuth;

    String email, username, password, confirm_password;

    private DatabaseReference mDatabase;

    String patternForEmail = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
    String patternForUsername = "^[a-zA-Z0-9]+$";

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

    void finishCreatingAccount(String AccountEmail, String AccountPassword) {
        fAuth.createUserWithEmailAndPassword(AccountEmail,AccountPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Register.this,"Uspesno ste kreirali nalog",Toast.LENGTH_LONG).show();

                    String userId = fAuth.getCurrentUser().getUid();
                    User user = new User(userId, AccountEmail, username, "default");
                    mDatabase.child("users").child(userId).setValue(user);

                    mDatabase.child("usersID").child(username).setValue(userId);
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
    void startCreatingAccount(String AccountEmail, String AccountPassword){
        mDatabase.child("usersID").child(username).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("Firebase", "Error getting data", task.getException());
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmail = findViewById(R.id.email);
        mUserName = findViewById(R.id.CustomName);
        mPassword = findViewById(R.id.password);
        mConfirmPassword = findViewById(R.id.confirm_password);

        fAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        loginButton = (TextView)findViewById(R.id.goToSignIn);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Register.this, Login.class);
                startActivity(i);
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

//luka ovo su nove izmene
//FirebaseFirestore db = FirebaseFirestore.getInstance();


//FirebaseDatabase.getInstance()
//        HashMap<String, String> hashMap = new HashMap<>();
//        hashMap.put("id", "1");
//        hashMap.put("username","yutopk");
//        hashMap.put("imageurl","default");

//FirebaseDatabase.getInstance().getReference("korisnik").child("1").setValue(hashMap);
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