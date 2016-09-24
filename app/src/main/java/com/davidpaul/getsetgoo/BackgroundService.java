package com.davidpaul.getsetgoo;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.telephony.SmsManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by David Paul on 8/27/2016.
 */

public class BackgroundService extends IntentService {

    //Declaring Variables
//    private Context context;
    private Session session;
    private String smsStatus = "";
    private String mailStatus = "";
    private PreferenceHelper preferenceHelper;
    private String currentTime = "";
    private String message = "";
    private String userName = "";
    private String receiverName = "";
    private String additionalInfo = "";
    private String number = "";
    private String email = "";
    private String strDestLat = "";
    private String strDestLong = "";
    private String strDestName = "";
    String address = "";
    String city = "";
    String state = "";
    String country = "";
    String postalCode = "";
    String knownName = "";
    private SimpleDateFormat dateFormat;
    private Calendar cal;
    private String subject = "";
    private float distanceInMeters = 0;
    private String strTrackStopTG="";

    public BackgroundService() {
        super(BackgroundService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        DBhelper dBhelper = new DBhelper(getApplicationContext());

        GPSTracker gps = new GPSTracker(getApplicationContext());
        preferenceHelper = PreferenceHelper.getInstance(getApplicationContext(), "ReceiverPrefs");

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        double latitude = intent.getDoubleExtra("latitude", 0);
        double longitude = intent.getDoubleExtra("longitude", 0);

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            country = addresses.get(0).getCountryName();
            postalCode = addresses.get(0).getPostalCode();
            knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

            dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            cal = Calendar.getInstance();
            System.out.println(dateFormat.format(cal.getTime()));
            currentTime = dateFormat.format(cal.getTime());

            message = "";
            userName = preferenceHelper.getData(UtilityClass.KEY_USERNAME);
            receiverName = preferenceHelper.getData(UtilityClass.KEY_REVEIVER_NAME);
            additionalInfo = preferenceHelper.getData(UtilityClass.KEY_SMS_ADDINFO);
            number = preferenceHelper.getData(UtilityClass.KEY_REVEIVER_NUMBER);
            email = preferenceHelper.getData(UtilityClass.KEY_REVEIVER_MAIL);
            strDestLat = preferenceHelper.getData(UtilityClass.KEY_DEST_LATITUDE);
            strDestLong = preferenceHelper.getData(UtilityClass.KEY_DEST_LONGITUDE);
            strDestName = preferenceHelper.getData(UtilityClass.KEY_DEST_NAME);

            strTrackStopTG = preferenceHelper.getData(UtilityClass.KEY_STOP_TRACK_TOGGLE);

            if (strDestLat != null && !strDestLat.equals("") && strDestLong != null && !strDestLong.equals("")) {
                double destLat = Double.parseDouble(strDestLat);
                double destLong = Double.parseDouble(strDestLong);
                distanceInMeters = gps.getDistanceInMeters(destLat, destLong, latitude, longitude);
                if (strTrackStopTG.equals("ON")) {
                    if (distanceInMeters < 3) {
                        preferenceHelper.setdata(UtilityClass.KEY_STOP_AT_THREE_KM, "NEAR");
                    } else {
                        preferenceHelper.setdata(UtilityClass.KEY_STOP_AT_THREE_KM, "FAR");
                    }
                } else {
                    preferenceHelper.setdata(UtilityClass.KEY_STOP_AT_THREE_KM, "FAR");
                }
            }

            if (additionalInfo.equals("YES")) {
                message = "Hi " + receiverName + ",\n" + userName + " shared his location through GetSetGoo. " + userName + " was at " + address + "," + city + "," + state + "," + country + "," + postalCode + "\nLatitude: " + latitude + "\nLongitude: " + longitude + " at " + dateFormat.format(cal.getTime()) + "\nYou can find his location here.\nhttp://www.google.com/maps/place/" + latitude + "," + longitude + "/@" + latitude + "," + longitude + ",17z/data=!3m1!1e3";
                if (distanceInMeters != 0) {
                    message = message + ".\n" + userName + " is currently " + (int) distanceInMeters + " kilometers away from " + strDestName + ".";
                    if (preferenceHelper.getData(UtilityClass.KEY_STOP_AT_THREE_KM).equals("NEAR")) {
                        message = message + ".\nTracking has been stopped since you are less than 3 kilometers from your destination.";
                    }
                }
            } else {
                message = "Hi " + receiverName + ",\n" + userName + " shared his location through GetSetGoo. " + userName + " was at " + address + "," + city + "," + state + "," + country + "," + postalCode + " at " + dateFormat.format(cal.getTime());
            }

            if (!number.equals("")) {
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    ArrayList<String> parts = smsManager.divideMessage(message);
                    smsManager.sendMultipartTextMessage(number, null, parts,
                            null, null);
//                Toast.makeText(context, "SMS sent to " + number, Toast.LENGTH_LONG).show();
                    smsStatus = "SMS was sent successfully";
                } catch (Exception e) {
//                Toast.makeText(context, "SMS failed, please try again." + number, Toast.LENGTH_LONG).show();
                    smsStatus = "SMS failed";
                    e.printStackTrace();
                }
            }

            if (!email.equals("")) {
                if (UtilityClass.isNetworkAvailable(getApplicationContext())) {
                    try {
                        message = "Hi " + receiverName + ",\n" + userName + " shared his location through GetSetGoo. " + userName + " was at " + address + "," + city + "," + state + "," + country + "," + postalCode + "\nLatitude: " + latitude + "\nLongitude: " + longitude + " at " + dateFormat.format(cal.getTime());

                        if (userName.equals("")) {
                            subject = "GetSetGoo!!!";
                        } else
                            subject = userName + "'s Location";
                        if (distanceInMeters != 0) {
                            message = message + ".\n" + userName + " is currently " + (int) distanceInMeters + " kilometers away from " + strDestName + ".";
                            if (preferenceHelper.getData(UtilityClass.KEY_STOP_AT_THREE_KM).equals("NEAR")) {
                                message = message +".\nTracking has been stopped since you are less than 3 kilometers from your destination.";
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        message = message + "\n\nYou can find his location here.\n http://www.google.com/maps/place/" + latitude + "," + longitude + "/@" + latitude + "," + longitude + ",17z/data=!3m1!1e3";
        //http://maps.google.com/?q=49.46800006494457,17.11514008755796
        //http://www.google.com/maps/place/49.46800006494457,17.11514008755796/@49.46800006494457,17.11514008755796,17z/data=!3m1!1e3

        Properties props = new Properties();
        //Configuring properties for gmail
        //If you are not using gmail you may need to change the values
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        //Creating a new session
        session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    //Authenticating the password
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(Config.EMAIL, Config.PASSWORD);
                    }
                });

        try {
            //Creating MimeMessage object
            MimeMessage mm = new MimeMessage(session);

            //Setting sender address
            mm.setFrom(new InternetAddress(Config.EMAIL));
            //Adding receiver
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            //Adding subject
            mm.setSubject(subject);
            //Adding message
            mm.setText(message);

            //Sending email
            Transport.send(mm);
            mailStatus = "Email sent successfully";

        } catch (MessagingException e) {
            e.printStackTrace();
            mailStatus = "Email failed";
        }

        Log.e("mailStatus-->", mailStatus);
        try {
            String dbAddress = dateFormat.format(cal.getTime()) + "\n" + address + ", " + city + ", " + state + ", " + country + ", " + postalCode + ", " + knownName;
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm a");
//            String timeStamp = sdf.format(Calendar.getInstance().getTime());
            String timeStamp = getDateTime();
//            int count = 0;
//            count++;
//            if (count == 1) {
//                latitude = 12.9818218;
//                longitude = 80.2326789;
//            } else if (count == 2) {
//                latitude = 12.9913869;
//                longitude = 80.2191835;
//            } else if (count == 3) {
//                latitude = 12.9954954;
//                longitude = 80.2169557;
//            }
            dBhelper.addAddressToDB(dbAddress + "\n" + smsStatus + "\n" + mailStatus, latitude + "", longitude + "", timeStamp);
            Log.e("Inserted to DB", latitude + " , " + longitude + "--timestamp-" + timeStamp /*+ " starttime" + startTime + " stotime" + stopTime*/);

            Log.e("timeStamp-->", timeStamp);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy/MM/dd hh:mm a", Locale.getDefault());
        Date date = new Date();
        return sdf.format(date);
    }
}