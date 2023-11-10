package com.gse23.dschielke;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Bundle;
import android.content.res.AssetManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;

import java.util.Arrays;
import java.util.Random;

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
    ArrayList<String> hadImage = new ArrayList<>();
    ArrayList<ImageInfo> imagesInf = new ArrayList<>();
    int currAlbum = 0;
    String albuName;
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
            currAlbum = albumNum;
            try {
                albuName = logCurrentFile(albumNum);
                readAllImages(albuName);
            } catch (IOException e) {
                Log.d(actName, "Folder doesn't exist");
            }
        }
        logImageData(imagesInf);
        try {
            String[] temp = assetManager.list(albuSlash + albuName);
            assert temp != null;
            hadImage.addAll(Arrays.asList(temp));
            showPicture(albuName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        nextPic();
    }
    private void nextPic() {
        Button showPictureButton = findViewById(R.id.showButton);
        showPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    showPicture(albuName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void showPicture(String foldername) throws IOException {
        assetManager = getAssets();
        if (hadImage != null && hadImage.size() > 0) {
            Random random = new Random();
            int randomNum = random.nextInt(hadImage.size());
            InputStream st = getAssets().open(albuSlash + foldername + "/" + hadImage.get(randomNum));
            ImageView imageView = findViewById(R.id.imageView);
            Drawable drawable = Drawable.createFromStream(st, null);
            imageView.setImageDrawable(drawable);
            hadImage.remove(randomNum);
        } else {
            noImagesDialog();
        }
    }
    private void noImagesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("That's it");
        builder.setMessage("You've gone through all images");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
    private void readAllImages(String foldername) throws IOException {
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
    private void logImageData(ArrayList<ImageInfo> imginf) {
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
    private void returnToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
    private String logCurrentFile(int pos) throws IOException {
        assetManager = getAssets();
        String[] albumNames = assetManager.list("albums");
        assert albumNames != null;
        Log.d("Album:", albumNames[pos]);
        return albumNames[pos];
    }
    private Boolean fitsFormat(String filename) {
        String lower = filename.toLowerCase();
        return lower.endsWith(".jpeg") || lower.endsWith(".jpg") || lower.endsWith(".png");
    }
}
