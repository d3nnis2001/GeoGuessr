package com.gse23.dschielke;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import androidx.exifinterface.media.ExifInterface;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;


/**
 * Das ist die MainActivity, die zuerst ausgeführt wird, wenn die App gestartet wird.
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AssetManager assetManager = getAssets();
        fileLogger(assetManager);

    }

    /**
     * FileLogger geht in die einzelnen Subdirectories und logged alle Files.
     * @param assetManager AssetManager für AssetDirectory
     */

    public void fileLogger(AssetManager assetManager) {
        try {
            // Store all subdirectories of albums
            String[] dirsInAlbum = assetManager.list("albums");
            assert dirsInAlbum != null;
            for (String folderName : dirsInAlbum) {
                try {
                    String al = "albums/";
                    String[] images = assetManager.list(al + folderName);
                    assert images != null;
                    for (String fileName: images) {
                        if (fitsFormat(fileName)) {
                            Log.d(fileName, folderName);
                            try (InputStream in =  getAssets().open(al + folderName + "/" + fileName)) {
                                readExif(in);
                            } catch (IOException e) {
                                Log.d("EXIFERROR", "Exif Information couldn't be found");
                            }
                        }
                    }
                } catch (IOException e) {
                    Log.d("FILEERROR", "No files in this Folder!");
                }
            }
        } catch (IOException e) {
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
}
