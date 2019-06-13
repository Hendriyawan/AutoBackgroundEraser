package com.hdev.autobgeraser.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class ImageUtils {

    public static Bitmap getRoundedBitmap(Bitmap bitmap, float round) {
        //image size
        int w = bitmap.getWidth(), h = bitmap.getHeight();

        //result of bitmap
        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        //set canvas fof painting
        Canvas canvas = new Canvas(result);
        canvas.drawARGB(0, 0, 0, 0);

        //config paint
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);

        //config rectangle for embedding
        Rect rect = new Rect(0, 0, w, h);
        RectF rectF = new RectF(rect);

        //draw to canvas
        canvas.drawRoundRect(rectF, round, round, paint);

        //create Xfer to mode
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //draw source image to canvas
        canvas.drawBitmap(bitmap, rect, rect, paint);

        //return final image
        return result;
    }
}
