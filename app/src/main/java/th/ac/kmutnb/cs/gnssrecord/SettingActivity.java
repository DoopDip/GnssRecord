package th.ac.kmutnb.cs.gnssrecord;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

public class SettingActivity extends AppCompatActivity {

    public static final String KEY_RINEX_VER = "rinexVer"; // value 0=2.11, 1=3.03
    public static final String KEY_MARK_NAME = "markName";
    public static final String KEY_MARK_TYPE = "markType";
    public static final String KEY_OBSERVER_NAME = "observerName";
    public static final String KEY_OBSERVER_AGENCY_NAME = "observerAgencyName";
    public static final String KEY_RECEIVER_NUMBER = "receiverNumber";
    public static final String KEY_RECEIVER_TYPE = "receiverType";
    public static final String KEY_RECEIVER_VERSION = "receiverVersion";
    public static final String KEY_ANTENNA_NUMBER = "antennaNumber";
    public static final String KEY_ANTENNA_TYPE = "antennaType";
    public static final String KEY_ANTENNA_ECCENTRICITY_EAST = "antennaEccentricityEast";
    public static final String KEY_ANTENNA_ECCENTRICITY_NORTH = "antennaEccentricityNorth";
    public static final String KEY_ANTENNA_HEIGHT = "antennaHeight";

    public static final int DEF_RINEX_VER = 0;
    public static final String DEF_MARK_NAME = "GnssRecord";
    public static final String DEF_MARK_TYPE = "Geodetic";
    public static final String DEF_OBSERVER_NAME = "RINEX Logger user";
    public static final String DEF_OBSERVER_AGENCY_NAME = "GnssRecord";
    public static final String DEF_RECEIVER_NUMBER = Build.SERIAL;
    public static final String DEF_RECEIVER_TYPE = Build.MANUFACTURER;
    public static final String DEF_RECEIVER_VERSION = Build.PRODUCT;
    public static final String DEF_ANTENNA_NUMBER = Build.SERIAL;
    public static final String DEF_ANTENNA_TYPE = Build.PRODUCT;
    public static final String DEF_ANTENNA_ECCENTRICITY_EAST = "0.0000";
    public static final String DEF_ANTENNA_ECCENTRICITY_NORTH = "0.0000";
    public static final String DEF_ANTENNA_HEIGHT = "0.0000";

    public static final String FILE_SETTING = "gnss_record_setting";

    private static final String TAG = SettingActivity.class.getSimpleName();

    private AppCompatSpinner spinnerVersion;

    private TextView textViewMarkName;
    private TextView textViewMarkType;
    private TextView textViewObserverName;
    private TextView textViewObserverAgencyName;
    private TextView textViewReceiverNumber;
    private TextView textViewReceiverType;
    private TextView textViewReceiverVersion;
    private TextView textViewAntennaNumber;
    private TextView textViewAntennaType;
    private TextView textViewAntennaEccentricityEast;
    private TextView textViewAntennaEccentricityNorth;
    private TextView textViewAntennaHeight;

    private TextView textViewBtnMarkName;
    private TextView textViewBtnMarkType;
    private TextView textViewBtnObserverName;
    private TextView textViewBtnObserverAgencyName;
    private TextView textViewBtnReceiverNumber;
    private TextView textViewBtnReceiverType;
    private TextView textViewBtnReceiverVersion;
    private TextView textViewBtnAntennaNumber;
    private TextView textViewBtnAntennaType;
    private TextView textViewBtnAntennaEccentricityEast;
    private TextView textViewBtnAntennaEccentricityNorth;
    private TextView textViewBtnAntennaHeight;

    private TextView textViewBtnRestore;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        spinnerVersion = findViewById(R.id.setting_version);

        textViewMarkName = findViewById(R.id.setting_markName);
        textViewMarkType = findViewById(R.id.setting_markType);
        textViewObserverName = findViewById(R.id.setting_observerName);
        textViewObserverAgencyName = findViewById(R.id.setting_observerAgencyName);
        textViewReceiverNumber = findViewById(R.id.setting_receiverNumber);
        textViewReceiverType = findViewById(R.id.setting_receiverType);
        textViewReceiverVersion = findViewById(R.id.setting_receiverVersion);
        textViewAntennaNumber = findViewById(R.id.setting_antennaNumber);
        textViewAntennaType = findViewById(R.id.setting_antennaType);
        textViewAntennaEccentricityEast = findViewById(R.id.setting_antennaEccentricityEast);
        textViewAntennaEccentricityNorth = findViewById(R.id.setting_antennaEccentricityNorth);
        textViewAntennaHeight = findViewById(R.id.setting_antennaHeight);

