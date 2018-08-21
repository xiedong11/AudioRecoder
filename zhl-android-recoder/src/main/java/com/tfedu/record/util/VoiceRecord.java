package com.tfedu.record.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class VoiceRecord implements OnErrorListener, OnPreparedListener,
        OnBufferingUpdateListener {

    public enum AudioPlayerState {
        READY, STARTED, PAUSED, END,
    }

    ;

    public AudioPlayerState state;
    public MediaPlayer player;
    private Context context;

    private int precent;

    private boolean isPrepared = false;

    public interface PreparedLister {
        public void aftrtPrepared();
    }

    ;

    public PreparedLister preparedLister;

    public void setOnPreparedLister(PreparedLister preparedLister) {
        this.preparedLister = preparedLister;
    }

    public VoiceRecord(Context context) {
        this.context = context;
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置媒体流类型
        player.setOnPreparedListener(this);
        player.setOnBufferingUpdateListener(this);

        state = AudioPlayerState.END;
    }

    public AudioPlayerState getState() {
        return state;
    }

    public boolean isState(AudioPlayerState state) {
        return this.state == state;
    }

    public void playUrl(Uri uri) {
        if (uri == null) {
            state = AudioPlayerState.END;
            if (preparedLister != null)
                preparedLister.aftrtPrepared();
            return;
        }

        setPrepared(false);

        if (player != null) {

            player.reset();

            try {
                //player.setDataSource(context, uri);
                //player.prepare();
                File file = new File(uri.getPath());
                FileInputStream fis = new FileInputStream(file);
                if (fis != null) {
                    player.setDataSource(fis.getFD());
                    player.prepare();
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
            player.setOnErrorListener(this);
            // 设置了player.setOnPreparedListener(this); 所以准备后直接进入播放
            //        state = AudioPlayerState.READY;


        state = AudioPlayerState.STARTED;
        }
    }

    public void start() {
        if (state == AudioPlayerState.END)
            return;

        player.start();
        state = AudioPlayerState.STARTED;
    }

    public void pause() {
        if (state != AudioPlayerState.STARTED
                && state != AudioPlayerState.PAUSED)
            return;

        player.pause();
        state = AudioPlayerState.PAUSED;
    }

    public void stop() {
        if (state != AudioPlayerState.STARTED
                && state != AudioPlayerState.PAUSED)
            return;

        player.stop();
        state = AudioPlayerState.PAUSED;
    }

    public void release() {
        if (state == AudioPlayerState.END)
            return;

        pause();

        player.stop();
        player.release();
        state = AudioPlayerState.END;
    }

    public void setOnCompletionListener(
            MediaPlayer.OnCompletionListener listener) {
        if (listener != null && player != null) {
            player.setOnCompletionListener(listener);
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        player.reset();
        player.release();
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        setPrepared(true);
        if (preparedLister != null)
            preparedLister.aftrtPrepared();
        mp.start();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        setPrecent(percent);
    }

    public int getPrecent() {
        return precent;
    }

    public void setPrecent(int precent) {
        this.precent = precent;
    }

    public boolean isPrepared() {
        return isPrepared;
    }

    public void setPrepared(boolean isPrepared) {
        this.isPrepared = isPrepared;
    }

}
