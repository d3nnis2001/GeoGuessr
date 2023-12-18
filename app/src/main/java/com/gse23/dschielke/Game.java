package com.gse23.dschielke;

import java.util.ArrayList;
import java.util.Random;

public class Game {
    Link link;
    String fulllink;
    Cords cord;
    Points points;
    double guessedLatitude;
    double guessedLongitude;
    double actualLatitude;
    double actualLongitude;
    double distance;
    ArrayList<ImageInfo> imgInfo;
    ArrayList<String> hadImage;
    public Game(ArrayList<ImageInfo> imgInfo, ArrayList<String> hadImage) {
        this.imgInfo = imgInfo;
        this.hadImage = hadImage;
    }
    public String getRandomPic() {
        if (hadImage != null && hadImage.size() > 0) {
            Random random = new Random();
            int randomNum = random.nextInt(hadImage.size());
            String randomString = hadImage.get(randomNum);
            hadImage.remove(randomNum);
            return randomString;
        }
        return "";
    }
    public void setCords(double guessedLatitude, double guessedLongitude,
                         double actualLatitude, double actualLongitude) {
        this.guessedLatitude = guessedLatitude;
        this.guessedLongitude = guessedLongitude;
        this.actualLatitude = actualLatitude;
        this.actualLongitude = actualLongitude;
    }
    public ImageInfo getCurrentImageInf(String filename) {
        for (ImageInfo imageInfo : imgInfo) {
            if (imageInfo.getFileName().equals(filename)) {
                return imageInfo;
            }
        }
        return null;
    }
    public Boolean checkValues() {
        final int boundlen = 180;
        final int boundwidth = 90;
        if (guessedLongitude > boundlen || guessedLongitude < (boundlen * -1) || guessedLatitude > boundwidth
                || guessedLatitude < (boundwidth * -1)) {
            return false;
        }
        return true;
    }
    public double getDistance() {
        cord = new Cords(actualLatitude, actualLongitude, guessedLatitude, guessedLongitude);
        distance = cord.getDistance();
        return distance;
    }
    public String getSensible() {
        return cord.sensibleUnitAddition();
    }
    public String getLink() {
        link = new Link(guessedLatitude, guessedLongitude, actualLatitude, actualLongitude);
        fulllink = link.getLink();
        return fulllink;
    }
    public double getPoints() {
        points = new Points(distance);
        return points.getPoints();
    }

    public double getGuessedLatitude() {
        return guessedLatitude;
    }

    public void setGuessedLatitude(double guessedLatitude) {
        this.guessedLatitude = guessedLatitude;
    }

    public double getGuessedLongitude() {
        return guessedLongitude;
    }

    public void setGuessedLongitude(double guessedLongitude) {
        this.guessedLongitude = guessedLongitude;
    }

    public double getActualLatitude() {
        return actualLatitude;
    }

    public void setActualLatitude(double actualLatitude) {
        this.actualLatitude = actualLatitude;
    }

    public double getActualLongitude() {
        return actualLongitude;
    }

    public void setActualLongitude(double actualLongitude) {
        this.actualLongitude = actualLongitude;
    }
}
