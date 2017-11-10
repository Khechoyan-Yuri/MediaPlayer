package com.yuri_khechoyan.hw6_mediaplayer;

import android.app.DownloadManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

/**
 * An {@link IntentService} subclass for handling a downloading service and will not
 * be on the main threads
 */
public class DownloadingService extends IntentService {
    //Declare the url string
    String url = "http://www.primetechconsult.com/CIS472/secretsong_mario.mp3";
    //Declare object
    DownloadManager down;
    //Assign final variable
    final int ID_NOTIFICATION = 1;

    /**
     * Constructor for the Super
     */
    public DownloadingService() {
        super("DownloadService");
    }

    /**
     * Overide to the Intent Handler
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        //Down is being assigned to the download service
        down = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

        //Parse the url
        DownloadManager.Request req = new DownloadManager.Request(Uri.parse(url));

        //Put the request in the queue
        down.enqueue(req);

        //Declare Object
        NotificationCompat.Builder build = new NotificationCompat.Builder(getApplicationContext());

        //Set the content title to Music Download
        build.setContentTitle("Music Download");

        //Set the text to Started
        build.setContentText("Downloading the song has begun");

        //Set the Icon to the icon
        build.setSmallIcon(android.R.mipmap.sym_def_app_icon);

        //create Notification object
        Notification notify = build.build();

        //Assign the flags to the notifications
        notify.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONLY_ALERT_ONCE ;

        //Declare Notification Manager object
        NotificationManager notifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //Call Function
        notifyManager.notify(ID_NOTIFICATION, notify);

        //Call Stop Self
        stopSelf();
    }
}

