package com.hdev.autobgeraser.remover;

import android.content.Context;
import android.graphics.Bitmap;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.BitmapRequestListener;
import com.hdev.autobgeraser.R;
import com.hdev.autobgeraser.instance.EndPoint;

import java.io.File;

public class RemoverPresenter {
    private Context context;
    private RemoverView removerView;

    public RemoverPresenter(Context context, RemoverView removerView) {
        this.context = context;
        this.removerView = removerView;
    }

    public void removeByBase64(String apiKey, String base64) {
        removerView.onStartProgress();
        AndroidNetworking.upload(EndPoint.URL)
                .addHeaders("X-Api-Key", apiKey)
                .addMultipartParameter("size", "regular")
                .addMultipartParameter("image_file_b64", base64)
                .setTag("removebg_base64")
                .setPriority(Priority.HIGH)
                .build()
                .getAsBitmap(new BitmapRequestListener() {
                    @Override
                    public void onResponse(Bitmap response) {
                        removerView.onStopProgress();
                        removerView.onBackgroundRemoved(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        removerView.onStopProgress();
                        int errorCode = anError.getErrorCode();
                        switch (errorCode) {
                            case 400:
                                removerView.onFailed(context.getResources().getString(R.string.error_code_400));
                                break;

                            case 402:
                                removerView.onFailed(context.getResources().getString(R.string.error_code_402));
                                break;

                            case 403:
                                removerView.onFailed(context.getResources().getString(R.string.error_code_403));
                                break;

                            case 429:
                                removerView.onFailed(context.getResources().getString(R.string.error_code_429));
                                break;

                            default:
                                removerView.onFailed(anError.getErrorDetail());
                                break;

                        }
                    }
                });
    }


    public void removeByBase64WithReplaceBg(String apiKey, String base64, File file) {
        removerView.onStartProgress();
        AndroidNetworking.upload(EndPoint.URL)
                .addHeaders("X-Api-Key", apiKey)
                .addMultipartParameter("size", "regular")
                .addMultipartParameter("image_file_b64", base64)
                .addMultipartFile("bg_image_file", file)
                .setTag("removebg_base64")
                .setPriority(Priority.HIGH)
                .build()
                .getAsBitmap(new BitmapRequestListener() {
                    @Override
                    public void onResponse(Bitmap response) {
                        removerView.onStopProgress();
                        removerView.onBackgroundRemoved(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        removerView.onStopProgress();
                        int errorCode = anError.getErrorCode();
                        switch (errorCode) {
                            case 400:
                                removerView.onFailed(context.getResources().getString(R.string.error_code_400));
                                break;

                            case 402:
                                removerView.onFailed(context.getResources().getString(R.string.error_code_402));
                                break;

                            case 403:
                                removerView.onFailed(context.getResources().getString(R.string.error_code_403));
                                break;

                            case 429:
                                removerView.onFailed(context.getResources().getString(R.string.error_code_429));
                                break;
                        }

                    }
                });
    }
}
