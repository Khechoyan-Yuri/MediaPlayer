package com.yuri_khechoyan.hw6_mediaplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class PlayingService extends Service {

    //Create a final IBinder object
    private final IBinder myBinder = new LocalBinder();
    //Declare Object
    MediaPlayer mediaPlayer;
    //Declare object
    NotificationManager notifyManager;
    //Assign the final Int
    final int ID_NOTIFICATION = 3;
    //Initialize name of file
    String nameOfFile ="";
    //Initialize song title
    String songTitle = "";
    //Initialize artist name
    String artistName = "";

    /**
     * Constructor to the playing service
     */
    public PlayingService() {
    }

    /**
     * Method for playing the music
     */
    public void Play() {

        //If mediaPlayer is not null
        if (mediaPlayer != null){

            //Start the music
            mediaPlayer.start();

            //Assign variable for Notification
            NotificationCompat.Builder build = new NotificationCompat.Builder(getApplicationContext());

            //Set the title as Music playing
            build.setContentTitle("Music Playing");

            //Set the content Text
            build.setContentText("Playing " + songTitle + " by " + artistName);

            //Set the icon
            build.setSmallIcon(android.R.drawable.sym_def_app_icon);

            //Declare and assign the myIntent
            Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);

            //Assign pendingIntent
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            //Set Action
            myIntent.setAction("android.intent.action.MAIN");

            //Add Category
            myIntent.addCategory("android.intent.category.LAUNCHER");

            build.setContentIntent(pendingIntent);

            //Assign notifcation object
            Notification notify = build.build();

            //Assign the flags
            notify.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONLY_ALERT_ONCE | Notification.FLAG_NO_CLEAR;

            //Assign the notify manger to get system
            notifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            //Cancel the notifcation
            notifyManager.cancel(2);

            //Notify the user
            notifyManager.notify(ID_NOTIFICATION, notify);
        }
    }

    /**
     * Method for the stoping music
     */
    public void Stop() {

        //Pause the music
        mediaPlayer.pause();

        //Go to the start
        mediaPlayer.seekTo(0);

        //Cancel the notification
        notifyManager.cancel(ID_NOTIFICATION);
    }

    /**
     * Pausing the music
     */
    public void Pause() {

        //Stop Playing for the media player
        mediaPlayer.pause();

        //Clear notification
        notifyManager.cancel(ID_NOTIFICATION);
    }

    /**
     * This class allows the localBinder to return the service
     */
    public class LocalBinder extends Binder {
        /**
         * This method returns the service
         * @return
         */
        PlayingService getService(){
            //Return the PlayingService
            return PlayingService.this;
        }
    }

    /**
     * Empty Override constructor
     */
    @Override
    public void onCreate(){

    }

    /**
     * Override the onDestroy
     */
    @Override
    public void onDestroy(){

        //if the player is not null
        if(mediaPlayer != null){
            mediaPlayer.release();      //media player is released
            mediaPlayer = null;         //Set it equal to null
        }

    }
    @Override public boolean onUnbind(Intent intent){
        return true;
    }

    /**
     * Override the Ibinder intent
     * @param intent the intent
     * @return the my brinder
     */
    @Override
    public IBinder onBind(Intent intent) {

        //If the extras is not null
        if (intent.getExtras() !=null){

            //Assign value to the variable
            nameOfFile = intent.getExtras().getString("File");
            //Assign value to the variable
            songTitle = intent.getExtras().getString("Song");
            //Assign value to the variable
            artistName = intent.getExtras().getString("Artist");
            //Assign object to new object
            mediaPlayer = new MediaPlayer();

            //Assign object
            mediaPlayer = MediaPlayer.create(this, Uri.parse(nameOfFile));
        }
        //Return myBinder
        return myBinder;
    }
}
