package com.example.tthazardalert;

import android.content.Context;

// Processes each received text and sends a reply.
public class CommandProcessor {

    public static void handle(Context c, final String from, String body) {
        DataStore ds = DataStore.get();
        ds.ensureLoaded(c);

        String[] parts = body.split("\\s+");
        String reply;

        switch (parts[0]) {
            case "SUBSCRIBE": {
                // Everything after the word SUBSCRIBE is the region (so "PORT OF SPAIN" works).
                String region = body.length() > 9 ? body.substring(9).trim() : "";
                if (region.isEmpty()) {
                    reply = "Use: SUBSCRIBE <REGION>";
                    break;
                }
                ds.subscribers.removeIf(x -> x.getPhone().equals(from));
                ds.subscribers.add(new Subscriber(from, region));
                reply = "Subscribed to hazard alerts for " + region + ". Text HELP for commands.";
                break;
            }

            case "UNSUBSCRIBE":
                ds.subscribers.removeIf(x -> x.getPhone().equals(from));
                reply = "You have been unsubscribed.";
                break;

            case "SAFE": {
                Subscriber s = ds.find(from);
                if (s == null) {
                    reply = "Not subscribed. Text SUBSCRIBE <REGION>.";
                } else {
                    s.setSafe(true);
                    reply = "Thanks, you are marked SAFE.";
                }
                break;
            }

            case "STATUS": {
                Subscriber s = ds.find(from);
                if (s == null) {
                    reply = "Not subscribed. Text SUBSCRIBE <REGION>.";
                } else {
                    reply = "No alerts for " + s.getRegion() + ".";
                    for (int i = ds.alerts.size() - 1; i >= 0; i--) {
                        Alert a = ds.alerts.get(i);
                        if (a.getRegion().equalsIgnoreCase(s.getRegion())) {
                            reply = "Latest (" + a.getType() + "): " + a.getMessage();
                            break;
                        }
                    }
                }
                break;
            }

            case "HELP":
                reply = "Commands: SUBSCRIBE <REGION>, SAFE, STATUS, UNSUBSCRIBE";
                break;

            default:
                reply = "Unknown command. Text HELP.";
        }

        ds.save(c);
        SmsHelper.send(c, from, reply);
    }
}
