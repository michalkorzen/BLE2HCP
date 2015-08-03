package cebit.innojam.ble2hcp;

import android.app.Application;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;

public class MainApplication extends Application {

    public void onCreate() {
        super.onCreate();

        BeaconManager beaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this);
        beaconManager.setForegroundScanPeriod(5000l);
        beaconManager.setForegroundBetweenScanPeriod(0l);

        // AltBeacons and Estimote will be discovered
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
    }

}