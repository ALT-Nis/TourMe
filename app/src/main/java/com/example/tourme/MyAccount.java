package com.example.tourme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tourme.Adapters.OglasAdapter;
import com.example.tourme.Model.Gradovi;
import com.example.tourme.Model.Oglas;
import com.example.tourme.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MyAccount extends AppCompatActivity {

    ImageView imageView;
    TextView textView;
    RecyclerView recyclerView;
    View viewNoInternet;

    RelativeLayout relativeLayout;

    DatabaseReference reference;
    FirebaseUser firebaseUser;
    FirebaseStorage storage;

    StorageReference storageReference;

    OglasAdapter oglasAdapter;
    List<Oglas> mOglas;

    Uri imageUri;

    Button tryAgainButton;

    int reasonForBadConnection = 1;
    int numberOfOglases;

    void HideEverything(){
        for (int i=0;i<relativeLayout.getChildCount();i++) {
            View v1=relativeLayout.getChildAt(i);
            v1.setVisibility(View.GONE);
        }

        viewNoInternet.setVisibility(View.VISIBLE);
    }

    void ShowEverything(){
        for (int i=0;i<relativeLayout.getChildCount();i++) {
            View v1=relativeLayout.getChildAt(i);
            v1.setVisibility(View.VISIBLE);
        }

        viewNoInternet.setVisibility(View.GONE);
    }

    Boolean IsConnectedToInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }

    void recursion1ForMyOglases(int index, List<String> idsForMyOglas){
        if(index == numberOfOglases){
            oglasAdapter = new OglasAdapter(MyAccount.this, mOglas);
            recyclerView.setAdapter(oglasAdapter);
        }else{
            DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference();
            ref1.child("oglasi").child(idsForMyOglas.get(index)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    Oglas oglas = task.getResult().getValue(Oglas.class);
                    mOglas.add(oglas);
                    recursion1ForMyOglases(index + 1, idsForMyOglas);
                }
            });
        }

    }

    private void showOglas(String userid){
        mOglas = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("oglas");
        reference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                mOglas.clear();
                List<String> idsForMyOglas = new ArrayList<>();

                for(DataSnapshot dataSnapshot : Objects.requireNonNull(task.getResult()).getChildren()){
                    String newIdOglasa = dataSnapshot.getValue(String.class);
                    idsForMyOglas.add(newIdOglasa);
                }
                numberOfOglases = idsForMyOglas.size();
                recursion1ForMyOglases(0, idsForMyOglas);
            }
        });
    }

    public boolean tryToStart(){
        if(IsConnectedToInternet()) {
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());

            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    textView.setText(user.getUsername());
                    if (user.getImageurl().equals("default")) {
                        imageView.setImageResource(R.mipmap.ic_launcher);
                    } else {
                        Glide.with(getApplicationContext()).load(user.getImageurl()).into(imageView);
                    }

                    showOglas(user.getId());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }else{
            HideEverything();
            reasonForBadConnection = 1;
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            setContentView(R.layout.activity_myaccount);

            imageView = findViewById(R.id.profile_image);
            textView = findViewById(R.id.username);

            relativeLayout = findViewById(R.id.MojNalog);
            viewNoInternet = (View) findViewById(R.id.nemaInternet);

            recyclerView = findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);

            recyclerView.setLayoutManager(new LinearLayoutManager(MyAccount.this));

            tryAgainButton = viewNoInternet.findViewById(R.id.TryAgainButton);
            tryAgainButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(reasonForBadConnection == 1) {
                        if (tryToStart())
                            ShowEverything();
                    }else
                        ShowEverything();
                }
            });

            tryToStart();

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(IsConnectedToInternet())
                        choseImage();
                    else{
                        reasonForBadConnection = 2;
                        HideEverything();
                    }
                }
            });
        }
        else{
            setContentView(R.layout.not_logged_in);
            Button dugme_login = findViewById(R.id.dugme_login);
            dugme_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MyAccount.this, Login.class);
                    startActivity(i);
                }
            });
        }

    }

    private void choseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
            uploadImage();
        }
    }

    private void uploadImage(){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        final String randomKey = UUID.randomUUID().toString();
        StorageReference riversRef = storageReference.child("images/" + randomKey);

        riversRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                if (taskSnapshot.getMetadata() != null) {
                    if (taskSnapshot.getMetadata().getReference() != null) {
                        Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid()).child("imageurl").setValue(imageUrl);
                                FirebaseDatabase.getInstance().getReference("oglasi").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                            Oglas oglas = dataSnapshot.getValue(Oglas.class);
                                            Log.e("2", "test + " + oglas.getUserId());
                                            Log.e("2", "test + " + firebaseUser.getUid());
                                            if(oglas.getUserId().equals(firebaseUser.getUid())){
                                                FirebaseDatabase.getInstance().getReference("oglasi").child(oglas.getIdOglasa()).child("imageurl").setValue(imageUrl);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                    }
                }
                Snackbar.make(findViewById(android.R.id.content), "Image uploaded.", Snackbar.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_LONG).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                progressDialog.setMessage("Percentage: " + (int)progressPercent + "%");
            }
        });
    }


    private void status(String status){
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("status", status);

            reference.updateChildren(hashMap);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

}