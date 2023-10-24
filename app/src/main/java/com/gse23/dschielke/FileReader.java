package com.gse23.dschielke;

import android.util.Log;
import android.content.res.AssetManager;

import java.io.IOException;

/**
 * FileReader welcher die Filenames in einer Directory all in den Logs ausgibt.
 */
public class FileReader {
    private AssetManager assetManager;
    /**
     * FileLogger geht in die einzelnen Subdirectories und logged alle Files.
     */
    public void fileLogger(AssetManager assetManager) {
        this.assetManager = assetManager;
        try {
            // Store all subdirectories of albums
            String[] dirsInAlbum = assetManager.list("albums");
            for (String folderName : dirsInAlbum) {
                try {
                    String[] images = assetManager.list("albums/" + folderName);
                    for (String fileName: images) {
                        if (fitsFormat(fileName)) {
                            Log.d(fileName, folderName);
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
     */
    public Boolean fitsFormat(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".jpeg") || lower.endsWith(".jpg") || lower.endsWith(".png")) {
            return true;
        }
        return false;
    }
}
