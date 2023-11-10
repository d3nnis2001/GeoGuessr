package com.gse23.dschielke;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;

public class GameActivity extends Activity {
    AssetManager assetManager;
    String albuNum = "AlbumNum";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);
        assetManager = getAssets();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(albuNum)) {
            int albumNum = intent.getIntExtra(albuNum, 0);
            try {
                logCurrentFile(albumNum);
            } catch (IOException e) {
                Log.d("GameActivity", "Folder doesn't exist");
            }
        }
    }
    public void logCurrentFile(int pos) throws IOException {
        assetManager = getAssets();
        String[] albumNames = assetManager.list("albums");
        Log.d("Album:", albumNames[pos]);
    }
}
