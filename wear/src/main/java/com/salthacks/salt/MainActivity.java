package com.salthacks.salt;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends WearableActivity implements DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private RelativeLayout mContainerView;
    private TextView mTextView;
    private TextView mClockView;

    private Vibrator vibrator;

    private ImageButton button1;
    private Button button2;
    private Switch button3;

    private int presses;
    private long pressTimes[];

    private long interval;

    private boolean salsa;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        pressTimes = new long[8];
        presses = 0;
        salsa = false;
        mContainerView = (RelativeLayout) findViewById(R.id.container);
        mTextView = (TextView) mContainerView.findViewById(R.id.button_presses);
        mTextView.setText(Integer.toString(8-presses));
        mTextView.setTextColor(Color.WHITE);
        button1 = (ImageButton) mContainerView.findViewById(R.id.button);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackBPM();
            }
        });
        button2 = (Button) mContainerView.findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelVibrate();
            }
        });
        button3 = (Switch) mContainerView.findViewById(R.id.button3);
        button3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    salsa = true;
                    setVibrateInterval();
                } else {
                    salsa =false;
                    setVibrateInterval();
                    // The toggle is disabled
                }
            }
        });
        vibrator =  (Vibrator) getSystemService(VIBRATOR_SERVICE);



    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {
            mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
            mTextView.setTextColor(getResources().getColor(android.R.color.white));



        } else {
            mContainerView.setBackground(null);
            mTextView.setTextColor(getResources().getColor(android.R.color.black));

        }
    }

    private void trackBPM(){
        PendingResult<DataItemBuffer> results = Wearable.DataApi.getDataItems(mGoogleApiClient);
        results.setResultCallback(new ResultCallback<DataItemBuffer>() {
            @Override
            public void onResult(DataItemBuffer dataItems) {
                if (dataItems.getCount() != 0) {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(dataItems.get(0));

                    // This should read the correct value.
                    interval = (long)dataMapItem.getDataMap().getDouble("BPM");
                }

                dataItems.release();
            }
        });

        /*
        if(presses < 8) {
            pressTimes[presses] = System.currentTimeMillis();
            ++presses;
            mTextView.setText(Integer.toString(8-presses));
            mTextView.setTextColor(Color.WHITE);
            //vibrator.vibrate(250);
            if(presses > 7){
                calculateAverage();
            }

        }else{
            presses = 0;
            pressTimes[presses] = System.currentTimeMillis();
            mTextView.setText(Integer.toString(presses));
            mTextView.setTextColor(Color.WHITE);
        }
        */
        setVibrateInterval();
    }
    private void calculateAverage(){
        long sum = 0;
        for(int i=0; i < 7; ++i){
            sum += (pressTimes[i+1] - pressTimes[i]);
        }
        interval = sum/8;
        mTextView.setText("Beating!");
        mTextView.setTextColor(Color.WHITE);
        setVibrateInterval();
    }

    private void setVibrateInterval(){
        if(salsa){
            long intervalPattern[] = new long[8];
            intervalPattern[0] = interval - 185;
            intervalPattern[1] = 250;
            intervalPattern[2] = interval - 185;
            intervalPattern[3] = 250;
            intervalPattern[4] = interval - 185;
            intervalPattern[5] = 250;
            intervalPattern[6] = interval - 185 + 250;
            intervalPattern[7] = 0;

            vibrator.vibrate(intervalPattern,0);
        }else {
            long intervalPattern[] = new long[2];
            intervalPattern[0] = interval - 185;
            intervalPattern[1] = 250;
            vibrator.vibrate(intervalPattern, 0);

        }
    }
    /*
    private void setSalsaBeat(){
        salsa = true;
        long intervalPattern[] = new long[8];
        intervalPattern[0] = interval - 185;
        intervalPattern[1] = 250;
        intervalPattern[2] = interval - 185;
        intervalPattern[3] = 250;
        intervalPattern[4] = interval - 185;
        intervalPattern[5] = 250;
        intervalPattern[6] = interval - 185 + 250;
        intervalPattern[7] = 0;
        vibrator.vibrate(intervalPattern,0);
    }
    */
    private void cancelVibrate(){
        salsa = false;
        button3.setChecked(false);
        presses = 0;
        mTextView.setText(Integer.toString(8-presses));
        mTextView.setTextColor(Color.WHITE);
        vibrator.cancel();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                item.getData();

            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
    }

}