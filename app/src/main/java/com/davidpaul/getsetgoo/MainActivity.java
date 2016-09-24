package com.davidpaul.getsetgoo;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

//import com.google.android.gms.gcm.GcmTaskService;
//import com.google.android.gms.gcm.TaskParams;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private EditText txtReceiverName;
    private EditText txtReceiverNumber;
    private ScrollView scrollView;
    private FloatingActionButton fabExpand;
    private SupportMapFragment mapFragment;
    boolean click = true;
    private LinearLayout lnHistory;
    private Button btnStartTracking;
    PreferenceHelper preferenceHelper;
    private Toolbar mToolbar;
    //    String receiverName = "";
//    String receiverNumber = "";
//    String receiverMail = "";
    private TextView txtHistory;
    private CoordinatorLayout CoordinatorParent;
    private Switch switchInterval;
    private TextView txtInterval;
    long INTERVAL_TIME_IN_MILLIS = 0;
    //    private CheckBox chkRememberNumber;
    private LinearLayout lnPhoneDetails;
    private LinearLayout lnMailDetails;
    //    private CheckBox chkRememberMail;
    private Switch switchPhone;
    private Switch switchMail;
    private EditText txtReceiverMail;
    private String userName = "";
    private long START_TIME = 0;
    private SimpleDateFormat sdf;
    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout mCollapsingLayout;
    private FloatingActionButton fabContact;

    private boolean mIsImageHidden;
    private RelativeLayout revealMap;
    private LinearLayout lnDestinationDetails;
    private Switch switchDestination;
    private EditText txtDestination;
    private FloatingActionButton fabSetDestination;
    private FloatingActionButton fabEditDestination;
    private String strDestLat = "";
    private String strDestLong = "";
    private String strDestName = "";
    private AdView mAdView;
    private GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!UtilityClass.isNetworkAvailable(this)) {
            android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);
            alertDialog.setCancelable(false);
            alertDialog.setTitle("No Internet!!");
            alertDialog.setMessage("There is no network connection. The maps wont be updated without internet. Do you want to go to settings menu to enable network connection");
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                    startActivity(intent);
                }
            });

            // on pressing cancel button
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();
        }
        setContentView(R.layout.activity_main_test);
        CoordinatorParent = (CoordinatorLayout) findViewById(R.id.CoordinatorParent);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setTitle("GetSetGoo");
        mToolbar.showOverflowMenu();
        setSupportActionBar(mToolbar);
        preferenceHelper = PreferenceHelper.getInstance(getApplicationContext(), "ReceiverPrefs");
        mCollapsingLayout = (CollapsingToolbarLayout) findViewById(R.id.mCollapsingLayout);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.mAppBarLayout);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = new AppBarLayout.Behavior();
        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(AppBarLayout appBarLayout) {
                return false;
            }
        });
        params.setBehavior(behavior);
        AppBarLayout.OnOffsetChangedListener mListener = new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (mCollapsingLayout.getHeight() + verticalOffset < 2 * ViewCompat.getMinimumHeight(mCollapsingLayout)) {
//                    fabExpand.animate().alpha(0).setDuration(200);
                    ViewCompat.animate(fabExpand).scaleY(0).scaleX(0).start();
                } else {
//                    fabExpand.animate().alpha(1).setDuration(200);
                    ViewCompat.animate(fabExpand).scaleY(1).scaleX(1).start();
                }
            }
        };
        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                // Check the LogCat to get your test device ID
                .addTestDevice(android_id)
                .build();
        mAdView.loadAd(adRequest);

        mAppBarLayout.addOnOffsetChangedListener(mListener);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        scrollView.setFocusableInTouchMode(true);
        lnPhoneDetails = (LinearLayout) findViewById(R.id.lnPhoneDetails);
        lnMailDetails = (LinearLayout) findViewById(R.id.lnMailDetails);
        lnDestinationDetails = (LinearLayout) findViewById(R.id.lnDestinationDetails);
        switchPhone = (Switch) findViewById(R.id.switchPhone);
        switchMail = (Switch) findViewById(R.id.switchMail);
        switchDestination = (Switch) findViewById(R.id.switchDestination);
        switchInterval = (Switch) findViewById(R.id.switchInterval);
        switchInterval.setOnCheckedChangeListener(checkedChangeListener);
        switchMail.setOnCheckedChangeListener(checkedChangeListener);
        switchPhone.setOnCheckedChangeListener(checkedChangeListener);
        switchDestination.setOnCheckedChangeListener(checkedChangeListener);
        lnHistory = (LinearLayout) findViewById(R.id.lnHistory);
        txtHistory = (TextView) findViewById(R.id.txtHistory);
        fabExpand = (FloatingActionButton) findViewById(R.id.fabExpand);
        fabContact = (FloatingActionButton) findViewById(R.id.fabContact);
        fabSetDestination = (FloatingActionButton) findViewById(R.id.fabSetDestination);
        fabEditDestination = (FloatingActionButton) findViewById(R.id.fabEditDestination);
        btnStartTracking = (Button) findViewById(R.id.btnStartTracking);
        txtInterval = (TextView) findViewById(R.id.txtInterval);
        txtInterval.setOnClickListener(clickListener);
        sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm a");
