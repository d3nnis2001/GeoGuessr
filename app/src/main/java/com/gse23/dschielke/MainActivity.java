package com.gse23.dschielke;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.os.Bundle;


/**
 * Das ist die MainActivity, die zuerst ausgeführt wird, wenn die App gestartet wird.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FileReader fileReader = new FileReader();

        AssetManager assetManager = getAssets();
        fileReader.fileLogger(assetManager);

    }
}
