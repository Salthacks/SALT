package com.salthacks.salt;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends WearableActivity {

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private RelativeLayout mContainerView;
    private TextView mTextView;
    private TextView mClockView;

    private Vibrator vibrator;

    private ImageButton button;

    private int presses;
    private long pressTimes[];

    private long interval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();
        pressTimes = new long[8];
        presses = 0;
        mContainerView = (RelativeLayout) findViewById(R.id.container);
        mTextView = (TextView) mContainerView.findViewById(R.id.button_presses);
        mTextView.setText(Integer.toString(8-presses));
        mTextView.setTextColor(Color.WHITE);
        button = (ImageButton) mContainerView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackBPM();
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
        if(presses < 8) {
            pressTimes[presses] = System.currentTimeMillis();
            ++presses;
            mTextView.setText(Integer.toString(8-presses));
            mTextView.setTextColor(Color.WHITE);
            vibrator.vibrate(250);
            if(presses > 7){
                calculateAverage();
            }

        }else{
            presses = 0;
            pressTimes[presses] = System.currentTimeMillis();
            mTextView.setText(Integer.toString(presses));
            mTextView.setTextColor(Color.WHITE);
        }
    }
    private void calculateAverage(){
        long sum = 0;
        for(int i=0; i < 7; ++i){
            sum += (pressTimes[i+1] - pressTimes[i]);
        }
        interval = sum/8;

        setVibrateInterval();
    }

    private void setVibrateInterval(){
        long intervalPattern[] = new long[2];
        intervalPattern[0] = interval-250;
        intervalPattern[1] = 250;
        vibrator.vibrate(intervalPattern,0);
    }

    private void captureAudio(){

    }
}
