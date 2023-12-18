package com.gse23.dschielke;

final class Util {
    private Util() { }
    public static String formatCord(String cord) {
        final int min = 60;
        final int sec = 3600;
        final int mil = 100000000;
        final int umrechnung = 1000000;
        final int drei = 3;
        cord = cord.replace(",", ".");
        String[] cords = cord.split("/");
        double output = Double.parseDouble(cords[0])
                + Double.parseDouble(cords[1]) / min
                + Double.parseDouble(cords[2]) / sec
                + Double.parseDouble(cords[drei]) / mil;
        double newres = Math.round(output * umrechnung);
        return String.valueOf(newres / umrechnung);
    }
    public static Boolean fitsFormat(String filename) {
        String lower = filename.toLowerCase();
        return lower.endsWith(".jpeg") || lower.endsWith(".jpg") || lower.endsWith(".png");
    }
}
