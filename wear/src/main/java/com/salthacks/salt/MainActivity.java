package com.salthacks.salt;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
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

    private ImageButton button1;
    private Button button2;
    private Switch button3;

    private int presses;
    private long pressTimes[];

    private long interval;

    private boolean salsa;

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
}
