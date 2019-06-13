package com.hdev.autobgeraser.remover;

import android.graphics.Bitmap;

public interface RemoverView {

    void onStartProgress();

    void onStopProgress();

    void onBackgroundRemoved(Bitmap result);

    void onFailed(String message);
}
