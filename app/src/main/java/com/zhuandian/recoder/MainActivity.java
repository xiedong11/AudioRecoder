package com.zhuandian.recoder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.tfedu.record.IRecoderCallBack;
import com.tfedu.record.RecoderPopupWindow;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.bth_audio_recoder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, com.tfedu.record.MainActivity.class));
            }
        });

        findViewById(R.id.bth_qq_recoder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecoderPopupWindow recoderPopupWindow = new RecoderPopupWindow(MainActivity.this);
                recoderPopupWindow.setRecoderCallBack(new IRecoderCallBack() {
                    @Override
                    public void onSave(long length, String strLength, String voicePath) {
                        //录音文件路径，上传服务器或者自定义处理
                        System.out.println(voicePath + "------------");
                    }
                });
                recoderPopupWindow.show();
            }
        });

    }
}
