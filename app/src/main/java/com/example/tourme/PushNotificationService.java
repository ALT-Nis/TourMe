package com.example.tourme;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;

public class PushNotificationService extends FirebaseMessagingService {

    private static final String TAG = PushNotificationService.class.getSimpleName();

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        Log.d(TAG, "Token: " + s);
    }
}
