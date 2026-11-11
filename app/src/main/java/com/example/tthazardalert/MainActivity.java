package com.example.tthazardalert;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_SMS = 100;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusText = findViewById(R.id.statusText);
        DataStore.get().ensureLoaded(this);
        requestSmsPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DataStore ds = DataStore.get();
        statusText.setText(ds.subscribers.size() + " subscribers  |  "
                + ds.alerts.size() + " alerts sent");
    }

    private void requestSmsPermissions() {
        boolean send = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED;
        boolean recv = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                == PackageManager.PERMISSION_GRANTED;
        if (!send || !recv) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS},
                    REQ_SMS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_SMS) {
            for (int r : grantResults) {
                if (r != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "SMS permissions are required for this app to work",
                            Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }

    // Wired with android:onClick in activity_main.xml
    public void openSendAlert(View v)   { startActivity(new Intent(this, SendAlertActivity.class)); }
    public void openSubscribers(View v) { startActivity(new Intent(this, SubscribersActivity.class)); }
    public void openAlertLog(View v)    { startActivity(new Intent(this, AlertLogActivity.class)); }

    public void exitApp(View v) {
        DataStore.get().save(this);
        finish();
    }
}
