package com.tfedu.record;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * desc :录音popupwindow
 * author：xiedong
 * data：2018/8/30
 */
public class RecoderPopupWindow extends PopupWindow implements View.OnClickListener {
    private Context context;
    private View view;
    private ImageButton ivStartRecoder;
    private PercentPalyView pivPlayRecoder;
    private TextView tvSaveRecoder;
    private VoiceManager voiceManager;
    private String recordPath;
    private IRecoderCallBack recoderCallBack;
    private ImageView ivCloseDialog;
    private LineWaveVoiceView lineWaveView;
    private LinearLayout llBottomMenu;
    private boolean isRecoding = false; //是否开始录音
    private String recordStrLength;
    private long recordlength;
    private TextView tvRecoderInfo;

    public RecoderPopupWindow(Context context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.layout_record_layout, null);
        int h = ViewGroup.LayoutParams.WRAP_CONTENT;
        int w = ViewGroup.LayoutParams.MATCH_PARENT;
        this.setWidth(w);
        this.setHeight(h);
        this.setContentView(view);
        this.setFocusable(false);
        this.setOutsideTouchable(false);
        this.update();
        view.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.setAnimationStyle(R.style.Animation_Design_BottomSheetDialog);
        setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                lineWaveView.stopRecord();
                voiceManager.stopRecordAndPlay();
                backgroundAlpha(1f);
            }
        });

        initView();
        initListener();
    }

    private void initView() {
        ivStartRecoder = (ImageButton) view.findViewById(R.id.btn_start);
        pivPlayRecoder = (PercentPalyView) view.findViewById(R.id.btn_play);
        tvSaveRecoder = (TextView) view.findViewById(R.id.tv_save);
        ivCloseDialog = (ImageView) view.findViewById(R.id.iv_close_dialog);
        lineWaveView = (LineWaveVoiceView) view.findViewById(R.id.lwv_wave_view);
        llBottomMenu = (LinearLayout) view.findViewById(R.id.ll_bottom_menu);
        tvRecoderInfo = (TextView) view.findViewById(R.id.tv_recoder_info);

        ivStartRecoder.setOnClickListener(this);
        tvSaveRecoder.setOnClickListener(this);
        pivPlayRecoder.setOnClickListener(this);
        ivCloseDialog.setOnClickListener(this);
        view.findViewById(R.id.tv_cancel).setOnClickListener(this);
    }

    private void initListener() {
        voiceManager = VoiceManager.getInstance();
        voiceManager.setContext(context);
        //录音监听
        voiceManager.setVoiceRecordListener(new VoiceManager.VoiceRecordCallBack() {
            @Override
            public void recDoing(long time, String strTime) {
                lineWaveView.setText(strTime);
            }

            @Override
            public void recVoiceGrade(int grade) {

            }

            @Override
            public void recStart(boolean init) {
                isRecoding = true;
            }

            @Override
            public void recPause(String str) {

            }

            @Override
            public void recFinish(long length, String strLength, String path) {
                recordPath = path;
                recordlength = length;
                recordStrLength = strLength;
            }

            @Override
            public void recException() {
                reset();
            }
        });

        //播放监听
        voiceManager.setVoicePlayListener(new VoiceManager.VoicePlayCallBack() {
            @Override
            public void voiceTotalLength(long time, String strTime) {
                pivPlayRecoder.setTimeTotalLength(time);
            }

            @Override
            public void playDoing(long time, String strTime) {
                pivPlayRecoder.setCurrentPercent(time);
                lineWaveView.setText(strTime);
            }

            @Override
            public void playPause() {
            }

            @Override
            public void playStart() {
            }

            @Override
            public void playFinish() {
                pivPlayRecoder.setCurrentPercent(0);
                pivPlayRecoder.setBackgroundResource(R.drawable.ic_recoder_play);
            }
        });
    }

    public void show() {
        showAtLocation(view, Gravity.BOTTOM, 0, 0);
        backgroundAlpha(0.8f);
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(final float bgAlpha) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
                lp.alpha = bgAlpha; //0.0-1.0
                ((Activity) context).getWindow().setAttributes(lp);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_start) {
            if (!isRecoding) {
                tvRecoderInfo.setVisibility(View.GONE);
                ivStartRecoder.setBackgroundResource(R.drawable.ic_recodering);
                pivPlayRecoder.setVisibility(View.GONE);

                lineWaveView.startRecord();
                voiceManager.startVoiceRecord(Environment.getExternalStorageDirectory().getPath() + "/Education/media/Education Audio");
            } else {
                ivStartRecoder.setVisibility(View.INVISIBLE);
                llBottomMenu.setVisibility(View.VISIBLE);
                pivPlayRecoder.setVisibility(View.VISIBLE);

                lineWaveView.stopRecord();
                voiceManager.stopVoiceRecord();
            }
        } else if (i == R.id.tv_save) {
            recoderCallBack.onSave(recordlength, recordStrLength, recordPath);
            this.dismiss();
        } else if (i == R.id.btn_play) {
            voiceManager.startPlay(recordPath);
            pivPlayRecoder.setBackgroundResource(R.drawable.ic_recodering);
        } else if (i == R.id.iv_close_dialog) {
            this.dismiss();
        } else if (i == R.id.tv_cancel) {
            reset();
        }
    }

    private void reset() {
        isRecoding = false; //重新可录音
        voiceManager.stopRecordAndPlay();
        lineWaveView.setVisibility(View.VISIBLE);
        llBottomMenu.setVisibility(View.GONE);
        lineWaveView.setText("00:00");
        pivPlayRecoder.setCurrentPercent(0);
        ivStartRecoder.setBackgroundResource(R.drawable.ic_recoder_start);
        tvRecoderInfo.setVisibility(View.VISIBLE);
        ivStartRecoder.setVisibility(View.VISIBLE);
    }

    public void setRecoderCallBack(IRecoderCallBack callBack) {
        this.recoderCallBack = callBack;
    }
}
