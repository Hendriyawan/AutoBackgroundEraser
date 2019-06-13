package com.hdev.autobgeraser.remover;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ProgressBar;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.BitmapRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hdev.autobgeraser.instance.EndPoint;

public class RemoverPresenter {

    private String apiKey;
    private RemoverView removerView;

    public RemoverPresenter(RemoverView removerView) {
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
                        removerView.onFailed(anError.getErrorDetail());
                    }
                });
    }


    private void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    private String getApiKey() {
        return apiKey;
    }
}
