package com.hdev.autobgeraser.remover;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.BitmapRequestListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hdev.autobgeraser.R;
import com.hdev.autobgeraser.instance.EndPoint;

import java.io.File;

public class RemoverPresenter {

    private String apiKey;
    private Context context;
    private RemoverView removerView;

    public RemoverPresenter(Context context, RemoverView removerView) {
        this.context = context;
        this.removerView = removerView;
    }

    public void loadApiKey() {
        removerView.onStartProgress();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("remove_bg_api");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                removerView.onStopProgress();
                String api = dataSnapshot.getValue(String.class);
                setApiKey(api);
                Log.d("onDataChanged", api);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                removerView.onStopProgress();
                removerView.onFailed(databaseError.getMessage());
            }
        });
    }

    public void removeByBase64(String base64) {
        Log.d("API", getApiKey());
        removerView.onStartProgress();
        AndroidNetworking.upload(EndPoint.URL)
                .addHeaders("X-Api-Key", getApiKey())
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
                        }
                    }
                });
    }


    public void removeByBase64WithReplaceBg(String base64, File file) {
        Log.d("API", getApiKey());
        removerView.onStartProgress();
        AndroidNetworking.upload(EndPoint.URL)
                .addHeaders("X-Api-Key", getApiKey())
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

    private String getApiKey() {
        return apiKey;
    }

    private void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