//        preferenceHelper.setdata(UtilityClass.KEY_STOP_TRACK_TOGGLE, "");
//        preferenceHelper.setdata(UtilityClass.KEY_SMS_ADDINFO, "");

        Log.e("STOP_TRACK_TOGGLE-->", preferenceHelper.getData(UtilityClass.KEY_STOP_TRACK_TOGGLE));
        Log.e("KEY_SMS_ADDINFO-->", preferenceHelper.getData(UtilityClass.KEY_SMS_ADDINFO));

        userName = preferenceHelper.getData(UtilityClass.KEY_USERNAME);

        if (!(preferenceHelper.getData(UtilityClass.KEY_TRACKING_STATUS).equals("") && preferenceHelper.getData(UtilityClass.KEY_REVEIVER_NAME).equals(null))) {
            if (preferenceHelper.getData(UtilityClass.KEY_TRACKING_STATUS).equals("YES")) {
                btnStartTracking.setText("Stop tracking");
                lnHistory.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.GONE);
            } else {
                btnStartTracking.setText("Start tracking");
                lnHistory.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                String interval = preferenceHelper.getData(UtilityClass.KEY_INTERVAL);
                txtHistory.setText("Hi " + userName + "\nEnjoy your ride!! We will send your location to " + preferenceHelper.getData("_keyName") + " every " + interval + " minutes.");
            }
        }
//        String track_tg = preferenceHelper.getData(UtilityClass.KEY_STOP_TRACK_TOGGLE);
//        String sms_addInfo = preferenceHelper.getData(UtilityClass.KEY_SMS_ADDINFO);
//        if (!track_tg.equals("OFF") && track_tg.equals("")) {
//            preferenceHelper.getData(UtilityClass.KEY_STOP_TRACK_TOGGLE).equals("ON");
//        }
//        if (!sms_addInfo.equals("NO") && !sms_addInfo.equals("")) {
//            preferenceHelper.getData(UtilityClass.KEY_SMS_ADDINFO).equals("YES");
//        }
        btnStartTracking.setOnClickListener(clickListener);

        txtReceiverName = (EditText) findViewById(R.id.txtReceiverName);
        txtReceiverNumber = (EditText) findViewById(R.id.txtReceiverNumber);
        txtReceiverMail = (EditText) findViewById(R.id.txtReceiverMail);
        txtDestination = (EditText) findViewById(R.id.txtDestination);
        revealMap = (RelativeLayout) findViewById(R.id.revealMap);
        getNameFromPreference();
        initializeMap();
//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
        hide(revealMap);
