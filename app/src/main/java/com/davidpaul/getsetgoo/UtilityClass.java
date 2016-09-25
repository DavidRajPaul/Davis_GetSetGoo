package com.davidpaul.getsetgoo;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by David Paul on 8/14/2016.
 */
public class UtilityClass {
    static Context context;


    static String KEY_REVEIVER_NAME = "_keyName";
    static String KEY_REVEIVER_NUMBER = "_keyNumber";
    static String KEY_REVEIVER_MAIL = "_keyMail";
    static String KEY_INTERVAL = "_keyInterval";
    static String KEY_START_DATE_TIME = "_keyStartDateTime";
    static String KEY_STOP_DATE_TIME = "_keyStopdateTime";
    static String KEY_TRACKING_STATUS = "_keyTrackingON";
    static String KEY_USERNAME = "_keyUserName";
    static String KEY_DEST_NAME = "_keyDestName";
    static String KEY_DEST_LATITUDE = "_keyDestLat";
    static String KEY_DEST_LONGITUDE = "_keyDestLong";
    static String KEY_STOP_AT_THREE_KM = "_keyStopOnSameLocation";
    static String KEY_SMS_ADDINFO = "_keyAddInfoinSMS";
    static String KEY_STOP_TRACK_TOGGLE = "_keyTrackStopToggle";
    static String KEY_START_NAME = "_keyStartName";
    static String KEY_START_LATITUDE = "_keyStartLat";
    static String KEY_START_LONGITUDE = "_keyStartLong";
    public static boolean mIsPremium = false;


    UtilityClass(Context context) {
        this.context = context;
    }

    //    public static final String fontHavy = "HelveticaNeueHv.ttf";
    public static final String fontItalic = "Roboto-LightItalic.ttf";
    //    public static final String fontLite = "cambria.ttf";
    public static final String fontLite = "Roboto-Light.ttf";

    public static void CheckNull() {

    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void setViewGroupTypeface(ViewGroup container, String fontName) {

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), fontName);

        final int children = container.getChildCount();

        for (int i = 0; i < children; i++) {
            View child = container.getChildAt(i);

            if (child instanceof TextView) {
                setTextViewTypeface((TextView) child, typeface);
            } else if (child instanceof ViewGroup) {
                setViewGroupTypeface((ViewGroup) child, fontName);
            }
        }
    }

    private void setTextViewTypeface(TextView textView, Typeface typeface) {
        textView.setTypeface(typeface);

    }

    public void setBoldFont(TextView textView) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/"
                + fontItalic);

        textView.setTypeface(typeface);
    }

    public void setLiteFont(TextView textView) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/"
                + fontLite);
        textView.setTypeface(typeface);
    }

    public static void fn_setImageDrawable(Context context, ImageView imageView, int drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //>= API 21
            imageView.setImageDrawable(context.getResources().getDrawable(drawable, context.getTheme()));
        } else {
            imageView.setImageDrawable(context.getResources().getDrawable(drawable));
        }
    }

    public static void showSnackbar(View v, String message) {
        Snackbar.make(v, message, Snackbar.LENGTH_SHORT).show();
    }

    public static void ShowAlertwithOK(Context ctx, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctx);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }
}
