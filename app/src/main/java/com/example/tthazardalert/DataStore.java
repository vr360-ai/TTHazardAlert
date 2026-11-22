package com.example.tthazardalert;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

// Holds the lists in memory and saves/loads them using the Objects in Files pattern with thread synchronization.
public class DataStore {
    private static final String TAG = "HazardApp";
    private static final String FILENAME = "hazard_data.dat";
    private static DataStore instance;

    public ArrayList<Subscriber> subscribers = new ArrayList<>();
    public ArrayList<Alert> alerts = new ArrayList<>();
    private boolean loaded = false;

    public static synchronized DataStore get() {
        if (instance == null) instance = new DataStore();
        return instance;
    }

    // Needed because SmsReceiver can run when no Activity has loaded the file yet.
    public synchronized void ensureLoaded(Context context) {
        if (!loaded) load(context);
    }

    public static boolean samePhone(String a, String b) {
        if (a == null || b == null) return false;
        String x = a.replaceAll("\\D", "");
        String y = b.replaceAll("\\D", "");
        if (x.isEmpty() || y.isEmpty()) return false;
        if (x.equals(y)) return true;
        int n = Math.min(10, Math.min(x.length(), y.length()));
        return x.substring(x.length() - n).equals(y.substring(y.length() - n));
    }

    public synchronized Subscriber find(String phone) {
        for (Subscriber s : subscribers) {
            if (samePhone(s.getPhone(), phone)) return s;
        }
        return null;
    }

    public synchronized void save(Context context) {
        try (FileOutputStream fout = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
             ObjectOutputStream oos = new ObjectOutputStream(fout)) {

            oos.writeInt(subscribers.size());
            for (Subscriber s : subscribers) oos.writeObject(s);
            oos.writeInt(alerts.size());
            for (Alert a : alerts) oos.writeObject(a);

            Log.d(TAG, "File write successful -> " + FILENAME);
        } catch (Exception e) {
            Log.d(TAG, "Write failed: " + e.getMessage(), e);
        }
    }

    public synchronized void load(Context context) {
        try (FileInputStream fin = context.openFileInput(FILENAME);
             ObjectInputStream ois = new ObjectInputStream(fin)) {

            subscribers.clear();
            alerts.clear();
            int n = ois.readInt();
            for (int i = 0; i < n; i++) subscribers.add((Subscriber) ois.readObject());
            int m = ois.readInt();
            for (int i = 0; i < m; i++) alerts.add((Alert) ois.readObject());

            Log.d(TAG, "Loaded " + n + " subscribers and " + m + " alerts");
        } catch (Exception e) {
            Log.d(TAG, "Open failed (probably first run): " + e.getMessage());
        }
        loaded = true;
    }
}


