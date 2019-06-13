package com.hdev.autobgeraser.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hdev.autobgeraser.R;
import com.hdev.autobgeraser.dialog.MenuDialogListener;
import com.hdev.autobgeraser.dialog.MenuImagePickerDialog;
import com.hdev.autobgeraser.instance.MenuDialog;
import com.hdev.autobgeraser.remover.RemoverPresenter;
import com.hdev.autobgeraser.remover.RemoverView;
import com.hdev.autobgeraser.utils.AppPreferences;
import com.hdev.autobgeraser.utils.Encoder;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.io.File;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import top.defaults.checkerboarddrawable.CheckerboardDrawable;

/*
30 Maret 2019 by Hendriyawan
@Gihtub : github.com/Hendriyawan
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MenuDialogListener.OnMenuSelect, RemoverView {
    private static final int GALLERY_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    @BindString(R.string.button_start)
    String textStart;
    @BindString(R.string.button_select_photo)
    String textSelectPhoto;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.cv_main)
    CardView cardViewMain;
    @BindView(R.id.cv_selected_replace_bg)
    CardView cvSelectedReplaceBg;
    @BindView(R.id.cv_result)
    CardView cardViewResult;
    @BindView(R.id.iv_selected)
    ImageView selectedImage;
    @BindView(R.id.iv_selected_replace_bg)
    ImageView selectedReplaceBg;
    @BindView(R.id.iv_transparent_bg)
    ImageView transparentBg;
    @BindView(R.id.iv_result)
    ImageView resultImage;
    @BindView(R.id.progressbar)
    ProgressBar progressBar;
    @BindView(R.id.tv_progress)
    TextView tvProgress;
    @BindView(R.id.btn_select_picker)
    Button buttonSelectPicker;


    private MenuImagePickerDialog menuDialog;
    private boolean granted;
    private boolean isOptionEnabled = false;
    private boolean start = false;
    private String base64;
    private RemoverPresenter removerPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //BindView
        ButterKnife.bind(this);

        initToolbar();
        initNavigationView();
        initPathDownloaded();
        initTransparentBackground();
        removerPresenter = new RemoverPresenter(this);
        removerPresenter.loadApiKey();
    }

    //Button Select Picker of Photo
    @OnClick(R.id.btn_select_picker)
    public void onSelectPhoto() {
        if (start) {
            removerPresenter.removeByBase64(base64);

        } else {
            //do request permission first
            if (requestPermissions()) {
                showMenuImagePickerDialog();
            } else {
                showToast(getResources().getString(R.string.request_permission_message));
            }
        }
    }

    //OnCheckedChanged CheckBox to enable/disable option automatically replace deleted Background
    @OnCheckedChanged(R.id.cb_auto_replace_bg)
    public void onChecked(boolean checked) {
        cvSelectedReplaceBg.setVisibility(checked ? View.VISIBLE : View.GONE);
        isOptionEnabled = checked;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMenuSelected(int menu) {
        switch (menu) {
            case MenuDialog.PICK_FROM_CAMERA:
                //pickFromCamera();
                break;

            case MenuDialog.PICK_FROM_GALLERY:
                pickImageFromGallery();
                break;

            case MenuDialog.PICK_FROM_URL:
                break;
        }

    }


    @Override
    public void onStartProgress() {
        progressBar.setVisibility(View.VISIBLE);
        tvProgress.setVisibility(View.VISIBLE);

    }

    @Override
    public void onStopProgress() {
        progressBar.setVisibility(View.GONE);
        tvProgress.setVisibility(View.GONE);
    }

    @Override
    public void onBackgroundRemoved(Bitmap result) {
        if (result != null) {
            buttonSelectPicker.setText(textSelectPhoto);
            if (cardViewResult.getVisibility() == View.GONE) {
                cardViewResult.setVisibility(View.VISIBLE);
                resultImage.setImageBitmap(result);
            }
        }

    }

    @Override
    public void onFailed(String message) {
        tvProgress.setVisibility(View.VISIBLE);
        tvProgress.setText(message);
        tvProgress.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_info_outline_error, 0, 0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                switch (requestCode) {
                    case GALLERY_REQUEST_CODE:
                        if (isOptionEnabled) {
                            start = true;
                            buttonSelectPicker.setText(textStart);
                            selectedImage.setImageURI(uri);
                            base64 = Encoder.imageUriToBase64(this, uri);

                        } else {

                            base64 = Encoder.imageUriToBase64(this, uri);
                            selectedImage.setImageURI(uri);
                            removerPresenter.removeByBase64(base64);

                        }
                        break;

                    case CAMERA_REQUEST_CODE:
                        transparentBg.setImageURI(uri);
                        break;
                }

            }
        }
    }


    //init Toolbar
    private void initToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        }
    }

    //init NavigationView
    private void initNavigationView() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    //Request permissions Camera and External Storage
    private boolean requestPermissions() {
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        Permissions.check(this, permissions, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                granted = true;
            }
        });
        return granted;
    }

    //init Path Downloaded
    private void initPathDownloaded() {
        File pathDownloaded = new File(Environment.getExternalStorageDirectory().toString() + "/AutoBGEraser");
        if (requestPermissions()) {
            pathDownloaded.mkdir();
            AppPreferences.savePath(this, pathDownloaded.getPath());
        }
    }

    //Pick from camera
    private void pickFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    //Pick Image from Gallery
    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    //show toast
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    //show MenuImagePickerDialog
    private void showMenuImagePickerDialog() {
        menuDialog = new MenuImagePickerDialog();
        menuDialog.setOnMenuSelect(this);
        menuDialog.showDialog(MainActivity.this, "MENU_IMAGE_PICKER_DIALOG");
    }

    //close Drawer
    private void closeDrawer() {
        drawer.closeDrawer(GravityCompat.START);
    }

    //init Transparent background
    private void initTransparentBackground() {
        CheckerboardDrawable drawable = new CheckerboardDrawable.Builder()
                .colorOdd(Color.LTGRAY)
                .colorEven(Color.DKGRAY)
                .size(20)
                .build();
        transparentBg.setImageDrawable(drawable);
    }
}