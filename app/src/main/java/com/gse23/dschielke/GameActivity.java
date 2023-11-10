package com.gse23.dschielke;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

public class GameActivity extends Activity {
    public static class NoImagesInAlbumException extends RuntimeException {
        public NoImagesInAlbumException(String message) {
            super(message);
        }
    }
    ArrayList<ImageInfo> imagesInf = new ArrayList<>();
    AssetManager assetManager;
    String albuNum = "AlbumNum";
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
        String[] albumNames = assetManager.list("albums/" + foldername);
        int counter = 0;
        assert albumNames != null;
        for (String fileName : albumNames) {
            if (fitsFormat(fileName)) {
                counter++;
                ImageInfo imageInfo = new ImageInfo(fileName);
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
            Log.d(actName, filename);
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
