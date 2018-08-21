package com.tfedu.record.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.tfedu.record.R;
import com.tfedu.record.recorder.MP3Recorder;
import com.tfedu.record.util.FileUtils;
import com.tfedu.record.util.StringUtils;
import com.tfedu.record.util.VoiceRecord;



public class RecordLayout extends ViewSwitcher implements OnClickListener {
    private Context mContext;
    private ImageButton btnPause, btnSave;
    private Chronometer chRecord, chAudition;
    private TextView tvReRecord, tvUpload, tvTotalTime;
    private long recordingTime = 0;// 记录下来的总时间
    private boolean isPause = true;
    private MP3Recorder recorder;
    private Drawable[] drawable_Anims;// 话筒动画
    private ViewSwitcher voiceSwitcher;
    private ImageView ivRecord, ivAudition;
    private VoiceRecord voicePlayer;
    private String voicePath;

    public RecordLayout(Context context, String voicePath) {
        super(context);
        mContext = context;
        this.voicePath = voicePath;
        init();
    }

    public RecordLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_recordlayout, this, false);
        this.addView(view);
        voiceSwitcher = (ViewSwitcher) view;
        Animation slide_in_left = AnimationUtils.loadAnimation(mContext,
                android.R.anim.slide_in_left);
        Animation slide_out_right = AnimationUtils.loadAnimation(mContext,
                android.R.anim.slide_out_right);
        voiceSwitcher.setInAnimation(slide_in_left);
        voiceSwitcher.setOutAnimation(slide_out_right);
        btnSave = (ImageButton) view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        btnPause = (ImageButton) view.findViewById(R.id.btnPause);
        btnPause.setOnClickListener(this);
        chRecord = (Chronometer) view.findViewById(R.id.chRecord);
        chRecord.setOnChronometerTickListener(
                new Chronometer.OnChronometerTickListener() {
                    @Override
                    public void onChronometerTick(Chronometer chronometer) {
                        String timeStr = chronometer.getText().toString().replace(" ", "");
                        if (("05:00".equals(timeStr) || "5:00".equals(timeStr))
                                && !isPause) {
                            save();
                        }
                    }
                });
        chAudition = (Chronometer) view.findViewById(R.id.chAudition);
        ivRecord = (ImageView) view.findViewById(R.id.ivRecord);
        ivAudition = (ImageView) view.findViewById(R.id.ivAudition);
        ivAudition.setOnClickListener(this);
        tvTotalTime = (TextView) view.findViewById(R.id.tvTotalTime);
        tvUpload = (TextView) view.findViewById(R.id.tvUpload);
        tvUpload.setOnClickListener(this);
        tvReRecord = (TextView) view.findViewById(R.id.tvReRecord);
        tvReRecord.setOnClickListener(this);

        initVoiceAnimRes();
        initRecordManager();

        voicePlayer = new VoiceRecord(mContext);
        voicePlayer.setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        voicePlayer.stop();
                        voicePlayer.setPrepared(false);
                        stopTime(chAudition);
                        ivAudition
                                .setImageResource(R.drawable.record_play_icon);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btnSave) {
            save();

        } else if (i == R.id.btnPause) {
            if (isPause) {
                String timeStr = ((String) chRecord.getText()).replace(" ", "");
                if ("00:00".equals(timeStr) || "0:00".equals(timeStr)) {
                    if (!FileUtils.Exists(voicePath)) {
                        FileUtils.createFile(voicePath);
                    }
                    recorder.start();
                } else {
                    recorder.restore();
                }
                isPause = false;
                startTime(chRecord);
                btnPause.setBackgroundResource(R.drawable.record_pause_icon);
            } else {
                isPause = true;
                recorder.pause();
                pauseTime(chRecord);
                btnPause.setBackgroundResource(R.drawable.record_play_icon);
            }

        } else if (i == R.id.tvReRecord) {
            isPause = true;
            btnPause.setBackgroundResource(R.drawable.record_icon);
            voiceSwitcher.setDisplayedChild(0);
            stopTime(chAudition);
            stopTime(chRecord);
            if (recorder.isPause()) {
                recorder.stop();
            }
            if (voicePlayer.state == VoiceRecord.AudioPlayerState.STARTED) {
                voicePlayer.pause();
            }

        } else if (i == R.id.tvUpload) {
            if (linstener != null) {
                String[] time = tvTotalTime.getText().toString().split(":");
                int duration = StringUtils.toInt(time[0]) * 60 + StringUtils.toInt(time[1]);
                linstener.onUpload(voicePath, duration);
            }

        } else if (i == R.id.ivAudition) {
            if (voicePlayer.state != VoiceRecord.AudioPlayerState.STARTED) {
                if (!voicePlayer.isPrepared()) {
                    voicePlayer.playUrl(Uri.parse(voicePath));
                } else {
                    voicePlayer.start();
                }
                startTime(chAudition);
                ivAudition.setImageResource(R.drawable.record_pause_icon);
            } else {
                voicePlayer.pause();
                pauseTime(chAudition);
                ivAudition.setImageResource(R.drawable.record_play_icon);
            }

        }
    }

    /**
     * @throws
     * @Title: save
     * @Description:保存录音
     * @param:
     * @return: void
     */
    private void save() {
        String timeStr = ((String) chRecord.getText()).replace(" ", "");
        if ("00:00".equals(timeStr) || "0:00".equals(timeStr)) {
            Toast.makeText(mContext, R.string.record_tip1, Toast.LENGTH_SHORT).show();
            return;
        }
        int recordTime = recorder.stop();
        if (recordTime <= 1) {
            btnPause.setBackgroundResource(R.drawable.record_icon);
            Toast.makeText(mContext, R.string.record_tip2, Toast.LENGTH_SHORT).show();
        } else {
            voiceSwitcher.setDisplayedChild(1);
            tvTotalTime.setText(" /" + timeStr);
        }
        isPause = true;
        stopTime(chRecord);
        ivAudition.setImageResource(R.drawable.record_play_icon);
    }

    public void startTime(Chronometer ch) {
        ch.setBase(SystemClock.elapsedRealtime() - recordingTime);// 跳过已经记录了的时间，起到继续计时的作用
        ch.start();
    }

    public void pauseTime(Chronometer ch) {
        ch.stop();
        recordingTime = SystemClock.elapsedRealtime() - ch.getBase();// 保存这次记录了的时间
    }

    public void stopTime(Chronometer ch) {
        recordingTime = 0;
        ch.setBase(SystemClock.elapsedRealtime());
        ch.stop();
    }

    /**
     * 初始化语音动画资源
     *
     * @Title: initVoiceAnimRes
     */
    private void initVoiceAnimRes() {
        drawable_Anims = new Drawable[]{
                getResources().getDrawable(R.drawable.chat_icon_voice2),
                getResources().getDrawable(R.drawable.chat_icon_voice3),
                getResources().getDrawable(R.drawable.chat_icon_voice4),
                getResources().getDrawable(R.drawable.chat_icon_voice5),
                getResources().getDrawable(R.drawable.chat_icon_voice6)};
    }

    private void initRecordManager() {
        // 8KHz录制MP3,记得要加上录音权限哦~~
        recorder = new MP3Recorder(voicePath, 8000);
        recorder.setHandle(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MP3Recorder.MSG_REC_WRITE_FILE) {
                    int value = msg.arg1;
                    if (0 <= value && value < 50) {
                        value = 0;
                    } else if (50 <= value && value < 100) {
                        value = 1;
                    } else if (100 <= value && value < 150) {
                        value = 2;
                    } else if (150 <= value && value < 200) {
                        value = 3;
                    } else {
                        value = 4;
                    }
                    ivRecord.setImageDrawable(drawable_Anims[value]);
                }
                super.handleMessage(msg);
            }
        });
    }

    public void reset() {
        isPause = true;
        this.setVisibility(View.GONE);
        stopTime(chAudition);
        stopTime(chRecord);
        btnPause.setBackgroundResource(R.drawable.record_icon);
        voiceSwitcher.reset();
        voiceSwitcher.setDisplayedChild(0);
        if (recorder.isPause()) {
            recorder.stop();
        }
    }

    OnRecordLayoutLinstener linstener;

    public void setOnRecordLayoutListener(OnRecordLayoutLinstener linstener) {
        this.linstener = linstener;
    }

    public interface OnRecordLayoutLinstener {
        void onUpload(String voicePath, int duration);
    }
}
