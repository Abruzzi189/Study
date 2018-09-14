package com.example.thangpham.testfirebasenhap;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by ThangPham on 12/23/2017.
 */

public class FirebaseService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseService";
    // hafm chay khi app dang bật
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "onMessageReceived: "+remoteMessage.getNotification());
        Log.d(TAG, "onMessageReceived: "+remoteMessage.getData());
    }
}
