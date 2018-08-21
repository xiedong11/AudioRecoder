package com.tfedu.record.recorder;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.czt.mp3recorder.util.LameUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * <b>类功能描述：</b>
 * <div style="margin-left:40px;margin-top:-10px"> MP3实时录制功能,可暂停,注意因踩用Native开发,
 * 不能混淆 配置,编译so查看:http://blog.csdn.net/cboy017/article/details/8455629 </div>
 *
 * @version 1.0
 *          </p>
 *          修改时间：</br>
 *          修改备注：</br>
 */
public class MP3Recorder {
    private String filePath;
    private int sampleRate;
    private boolean isRecording = false;
    private boolean isPause = false;
    private Handler handler;
    private long startTime, endTime;

    /**
     * 开始录音
     */
    public static final int MSG_REC_STARTED = 1;

    /**
     * 结束录音
     */
    public static final int MSG_REC_STOPPED = 2;

    /**
     * 暂停录音
     */
    public static final int MSG_REC_PAUSE = 3;

    /**
     * 继续录音
     */
    public static final int MSG_REC_RESTORE = 4;

    /**
     * 缓冲区挂了,采样率手机不支持
     */
    public static final int MSG_ERROR_GET_MIN_BUFFERSIZE = -1;

    /**
     * 创建文件
     */
    public static final int MSG_ERROR_CREATE_FILE = -2;

    /**
     * 初始化录音器
     */
    public static final int MSG_ERROR_REC_START = -3;

    /**
     * 录音的时候出错
     */
    public static final int MSG_ERROR_AUDIO_RECORD = -4;

    /**
     * 编码时挂了
     */
    public static final int MSG_ERROR_AUDIO_ENCODE = -5;

    /**
     * 写文件时挂了
     */
    public static final int MSG_ERROR_WRITE_FILE = -6;

    /**
     * 没法关闭文件流
     */
    public static final int MSG_ERROR_CLOSE_FILE = -7;

    public static final int MSG_REC_WRITE_FILE = -8;

    public MP3Recorder(String filePath, int sampleRate) {
        this.filePath = filePath;
        this.sampleRate = sampleRate;
    }

