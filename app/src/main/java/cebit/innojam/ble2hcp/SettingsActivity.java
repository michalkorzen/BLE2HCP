package cebit.innojam.ble2hcp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    public static final String SHARED_PREFERENCES_KEY = "hanaIoT";
    public static final String ENDPOINT_IOT_KEY = "endpointIoT";
    public static final String DEVICE_IOT_KEY = "deviceIoT";
    public static final String AUTH_IOT_KEY = "authIoT";

    SharedPreferences userSettings;

    private EditText endpointEditText;
    private EditText authEditText;
    private EditText deviceEditText;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back);
        toolbar.setTitle(getTitle());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        userSettings = getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);

        endpointEditText = (EditText) findViewById(R.id.endpointIoT);
        endpointEditText.setText(userSettings.getString(ENDPOINT_IOT_KEY, ""));

        authEditText = (EditText) findViewById(R.id.authIoT);
        authEditText.setText(userSettings.getString(AUTH_IOT_KEY, ""));

        deviceEditText = (EditText) findViewById(R.id.deviceIoT);
        deviceEditText.setText(userSettings.getString(DEVICE_IOT_KEY, ""));

        saveButton = (Button) findViewById(R.id.save);
        saveButton.setOnClickListener(createSaveButtonListener());
    }

    /**
     * Returns click listener on save button.
     */
    private View.OnClickListener createSaveButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = userSettings.edit();

                editor.putString(ENDPOINT_IOT_KEY, endpointEditText.getText().toString());
                editor.putString(AUTH_IOT_KEY, authEditText.getText().toString());
                editor.putString(DEVICE_IOT_KEY, deviceEditText.getText().toString());

                editor.apply();

                showToast("Saved");
            }
        };
    }

    private void showToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }
}
