package com.davidpaul.getsetgoo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private RelativeLayout rlContainer;
    private RelativeLayout rlFab;
    private RelativeLayout rlMapContainer;
    private PreferenceHelper preferenceHelper;
    private String strDestLat = "";
    private String strDestLong = "";
    private String strDestName = "";
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.scene_transition_main));
        }
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        rlContainer = (RelativeLayout) findViewById(R.id.rlContainer);
        rlMapContainer = (RelativeLayout) findViewById(R.id.rlMapContainer);
        rlFab = (RelativeLayout) findViewById(R.id.rlFab);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rlMapContainer.postDelayed(new Runnable() {
                @Override
                public void run() {
                    show(rlMapContainer);
                }
            }, 410);
        } else {
            rlMapContainer.setVisibility(View.VISIBLE);
        }
        preferenceHelper = PreferenceHelper.getInstance(getApplicationContext(), "ReceiverPrefs");


        strDestLat = preferenceHelper.getData(UtilityClass.KEY_DEST_LATITUDE);
        strDestLong = preferenceHelper.getData(UtilityClass.KEY_DEST_LONGITUDE);
        strDestName = preferenceHelper.getData(UtilityClass.KEY_DEST_NAME);

        initializeInterstitialAD();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            hide(rlMapContainer);
        } else {
            rlMapContainer.setVisibility(View.GONE);
        }
        super.onBackPressed();
    }
    private void initializeInterstitialAD() {
        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
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
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.isIndoorEnabled();
        mMap.setBuildingsEnabled(true);
        mMap.isTrafficEnabled();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        try {
            GPSTracker gps = new GPSTracker(this);

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
                    DBhelper dBhelper = new DBhelper(MapsActivity.this);
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
//        // create class object
//        GPSTracker gps = new GPSTracker(this);
//        // check if GPS enabled
//        if (gps.canGetLocation()) {
//            double latitude = gps.getLatitude();
//            double longitude = gps.getLongitude();
//            LatLng latlang = new LatLng(latitude, longitude);
//            mMap.addMarker(new MarkerOptions().position(latlang).title("You are here"));
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlang, 15));
//        } else {
//            gps.showSettingsAlert();
//        }
    }

    // To reveal a previously invisible view using this effect:
    private void show(final View view) {
        // get the center for the clipping circle
        int cx = (view.getLeft() + view.getRight()) / 2;
        int cy = (view.getTop() + view.getBottom()) / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(view.getWidth(), view.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy,
                0, finalRadius);
        anim.setDuration(800);

        // make the view visible and start the animation
        view.setVisibility(View.VISIBLE);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.VISIBLE);
            }
        });

        anim.start();
    }

    // To hide a previously visible view using this effect:
    private void hide(final View view) {

        // get the center for the clipping circle
        int cx = (view.getLeft() + view.getRight()) / 2;
        int cy = (view.getTop() + view.getBottom()) / 2;

        // get the initial radius for the clipping circle
        int initialRadius = view.getWidth();

        // create the animation (the final radius is zero)

        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy,
                initialRadius, 0);
        anim.setDuration(800);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.GONE);
            }
        });

        // start the animation
        anim.start();
    }

}
