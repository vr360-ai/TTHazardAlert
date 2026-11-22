package com.example.tthazardalert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) return;

        final PendingResult pendingResult = goAsync();
        final Context appContext = context.getApplicationContext();
        final Intent receivedIntent = intent;

        new Thread(() -> {
            try {
                for (SmsMessage m : Telephony.Sms.Intents.getMessagesFromIntent(receivedIntent)) {
                    if (m != null) {
                        String sender = m.getOriginatingAddress();
                        String rawBody = m.getMessageBody();
                        if (rawBody != null) {
                            String body = rawBody.trim().toUpperCase();
                            Log.d("HazardApp", "SMS received from " + sender + ": " + body);
                            CommandProcessor.handle(appContext, sender, body);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("HazardApp", "Error processing received SMS", e);
            } finally {
                pendingResult.finish();
            }
        }).start();
    }
}


