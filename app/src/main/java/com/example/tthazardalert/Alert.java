package com.example.tthazardalert;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Alert implements Serializable {
    private static final long serialVersionUID = 1L;

    public String region, type, message;
    public long time;

    public Alert(String region, String type, String message) {
        this.region = region;
        this.type = type;
        this.message = message;
        this.time = System.currentTimeMillis();
    }

    public String getRegion()  { return region; }
    public String getType()    { return type; }
    public String getMessage() { return message; }

    @Override
    public String toString() {
        SimpleDateFormat f = new SimpleDateFormat("dd MMM HH:mm", Locale.US);
        return f.format(new Date(time)) + "   " + type + " - " + region + "\n" + message;
    }
}
