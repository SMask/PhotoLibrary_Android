package com.mask.photo.interfaces;

import java.io.File;

/**
 * 保存Bitmap回调
 * Created by lishilin on 2020/03/16
 */
public abstract class SaveBitmapCallback {

    /**
     * 成功
     *
     * @param file 保存的文件
     */
    public void onSuccess(File file) {

    }

    /**
     * 失败
     *
     * @param e Exception
     */
    public void onFail(Exception e) {

    }

}
