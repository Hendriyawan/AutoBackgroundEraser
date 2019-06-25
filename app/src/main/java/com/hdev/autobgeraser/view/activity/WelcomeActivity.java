package com.hdev.autobgeraser.view.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.hdev.autobgeraser.R;
import com.hdev.autobgeraser.utils.AppPreferences;
import com.hdev.autobgeraser.view.animation.ZigZagAnimation;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class WelcomeActivity extends AppCompatActivity {
    @BindView(R.id.iv_eraser)
    ImageView imageEraser;
    @BindString(R.string.dialog_first_launch_message)
    String dialogFirstLaunchMessage;
    @BindString(R.string.dialog_first_launch_later_button)
    String dialogFirstLaunchLaterButton;
    @BindString(R.string.dialog_first_launch_read_button)
    String dialogFirstLaunchReadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
        //BindView
        ButterKnife.bind(this);

        ZigZagAnimation anim = new ZigZagAnimation();
        imageEraser.setAnimation(anim);
        anim.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AppPreferences.getFirstLaunch()) {
                    showDialogFirstLaunch();
                } else {
                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                    finish();
                }
            }
        }, 4000);
    }

    //show dialog First Launch
    private void showDialogFirstLaunch() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(getResources().getDrawable(R.drawable.icon8_eraser));
        builder.setCancelable(false);
        builder.setTitle(getResources().getString(R.string.app_name));
        builder.setMessage(dialogFirstLaunchMessage);
        builder.setPositiveButton(dialogFirstLaunchLaterButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                finish();
            }
        });
        builder.setNegativeButton(dialogFirstLaunchReadButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(WelcomeActivity.this, TutorialActivity.class));
                finish();
            }
        });
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
