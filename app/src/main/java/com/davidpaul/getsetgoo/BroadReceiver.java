package com.davidpaul.getsetgoo;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by David Paul on 8/14/2016.
 */
public class BroadReceiver extends BroadcastReceiver {
    private PreferenceHelper preferenceHelper;
    private Context context;
    double latitude = 0;
    double longitude = 0;
    String address = "";
    String city = "";
    String state = "";
    String country = "";
    String postalCode = "";
    String knownName = "";
    String currentTime = "";
    List<String> arrayList = new ArrayList<String>();
    private GPSTracker gps;
    private DBhelper dBhelper;
    private String canMail = "No";
    private String canText = "No";
    private String subject = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        dBhelper = new DBhelper(context);
        gps = new GPSTracker(context);
        preferenceHelper = PreferenceHelper.getInstance(context, "ReceiverPrefs");
        // check if GPS enabled
        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            SimpleDateFormat sdf;
            if (preferenceHelper.getData(UtilityClass.KEY_STOP_AT_THREE_KM).equals("NEAR")) {
                sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm a");
                preferenceHelper.setdata(UtilityClass.KEY_TRACKING_STATUS, "NO");
                String StopdateTime = sdf.format(Calendar.getInstance().getTime());
                preferenceHelper.setdata(UtilityClass.KEY_STOP_DATE_TIME, StopdateTime);
                preferenceHelper.setdata(UtilityClass.KEY_STOP_AT_THREE_KM, "");
                Log.e("StopdateTime on NEAR-->", StopdateTime);

                Intent i = new Intent(context, BroadReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
                Log.e("Alarm Manager status-->", "--STOPPED--");
            } else {
                getAddressFromLATLANG();
            }
        }


        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.reached_icon);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.reached_icon_small)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Location updated. Please check location history for more details. Dont forget to stop tracking once you reach your destination because sms will be sent unless you stop tracking."))
                .setLargeIcon(largeIcon)
                .setColor(Color.parseColor("#607D8B"))
                .setContentTitle("Reached")
                .setContentText("Your coordinates is - \nLatitude: " + latitude + "\nLongitude: " + longitude);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent myIntent = new Intent(context, StartupNavigation.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                myIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true);
        notificationManager.notify(1, mBuilder.build());
//        notificationManager.cancelAll();
        gps.stopSelf();
