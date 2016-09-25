package com.davidpaul.getsetgoo;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.Calendar;

import inapp_util.IabHelper;
import inapp_util.IabResult;
import inapp_util.Purchase;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private CoordinatorLayout CoordinatorParent;
    private CollapsingToolbarLayout mCollapsingLayout;
    private AppBarLayout mAppBarLayout;
    private ImageView toolbarImage;
    private LinearLayout lnProfile;
    Context context;
    private LinearLayout lnReceiverProfile;
    private LinearLayout lnSmsChar;
    private LinearLayout lnTrackSettings;
    private LinearLayout lnPurchase;
    private LinearLayout lnRate;
    private TextView lbltrackinfo;
    private TextView lblSmsinfo;
    private Switch switchtrack;
    private Switch switchSmsLength;
    private PreferenceHelper preferenceHelper;
    private LinearLayout lnContact;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private String android_id = "";
    static final String ITEM_SKU = "android.test.purchased";
    private IabHelper mHelper;
    private FrameLayout frameLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_settings);
        preferenceHelper = PreferenceHelper.getInstance(getApplicationContext(), "ReceiverPrefs");
        CoordinatorParent = (CoordinatorLayout) findViewById(R.id.CoordinatorParent);
        mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setTitle("Settings");
        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mCollapsingLayout = (CollapsingToolbarLayout) findViewById(R.id.mCollapsingLayout);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.mAppBarLayout);
        toolbarImage = (ImageView) findViewById(R.id.toolbarImage);

        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                // Check the LogCat to get your test device ID
                .addTestDevice(android_id)
                .build();
        mAdView.loadAd(adRequest);
        initializeInterstitialAD();
        Calendar cal = Calendar.getInstance();
        int timeOfDay = cal.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 0 && timeOfDay < 12) {
            UtilityClass.fn_setImageDrawable(context, toolbarImage, R.drawable.morning);//"Good Morning"
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            UtilityClass.fn_setImageDrawable(context, toolbarImage, R.drawable.afternoon);//"Good Afternoon"
        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            UtilityClass.fn_setImageDrawable(context, toolbarImage, R.drawable.evening);//"Good Evening"
        } else if (timeOfDay >= 21 && timeOfDay < 24) {
            UtilityClass.fn_setImageDrawable(context, toolbarImage, R.drawable.night); //"Good Night";
        }
        String base64EncodedPublicKey = getResources().getString(R.string.base64RSA);

        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d("IAB startSetup", "In-app Billing setup failed: " +
                            result);
                } else {
                    Log.d("IAB startSetup", "In-app Billing is set up OK");
                }
            }
        });
        lnProfile = (LinearLayout) findViewById(R.id.lnProfile);
        lnReceiverProfile = (LinearLayout) findViewById(R.id.lnReceiverProfile);
        lnSmsChar = (LinearLayout) findViewById(R.id.lnSmsChar);
        lnTrackSettings = (LinearLayout) findViewById(R.id.lnTrackSettings);
        lnPurchase = (LinearLayout) findViewById(R.id.lnPurchase);
        lnRate = (LinearLayout) findViewById(R.id.lnRate);
        lnContact = (LinearLayout) findViewById(R.id.lnContact);

        lbltrackinfo = (TextView) findViewById(R.id.lbltrackinfo);
        lblSmsinfo = (TextView) findViewById(R.id.lblSmsinfo);

        switchtrack = (Switch) findViewById(R.id.switchtrack);
        switchSmsLength = (Switch) findViewById(R.id.switchSmsLength);

        frameLock = (FrameLayout) findViewById(R.id.frameLock);

        if (preferenceHelper.getData(UtilityClass.KEY_SMS_ADDINFO).equals("YES")) {
            switchSmsLength.setChecked(true);
        }
        if (preferenceHelper.getData(UtilityClass.KEY_STOP_TRACK_TOGGLE).equals("ON")) {
            switchtrack.setChecked(true);
        }

        lnProfile.setOnClickListener(clickListener);
        lnReceiverProfile.setOnClickListener(clickListener);
        lnSmsChar.setOnClickListener(clickListener);
        lnTrackSettings.setOnClickListener(clickListener);
        lnPurchase.setOnClickListener(clickListener);
        lnRate.setOnClickListener(clickListener);
        lnContact.setOnClickListener(clickListener);
        switchtrack.setOnCheckedChangeListener(checkedChangeListener);
        switchSmsLength.setOnCheckedChangeListener(checkedChangeListener);

        new UtilityClass(this).setViewGroupTypeface(CoordinatorParent, UtilityClass.fontLite);

    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.lnProfile:
                    final EditText inputUsername = new EditText(context);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(10, 0, 10, 0);
                    inputUsername.setText(preferenceHelper.getData(UtilityClass.KEY_USERNAME));
                    inputUsername.setLayoutParams(lp);
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setTitle("Edit your Profile");
                    alertDialog.setView(inputUsername);
                    alertDialog.setMessage("Are you sure you want to change the profile name");
                    alertDialog.setNegativeButton("Change", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            preferenceHelper.setdata(UtilityClass.KEY_USERNAME, inputUsername.getText().toString());
                            UtilityClass.showSnackbar(CoordinatorParent, "Profile name changed successfully");
                        }
                    });
                    alertDialog.show();
                    break;
                case R.id.lnReceiverProfile:
                    final EditText input = new EditText(context);
                    LinearLayout.LayoutParams ln = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    ln.setMargins(10, 0, 10, 0);
                    input.setText(preferenceHelper.getData(UtilityClass.KEY_REVEIVER_NAME));
                    input.setLayoutParams(ln);
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Edit receiver profile");
                    alert.setView(input);
                    alert.setMessage("Are you sure you want to change the receiver profile name");
                    alert.setNegativeButton("Change", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            preferenceHelper.setdata(UtilityClass.KEY_REVEIVER_NAME, input.getText().toString());
                            UtilityClass.showSnackbar(CoordinatorParent, "Receive profile name changed successfully");
                        }
                    });
                    alert.show();
                    break;
                case R.id.lnSmsChar:

                    break;
                case R.id.lnTrackSettings:

                    break;
                case R.id.lnPurchase:
                    android.app.AlertDialog.Builder purchaseAlert = new android.app.AlertDialog.Builder(context);
                    purchaseAlert.setCancelable(false);
                    purchaseAlert.setTitle("Upgrade to premium Version");
                    purchaseAlert.setMessage("The Premium Version is Ad free and also will unlock few useful features. Do you want to upgrade?");
                    purchaseAlert.setPositiveButton("Upgrade", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            mHelper.launchPurchaseFlow(SettingsActivity.this, ITEM_SKU, 10001,
                                    mPurchaseFinishedListener, "mypurchasetoken");
                        }
                    });

                    // on pressing cancel button
                    purchaseAlert.setNegativeButton("Not now", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    purchaseAlert.show();
                    break;
                case R.id.lnRate:

                    break;
                case R.id.lnContact:
                    UtilityClass.ShowAlertwithOK(context, "Contact us", " Please share your valuable feedback with us. This will help us to improve this app,\nMail us at feedback.DavApps@gmail.com");
                    break;
                default:
                    break;
            }
        }
    };
    CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {

                case R.id.switchtrack:
                    if (isChecked) {
                        lbltrackinfo.setText("Tracking will automatically stop when you are less than 3 kilometers from your destination");
                        preferenceHelper.setdata(UtilityClass.KEY_STOP_TRACK_TOGGLE, "ON");
                        Log.e("KEY_STOP_TRACK_TOGGLE-->", preferenceHelper.getData(UtilityClass.KEY_STOP_TRACK_TOGGLE));
                    } else {
                        lbltrackinfo.setText("Tracking will not be stopped even, if you reach your destination. Please manually stop tracking");
                        preferenceHelper.setdata(UtilityClass.KEY_STOP_TRACK_TOGGLE, "OFF");
                        Log.e("KEY_STOP_TRACK_TOGGLE-->", preferenceHelper.getData(UtilityClass.KEY_STOP_TRACK_TOGGLE));
                    }
                    break;
                case R.id.switchSmsLength:
                    if (isChecked) {
                        lblSmsinfo.setText("Less information will be set through SMS");
                        preferenceHelper.setdata(UtilityClass.KEY_SMS_ADDINFO, "YES");
                        Log.e("KEY_SMS_ADDINFO-->", preferenceHelper.getData(UtilityClass.KEY_SMS_ADDINFO));
                    } else {
                        lblSmsinfo.setText("Additional information will be set through SMS");
                        preferenceHelper.setdata(UtilityClass.KEY_SMS_ADDINFO, "NO");
                        Log.e("KEY_SMS_ADDINFO-->", preferenceHelper.getData(UtilityClass.KEY_SMS_ADDINFO));
                    }
                    break;
                case R.id.frameLock:
                    UtilityClass.ShowAlertwithOK(context, "Features Locked!!", "Buy the premium version to unlock these features.");
                    break;

                default:
                    break;
            }

        }
    };

    private void initializeInterstitialAD() {
        mInterstitialAd = new InterstitialAd(this);
        // set the ad unit ID
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                // Check the LogCat to get your test device ID
                .addTestDevice(android_id)
                .build();
        // Load ads into Interstitial Ads
        mInterstitialAd.loadAd(adRequest);
        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                showInterstitial();
            }
        });
    }

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result,
                                          Purchase purchase) {
            if (result.isFailure()) {
                Toast.makeText(context, "Error purchasing: " + result, Toast.LENGTH_LONG).show();
                return;
            } else if (purchase.getSku().equals(ITEM_SKU)) {
//                consumeItem();
                Toast.makeText(context, "Thank you for upgrade", Toast.LENGTH_LONG).show();
                UtilityClass.mIsPremium = true;
                frameLock.setVisibility(View.GONE);
//                setUserStatus(true);
//                upgradeDialog.dismiss();
            }

        }
    };

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
        if (UtilityClass.mIsPremium) {
            frameLock.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
        super.onDestroy();
    }

}
