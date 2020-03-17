package com.mask.photo.interfaces;

import java.io.File;

/**
 * 压缩回调
 * Created by lishilin on 2020/03/17
 */
public abstract class CompressCallback {

    /**
     * 开始
     */
    public void onStart() {

    }

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
     * @param e Throwable
     */
    public void onFail(Throwable e) {

    }

}
