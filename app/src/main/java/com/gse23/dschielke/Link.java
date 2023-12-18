package com.gse23.dschielke;

public class Link {
    double guessedLatitude;
    double guessedLongitude;
    double actualLatitude;
    double actualLongitude;

    public Link(double guessedLatitude, double guessedLongitude, double actualLatitude, double actualLongitude) {
        this.guessedLatitude = guessedLatitude;
        this.guessedLongitude = guessedLongitude;
        this.actualLatitude = actualLatitude;
        this.actualLongitude = actualLongitude;
    }
    public String getLink() {
        String link = "https://www.openstreetmap.org/directions?"
                + "engine=fossgis_valhalla_foot&route=";
        String komma = ",";
        String fullLink = link + guessedLatitude + komma + guessedLongitude
                + ";" + actualLatitude + komma + actualLongitude;
        return fullLink;
    }

}