//        isRunning = isServiceRunning(GPSTracker.class,context);
//        Toast.makeText(context, "Service Status "+isRunning, Toast.LENGTH_LONG).show();
    }

    public void getAddressFromLATLANG() {
//        Geocoder geocoder;
//        List<Address> addresses;
//        geocoder = new Geocoder(context, Locale.getDefault());

        try {
//            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//            city = addresses.get(0).getLocality();
//            state = addresses.get(0).getAdminArea();
//            country = addresses.get(0).getCountryName();
//            postalCode = addresses.get(0).getPostalCode();
//            knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
//
//            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
//            Calendar cal = Calendar.getInstance();
//            System.out.println(dateFormat.format(cal.getTime()));
//            currentTime = dateFormat.format(cal.getTime());
//
//            String message = "";
//            String userName = preferenceHelper.getData("_keyUserName");
//            String receiverName = preferenceHelper.getData("_keyName");
//            String additionalInfo = preferenceHelper.getData("_keyAddInfo");
//            String number = preferenceHelper.getData("_keyNumber");
//            String mail = preferenceHelper.getData("_keyMail");
//            String strDestLat = preferenceHelper.getData("_keyDestLat");
//            String strDestLong = preferenceHelper.getData("_keyDestLong");
//            String strDestName = preferenceHelper.getData("_keyDestName");
//
//
//            if (additionalInfo.equals("YES")) {
//                message = "Hi " + receiverName + ",\n" + userName + " shared his location through GetSetGoo. " + userName + " was at " + address + "," + city + "," + state + "," + country + "," + postalCode + "\nLatitude: " + latitude + "\nLongitude: " + longitude + " at " + dateFormat.format(cal.getTime()) + "\nYou can find his location here.\nhttp://www.google.com/maps/place/" + latitude + "," + longitude + "/@" + latitude + "," + longitude + ",17z/data=!3m1!1e3";
//            } else {
//                message = "Hi " + receiverName + ",\n" + userName + " shared his location through GetSetGoo. " + userName + " was at " + address + "," + city + "," + state + "," + country + "," + postalCode + " at " + dateFormat.format(cal.getTime());
//            }
//            if (!number.equals("")){
//                canText="Yes";
//            }

//            if (!number.equals("")) {
//                try {
//                    SmsManager smsManager = SmsManager.getDefault();
//                    ArrayList<String> parts = smsManager.divideMessage(message);
//                    smsManager.sendMultipartTextMessage(number, null, parts,
//                            null, null);
//                    Toast.makeText(context, "SMS sent to " + number, Toast.LENGTH_LONG).show();
//                    smsStatus = "SMS was sent successfully";
//                } catch (Exception e) {
//                    Toast.makeText(context, "SMS failed, please try again." + number, Toast.LENGTH_LONG).show();
//                    smsStatus = "SMS failed";
//                    e.printStackTrace();
//                }
//            }
//            if (!mail.equals("")) {
//                if (UtilityClass.isNetworkAvailable(context)) {
//                    canMail="Yes";
//                    try {
//                        message = "Hi " + receiverName + ",\n" + userName + " shared his location through GetSetGoo. " + userName + " was at " + address + "," + city + "," + state + "," + country + "," + postalCode + "\nLatitude: " + latitude + "\nLongitude: " + longitude + " at " + dateFormat.format(cal.getTime());
//
//                        if (userName.equals("")) {
//                            subject = "GetSetGoo!!!";
//                        } else
//                            subject = userName + "'s Location";
//                        if (strDestLat != null && !strDestLat.equals("") && strDestLong != null && !strDestLong.equals("")) {
//                            double destLat = Double.parseDouble(strDestLat);
//                            double destLong = Double.parseDouble(strDestLong);
//                            float distanceInMeters = gps.getDistanceInMeters(destLat, destLong, latitude, longitude);
//                            message = message + ". " + userName + " is currently " + (int) distanceInMeters + " kilometers away from " + strDestName + ".";
//                        }
////                        Intent intent = new Intent(Intent.ACTION_SYNC, null, context, BackgroundService.class);
////                        /* Send optional extras to Download IntentService */
////                        intent.putExtra("email", mail);
////                        intent.putExtra("subject", subject);
////                        intent.putExtra("message", message);
////                        intent.putExtra("latitude", latitude);
////                        intent.putExtra("longitude", longitude);
////                        context.startService(intent);
////                        mailStatus = "Email sent successfully";
//                    } catch (Exception e) {
////                        Toast.makeText(context, "Email failed, please try again." + mail, Toast.LENGTH_LONG).show();
////                        mailStatus = "Email failed";
//                        e.printStackTrace();
//                    }
//                }
//            }
//            String dbAddress = dateFormat.format(cal.getTime()) + "\n" + address + ", " + city + ", " + state + ", " + country + ", " + postalCode + ", " + knownName;
//            String timeStamp = dateFormat.format(cal.getTime());


            Intent intent = new Intent(Intent.ACTION_SYNC, null, context, BackgroundService.class);
                        /* Send optional extras to Download IntentService */
//            intent.putExtra("email", mail);
//            intent.putExtra("subject", subject);
//            intent.putExtra("canMail", canMail);
//            intent.putExtra("canText", canText);
//            intent.putExtra("number", number);
//            intent.putExtra("message", message);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);

//            intent.putExtra("dbAddress", dbAddress);
//            intent.putExtra("timeStamp", timeStamp);
            context.startService(intent);


//            dBhelper.addAddressToDB(dbAddress, latitude + "", latitude + "", timeStamp);
//            String startTime = preferenceHelper.getData("_keyStartDateTime");
//            String stopTime = preferenceHelper.getData("_keyStopdateTime");
//            Log.e("Inserted to DB", latitude + " , " + longitude + "timestamp-" + timeStamp + " starttime" + startTime + " stotime" + stopTime);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
