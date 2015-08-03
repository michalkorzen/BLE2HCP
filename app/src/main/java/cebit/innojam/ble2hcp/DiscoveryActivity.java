package cebit.innojam.ble2hcp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

public class DiscoveryActivity extends AppCompatActivity implements BeaconConsumer {
    private static final String TAG = SettingsActivity.class.getSimpleName();
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);

    private BeaconAdapter adapter;
    private Toolbar toolbar;

    private String endpoint;
    private String device;
    private String auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getTitle());
        toolbar.setSubtitle("Discovery service starting...");

        SharedPreferences userSettings = getSharedPreferences(SettingsActivity.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        endpoint = userSettings.getString(SettingsActivity.ENDPOINT_IOT_KEY, "");
        device = userSettings.getString(SettingsActivity.DEVICE_IOT_KEY, "");
        auth = userSettings.getString(SettingsActivity.AUTH_IOT_KEY, "");

        adapter = new BeaconAdapter(new ArrayList<Beacon>(), this);
        ListView list = (ListView) findViewById(R.id.device_list);
        list.setAdapter(adapter);

        beaconManager.bind(this);

        Button sendButton = (Button) findViewById(R.id.stream_button);
        sendButton.setOnClickListener(createSendButtonListener());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                final Collection<Beacon> beaconsfinal = new ArrayList<Beacon>(beacons);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toolbar.setSubtitle(String.format("%d beacons discovered.", beaconsfinal.size()));
                        adapter.replaceWith(beaconsfinal);
                    }
                });
            }

        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            Log.d(TAG, "RemoteException");
        }
    }

    private View.OnClickListener createSendButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    public void run() {
                        try {
                            URL url = new URL(endpoint + device);
                            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
                            httpConn.setRequestMethod("POST");

                            httpConn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                            httpConn.setRequestProperty("Authorization", auth);

                            JSONObject obj = new JSONObject();
                            obj.put("mode", "sync");
                            obj.put("messageType", "1");

                            obj.put("messages", adapter.getJSONArray());

                            httpConn.setDoOutput(true);
                            DataOutputStream wr = new DataOutputStream(httpConn.getOutputStream());
                            wr.writeBytes(obj.toString());
                            wr.flush();
                            wr.close();

                            final int httpStatus = httpConn.getResponseCode();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showToast(httpStatus == 200 ? "Data sent" : "Response Code " + httpStatus);
                                }
                            });

                            httpConn.disconnect();

                        } catch (MalformedURLException e) {
                            Log.d(TAG, "MalformedURLException");
                        } catch (IOException e) {
                            Log.d(TAG, "IOException");
                        } catch (Exception e) {
                            Log.d(TAG, "Exception");
                        }
                    }
                }.start();
            }
        };
    }

    private void showToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }
}
