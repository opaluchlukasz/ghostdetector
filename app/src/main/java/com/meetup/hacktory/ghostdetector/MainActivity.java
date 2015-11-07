package com.meetup.hacktory.ghostdetector;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Nearable;
import com.estimote.sdk.SystemRequirementsChecker;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    /*
     * 49b55e37ca533f56 - yellow
     * b3735f8fb8b79289 - green
     * 8cbaff04dd11e9e8 - blue
     */
    private Map<String, StickerMetaData> STICKERS_METADATA = new HashMap<>();

    private RestTemplate restTemplate;
    private BeaconManager beaconManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        STICKERS_METADATA.put("49b55e37ca533f56", new StickerMetaData("yellow", (TextView) findViewById(R.id.yeallowsticker)));
        STICKERS_METADATA.put("b3735f8fb8b79289", new StickerMetaData("green", (TextView) findViewById(R.id.greensticker)));
        STICKERS_METADATA.put("8cbaff04dd11e9e8", new StickerMetaData("blue", (TextView) findViewById(R.id.bluesticker)));

        beaconManager = new BeaconManager(this);

        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
                    if (STICKERS_METADATA.keySet().contains(nearable.identifier)) {
                        NearableModel nearableModel = new NearableModel(nearable);
                        STICKERS_METADATA.get(nearableModel.identifier).textView.setText(nearableModel.toString());
                        new SendInfoAsyncTask().execute(nearableModel);
                        if(nearableModel.isMoving) {
                            Toast.makeText(getApplicationContext(), STICKERS_METADATA.get(nearable.identifier).color + " sticker is moving!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startNearableDiscovery();
            }
        });
    }

    class SendInfoAsyncTask extends AsyncTask<NearableModel, Void, Object> {
        @Override
        protected Object doInBackground(NearableModel... nearable) {
            if(nearable.length > 0) {
                try {
                    return restTemplate.postForEntity("http://f180eab4.ngrok.io", nearable[0], Object.class, Collections.EMPTY_MAP);
                } catch (Exception e) {
                    Log.e(MainActivity.class.getName(), "exception", e);
                }
            }
            return null;
        }
    }
}