        textViewBtnMarkName = findViewById(R.id.setting_btnMarkName);
        textViewBtnMarkType = findViewById(R.id.setting_btnMarkType);
        textViewBtnObserverName = findViewById(R.id.setting_btnObserverName);
        textViewBtnObserverAgencyName = findViewById(R.id.setting_btnObserverAgencyName);
        textViewBtnReceiverNumber = findViewById(R.id.setting_btnReceiverNumber);
        textViewBtnReceiverType = findViewById(R.id.setting_btnReceiverType);
        textViewBtnReceiverVersion = findViewById(R.id.setting_btnReceiverVersion);
        textViewBtnAntennaNumber = findViewById(R.id.setting_btnAntennaNumber);
        textViewBtnAntennaType = findViewById(R.id.setting_btnAntennaType);
        textViewBtnAntennaEccentricityEast = findViewById(R.id.setting_btnAntennaEccentricityEast);
        textViewBtnAntennaEccentricityNorth = findViewById(R.id.setting_btnAntennaEccentricityNorth);
        textViewBtnAntennaHeight = findViewById(R.id.setting_btnAntennaHeight);

        textViewBtnRestore = findViewById(R.id.setting_btnRestore);

        sharedPreferences = getSharedPreferences(FILE_SETTING, 0);

        reloadSettingText();