//            }
//        }, 500);
        fabExpand.setOnClickListener(clickListener);
        fabContact.setOnClickListener(clickListener);
        fabSetDestination.setOnClickListener(clickListener);
        fabEditDestination.setOnClickListener(clickListener);
        strDestLat = preferenceHelper.getData(UtilityClass.KEY_DEST_LATITUDE);
        strDestLong = preferenceHelper.getData(UtilityClass.KEY_DEST_LONGITUDE);
        strDestName = preferenceHelper.getData(UtilityClass.KEY_DEST_NAME);
        if (strDestLat != null && !strDestLat.equals("") && strDestLong != null && !strDestLong.equals("")) {
            fabSetDestination.setVisibility(View.GONE);
            fabEditDestination.setVisibility(View.VISIBLE);
            txtDestination.setText(strDestName);
            txtDestination.setEnabled(false);
            txtDestination.setFocusable(false);

        } else {
            fabSetDestination.setVisibility(View.VISIBLE);
            txtDestination.setEnabled(true);
            txtDestination.setFocusable(true);
            fabEditDestination.setVisibility(View.GONE);
        }
        new UtilityClass(this).setViewGroupTypeface(CoordinatorParent, UtilityClass.fontLite);
    }

    private void getNameFromPreference() {
        if (!(preferenceHelper.getData(UtilityClass.KEY_REVEIVER_NAME).equals("") && preferenceHelper.getData(UtilityClass.KEY_REVEIVER_NAME).equals(null))) {
            txtReceiverName.setText(preferenceHelper.getData(UtilityClass.KEY_REVEIVER_NAME));
        }
    }

    private void getNumberFromPreference() {

        if (!(preferenceHelper.getData(UtilityClass.KEY_REVEIVER_NUMBER).equals("") && preferenceHelper.getData(UtilityClass.KEY_REVEIVER_NUMBER).equals(null))) {
            txtReceiverNumber.setText(preferenceHelper.getData(UtilityClass.KEY_REVEIVER_NUMBER));
//            chkRememberNumber.setChecked(true);
        }

    }

    private void getMailFromPreference() {

        if (!(preferenceHelper.getData(UtilityClass.KEY_REVEIVER_MAIL).equals("") && preferenceHelper.getData(UtilityClass.KEY_REVEIVER_MAIL).equals(null))) {
            txtReceiverMail.setText(preferenceHelper.getData(UtilityClass.KEY_REVEIVER_MAIL));
//            chkRememberMail.setChecked(true);
        }
    }

    private void setDestinationToPreference(String latitude, String longitude, String destName) {
        preferenceHelper.setdata(UtilityClass.KEY_DEST_NAME, destName);
        preferenceHelper.setdata(UtilityClass.KEY_DEST_LATITUDE, latitude);
        preferenceHelper.setdata(UtilityClass.KEY_DEST_LONGITUDE, longitude);
    }

    private void setStartingPointToPreference(String latitude, String longitude) {
        preferenceHelper.setdata(UtilityClass.KEY_START_LATITUDE, latitude);
        preferenceHelper.setdata(UtilityClass.KEY_START_LONGITUDE, longitude);
    }

    private void getDestinationToPreference() {
        if (!(preferenceHelper.getData(UtilityClass.KEY_DEST_NAME).equals("") && preferenceHelper.getData(UtilityClass.KEY_DEST_NAME).equals(null))) {
            txtDestination.setText(preferenceHelper.getData(UtilityClass.KEY_DEST_NAME));
//            chkRememberMail.setChecked(true);
        }
    }

    private void hide(final View view) {
        Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        animation.setDuration(5000);
        view.setAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
//                    view.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//
//                        }
//                    }, 500);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animation.start();
    }

    private void initializeMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        setMap(googleMap);
    }

    public void setMap(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        mMap.setMyLocationEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.setPadding(0, (int) getResources().getDimension(R.dimen._35sdp), 0, 0);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        // create class object
        try {
            gps = new GPSTracker(this);

            // check if GPS enabled
            if (gps.canGetLocation()) {
                LatLng latlang;
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();
                latlang = new LatLng(latitude, longitude);
                String markerTitle = "You are here";
                String markerSnippet = "";
                if (preferenceHelper.getData(UtilityClass.KEY_TRACKING_STATUS).equals("yes")) {
                    markerTitle = "Starting Point";
                    markerSnippet = "Tracking started from here";
                }
                mMap.addMarker(new MarkerOptions()
                        .position(latlang)
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_marker))
                        .title(markerTitle)
                        .snippet(markerSnippet));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlang, 10));
                String startTime = preferenceHelper.getData(UtilityClass.KEY_START_DATE_TIME);
                String stopTime = preferenceHelper.getData(UtilityClass.KEY_STOP_DATE_TIME);
                if (!startTime.equals("") && !stopTime.equals("")) {
                    DBhelper dBhelper = new DBhelper(MainActivity.this);
                    ArrayList<LatLng> latLngs = dBhelper.getLATLANGlist(startTime, stopTime);
                    for (LatLng location : latLngs) {
                        mMap.addMarker(new MarkerOptions()
                                .position(location)
                                .snippet("Location Sent")
                                .title("SMS/Mail sent from this location")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.sent_marker))
                                .title("This location was sent to " + preferenceHelper.getData(UtilityClass.KEY_REVEIVER_NAME)));
                    }
                }

                String str_startLat = preferenceHelper.getData(UtilityClass.KEY_START_LATITUDE);
                String str_startLong = preferenceHelper.getData(UtilityClass.KEY_START_LONGITUDE);
                if (str_startLat != null && !str_startLat.equals("") && str_startLong != null && !str_startLong.equals("")) {
                    double startLat = Double.parseDouble(str_startLat);
                    double startLong = Double.parseDouble(str_startLong);
                    LatLng latlang1 = new LatLng(startLat, startLong);
                    mMap.addMarker(new MarkerOptions()
                            .position(latlang1)
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
//                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_marker))
                            .title("Starting point")
                            .snippet("You started here"));
                }
                if (strDestLat != null && !strDestLat.equals("") && strDestLong != null && !strDestLong.equals("")) {
                    double destLat = Double.parseDouble(strDestLat);
                    double destLong = Double.parseDouble(strDestLong);
                    LatLng latlang1 = new LatLng(destLat, destLong);
                    mMap.addMarker(new MarkerOptions()
                            .position(latlang1)
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
//                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_marker))
                            .title("Your destination")
                            .snippet(strDestName));
                }
                // \n is for new line
