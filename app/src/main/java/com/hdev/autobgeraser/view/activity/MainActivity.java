package com.hdev.autobgeraser.view.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.hdev.autobgeraser.BuildConfig;
import com.hdev.autobgeraser.R;
import com.hdev.autobgeraser.dialog.APIKeySetDialog;
import com.hdev.autobgeraser.dialog.MenuDialogListener;
import com.hdev.autobgeraser.dialog.MenuImagePickerDialog;
import com.hdev.autobgeraser.fcm.FirebaseCloudMessagingService;
import com.hdev.autobgeraser.instance.MenuDialog;
import com.hdev.autobgeraser.remover.RemoverPresenter;
import com.hdev.autobgeraser.remover.RemoverView;
import com.hdev.autobgeraser.utils.AppPreferences;
import com.hdev.autobgeraser.utils.Config;
import com.hdev.autobgeraser.utils.Encoder;
import com.hdev.autobgeraser.utils.ImageUtils;
import com.hdev.autobgeraser.view.app.AutoBackgroundEraser;
import com.hdev.autobgeraser.view.dialog.DialogAppUpdate;
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
    private static final int GALLERY_REQUEST_BG_CODE = 3;
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
    CardView cardViewSelectedReplaceBackground;
    @BindView(R.id.cv_result)
    CardView cardViewResult;
    @BindView(R.id.iv_selected)
    ImageView selectedImage;
    @BindView(R.id.iv_selected_replace_bg)
    ImageView selectedReplaceBackground;
    @BindView(R.id.iv_transparent_bg)
    ImageView transparentBackground;
    @BindView(R.id.iv_result)
    ImageView resultImage;
    @BindView(R.id.progressbar)
    ProgressBar progressBar;
    @BindView(R.id.tv_progress)
    TextView textViewProgress;
    @BindView(R.id.btn_select_picker)
    Button buttonSelectPicker;
    @BindView(R.id.iv_more_menu)
    ImageView imageMoreMenu;
    @BindView(R.id.linear_layout_ad_view)
    LinearLayout linearLayoutAdView;

    private boolean granted;
    private boolean isOptionEnabled = false;
    private boolean start = false;
    private String base64;
    private String apiKey;
    private File backgroundImageFile;
    private MenuImagePickerDialog menuDialog;
    private RemoverPresenter removerPresenter;
    private Bitmap resultBitmap;
    private AdView adView;
    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //BindView
        ButterKnife.bind(this);

        apiKey = AppPreferences.getApiKey();
        Log.d("APIKEY", apiKey);
        initToolbar(apiKey);
        initNavigationView();
        initPathDownloaded();
        initTransparentBackground();

        if (Config.ENABLE_ADMOB) {
            initAdView();
            initAdInterstitial();
        }

        FirebaseCloudMessagingService.subscribe("AutoBackgroundEraser");

        AutoBackgroundEraser.sendEvent(this, "onCreate()");
        AutoBackgroundEraser.checkAppUpdate(new AutoBackgroundEraser.OnAppUpdate() {
            @Override
            public void onUpdate(String updateTitle, String updateDescription, boolean isCancle) {
                DialogAppUpdate dialogAppUpdate = DialogAppUpdate.getInstance();
                dialogAppUpdate.addContent(updateTitle, updateDescription, isCancle);
                dialogAppUpdate.show(getSupportFragmentManager(), "DIALOG UPDATE APP");
            }
        });

        removerPresenter = new RemoverPresenter(this, this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (adView != null) {
            adView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adView != null) {
            adView.destroy();
        }
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    //Button Select Picker of Photo
    @OnClick(R.id.btn_select_picker)
    public void onSelectPhoto() {
        if (start) {
            selectedImage.requestFocus();
            if (backgroundImageFile != null) {
                if (!TextUtils.isEmpty(apiKey)) {
                    removerPresenter.removeByBase64WithReplaceBg(apiKey, base64, backgroundImageFile);
                } else {
                    showDialogWarningAPIKey();
                }
            } else {
                showToast(getResources().getString(R.string.err_message_bg_image_file_null));
            }
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
        cardViewSelectedReplaceBackground.setVisibility(checked ? View.VISIBLE : View.GONE);
        isOptionEnabled = checked;
    }

    //OnClick pick image from gallery for Replace deleted background
    @OnClick(R.id.ib_add_image_replace_bg)
    public void onSelectImageBG() {
        pickImageFromGallery(GALLERY_REQUEST_BG_CODE);
    }


    //OnClick more menu
    @OnClick(R.id.iv_more_menu)
    public void onMoreMenuClick() {
        PopupMenu popupMenu = new PopupMenu(this, imageMoreMenu);
        popupMenu.inflate(R.menu.more_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_popup_menu_save_result) {
                    //do save result of removed background
                    if (resultBitmap != null) {
                        if (ImageUtils.saveBitmap(MainActivity.this, resultBitmap)) {
                            showToast("Success saved to " + AppPreferences.getPath());

                        }
                    }

                }
                return false;
            }
        });
        popupMenu.show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_tutorial:
                startActivity(new Intent(this, TutorialActivity.class));
                closeDrawer();
                break;

            case R.id.nav_set_apikey:
                APIKeySetDialog apiKeySetDialog = new APIKeySetDialog();
                apiKeySetDialog.show(getSupportFragmentManager(), "API KEY");
                apiKeySetDialog.setOnButtonSetAPIListener(new APIKeySetDialog.OnApiKeySet() {
                    @Override
                    public void onSet(String newApiKey) {
                        apiKey = newApiKey;
                        AppPreferences.setApiKey(apiKey);
                        initToolbar(apiKey);
                    }
                });
                closeDrawer();
                break;

            case R.id.nav_rating:
                openGooglePlaystore();
                closeDrawer();
                break;

            case R.id.nav_about_app:
                startActivity(new Intent(this, AboutActivity.class));
                closeDrawer();
                break;
        }
        return false;
    }

    @Override
    public void onMenuSelected(int menu) {
        switch (menu) {
            case MenuDialog.PICK_FROM_GALLERY:
                pickImageFromGallery(GALLERY_REQUEST_CODE);
                break;

            case MenuDialog.PICK_FROM_URL:
                showToast("Not available now !");
                break;
        }

    }


    @Override
    public void onStartProgress() {
        progressBar.setVisibility(View.VISIBLE);
        textViewProgress.setVisibility(View.VISIBLE);

    }

    @Override
    public void onStopProgress() {
        progressBar.setVisibility(View.GONE);
        textViewProgress.setVisibility(View.GONE);
    }

    @Override
    public void onBackgroundRemoved(Bitmap result) {
        showAdInterstitial();
        start = false;
        buttonSelectPicker.setText(textSelectPhoto);
        cardViewResult.setVisibility(View.VISIBLE);
        resultImage.setImageBitmap(result);
        resultBitmap = result;
    }

    @Override
    public void onFailed(String message) {
        start = false;
        buttonSelectPicker.setText(textSelectPhoto);
        textViewProgress.setVisibility(View.VISIBLE);
        textViewProgress.setText(message);
        textViewProgress.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_info_outline_error, 0, 0, 0);
    }


    /*
    onActivityResult (MainActivity class)
     */
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
                            if (!TextUtils.isEmpty(apiKey)) {
                                selectedImage.requestFocus();
                                removerPresenter.removeByBase64(apiKey, base64);

                            } else {
                                showDialogWarningAPIKey();
                            }

                        }
                        break;

                    case GALLERY_REQUEST_BG_CODE:
                        selectedReplaceBackground.setImageURI(uri);
                        backgroundImageFile = new File(getRealPathFromURI(uri));
                        break;
                }

            }
        }
    }


    /*
    init Toolbar (android Toolbar)
     */
    private void initToolbar(String subtitle) {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
            getSupportActionBar().setSubtitle(TextUtils.isEmpty(subtitle) ? "no API Key set !" : "API Key : " + apiKey.substring(0, 3) + "xxxxxx");
        }
    }

    /*
    init NavigationView (android NavigationView)
     */
    private void initNavigationView() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        TextView appVersion = navigationView.getHeaderView(0).findViewById(R.id.tv_app_version);
        appVersion.setText(BuildConfig.VERSION_NAME);
    }


    /*
    Request permissions Camera and External Storage (Android Runtime Permissions)
     */
    private boolean requestPermissions() {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        Permissions.check(this, permissions, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                granted = true;
            }
        });
        return granted;
    }

    /*
    init Path Downloaded (Android set Path Downloaded)
     */
    private void initPathDownloaded() {
        File pathDownloaded = new File(Environment.getExternalStorageDirectory().toString() + "/AutoBGEraser");
        if (requestPermissions()) {
            pathDownloaded.mkdir();
            AppPreferences.savePath(pathDownloaded.getPath());
        }
    }

    /*
    Pick Image from Gallery (Android Implicit Intent)
     */
    private void pickImageFromGallery(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, requestCode);
    }

    /*
    Open Google Playstore (Android Implicit Intent)
     */
    private void openGooglePlaystore() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    /*
    show toast (Android Widget Toast)
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    /*
    show MenuImagePickerDialog (Android Custom Dialog)
     */
    private void showMenuImagePickerDialog() {
        menuDialog = new MenuImagePickerDialog();
        menuDialog.setOnMenuSelect(this);
        menuDialog.showDialog(MainActivity.this, "MENU_IMAGE_PICKER_DIALOG");
    }


    /*
    close Drawer (Android DrawerLayout)
     */
    private void closeDrawer() {
        drawer.closeDrawer(GravityCompat.START);
    }


    /*
    init Transparent background (open source android library Checkerboard)
     */
    private void initTransparentBackground() {
        CheckerboardDrawable drawable = new CheckerboardDrawable.Builder()
                .colorOdd(Color.LTGRAY)
                .colorEven(Color.DKGRAY)
                .size(20)
                .build();
        transparentBackground.setImageDrawable(drawable);
    }

    //get Real Path from Uri
    private String getRealPathFromURI(Uri uri) {
        String result;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            result = uri.getPath();

        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }


    /*
    show Dialog Warning set API Key (Android AlertDialog)
     */
    private void showDialogWarningAPIKey() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.app_name));
        builder.setMessage(getResources().getString(R.string.err_message_no_api_key_set));
        builder.setIcon(getResources().getDrawable(R.drawable.ic_info_outline_error));
        builder.setPositiveButton(getResources().getString(R.string.button_positive_dialog_warning), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startActivity(new Intent(MainActivity.this, TutorialActivity.class));
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.button_negative_dialog_warning), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
    }

    /*
    init AdView
     */
    private void initAdView() {
        adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId(BuildConfig.BANNER);
        linearLayoutAdView.addView(adView);

        adView.loadAd(new AdRequest.Builder().build());
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
            }

            @Override
            public void onAdFailedToLoad(int i) {
                linearLayoutAdView.setVisibility(View.GONE);
                Log.d("DEBUG", "onFailed");
            }

            @Override
            public void onAdLeftApplication() {
            }

            @Override
            public void onAdOpened() {
                linearLayoutAdView.setVisibility(View.VISIBLE);
                Log.d("DEBUG", "onOpened");
            }
        });
    }

    /*
    Init Interstitial Ad
     */
    private void initAdInterstitial() {
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(BuildConfig.INTERSTITIAL);
        interstitialAd.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                interstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
    }

    /*
    show Interstitial Ad
     */
    private void showAdInterstitial() {
        if (interstitialAd != null && interstitialAd.isLoaded()) {
            interstitialAd.show();
        }
    }
}