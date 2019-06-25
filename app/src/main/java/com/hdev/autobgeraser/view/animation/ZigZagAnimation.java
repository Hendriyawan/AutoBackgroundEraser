package com.hdev.autobgeraser.view.animation;

import android.graphics.Path;
import android.graphics.PathMeasure;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ZigZagAnimation extends Animation {
    private PathMeasure pm;

    public ZigZagAnimation() {
        Path path = new Path();
        path.moveTo(0f, 0f);
        path.lineTo(100f, 70f);
        path.lineTo(300f, 500f);
        path.lineTo(135f, 200f);
        path.lineTo(45f, 500f);
        pm = new PathMeasure(path, false);
        setDuration(2000);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float distance = pm.getLength() * interpolatedTime;
        float[] pos = new float[2];
        pm.getPosTan(distance, pos, null);
        t.getMatrix().postTranslate(pos[0], pos[1]);
    }
}
