package cebit.innojam.ble2hcp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;

public class BeaconAdapter extends ArrayAdapter<Beacon> {

    private static final String TAG = BeaconAdapter.class.getSimpleName();

    private ArrayList<Beacon> beaconArrayList;
    private Context context;

    public BeaconAdapter(ArrayList<Beacon> beaconList, Context ctx) {
        super(ctx, R.layout.beacon_listviewitem, beaconList);
        this.beaconArrayList = beaconList;
        this.context = ctx;
    }

    public void replaceWith(Collection<Beacon> newBeacons) {
        this.beaconArrayList.clear();
        this.beaconArrayList.addAll(newBeacons);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return beaconArrayList.size();
    }

    @Override
    public Beacon getItem(int position) {
        return beaconArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.beacon_listviewitem, null);
            view.setTag(new BeaconHolder(view));
        }
        ((BeaconHolder) view.getTag()).setTexts(getItem(position));
        return view;
    }

    public JSONArray getJSONArray() {
        JSONArray jsonArr = new JSONArray();

        try {
            for (Beacon beacon : beaconArrayList) {
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("MAC", beacon.getBluetoothAddress());
                jsonObj.put("RSSI", beacon.getRssi());
                jsonObj.put("TxPower", beacon.getTxPower());
                jsonArr.put(jsonObj);
            }
        } catch (JSONException e) {
            Log.d(TAG, "Error while returning JSON array");
        }

        return jsonArr;
    }

    static class BeaconHolder {
        final TextView macTextView;
        final TextView uuidTextView;

        final TextView majorTextView;
        final TextView minorTextView;

        final TextView txPowerTextView;
        final TextView rssiTextView;

        BeaconHolder(View view) {
            macTextView = (TextView) view.findViewWithTag("mac");
            uuidTextView = (TextView) view.findViewWithTag("uuid");

            majorTextView = (TextView) view.findViewWithTag("major");
            minorTextView = (TextView) view.findViewWithTag("minor");

            txPowerTextView = (TextView) view.findViewWithTag("txpower");
            rssiTextView = (TextView) view.findViewWithTag("rssi");
        }

        void setTexts(Beacon beacon)
        {
            macTextView.setText(String.format("MAC: %s", beacon.getBluetoothAddress()));
            uuidTextView.setText(String.format("UUID: %s", beacon.getId1().toString()));

            majorTextView.setText(String.format("Major: %s", beacon.getId2().toString()));
            minorTextView.setText(String.format("Minor: %s", beacon.getId3().toString()));

            txPowerTextView.setText("Tx Power: " + beacon.getTxPower());
            rssiTextView.setText("RSSI: " + beacon.getRssi());
        }
    }
}
