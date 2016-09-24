package com.davidpaul.getsetgoo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class StartupNavigation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckGPSandNetwork();
    }

    public void CheckGPSandNetwork() {
        GPSTracker gps = new GPSTracker(this);
        if (gps.canGetLocation()&&UtilityClass.isNetworkAvailable(StartupNavigation.this)) {
            startActivity(new Intent(StartupNavigation.this, SplashActivity.class));
            finish();
        } else if(!gps.canGetLocation()){
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setCancelable(false);
            // Setting Dialog Title
            alertDialog.setTitle("Enable GPS settings");

            // Setting Dialog Message
            alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

            // On pressing Settings button
            alertDialog.setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                    dialog.cancel();
                }
            });

            // on pressing cancel button
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent= new Intent(StartupNavigation.this,StartupNavigation.class);
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                }
            });
            alertDialog.show();
        }else if(!UtilityClass.isNetworkAvailable(StartupNavigation.this)){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setCancelable(false);
            alertDialog.setTitle("Connection Unavailable");
            alertDialog.setMessage("Please check your internet connection. Please enable your data connection from settings.");
            alertDialog.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent= new Intent(StartupNavigation.this,StartupNavigation.class);
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                }
            });
            alertDialog.show();

        }
    }
}
