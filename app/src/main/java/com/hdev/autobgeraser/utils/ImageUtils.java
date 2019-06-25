package com.hdev.autobgeraser.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaScannerConnection;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageUtils {
    private static String file;

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


    public static boolean saveBitmap(Context context, Bitmap bitmap) {
        boolean success = false;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "auto_bg_eraser_removed_bg_" + timeStamp + ".jpg";
        File file = new File(AppPreferences.getPath(), fileName);
        if (file.exists()) file.delete();
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            success = true;
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            MediaScannerConnection.scanFile(context, new String[]{file.toString()}, new String[]{file.getName()}, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }
}
