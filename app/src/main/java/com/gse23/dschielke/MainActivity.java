package com.gse23.dschielke;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.gse23.dschielke.R;

/**
 * Das ist die MainActivity, die zuerst ausgeführt wird, wenn die App gestartet wird.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
