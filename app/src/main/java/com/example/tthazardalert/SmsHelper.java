package com.example.tthazardalert;

import android.content.Context;
import android.os.Build;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.ArrayList;

public class SmsHelper {
    public static void send(Context context, String to, String text) {
        try {
            SmsManager sm = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    ? context.getSystemService(SmsManager.class)
                    : SmsManager.getDefault();
            ArrayList<String> parts = sm.divideMessage(text);
            sm.sendMultipartTextMessage(to, null, parts, null, null);
            Log.d("HazardApp", "SMS sent to " + to + ": " + text);
        } catch (Exception e) {
            Log.d("HazardApp", "SMS failed: " + e.getMessage(), e);
        }
    }
}
