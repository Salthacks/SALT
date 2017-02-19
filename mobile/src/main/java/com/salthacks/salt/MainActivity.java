package com.salthacks.salt;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    static {
        System.loadLibrary("aubio-lib");
    }

    public native void processAubio(int hopSize, int sampleRate, float in[]);


    // fiddable constants
    private static final int RECORDER_SAMPLERATE = 44100;
    private static final int RECORDER_SAMPLESECS = 5;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_FLOAT;

    // derived constants
    private static final int RECODER_BUFSIZE = RECORDER_SAMPLERATE * RECORDER_SAMPLESECS;

    // class vars
    private AudioRecord recorder = null;
    private Thread recordingThread = null;
    private boolean isRecording = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setButtonHandlers();
        enableButtons(false);
        int bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
                RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
        Log.d("MAXWELL DEBUG", "Min Buffer Size: " + bufferSize);
        Log.d("MAXWELL DEBUG", "Curr Buffer Size: " + RECODER_BUFSIZE);
    }

    private void setButtonHandlers() {
        ((Button) findViewById(R.id.btnStart)).setOnClickListener(btnClick);
        ((Button) findViewById(R.id.btnStop)).setOnClickListener(btnClick);
    }

    private void enableButton(int id, boolean isEnable) {
        ((Button) findViewById(id)).setEnabled(isEnable);
    }

    private void enableButtons(boolean isRecording) {
        enableButton(R.id.btnStart, !isRecording);
        enableButton(R.id.btnStop, isRecording);
    }

    private void startRecording() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            recorder = new AudioRecord.Builder()
                    .setAudioSource(MediaRecorder.AudioSource.MIC)
                    .setAudioFormat(new AudioFormat.Builder()
                        .setEncoding(RECORDER_AUDIO_ENCODING)
                        .setSampleRate(RECORDER_SAMPLERATE)
                        .setChannelMask(RECORDER_CHANNELS)
                        .build())
                    .build();
        } else {
            Log.d("MAXWELL DEBUG", "Samsung no updatey :(");
        }
        Log.d("MAXWELL DEBUG", "Recorder State: " + recorder.getState());

        recorder.startRecording();
        isRecording = true;
        recordingThread = new Thread(new Runnable() {
            public void run() {
                writeAudioDataToFileJK();
            }
        }, "AudioRecorder Thread");
        recordingThread.start();
    }


    private void writeAudioDataToFileJK() {
        // Write the output audio in byte


        float sData[] = new float[RECODER_BUFSIZE];

        while (isRecording) {
            // gets the voice output from microphone to byte format

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                recorder.read(sData, 0, RECODER_BUFSIZE, AudioRecord.READ_BLOCKING);
                for (int i = 0; i != sData.length; ++i) {
                    if (sData[i] != 0) {
                        Log.d("MAXWELL DEBUG", "sData[: " + i + "] = " + sData[i]);
                    }
                }
                processAubio(RECODER_BUFSIZE/8, RECORDER_SAMPLERATE, sData);
            }
        }

    }

    private void stopRecording() {
        // stops the recording activity
        if (null != recorder) {
            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;
        }
    }

    private View.OnClickListener btnClick = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnStart: {
                    enableButtons(true);
                    startRecording();
                    break;
                }
                case R.id.btnStop: {
                    enableButtons(false);
                    stopRecording();
                    break;
                }
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
