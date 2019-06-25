package com.hdev.autobgeraser.view.dialog;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hdev.autobgeraser.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class DialogAppUpdate extends DialogFragment {
    private static DialogAppUpdate dialogAppUpdate;
    @BindView(R.id.text_dialog_update_title)
    TextView textViewDialogTitle;
    @BindView(R.id.text_dialog_update_description)
    TextView textViewDialogMessage;
    @BindView(R.id.button_dialog_cancel)
    Button buttonCancel;
    private String updateTitle;
    private String updateDescription;
    private boolean isCancel;
    private Unbinder unbinder;

    public DialogAppUpdate() {
    }

    public static DialogAppUpdate getInstance() {
        if (dialogAppUpdate == null) {
            dialogAppUpdate = new DialogAppUpdate();
        }
        return dialogAppUpdate;
    }

    public void addContent(String updateTitle, String updateDescription, boolean isCancel) {
        this.updateTitle = updateTitle;
        this.updateDescription = updateDescription;
        this.isCancel = isCancel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_app_update_layout, container, false);
        unbinder = ButterKnife.bind(this, view);

        initView();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    /*
        OnClick Button Cancel (for dismiss the Dialog)
         */
    @OnClick(R.id.button_dialog_cancel)
    public void onButtonCancel() {
        dismiss();
    }

    /*
    OnClick Button Update (for action open the Google Playstore)
     */
    @OnClick(R.id.button_dialog_update)
    public void onButtonUpdate() {
        openPlayStore();
        dismiss();
    }


    /*
    Init Views
     */
    private void initView() {
        textViewDialogTitle.setText(updateTitle != null && !TextUtils.isEmpty(updateTitle) ? updateTitle : "");
        textViewDialogMessage.setText(updateDescription != null && !TextUtils.isEmpty(updateDescription) ? updateDescription : "");
        buttonCancel.setVisibility(isCancel ? View.GONE : View.VISIBLE);
    }

    //open Google Playstore
    private void openPlayStore() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getActivity().getPackageName())));
        } catch (ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
        }
    }
}
