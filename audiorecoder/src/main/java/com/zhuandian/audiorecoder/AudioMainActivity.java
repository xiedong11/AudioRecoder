package com.zhuandian.audiorecoder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class AudioMainActivity extends AppCompatActivity {

    private ImageView ivAudioStop;
    private ImageView ivAudioStart;
    private ImageView ivAudioReset;
    private TextView tvAudioSave;
    private TextView tvAudioTime;
    private long currentSecond = 0;
    private boolean isPause = true;//是否暂停
    private Handler mhandle = new Handler();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_main);
        ivAudioStop = (ImageView) findViewById(R.id.iv_audio_stop);
        ivAudioStart = (ImageView) findViewById(R.id.iv_audio_start);
        ivAudioReset = (ImageView) findViewById(R.id.iv_audio_reset);
        tvAudioSave = (TextView) findViewById(R.id.tv_audio_save);
        tvAudioTime = (TextView) findViewById(R.id.tv_audio_time);
        timeRunable.run();
        ivAudioStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AudioMainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AudioMainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO}, 1);
                } else {
                    onRecord(true);
                }
            }
        });

        tvAudioSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecord(false);
            }
        });

    }

    private void onRecord(boolean start) {
        Intent intent = new Intent(AudioMainActivity.this, RecodingService.class);

        if (start) {
            isPause = false;
            Toast.makeText(AudioMainActivity.this, "开始录音...", Toast.LENGTH_SHORT).show();
            File folder = new File(Environment.getExternalStorageDirectory() + "/SoundRecorder");
            if (!folder.exists()) {
                folder.mkdir();
            }
            this.startService(intent);
        } else {
            isPause = true;
            Toast.makeText(AudioMainActivity.this, "录音结束...", Toast.LENGTH_SHORT).show();
            this.stopService(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    onRecord(true);
                }
                break;
        }
    }

    private Runnable timeRunable = new Runnable() {
        @Override
        public void run() {
            mhandle.postDelayed(timeRunable, 1000);
            if (!isPause) {
                currentSecond = currentSecond + 1000;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvAudioTime.setText(getFormatHMS(currentSecond) + "/05:00");
                    }
                });
            }
        }
    };

    public String getFormatHMS(long time) {
        time = time / 1000;//总秒数
        int s = (int) (time % 60);//秒
        int m = (int) (time / 60);//分
        return String.format("%02d:%02d", m, s);
    }
}