//            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            } else {
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gps.showSettingsAlert();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    class MyLocationService extends GcmTaskService {
//
//        public void onInitializeTasks() {
//
//        }
//
//        @Override
//        public int onRunTask(TaskParams taskParams) {
//            return 0;
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),
                    android.R.anim.fade_in);
            Animation animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(),
                    android.R.anim.fade_out);
            animFadeIn.setDuration(800);
            animFadeOut.setDuration(800);
            switch (buttonView.getId()) {
                case R.id.switchInterval:
                    if (isChecked) {
                        txtInterval.setVisibility(View.VISIBLE);
                        txtInterval.startAnimation(animFadeIn);
                        switchInterval.setText("");
                    } else {
                        switchInterval.setText("Set Time Interval");
                        txtInterval.setText("Tap to set Time Interval");
                        txtInterval.setVisibility(View.GONE);
                    }
                    break;
                case R.id.switchPhone:
                    if (isChecked) {
                        getNumberFromPreference();
                        switchPhone.setText("");
                        lnPhoneDetails.setVisibility(View.VISIBLE);
                        lnPhoneDetails.startAnimation(animFadeIn);
                    } else {
                        switchPhone.setText("Recipient Number");
                        txtReceiverNumber.setText("");
                        lnPhoneDetails.setVisibility(View.GONE);
                    }
                    break;
                case R.id.switchMail:
                    if (isChecked) {
                        getMailFromPreference();
                        switchMail.setText("");
                        lnMailDetails.setVisibility(View.VISIBLE);
                        lnMailDetails.startAnimation(animFadeIn);
                    } else {
                        switchMail.setText("Recipient Mail");
                        txtReceiverMail.setText("");
                        lnMailDetails.setVisibility(View.GONE);
                    }
                    break;
                case R.id.switchDestination:
                    if (isChecked) {
                        getDestinationToPreference();
                        switchDestination.setText("");
                        lnDestinationDetails.setVisibility(View.VISIBLE);
                        lnDestinationDetails.startAnimation(animFadeIn);
                    } else {
                        switchDestination.setText("Set Destination");
                        txtDestination.setText("");
                        lnDestinationDetails.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }

        }
    };

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnStartTracking:
                    funTracking(v);
                    break;

                case R.id.txtInterval:
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(MainActivity.this, TimePickerDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            txtInterval.setText(selectedHour + ":" + selectedMinute);
                            INTERVAL_TIME_IN_MILLIS = TimeUnit.HOURS.toMillis(selectedHour) + TimeUnit.MINUTES.toMillis(selectedMinute);
//                            Toast.makeText(MainActivity.this, INTERVAL_TIME_IN_MILLIS + "", Toast.LENGTH_LONG).show();
                            Log.e("INTERVAL_TIME-", "INTERVAL_TIME_IN_MILLIS--" + INTERVAL_TIME_IN_MILLIS);
                        }
                    }, hour, minute, true);//Yes 24 hour time
