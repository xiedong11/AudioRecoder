package com.tfedu.record.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.tfedu.record.R;
import com.tfedu.record.util.FileUtils;


import java.io.File;

public class RecordDialog extends Dialog {
    private Context context;
    private String fileName;
    public RecordLayout recordLayout;

    public RecordDialog(Context context, String voicePath) {
        super(context, R.style.custom_dialog);
        this.context = context;
        File voiceFile = new File(voicePath);
        fileName = voiceFile.getName();
        FileUtils.createDirAndFile(voiceFile.getParent(), fileName);

        recordLayout = new RecordLayout(context, voicePath);
        setContentView(recordLayout);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);

        recordLayout.setOnRecordLayoutListener(
                new RecordLayout.OnRecordLayoutLinstener() {
                    @Override
                    public void onUpload(String voicePath, int duration) {
                        if (onConfirmListener != null) {
                            onConfirmListener.onConfirm(fileName, duration);
                            RecordDialog.this.dismiss();
                        }
                    }
                });
    }

    public interface OnConfirmListener {
        public void onConfirm(String voicePath, int duration);
    }

    private OnConfirmListener onConfirmListener;

    public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }
}
