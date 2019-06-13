package com.hdev.autobgeraser.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/*
30 Maret 2019 by Hendriyawan
 */
public class Encoder {

    public static String imageUriToBase64(Context context, Uri uri) {
        String base64 = "";
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            base64 = encode(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return base64;
    }

    private static String encode(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
}
