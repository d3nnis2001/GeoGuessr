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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class GameActivity extends Activity {
    EditText breitengrad;
    EditText laengengrad;
    ImageView image;
    Button submit;
    TextView linkView;
    TextView dist;
    TextView result;
    Button showPictureButton;
    ArrayList<ImageInfo> imagesInf = new ArrayList<>();
    int currAlbum = 0;
    String albuName;
    AlertDialog dialog;
    AssetManager assetManager;
    String albuNum = "AlbumNum";
    String albuSlash = "albums/";
    String actName = "GameActivity";
    Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);
        // ---------------- VIEWS -----------------
        result = findViewById(R.id.points);
        breitengrad = findViewById(R.id.Breitengrad);
        laengengrad = findViewById(R.id.Laengengrad);
        image = findViewById(R.id.imageView);
        submit = findViewById(R.id.submitButton);
        linkView = findViewById(R.id.mapLink);
        dist = findViewById(R.id.distance);
        showPictureButton = findViewById(R.id.showButton);

        assetManager = getAssets();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(albuNum)) {
            int albumNum = intent.getIntExtra(albuNum, 0);
            currAlbum = albumNum;
            try {
                albuName = logCurrentFolder(albumNum);
                readAllImages(albuName);
                try {
                    // Log all data from datastructure
                    Logging.logImageData(imagesInf, actName);
                    // Display picture
                    String[] temp = assetManager.list(albuSlash + albuName);
                    assert temp != null;
                    ArrayList<String> hadImage = new ArrayList<>(Arrays.asList(temp));
                    game = new Game(imagesInf, hadImage);
                    showPicture(albuName);
                    submitGuess();
                    nextPic();
                } catch (IOException e) {
                    String title = "UPPPSS";
                    String message = "This wasn't supposed to happen. Please contact our support!";
                    String positive = "Alright...";
                    standardDialog(title, message, positive, true);
                    throw new RuntimeException(e);
                }
            } catch (IOException | NoImagesInAlbumException | CorruptedExifDataException e) {
                Log.d(actName, "Folder doesn't exist");
                String title = "ERROR";
                String message = "Something went wrong. Please choose a different album";
                String positive = "I will";
                standardDialog(title, message, positive, false);
            }
        }
    }
    private void nextPic() {
        showPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // Show next pic and reset the inputs and textviews
                    showPicture(albuName);
                    laengengrad.setEnabled(true);
                    breitengrad.setEnabled(true);
                    setDistance("No matter", false);
                    result.setText(String.valueOf(0));
                } catch (IOException e) {
                    String title = "UPPSS";
                    String message = "Something went wrong when setting up the next round";
                    String positive = "Choose different album";
                    standardDialog(title, message, positive, true);
                    e.printStackTrace();
                }
            }
        });
    }
    private void readAllImages(String foldername) throws IOException,
            NoImagesInAlbumException, CorruptedExifDataException {
        assetManager = getAssets();
        String[] albumNames = assetManager.list(albuSlash + foldername);
        int counter = 0;
        assert albumNames != null;
        for (String fileName : albumNames) {
            if (Util.fitsFormat(fileName)) {
                counter++;
                InputStream in = getAssets().open(albuSlash + foldername + "/"
                        + fileName);
                ExifInterface exifInterface = new ExifInterface(in);
                String width = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                String length = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                in.close();
                if (width == null && length == null) {
                    String title = "An error has occured...";
                    String message = "The Exif data for the one of the pictures in the album is"
                            + " missing. Pls choose a different one!";
                    String positive = "Choose other";
                    standardDialog(title, message, positive, true);
                    throw new CorruptedExifDataException("Exif Data not complete");
                }
                String desc = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION);
                ImageInfo imageInfo = new ImageInfo(fileName, width, length, desc);
                imagesInf.add(imageInfo);
            }
        }
        if (counter == 0) {
            String title = "We apologize...";
            String message = "This folder is right now empty. Check it out in the near future again!";
            String positive = "Understood";
            standardDialog(title, message, positive, true);
            throw new NoImagesInAlbumException("No files found! Return to start");
        }
    }
    private void showPicture(String foldername) throws IOException {
        assetManager = getAssets();
        String randomString = game.getRandomPic();
        if (!randomString.equals("")) {
            InputStream st = getAssets().open(albuSlash + foldername + "/" + randomString);
            Drawable drawable = Drawable.createFromStream(st, null);
            st.close();
            image.setImageDrawable(drawable);
            breitengrad.setText("");
            laengengrad.setText("");
            // Added Logging of the picture before removing it from the arr
            Logging.logCurrentImage(randomString, imagesInf, actName);
            ImageInfo img = game.getCurrentImageInf(randomString);
            String len = Util.formatCord(img.getLength());
            String wid = Util.formatCord(img.getWidth());
            game.setActualLatitude(Double.parseDouble(len));
            game.setActualLongitude(Double.parseDouble(wid));
        } else {
            String title = "That's it!";
            String message = "You've gone through all images";
            String positive = "Alright";
            standardDialog(title, message, positive, false);
        }
    }
    // ------------------------ BUTTONS -------------------------
    private void submitGuess() {
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputLenString = laengengrad.getText().toString();
                String inputWidthString = breitengrad.getText().toString();

                // Check rather nothing was written in the user inputs
                if (inputLenString.isEmpty() || inputWidthString.isEmpty()) {
                    String title = "No values";
                    String message = "Please input some values!";
                    String positive = "Alrighty";
                    standardDialog(title, message, positive, false);
                } else {
                    double inputlen = Double.parseDouble(laengengrad.getText().toString());
                    double inputwidth = Double.parseDouble(breitengrad.getText().toString());
                    game.setGuessedLatitude(inputwidth);
                    game.setGuessedLongitude(inputlen);
                    if (!game.checkValues()) {
                        String title = "Wrong values";
                        String message = "The longitude goes from -180 to 180 and the latitude"
                                + "from -90 to 90. Correct your answer!";
                        String positive = "Fix it";
                        standardDialog(title, message, positive, false);
                    } else {
                        laengengrad.setEnabled(false);
                        breitengrad.setEnabled(false);

                        String currlink = game.getLink();
                        double dist = game.getDistance();
                        String output = game.getSensible();
                        double points = game.getPoints();

                        String roundIt = "%.2f";

                        Log.d("GetLink", currlink);
                        Log.d("Distance", String.format(roundIt, dist) + "m");

                        setLink(currlink);
                        setPoints(points);
                        setDistance(output, true);
                    }
                }
            }
        });
    }
    private void returnToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
    // ------------------------ SET VIEWS -------------------------
    private void setLink(String link) {
        String text = "<string name='hyperlink'><a href='" + link + "'>Map</a></string>";
        linkView.setText(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY));
        linkView.setMovementMethod(LinkMovementMethod.getInstance());
    }
    private void setDistance(String dis, Boolean vis) {
        if (vis) {
            dist.setText(dis);
            dist.setVisibility(View.VISIBLE);
        } else {
            dist.setVisibility(View.INVISIBLE);
        }
    }
    private void setPoints(double points) {
        result.setText(String.valueOf(points));
    }
    public void onBackPressed() {
        showExitDialog();
    }
    // ------------------------ LOGGING -------------------------
    private String logCurrentFolder(int pos) throws IOException {
        assetManager = getAssets();
        String[] albumNames = assetManager.list("albums");
        assert albumNames != null;
        Log.d("Album:", albumNames[pos]);
        return albumNames[pos];
    }
    // ------------------------ DIALOGS -------------------------
    public void standardDialog(String title, String message, String positive, Boolean returnMain) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (!isFinishing() && !isDestroyed()) {
                        if (returnMain) {
                            returnToMain();
                        }
                    }
                }
            });
            dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
    }
    public void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Leave the Game");
        builder.setMessage("Do you want to leave your current Game and go back to choosing a new Album?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                returnToMain();
            }
        });
        builder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogTwo, int whichTwo) {
                dialogTwo.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
