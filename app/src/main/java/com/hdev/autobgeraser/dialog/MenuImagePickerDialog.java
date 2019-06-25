package com.hdev.autobgeraser.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import com.hdev.autobgeraser.R;
import com.hdev.autobgeraser.instance.MenuDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MenuImagePickerDialog extends DialogFragment {
    @BindView(R.id.btn_pick_from_gallery)
    ImageButton btnPickFromGallery;
    @BindView(R.id.btn_pick_from_url)
    ImageButton btnPickfromUrl;
    private MenuDialogListener.OnMenuSelect onMenuSelect;

    //constructor
    public MenuImagePickerDialog() {
    }

    public void setOnMenuSelect(MenuDialogListener.OnMenuSelect onMenuSelect) {
        this.onMenuSelect = onMenuSelect;
    }

    public void showDialog(AppCompatActivity activity, String TAG) {
        MenuImagePickerDialog dialog = new MenuImagePickerDialog();
        dialog.setOnMenuSelect(onMenuSelect);
        dialog.show(activity.getSupportFragmentManager(), TAG);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_menu_image_picker_dialog, null);
        ButterKnife.bind(this, view);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.dialog_menu_title));
        builder.setView(view);

        final Dialog dialog = builder.create();
        return dialog;
    }

    @OnClick(R.id.btn_pick_from_gallery)
    public void pickFromGallery() {
        onMenuSelect.onMenuSelected(MenuDialog.PICK_FROM_GALLERY);
        dismiss();
    }

    @OnClick(R.id.btn_pick_from_url)
    public void pickFromURL() {
        onMenuSelect.onMenuSelected(MenuDialog.PICK_FROM_URL);
        dismiss();
    }
}
