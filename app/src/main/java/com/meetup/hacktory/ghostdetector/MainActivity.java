package com.meetup.hacktory.ghostdetector;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Nearable;
import com.estimote.sdk.SystemRequirementsChecker;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private BeaconManager beaconManager;
    private TextView tv;

    /*
     * 49b55e37ca533f56 - yellow
     * b3735f8fb8b79289 - green
     * 8cbaff04dd11e9e8 - blue
     */
    private List<String> STICKERS = Arrays.asList("49b55e37ca533f56", "b3735f8fb8b79289", "8cbaff04dd11e9e8");
    private RestTemplate restTemplate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        tv = (TextView) findViewById(R.id.beacon);

        beaconManager = new BeaconManager(this);

        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        beaconManager.disconnect();
        super.onDestroy();
    }

    @Override protected void onResume() {
        super.onResume();

        if (SystemRequirementsChecker.checkWithDefaultDialogs(this)) {
            startScanning();
        }
    }

    @Override
    protected void onStop() {
        beaconManager.disconnect();
        super.onStop();
    }

    protected void startScanning() {

        beaconManager.setNearableListener(new BeaconManager.NearableListener() {
            @Override
            public void onNearablesDiscovered(List<Nearable> nearables) {
                for(Nearable nearable : nearables) {
                    if (STICKERS.contains(nearable.identifier)) {
                        tv.setText(nearable.toString());
                        new SendInfoAsyncTask().execute(nearable);
                        if(nearable.isMoving) {
                            Toast.makeText(getApplicationContext(), nearable.identifier + "is moving!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.setForegroundScanPeriod(100, 0);
                beaconManager.startNearableDiscovery();
            }
        });
    }

    class SendInfoAsyncTask extends AsyncTask<Nearable, Void, Object> {
        @Override
        protected Object doInBackground(Nearable... nearable) {
            if(nearable.length > 0) {
                try {
                    return restTemplate.postForEntity("http://f180eab4.ngrok.io", new NearableModel(nearable[0]), Object.class, Collections.EMPTY_MAP);
                } catch (Exception e) {
                    Log.e(MainActivity.class.getName(), "exception", e);
                }
            }
            return null;
        }
    }
}
