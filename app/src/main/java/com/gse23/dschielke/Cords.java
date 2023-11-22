package com.gse23.dschielke;

import android.util.Log;

public class Cords {
    double actualLongitude;
    double guessedLongitude;
    double actualLatitude;
    double guessedLatitude;
    public Cords(double actualLatitude, double actualLongitude, double guessedLatitude, double guessedLongitude) {
        this.actualLatitude = actualLatitude;
        this.actualLongitude = actualLongitude;
        this.guessedLatitude = guessedLatitude;
        this.guessedLongitude = guessedLongitude;
    }
    public double getDistance() {
        // Earth radius in meters
        final int radius = 6371000;
        double diffLat = Math.toRadians(actualLatitude - guessedLatitude);
        double diffLong = Math.toRadians(actualLongitude - guessedLongitude);
        double first = Math.pow(Math.sin(diffLat / 2), 2) + Math.cos(Math.toRadians(actualLatitude))
                * Math.cos(Math.toRadians(guessedLatitude)) * Math.sin(diffLong / 2)
                * Math.sin(diffLong / 2);
        double second = 2 * Math.atan2(Math.sqrt(first), Math.sqrt(1 - first));
        return radius * second;
    }
    public String sensibleUnitAddition() {
        final int mega = 1000000;
        final int kilo = 1000;
        final int hekto = 100;
        final int deka = 10;
        String roundIt = "%.2f";
        char meter = 'm';
        String output = "";
        double dist = getDistance();
        Log.d("Distance", String.format(roundIt, dist) + meter);
        if (dist >= mega) {
            output = "M" + String.format(roundIt, dist / mega) + meter;
        } else if (dist >= kilo) {
            output = "k" + String.format(roundIt, dist / kilo) + meter;
        } else if (dist >= hekto) {
            output = "h" + String.format(roundIt, dist / hekto) + meter;
        } else {
            output = "da" + String.format(roundIt, dist / deka) + meter;
        }
        return output;
    }
}