    /**
     * 开始录音
     */
    public void start() {
        if (isRecording) {
            return;
        }

        new Thread() {
            @Override
            public void run() {
                startTime = System.currentTimeMillis();
                android.os.Process.setThreadPriority(
                        android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
                // 根据定义好的几个配置，来获取合适的缓冲大小
                final int minBufferSize = AudioRecord.getMinBufferSize(
                        sampleRate, AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);
                if (minBufferSize < 0) {
                    if (handler != null) {
                        handler.sendEmptyMessage(MSG_ERROR_GET_MIN_BUFFERSIZE);
                    }
                    return;
                }
                AudioRecord audioRecord = new AudioRecord(
                        MediaRecorder.AudioSource.MIC, sampleRate,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT, minBufferSize * 2);
                // 5秒的缓冲
                short[] buffer = new short[sampleRate * (16 / 8) * 1 * 5];
                byte[] mp3buffer = new byte[(int) (7200
                        + buffer.length * 2 * 1.25)];

                FileOutputStream output = null;
                try {
                    output = new FileOutputStream(new File(filePath));
                } catch (FileNotFoundException e) {
                    if (handler != null) {
                        handler.sendEmptyMessage(MSG_ERROR_CREATE_FILE);
                    }
                    return;
                }
                LameUtil.init(sampleRate, 1, sampleRate, 32, 7);
                isRecording = true; // 录音状态
                isPause = false; // 录音状态
                try {
                    try {
                        audioRecord.startRecording(); // 开启录音获取音频数据
                    } catch (IllegalStateException e) {
                        // 不给录音...
                        if (handler != null) {
                            handler.sendEmptyMessage(MSG_ERROR_REC_START);
                        }
                        return;
                    }

                    try {
                        // 开始录音
                        if (handler != null) {
                            handler.sendEmptyMessage(MSG_REC_STARTED);
                        }

                        int readSize = 0;
                        boolean pause = false;
                        while (isRecording) {
                            /*--暂停--*/
                            if (isPause) {
                                if (!pause) {
                                    handler.sendEmptyMessage(MSG_REC_PAUSE);
                                    pause = true;
                                }
                                continue;
                            }
                            if (pause) {
                                handler.sendEmptyMessage(MSG_REC_RESTORE);
                                pause = false;
                            }
                            /*--End--*/
                            /*--实时录音写数据--*/
                            readSize = audioRecord.read(buffer, 0,
                                    minBufferSize);
                            if (readSize < 0) {
                                if (handler != null) {
                                    handler.sendEmptyMessage(
                                            MSG_ERROR_AUDIO_RECORD);
                                }
                                break;
                            } else if (readSize == 0) {
                                ;
                            } else {
                                int encResult = LameUtil.encode(buffer,
                                        buffer, readSize, mp3buffer);
                                if (encResult < 0) {
                                    if (handler != null) {
                                        handler.sendEmptyMessage(
                                                MSG_ERROR_AUDIO_ENCODE);
                                    }
                                    break;
                                }
                                if (encResult != 0) {
                                    try {
                                        output.write(mp3buffer, 0, encResult);

                                        int v = 0;
                                        // 将 buffer 内容取出，进行平方和运算
                                        for (int i = 0; i < buffer.length; i++) {
                                            // 这里没有做运算的优化，为了更加清晰的展示代码
                                            v += buffer[i] * buffer[i];
                                        }
                                        // 平方和除以数据总长度，得到音量大小。可以获取白噪声值，然后对实际采样进行标准化。
                                        // 如果想利用这个数值进行操作，建议用 sendMessage 将其抛出，在
                                        // Handler 里进行处理。
                                        int value = (int) (Math.abs(
                                                (int) (v / (float) readSize)
                                                        / 10000) >> 1);
                                        Message msg = handler.obtainMessage();
                                        msg.what = MSG_REC_WRITE_FILE;
                                        msg.arg1 = value;
                                        handler.sendMessage(msg);
                                    } catch (IOException e) {
                                        if (handler != null) {
                                            handler.sendEmptyMessage(
                                                    MSG_ERROR_WRITE_FILE);
                                        }
                                        break;
                                    }
                                }
                            }
                            /*--End--*/
                        }
                        /*--录音完--*/
                        int flushResult = LameUtil.flush(mp3buffer);
                        if (flushResult < 0) {
                            if (handler != null) {
                                handler.sendEmptyMessage(
                                        MSG_ERROR_AUDIO_ENCODE);
                            }
                        }
                        if (flushResult > 0) {
                            try {
                                output.write(mp3buffer, 0, flushResult);
                            } catch (Exception e) {
                                if (handler != null) {
                                    handler.sendEmptyMessage(MSG_ERROR_WRITE_FILE);
                                }
                            }
                        }
                        try {
                            output.close();
                        } catch (IOException e) {
                            if (handler != null) {
                                handler.sendEmptyMessage(MSG_ERROR_CLOSE_FILE);
                            }
                        }
						/*--End--*/
                    } finally {
                        audioRecord.stop();
                        audioRecord.release();
                    }
                } finally {
                    LameUtil.close();
                    isRecording = false;
                }
                if (handler != null) {
                    handler.sendEmptyMessage(MSG_REC_STOPPED);
                }
            }
        }.start();
    }

    public int stop() {
        isRecording = false;
        endTime = System.currentTimeMillis();
        return (int) ((endTime - startTime) / 1000);
    }

    public void pause() {
        isPause = true;
    }

    public void restore() {
        isPause = false;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public boolean isPause() {
        if (!isRecording) {
            return false;
        }
        return isPause;
    }

    /**
     * 录音状态管理
     *
     * @see RecMicToMp3#MSG_REC_STARTED
     * @see RecMicToMp3#MSG_REC_STOPPED
     * @see RecMicToMp3#MSG_REC_PAUSE
     * @see RecMicToMp3#MSG_REC_RESTORE
     * @see RecMicToMp3#MSG_ERROR_GET_MIN_BUFFERSIZE
     * @see RecMicToMp3#MSG_ERROR_CREATE_FILE
     * @see RecMicToMp3#MSG_ERROR_REC_START
     * @see RecMicToMp3#MSG_ERROR_AUDIO_RECORD
     * @see RecMicToMp3#MSG_ERROR_AUDIO_ENCODE
     * @see RecMicToMp3#MSG_ERROR_WRITE_FILE
     * @see RecMicToMp3#MSG_ERROR_CLOSE_FILE
     */
    public void setHandle(Handler handler) {
        this.handler = handler;
    }


    /**
     * 初始化录制参数
     *//*
	public static void init(int inSamplerate, int outChannel, int outSamplerate,
			int outBitrate) {
		init(inSamplerate, outChannel, outSamplerate, outBitrate, 7);
	}

	*//**
     * 初始化录制参数 quality:0=很好很慢 9=很差很快
     *//*
	public native static void init(int inSamplerate, int outChannel,
			int outSamplerate, int outBitrate, int quality);

	*//**
     * 音频数据编码(PCM左进,PCM右进,MP3输出)
     *//*
	public native static int encode(short[] buffer_l, short[] buffer_r,
			int samples, byte[] mp3buf);

	*//**
     * 录完之后要清楚缓冲区
     *//*
	public native static int flush(byte[] mp3buf);

	*//**
     * 结束编码
     *//*
	public native static void close();*/

    public static class VoicePlayer implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener,
            MediaPlayer.OnBufferingUpdateListener {

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

        public VoicePlayer(Context context) {
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

            if (player != null){


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
}
