package com.example.tthazardalert;

import java.io.Serializable;

public class Subscriber implements Serializable {
    private static final long serialVersionUID = 1L;

    public String phone;
    public String region;
    public boolean safe;

    public Subscriber(String phone, String region) {
        this.phone = phone;
        this.region = region;
        this.safe = false;
    }

    public String getPhone()  { return phone; }
    public String getRegion() { return region; }
    public boolean isSafe()   { return safe; }
    public void setSafe(boolean s) { safe = s; }

    @Override
    public String toString() {
        return phone + "  (" + region + ")  " + (safe ? "SAFE" : "unconfirmed");
    }
}

