package com.mask.photo.interfaces;

import android.graphics.Bitmap;

/**
 * 拼图回调
 * Created by lishilin on 2020/03/17
 */
public abstract class PuzzleCallback {

    /**
     * 成功
     *
     * @param bitmap 拼图后的Bitmap
     */
    public void onSuccess(Bitmap bitmap) {

    }

    /**
     * 失败
     *
     * @param e Exception
     */
    public void onFail(Exception e) {

    }

}
