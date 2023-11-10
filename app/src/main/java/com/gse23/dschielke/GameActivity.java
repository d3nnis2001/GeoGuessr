package com.gse23.dschielke;

import android.app.Activity;
import android.content.Intent;
import android.media.ExifInterface;
import android.os.Bundle;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class GameActivity extends Activity {
    public static class NoImagesInAlbumException extends RuntimeException {
        public NoImagesInAlbumException(String message) {
            super(message);
        }
    }
    public static class CorruptedExifDataException extends NullPointerException {
        public CorruptedExifDataException(String message) {
            super(message);
        }
    }
    ArrayList<ImageInfo> imagesInf = new ArrayList<>();
    AssetManager assetManager;
    String albuNum = "AlbumNum";
    String albuSlash = "albums/";
    String actName = "GameActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);
        assetManager = getAssets();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(albuNum)) {
            int albumNum = intent.getIntExtra(albuNum, 0);
            try {
                String albuName = logCurrentFile(albumNum);
                readAllImages(albuName);
            } catch (IOException e) {
                Log.d(actName, "Folder doesn't exist");
            }
        }
        logImageData(imagesInf);
    }
    public void readAllImages(String foldername) throws IOException {
        assetManager = getAssets();
        String[] albumNames = assetManager.list(albuSlash + foldername);
        int counter = 0;
        assert albumNames != null;
        for (String fileName : albumNames) {
            if (fitsFormat(fileName)) {
                counter++;
                InputStream in =  getAssets().open(albuSlash + foldername + "/"
                        + fileName);
                ExifInterface exifInterface = new ExifInterface(in);
                String width = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                String length = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                if (width == null && length == null) {
                    returnToMain();
                    throw new CorruptedExifDataException("Exif Data not complete");
                }
                String desc = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION);
                ImageInfo imageInfo = new ImageInfo(fileName, width, length, desc);
                imagesInf.add(imageInfo);
            }
        }
        if (counter == 0) {
            returnToMain();
            throw new NoImagesInAlbumException("No files found! Return to start");
        }
    }
    public void logImageData(ArrayList<ImageInfo> imginf) {
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
    public void returnToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
    public String logCurrentFile(int pos) throws IOException {
        assetManager = getAssets();
        String[] albumNames = assetManager.list("albums");
        Log.d("Album:", albumNames[pos]);
        return albumNames[pos];
    }
    public Boolean fitsFormat(String filename) {
        String lower = filename.toLowerCase();
        return lower.endsWith(".jpeg") || lower.endsWith(".jpg") || lower.endsWith(".png");
    }
}
