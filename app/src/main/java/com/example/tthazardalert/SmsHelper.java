package com.example.tthazardalert;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class SmsHelper {
    private static final String TAG = "HazardApp";
    private static final String ACTION_SMS_SENT = "com.example.tthazardalert.SMS_SENT";
    private static final String ACTION_SMS_DELIVERED = "com.example.tthazardalert.SMS_DELIVERED";
    private static boolean receiversRegistered = false;

    public static synchronized void registerReceivers(Context context) {
        if (receiversRegistered) return;
        Context appContext = context.getApplicationContext();
        BroadcastReceiver statusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                if (intent == null) return;
                if (ACTION_SMS_SENT.equals(intent.getAction())) {
                    int resultCode = getResultCode();
                    if (resultCode == android.app.Activity.RESULT_OK) {
                        Log.d(TAG, "SMS successfully sent.");
                    } else {
                        Log.e(TAG, "SMS send failed with error code: " + resultCode);
                    }
                } else if (ACTION_SMS_DELIVERED.equals(intent.getAction())) {
                    int resultCode = getResultCode();
                    if (resultCode == android.app.Activity.RESULT_OK) {
                        Log.d(TAG, "SMS successfully delivered.");
                    } else {
                        Log.e(TAG, "SMS delivery failed with error code: " + resultCode);
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SMS_SENT);
        filter.addAction(ACTION_SMS_DELIVERED);

        ContextCompat.registerReceiver(
                appContext,
                statusReceiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
        );
        receiversRegistered = true;
    }

    public static void send(Context context, String to, String text) {
        try {
            registerReceivers(context);
            SmsManager sm = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    ? context.getSystemService(SmsManager.class)
                    : SmsManager.getDefault();
            ArrayList<String> parts = sm.divideMessage(text);

            int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_IMMUTABLE : 0;
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), flags);
            PendingIntent deliveredIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_DELIVERED), flags);

            ArrayList<PendingIntent> sentIntents = new ArrayList<>();
            ArrayList<PendingIntent> deliveryIntents = new ArrayList<>();
            int size = parts.size();
            for (int i = 0; i < size; i++) {
                sentIntents.add(sentIntent);
                deliveryIntents.add(deliveredIntent);
            }

            sm.sendMultipartTextMessage(to, null, parts, sentIntents, deliveryIntents);
            Log.d(TAG, "SMS sent to " + to + ": " + text);
        } catch (Exception e) {
            Log.d(TAG, "SMS failed: " + e.getMessage(), e);
        }
    }
}


