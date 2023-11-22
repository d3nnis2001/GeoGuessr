package com.gse23.dschielke;

public class Points {
    private double distance;
    public Points(double distance) {
        this.distance = distance;
    }
    public int getPoints() {
        final int maxpoints = 5000;
        final int maxDistance = 10000;
        final int minDistance = 10;
        if (distance <= minDistance) {
            return maxpoints;
        } else if (distance >= maxDistance) {
            return 0;
        } else {
            return (int) Math.round(maxpoints / (Math.log(maxDistance / minDistance))
                    * Math.log(maxDistance / distance));
        }
    }
}
