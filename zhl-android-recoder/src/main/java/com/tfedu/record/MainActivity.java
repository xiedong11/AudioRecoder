package com.tfedu.record;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.tfedu.record.view.RecordDialog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String voicePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test.mp3";
                        RecordDialog recordDialog = new RecordDialog(MainActivity.this, voicePath);
                        recordDialog.setOnConfirmListener(new RecordDialog.OnConfirmListener() {
                            @Override
                            public void onConfirm(String voicePath, int duration) {
                                Log.e("MainActivity", voicePath);
                            }
                        });
                        recordDialog.setCancelable(true);
                        recordDialog.setCanceledOnTouchOutside(false);
                        recordDialog.show();
                    }
                });
            }
        }).start();

    }
}
