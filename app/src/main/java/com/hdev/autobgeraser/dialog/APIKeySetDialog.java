package com.hdev.autobgeraser.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.hdev.autobgeraser.R;
import com.hdev.autobgeraser.utils.AppPreferences;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class APIKeySetDialog extends DialogFragment {
    @BindView(R.id.input_api_key)
    TextInputEditText inputApiKey;
    private OnApiKeySet onApiKeySet;

    public void setOnButtonSetAPIListener(OnApiKeySet onApiKeySet) {
        this.onApiKeySet = onApiKeySet;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_set_api_key, null);
        ButterKnife.bind(this, view);

        inputApiKey.setText(!TextUtils.isEmpty(AppPreferences.getApiKey()) ? AppPreferences.getApiKey() : "");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.api_key));
        builder.setView(view);

        Dialog dialog = builder.create();
        return dialog;
    }

    @OnClick(R.id.button_action_save)
    public void onSave() {
        String apiKey = inputApiKey.getText().toString().trim();
        if (TextUtils.isEmpty(apiKey)) {
            inputApiKey.setError(getResources().getString(R.string.input_api_key_empty_message));
            //inputApiKey.setText(getResources().getString(R.string.input_api_key_empty_message));
            inputApiKey.requestFocus();

        } else {
            onApiKeySet.onSet(apiKey);
            dismiss();
        }
    }

    @OnClick(R.id.button_action_cancel)
    public void onCancel() {
        dismiss();
    }

    public interface OnApiKeySet {
        void onSet(String apiKey);
    }
}
