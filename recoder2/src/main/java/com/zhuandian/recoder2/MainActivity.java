package com.zhuandian.recoder2;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton start;
    private ImageButton play;
    private TextView stop;
    private VoiceManager voiceManager;
    private Chronometer chronometer;
    private String recordPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = (ImageButton) findViewById(R.id.btn_start);
        play = (ImageButton) findViewById(R.id.btn_play);
        stop = (TextView) findViewById(R.id.tvUpload);
        chronometer = (Chronometer) findViewById(R.id.chRecord);
        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        play.setOnClickListener(this);


        voiceManager = VoiceManager.getInstance();
        voiceManager.setContext(this);
        voiceManager.setVoiceRecordListener(new VoiceManager.VoiceRecordCallBack() {
            @Override
            public void recDoing(long time, String strTime) {
                System.out.println(time+"-----------------"+strTime);
                chronometer.setText(strTime);
            }

            @Override
            public void recVoiceGrade(int grade) {

            }

            @Override
            public void recStart(boolean init) {

            }

            @Override
            public void recPause(String str) {

            }

            @Override
            public void recFinish(long length, String strLength, String path) {
                recordPath = path;
                System.out.println("-----------"+path);
            }

            @Override
            public void recException() {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                voiceManager.startVoiceRecord(Environment.getExternalStorageDirectory().getPath() + "/myAudio");
                break;
            case R.id.tvUpload:
                voiceManager.stopVoiceRecord();
                break;
            case R.id.btn_play:
                voiceManager.startPlay(recordPath);
                break;

        }
    }
}
