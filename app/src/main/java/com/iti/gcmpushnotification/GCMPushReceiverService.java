package com.iti.gcmpushnotification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by HP on 12/05/2017.
 */

public class GCMPushReceiverService extends GcmListenerService {


    public static HashMap<String,String> rides=new HashMap<String,String>();
    //String notificationTime;
    //String message="";
    //String title="";
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        String title=data.getString("title");
        //Log.i("msg",message);
        String acceptedToken=data.getString("acceptedToken");
        String notificationTime= data.getString("time");

        Log.i("time in emo in receivr",notificationTime);

        if(message.contains("already accepted"))
        {
            rides.put(notificationTime,"taken");
        }else{
            rides.put(notificationTime,"available");
        }

        if(GCMRegistrationIntentService.token==null)
        {
            new GCMRegistrationIntentService().registerGCM();
        }
        if(acceptedToken.equals(GCMRegistrationIntentService.token))
        {
            sendNotification("Congratulations you got "+title+" ride",notificationTime,title);
        }else {
            sendNotification(message,notificationTime,title);
        }
    }
    private void sendNotification(String message,String notificationTime,String title) {


        Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
        intent.putExtra("time",notificationTime);
        //intent.putExtra("message",message);
        intent.putExtra("title",title);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction(Long.toString(System.currentTimeMillis()));
        int requestCode = 0;//Your request code
        PendingIntent pendingIntent;
        if(message.contains("Congratulations")||message.contains("Sorry")) {
            pendingIntent = PendingIntent.getActivity(this, requestCode, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
        }else{
            pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        }
        //Setup notification
        //Sound
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //Build notification
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("you have new notification")
                .setContentText(message)
                .setAutoCancel(false)
                .setContentIntent(pendingIntent);

        SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss",
                Locale.ENGLISH);

        Date date2= null;
        try {
            date2 = sdf.parse(notificationTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int i = (int)date2.getTime();

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        Log.i("identifier=",String.valueOf(i));
        notificationManager.notify(i, noBuilder.build()); //0 = ID of notification
    }
}
