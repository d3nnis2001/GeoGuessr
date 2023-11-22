package com.gse23.dschielke;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Bundle;
import android.content.res.AssetManager;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.text.HtmlCompat;


import java.util.Arrays;
import java.util.Random;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class GameActivity extends Activity {
    double actualLongitude;
    double actualLatitude;
    String currentFilename;
    EditText breitengrad;
    EditText laengengrad;
    ArrayList<String> hadImage = new ArrayList<>();
    ArrayList<ImageInfo> imagesInf = new ArrayList<>();
    int currAlbum = 0;
    String albuName;
    AssetManager assetManager;
    String albuNum = "AlbumNum";
    String albuSlash = "albums/";
    String actName = "GameActivity";
    String komma = ",";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);
        breitengrad = findViewById(R.id.Breitengrad);
        laengengrad = findViewById(R.id.Laengengrad);
        assetManager = getAssets();
        Intent intent = getIntent();
        // Add Exif data to datastructure
        if (intent != null && intent.hasExtra(albuNum)) {
            int albumNum = intent.getIntExtra(albuNum, 0);
            currAlbum = albumNum;
            try {
                albuName = logCurrentFile(albumNum);
                readAllImages(albuName);
            } catch (IOException | NoImagesInAlbumException | CorruptedExifDataException e) {
                Log.d(actName, "Folder doesn't exist");
            }
        }
        // Log all data from datastructure
        logImageData(imagesInf);
        // Display picture
        try {
            String[] temp = assetManager.list(albuSlash + albuName);
            assert temp != null;
            hadImage.addAll(Arrays.asList(temp));
            showPicture(albuName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        submitGuess();
        // Next Picture Button
        nextPic();
    }
    public void onBackPressed() {
        showExitDialog();
    }
    public void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Leave the Game");
        builder.setMessage("Do you want to leave your current Game and go back to choosing a new Album?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                returnToMain();
            }
        });
        builder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogTwo, int whichTwo) {
                dialogTwo.dismiss();
            }
        });
        builder.create().show();
    }
    private void nextPic() {
        Button showPictureButton = findViewById(R.id.showButton);
        showPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    showPicture(albuName);
                    laengengrad.setEnabled(true);
                    breitengrad.setEnabled(true);
                    setDistance("No matter", false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void submitGuess() {
        Button submit = findViewById(R.id.submitButton);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double inputlen = Double.parseDouble(laengengrad.getText().toString());
                double inputwidth = Double.parseDouble(breitengrad.getText().toString());
                final int boundlen = 180;
                final int boundwidth = 90;
                if (inputlen > boundlen || inputlen < (boundlen * -1) || inputwidth > boundwidth
                    || inputwidth < (boundwidth * -1)) {
                    wrongValuesDialog();
                } else {
                    laengengrad.setEnabled(false);
                    breitengrad.setEnabled(false);
                    // get link and set link on textview
                    String link = getLink(inputlen, inputwidth);
                    setLink(link);
                    Cords cord = new Cords(actualLatitude, actualLongitude, inputwidth, inputlen);
                    double dist = cord.getDistance();
                    String output = cord.sensibleUnitAddition();
                    Points point = new Points(dist);
                    setPoints(point.getPoints());
                    setDistance(output, true);
                }
            }
        });
    }
    private void setPoints(double points) {
        TextView result = findViewById(R.id.points);
        result.setText(String.valueOf(points));
    }

    private void setDistance(String dis, Boolean vis) {
        TextView dist = findViewById(R.id.distance);
        if (vis) {
            dist.setText(dis);
            dist.setVisibility(View.VISIBLE);
        } else {
            dist.setVisibility(View.INVISIBLE);
        }
    }

    private void setLink(String link) {
        TextView linkView = findViewById(R.id.mapLink);
        String text = "<string name='hyperlink'><a href='" + link + "'>Map</a></string>";
        linkView.setText(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY));
        linkView.setMovementMethod(LinkMovementMethod.getInstance());
    }
    private String getLink(double inputlen, double inputwidth) {
        String link = "https://www.openstreetmap.org/directions?"
                + "engine=fossgis_valhalla_foot&route=";
        ImageInfo img =  getCurrentImageInf(currentFilename);
        assert img != null;
        String currLen = formatCord(img.getLength());
        String currWid = formatCord(img.getWidth());
        actualLatitude = Double.parseDouble(currWid);
        actualLongitude = Double.parseDouble(currLen);
        String map = link + inputwidth + komma + inputlen + ";" + currWid + komma + currLen;
        Log.d("GetLink", map);
        return map;
    }
    private void showPicture(String foldername) throws IOException {
        assetManager = getAssets();
        if (hadImage != null && hadImage.size() > 0) {
            Random random = new Random();
            int randomNum = random.nextInt(hadImage.size());
            String randomString = hadImage.get(randomNum);
            currentFilename = randomString;
            InputStream st = getAssets().open(albuSlash + foldername + "/" + randomString);
            ImageView imageView = findViewById(R.id.imageView);
            Drawable drawable = Drawable.createFromStream(st, null);
            imageView.setImageDrawable(drawable);
            breitengrad.setText("");
            laengengrad.setText("");
            // Added Logging of the picture before removing it from the arr
            logCurrentImage(randomString);
            hadImage.remove(randomNum);
        } else {
            noImagesDialog();
        }
    }
    private void logCurrentImage(String filename) {
        for (ImageInfo imageInfo : imagesInf) {
            if (imageInfo.getFileName().equals(filename)) {
                Log.d(actName, imageInfo.getFileName());
                Log.d(actName, imageInfo.getWidth());
                Log.d(actName, imageInfo.getLength());
            }
        }
    }
    private ImageInfo getCurrentImageInf(String filename) {
        for (ImageInfo imageInfo : imagesInf) {
            if (imageInfo.getFileName().equals(filename)) {
                return imageInfo;
            }
        }
        return null;
    }
    private void noImagesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("That's it");
        builder.setMessage("You've gone through all images");
        builder.setPositiveButton("Understood", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void wrongValuesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Wrong Values");
        builder.setMessage("The longitude goes from -180 to 180 and the latitude from -90 to 90. Correct your answer!");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
    private void readAllImages(String foldername) throws IOException,
            NoImagesInAlbumException, CorruptedExifDataException {
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
                String width = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                String length = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
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
    private String formatCord(String cord) {
        final int min = 60;
        final int sec = 3600;
        final int mil = 100000000;
        final int umrechnung = 1000000;
        final int drei = 3;
        cord = cord.replace(komma, ".");
        String[] cords = cord.split("/");
        double output = Double.parseDouble(cords[0])
                + Double.parseDouble(cords[1]) / min
                + Double.parseDouble(cords[2]) / sec
                + Double.parseDouble(cords[drei]) / mil;
        return String.valueOf(Math.round((output) * umrechnung) / umrechnung);
    }
}