//                    mTimePicker.setTitle("Set Hours and Minutes");
                    LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.time_picker_title, null);
                    TextView txtTitle = (TextView) dialogView.findViewById(R.id.txtTitle);
                    txtTitle.setText("Set Hours and Minutes");
                    mTimePicker.setCustomTitle(dialogView);
                    mTimePicker.show();
                    break;

//                case R.id.chkRememberNumber:
//                    receiverName = txtReceiverName.getText().toString();
//                    receiverNumber = txtReceiverNumber.getText().toString();
//                    if (receiverNumber.equals("")) {
//                        chkRememberNumber.setChecked(false);
//                        Snackbar.make(v, "What to remember???", Snackbar.LENGTH_LONG).show();
//                    } else {
//                        if (chkRememberNumber.isChecked()) {
//                            preferenceHelper.setdata("_keyName", receiverName);
//                            preferenceHelper.setdata("_keyNumber", receiverNumber);
//                        } else {
//                            preferenceHelper.clearData();
//                        }
//                    }
//                    break;
//
//                case R.id.chkRememberMail:
//                    receiverMail = txtReceiverMail.getText().toString();
//                    if (receiverMail.equals("")) {
//                        chkRememberMail.setChecked(false);
//                        Snackbar.make(v, "What to remember???", Snackbar.LENGTH_LONG).show();
//                    } else {
//                        if (chkRememberMail.isChecked()) {
//                            preferenceHelper.setdata("_keyMail", receiverMail);
//                        } else {
//                            preferenceHelper.clearData();
//                        }
//                    }
//                    break;

                case R.id.fabExpand:

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        v.setTransitionName("fabTransition");
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, v, v.getTransitionName());
                        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                        startActivity(intent, options.toBundle());
                    } else {
                        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                        startActivity(intent);
                    }

                    break;
                case R.id.fabContact:

                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, 111);
                    break;
                case R.id.fabSetDestination:
                    String destination = txtDestination.getText().toString();
                    List<Address> addrList = null;
                    if (!destination.equals("") || destination != null) {
                        Geocoder geocoder = new Geocoder(MainActivity.this);
                        try {
                            addrList = geocoder.getFromLocationName(destination, 1);
                            Address address = addrList.get(0);
                            if (address != null) {
                                LatLng latlang = new LatLng(address.getLatitude(), address.getLongitude());
                                setDestinationToPreference("" + address.getLatitude(), "" + address.getLongitude(), destination);
                                Snackbar.make(v, "Destination set successfully", Snackbar.LENGTH_SHORT).show();
                                fabEditDestination.setVisibility(View.VISIBLE);
                                txtDestination.setEnabled(false);
                                txtDestination.setFocusable(false);
                                fabSetDestination.setVisibility(View.GONE);
                                strDestLat = preferenceHelper.getData(UtilityClass.KEY_DEST_LATITUDE);
                                strDestLong = preferenceHelper.getData(UtilityClass.KEY_DEST_LONGITUDE);
                                setMap(mMap);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Snackbar.make(v, "Invalid destination!!", Snackbar.LENGTH_SHORT).show();
                        }

                    } else {
                        Snackbar.make(v, "Please specify your destination", Snackbar.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.fabEditDestination:
                    txtDestination.setEnabled(true);
                    txtDestination.setFocusable(true);
                    txtDestination.setClickable(true);
                    txtDestination.setFocusableInTouchMode(true);
                    fabEditDestination.setVisibility(View.GONE);
                    fabSetDestination.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (111):
                if (resultCode == Activity.RESULT_OK) {

                    Uri contactData = data.getData();
                    Cursor c = managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                        String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                    null, null);
                            phones.moveToFirst();
                            String cNumber = phones.getString(phones.getColumnIndex("data1"));
                            System.out.println("number is:" + cNumber);
                            txtReceiverNumber.setText(cNumber);
                        }
                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));


                    }
                }
                break;
        }
    }

    private void funTracking(View v) {
        String name = txtReceiverName.getText().toString();
        String number = txtReceiverNumber.getText().toString();
        String mail = txtReceiverMail.getText().toString();

//                PeriodicTask task = new PeriodicTask.Builder().setService(GPSTracker.class).setTag("periodic").setPeriod(30).setPersisted(true).build();
        if (btnStartTracking.getText().toString().equalsIgnoreCase("Start tracking")) {
            if ((!mail.equals("") || !number.equals("")) && INTERVAL_TIME_IN_MILLIS != 0 && (switchPhone.isChecked() || switchMail.isChecked())) {
//                preferenceHelper.clearData();
                Calendar c = Calendar.getInstance();
                START_TIME = c.getTimeInMillis();
                String StartdateTime = sdf.format(Calendar.getInstance().getTime());

                preferenceHelper.setdata("_keyName", name);
                preferenceHelper.setdata("_keyNumber", number);
                preferenceHelper.setdata("_keyMail", mail);
                preferenceHelper.setdata("_keyStartDateTime", StartdateTime);
                preferenceHelper.setdata("_keyInterval", TimeUnit.MILLISECONDS.toMinutes(INTERVAL_TIME_IN_MILLIS) + "");
                Log.e("StartdateTime-->", StartdateTime);
                String Track_TG = preferenceHelper.getData(UtilityClass.KEY_STOP_TRACK_TOGGLE);
                String alertMsg = "";
                if (Track_TG.equals("ON")) {
                    alertMsg = "You have enabled tracking settings. Tracking will be automatically stopped when you are nearer to your destination.";
                } else {
                    alertMsg = "Please Stop tracking once you reach the destination. If you dont stop tracking, SMS will be sent and you may be charged. To avoid this please go to settings and enable tracking settings Thanks.";
                }

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Warning!!!");
                alertDialog.setMessage("SMS may incur charges. Please check whether you have sufficient balance. Are you sure you want the start tracking?");
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(MainActivity.this, BroadReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, START_TIME/*System.currentTimeMillis()*/ + INTERVAL_TIME_IN_MILLIS, INTERVAL_TIME_IN_MILLIS /*30 * 1000*/, pendingIntent);
                        lnHistory.setVisibility(View.VISIBLE);
                        String interval = preferenceHelper.getData(UtilityClass.KEY_INTERVAL);
                        txtHistory.setText("Hi " + userName + "\nEnjoy your ride!! We will send your location to " + preferenceHelper.getData("_keyName") + " every " + interval + " minutes. You can close the app if you want to.");
                        scrollView.setVisibility(View.GONE);
                        btnStartTracking.setText("Stop tracking");
                        preferenceHelper.setdata("_keyTrackingON", "YES");
                        setStartingPointToPreference("" + gps.getLatitude(), "" + gps.getLongitude());
                        setMap(mMap);
                        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(MainActivity.this);
                        alertDialog.setCancelable(false);
                        alertDialog.setTitle("Alert!!!");
                        alertDialog.setMessage("Please Stop tracking once you reach the destination. If you dont stop tracking, SMS will be sent and you may be charged. Thanks. Enjoy your Ride");
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        alertDialog.show();
                    }
                });
                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            } else {
                Snackbar.make(v, "Please fill up all the details", Snackbar.LENGTH_SHORT).show();
            }
        } else {
            btnStartTracking.setText("Start tracking");
            lnHistory.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
            preferenceHelper.setdata("_keyTrackingON", "NO");
            String StopdateTime = sdf.format(Calendar.getInstance().getTime());
            preferenceHelper.setdata("_keyStopdateTime", StopdateTime);
            Log.e("StopdateTime-->", StopdateTime);
            Intent intent = new Intent(MainActivity.this, BroadReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case R.id.action_history:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    fabExpand.setTransitionName("fabTransition");
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, fabExpand, fabExpand.getTransitionName());
                    Intent intent = new Intent(MainActivity.this, LocationHistory.class);
                    startActivity(intent, options.toBundle());
                } else {
                    startActivity(new Intent(MainActivity.this, LocationHistory.class));
                }
                break;

            default:
                break;
        }
        return true;
    }

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
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
}
