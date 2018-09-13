package com.tfedu.record;

/**
 * desc :录音结果回调
 * author：xiedong
 * data：2018/8/30
 */
public interface IRecoderCallBack {
    void onSave(long length, String strLength, String path);
}
