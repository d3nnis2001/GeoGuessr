package com.gse23.dschielke;

import android.util.Log;

import java.util.ArrayList;

final class Logging {
    private Logging() { }
    public static void logCurrentImage(String filename, ArrayList<ImageInfo> imagesInf, String actName) {
        for (ImageInfo imageInfo : imagesInf) {
            if (imageInfo.getFileName().equals(filename)) {
                Log.d(actName, imageInfo.getFileName());
                Log.d(actName, imageInfo.getWidth());
                Log.d(actName, imageInfo.getLength());
            }
        }
    }
    public static void logImageData(ArrayList<ImageInfo> imginf, String actName) {
        for (int i = 0; i < imginf.size(); i++) {
            String filename = imginf.get(i).getFileName();
            String width = imginf.get(i).getWidth();
            String length = imginf.get(i).getLength();
            String desc = imginf.get(i).getDesc();
            Log.d(actName, filename);
            Log.d(actName, width);
            Log.d(actName, length);
            if (desc != null) {
                Log.d(actName, desc);
            } else {
                Log.d(actName, "No Description");
            }
        }
    }
}
