package com.gse23.dschielke;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Log;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;


/**
 * Das ist die MainActivity, die zuerst ausgeführt wird, wenn die App gestartet wird.
 */
public class MainActivity extends AppCompatActivity {
    int currpos = -1;
    String albu = "albums";
    String albuSlash = "albums/";
    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createListview();
        actionButton();
    }

    public void actionButton() {
        Button button = (Button) findViewById(R.id.startgamebutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currpos != -1) {
                    Intent intent = new Intent(MainActivity.this, GameActivity.class);
                    startActivity(intent);
                    // Transfer the pos of the album that we are currently in
                    Intent intentTwo = new Intent(MainActivity.this, GameActivity.class);
                    intentTwo.putExtra("AlbumNum", currpos);
                    startActivity(intentTwo);
                } else {
                    forgotAlbum();
                }
            }
        });
    }

    public void forgotAlbum() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Don't forget...");
        builder.setMessage("To choose an album to play the game!");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void createListview() {
        AssetManager assetManager = getAssets();
        String[] albumNames = new String[0];
        try {
            albumNames = assetManager.list(albu);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //fileLogger(assetManager);
        lv = (ListView) findViewById(R.id.albumlist);
        AlbumList aa = new AlbumList(getApplicationContext(), albumNames);
        lv.setAdapter(aa);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int item, long lag) {
                if (currpos != item) {
                    if (currpos != -1) {
                        aa.putSelected(currpos, false);
                    }
                    currpos = item;
                    aa.putSelected(currpos, true);
                }
            }
        });
    }
    /**
     * FileLogger geht in die einzelnen Subdirectories und logged alle Files.
     * @param assetManager AssetManager für AssetDirectory
     */

    public void fileLogger(AssetManager assetManager) {
        try {
            // Store all subdirectories of albums
            String[] dirsInAlbum = assetManager.list(albu);
            assert dirsInAlbum != null;
            for (String folderName : dirsInAlbum) {
                String[] images = assetManager.list(albuSlash + folderName);
                if (images.length == 0) {
                    Log.d("FOLDEREMPTY", "Folder doesn't contain anything!");
                } else {
                    int imageCounter = 0;
                    for (String fileName: images) {
                        if (fitsFormat(fileName)) {
                            Log.d(fileName, folderName);
                            imageCounter++;
                            try (InputStream in =  getAssets().open(albuSlash + folderName + "/" + fileName)) {
                                readExif(in);
                            } catch (IOException e) {
                                // Already being handled in game activity and method is not being used
                                Log.d("EXIFERROR", "Exif Information couldn't be found");
                            }
                        }
                    }
                    if (imageCounter == 0) {
                        // Already being handled in game activity and method is not being used
                        Log.d("FILEERROR", "No compatible files have been found in folder " + folderName);
                    }
                }
            }
        } catch (IOException e) {
            // Already being handled in game activity and method is not being used
            Log.d("FOLDERERROR", "There are no Folder/Files");
        }
    }

    /**
     * Wird von FileLogger vor dem loggen genutzt um zu gucken ob das Bildformat richtig ist.
     * @param filename Filename für die Überprüfung des Filetyps
     * @return true, wenn der Dateiname das erwartete Format hat, ansonsten false.
     */
    public Boolean fitsFormat(String filename) {
        String lower = filename.toLowerCase();
        return lower.endsWith(".jpeg") || lower.endsWith(".jpg") || lower.endsWith(".png");
    }
    /**
     * Liest die Exif Daten ein und logged diese aus.
     * @param in InputStream für eine File.
     */
    public static void readExif(InputStream in) throws IOException {
        ExifInterface exifInterface = new ExifInterface(in);

        String focalLength = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
        String la = "Längengrad";
        if (focalLength != null) {
            Log.d(la, focalLength);
        } else {
            Log.d(la, "Längengrad nicht gefunden");
        }
        String focalWidth = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        String br = "Breitengrad";
        if (focalWidth != null) {
            Log.d(br, focalWidth);
        } else {
            Log.d(br, "Breitengrad nicht gefunden");
        }
        String desc = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION);
        String de = "Description";
        if (desc != null) {
            Log.d(de, desc);
        } else {
            Log.d(de, "Image Description nicht gefunden");
        }
    }
    public void standardDialog(String title, String message, String positive) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