        spinnerVersion.setAdapter(
                new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        new String[]{"2.11", "3.03"}
                )
        );
        spinnerVersion.setSelection(sharedPreferences.getInt(KEY_RINEX_VER, DEF_RINEX_VER), true);
        ((TextView) spinnerVersion.getSelectedView()).setTextColor(getColor(R.color.colorWhite));
        spinnerVersion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) spinnerVersion.getSelectedView()).setTextColor(getColor(R.color.colorWhite));
                sharedPreferences.edit().putInt(KEY_RINEX_VER, position).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        textViewBtnMarkName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Click -> MarkName");
                new MaterialDialog.Builder(v.getContext())
                        .title(R.string.mark_name)
                        .inputRangeRes(0, 20, R.color.colorDanger)
                        .input(getString(R.string.mark_name),
                                sharedPreferences.getString(KEY_MARK_NAME, DEF_MARK_NAME),
                                new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                        Log.i(TAG, "Marker input = " + input);
                                        sharedPreferences.edit().putString(KEY_MARK_NAME, input.toString()).apply();
                                        textViewMarkName.setText(input.toString());
                                    }
                                }).show();
            }
        });

        textViewBtnMarkType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Click -> MarkType");
                String[] type = {
                        "Geodetic",
                        "Non Geodetic",
                        "Non Physical",
                        "Space borne",
                        "Air borne",
                        "Water Craft",
                        "Ground Craft",
                        "Fixed Buoy",
                        "Floating Buoy",
                        "Floating Ice",
                        "Glacier",
                        "Ballistic",
                        "Animal",
                        "Human"
                };
                new MaterialDialog.Builder(v.getContext())
                        .title(R.string.mark_type)
                        .items(type)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                sharedPreferences.edit().putString(KEY_MARK_TYPE, text.toString()).apply();
                                textViewMarkType.setText(text.toString());
                            }
                        })
                        .show();
            }
        });


        textViewBtnObserverName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Click -> ObserverName");
                new MaterialDialog.Builder(v.getContext())
                        .title(R.string.observer_name)
                        .inputRangeRes(0, 20, R.color.colorDanger)
                        .input(getString(R.string.observer_name),
                                sharedPreferences.getString(KEY_OBSERVER_NAME, DEF_OBSERVER_NAME),
                                new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                        sharedPreferences.edit().putString(KEY_OBSERVER_NAME, input.toString()).apply();
                                        textViewObserverName.setText(input.toString());
                                    }
                                }).show();
            }
        });

        textViewBtnObserverAgencyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Click -> ObserverAgencyName");
                new MaterialDialog.Builder(v.getContext())
                        .title(R.string.observer_agency_name)
                        .inputRangeRes(0, 20, R.color.colorDanger)
                        .input(getString(R.string.observer_agency_name),
                                sharedPreferences.getString(KEY_OBSERVER_AGENCY_NAME, DEF_OBSERVER_AGENCY_NAME),
                                new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                        sharedPreferences.edit().putString(KEY_OBSERVER_AGENCY_NAME, input.toString()).apply();
                                        textViewObserverAgencyName.setText(input.toString());
                                    }
                                }).show();
            }
        });

        textViewBtnReceiverNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Click -> ReceiverNumber");
                new MaterialDialog.Builder(v.getContext())
                        .title(R.string.receiver_number)
                        .inputRangeRes(0, 20, R.color.colorDanger)
                        .input(getString(R.string.receiver_number),
                                sharedPreferences.getString(KEY_RECEIVER_NUMBER, DEF_RECEIVER_NUMBER),
                                new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                        sharedPreferences.edit().putString(KEY_RECEIVER_NUMBER, input.toString()).apply();
                                        textViewReceiverNumber.setText(input.toString());
                                    }
                                }).show();
            }
        });

        textViewBtnReceiverType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Click -> ReceiverType");
                new MaterialDialog.Builder(v.getContext())
                        .title(R.string.receiver_type)
                        .inputRangeRes(0, 20, R.color.colorDanger)
                        .input(getString(R.string.receiver_type),
                                sharedPreferences.getString(KEY_RECEIVER_TYPE, DEF_RECEIVER_TYPE),
                                new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                        sharedPreferences.edit().putString(KEY_RECEIVER_TYPE, input.toString()).apply();
                                        textViewReceiverType.setText(input.toString());
                                    }
                                }).show();
            }
        });

        textViewBtnReceiverVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Click -> ReceiverVersion");
                new MaterialDialog.Builder(v.getContext())
                        .title(R.string.receiver_version)
                        .inputRangeRes(0, 20, R.color.colorDanger)
                        .input(getString(R.string.receiver_version),
                                sharedPreferences.getString(KEY_RECEIVER_VERSION, DEF_RECEIVER_VERSION),
                                new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                        sharedPreferences.edit().putString(KEY_RECEIVER_VERSION, input.toString()).apply();
                                        textViewReceiverVersion.setText(input.toString());
                                    }
                                }).show();
            }
        });

        textViewBtnAntennaNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Click -> AntennaNumber");
                new MaterialDialog.Builder(v.getContext())
                        .title(R.string.antenna_number)
                        .inputRangeRes(0, 20, R.color.colorDanger)
                        .input(getString(R.string.antenna_number),
                                sharedPreferences.getString(KEY_ANTENNA_NUMBER, DEF_ANTENNA_NUMBER),
                                new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                        sharedPreferences.edit().putString(KEY_ANTENNA_NUMBER, input.toString()).apply();
                                        textViewAntennaNumber.setText(input.toString());
                                    }
                                }).show();
            }
        });

        textViewBtnAntennaType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Click -> AntennaType");
                new MaterialDialog.Builder(v.getContext())
                        .title(R.string.antenna_type)
                        .inputRangeRes(0, 20, R.color.colorDanger)
                        .input(getString(R.string.antenna_type),
                                sharedPreferences.getString(KEY_ANTENNA_TYPE, DEF_ANTENNA_TYPE),
                                new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                        sharedPreferences.edit().putString(KEY_ANTENNA_TYPE, input.toString()).apply();
                                        textViewAntennaType.setText(input.toString());
                                    }
                                }).show();
            }
        });

        textViewBtnAntennaEccentricityEast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Click -> AntennaEccentricityEast");
                new MaterialDialog.Builder(v.getContext())
                        .title(R.string.antenna_eccentricity_east)
                        .inputRangeRes(0, 6, R.color.colorDanger)
                        .input(getString(R.string.antenna_eccentricity_east),
                                sharedPreferences.getString(KEY_ANTENNA_ECCENTRICITY_EAST, DEF_ANTENNA_ECCENTRICITY_EAST),
                                new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                        sharedPreferences.edit().putString(KEY_ANTENNA_ECCENTRICITY_EAST, input.toString()).apply();
                                        textViewAntennaEccentricityEast.setText(input.toString());
                                    }
                                }).show();
            }
        });

        textViewBtnAntennaEccentricityNorth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Click -> AntennaEccentricityNorth");
                new MaterialDialog.Builder(v.getContext())
                        .title(R.string.antenna_eccentricity_north)
                        .inputRangeRes(0, 6, R.color.colorDanger)
                        .input(getString(R.string.antenna_eccentricity_north),
                                sharedPreferences.getString(KEY_ANTENNA_ECCENTRICITY_NORTH, DEF_ANTENNA_ECCENTRICITY_NORTH),
                                new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                        sharedPreferences.edit().putString(KEY_ANTENNA_ECCENTRICITY_NORTH, input.toString()).apply();
                                        textViewAntennaEccentricityNorth.setText(input.toString());
                                    }
                                }).show();
            }
        });

        textViewBtnAntennaHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Click -> AntennaHeight");
                new MaterialDialog.Builder(v.getContext())
                        .title(R.string.antenna_height)
                        .inputRangeRes(0, 6, R.color.colorDanger)
                        .input(getString(R.string.antenna_height),
                                sharedPreferences.getString(KEY_ANTENNA_HEIGHT, DEF_ANTENNA_HEIGHT),
                                new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                        sharedPreferences.edit().putString(KEY_ANTENNA_HEIGHT, input.toString()).apply();
                                        textViewAntennaHeight.setText(input.toString());
                                    }
                                }).show();
            }
        });

        textViewBtnRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Click -> Restore");

                new MaterialDialog.Builder(v.getContext())
                        .title(R.string.restore_defaults)
                        .content(R.string.please_confirm)
                        .positiveText(R.string.ok)
                        .negativeText(R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                restoreSettingText();
                                reloadSettingText();
                            }
                        })
                        .show();
            }
        });

    }

    private void reloadSettingText() {
        textViewMarkName.setText(sharedPreferences.getString(KEY_MARK_NAME, DEF_MARK_NAME));
        textViewMarkType.setText(sharedPreferences.getString(KEY_MARK_TYPE, DEF_MARK_TYPE));
        textViewObserverName.setText(sharedPreferences.getString(KEY_OBSERVER_NAME, DEF_OBSERVER_NAME));
        textViewObserverAgencyName.setText(sharedPreferences.getString(KEY_OBSERVER_AGENCY_NAME, DEF_OBSERVER_AGENCY_NAME));
        textViewReceiverNumber.setText(sharedPreferences.getString(KEY_RECEIVER_NUMBER, DEF_RECEIVER_NUMBER));
        textViewReceiverType.setText(sharedPreferences.getString(KEY_RECEIVER_TYPE, DEF_RECEIVER_TYPE));
        textViewReceiverVersion.setText(sharedPreferences.getString(KEY_RECEIVER_VERSION, DEF_RECEIVER_VERSION));
        textViewAntennaNumber.setText(sharedPreferences.getString(KEY_ANTENNA_NUMBER, DEF_ANTENNA_NUMBER));
        textViewAntennaType.setText(sharedPreferences.getString(KEY_ANTENNA_TYPE, DEF_ANTENNA_TYPE));
        textViewAntennaEccentricityEast.setText(sharedPreferences.getString(KEY_ANTENNA_ECCENTRICITY_EAST, DEF_ANTENNA_ECCENTRICITY_EAST));
        textViewAntennaEccentricityNorth.setText(sharedPreferences.getString(KEY_ANTENNA_ECCENTRICITY_NORTH, DEF_ANTENNA_ECCENTRICITY_NORTH));
        textViewAntennaHeight.setText(sharedPreferences.getString(KEY_ANTENNA_HEIGHT, DEF_ANTENNA_HEIGHT));
    }

    private void restoreSettingText() {
        sharedPreferences.edit().putString(KEY_MARK_NAME, DEF_MARK_NAME).apply();
        sharedPreferences.edit().putString(KEY_MARK_TYPE, DEF_MARK_TYPE).apply();
        sharedPreferences.edit().putString(KEY_OBSERVER_NAME, DEF_OBSERVER_NAME).apply();
        sharedPreferences.edit().putString(KEY_OBSERVER_AGENCY_NAME, DEF_OBSERVER_AGENCY_NAME).apply();
        sharedPreferences.edit().putString(KEY_RECEIVER_NUMBER, DEF_RECEIVER_NUMBER).apply();
        sharedPreferences.edit().putString(KEY_RECEIVER_TYPE, DEF_RECEIVER_TYPE).apply();
        sharedPreferences.edit().putString(KEY_RECEIVER_VERSION, DEF_RECEIVER_VERSION).apply();
        sharedPreferences.edit().putString(KEY_ANTENNA_NUMBER, DEF_ANTENNA_NUMBER).apply();
        sharedPreferences.edit().putString(KEY_ANTENNA_TYPE, DEF_ANTENNA_TYPE).apply();
        sharedPreferences.edit().putString(KEY_ANTENNA_ECCENTRICITY_EAST, DEF_ANTENNA_ECCENTRICITY_EAST).apply();
        sharedPreferences.edit().putString(KEY_ANTENNA_ECCENTRICITY_NORTH, DEF_ANTENNA_ECCENTRICITY_NORTH).apply();
        sharedPreferences.edit().putString(KEY_ANTENNA_HEIGHT, DEF_ANTENNA_HEIGHT).apply();
    }
}
