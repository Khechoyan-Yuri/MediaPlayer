package com.yuri_khechoyan.hw6_mediaplayer;

/* ========================================================================== */
/*	PROGRAM Music Player

    AUTHOR: Yuri Khechoyan
    COURSE NUMBER: CIS 472
    COURSE SECTION NUMBER: 01
    INSTRUCTOR NAME: Dr. Tian
    PROJECT NUMBER: 6
    DUE DATE: 4/18/2017

SUMMARY

    This program is designed to be a music player.
    When the program is launched, The ActionBar Menu needs to be pressed.
    Once ActionBar Menu is pressed, user has 2 options:
    -Download Song
    -Exit Now

    If Download Song is pressed, the app will create a service that will
    start to download the song from from the hard-coded URL. A toast is thrown
    to let user know that song is downloading.

    Once complete, the album artwork will change to the designated album art.

    From there, user will have the ability to Play, Stop, or Pause the song.

    Play - plays the song from the beginning or resume from the paused location
    Stop - stops the song and moves the seek-bar to the start of the song
    (seek-bar is not visible or accessible by user)
    Pause - pauses the song at the users' discretion

    When Exit Now is tapped (in ActionBar),
    the app quits - destroys itself to free up phone resources

INPUT

        -Open app & Tap Download Song (Action Bar Menu)
        -Play Song (when download is complete)
        -Pause/Stop/Play Song

OUTPUT

    Song plays through either phone's speaker or through headphones

ASSUMPTIONS
- Users have Internet connection in order to download the song
- Users have knowledge of how Play, Pause, Stop buttons work
- Users understand that they don't have the song downloaded (initially)
*/



import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    //Declare object
    MyBroadcastReceiver myReceiver;
    //Declare object
    PlayingService player;
    //Declare Button
    Button btn_play;
    //Declare Button
    Button btn_pause;
    //Declare Button
    Button btn_stop;
    //Declare TextView
    TextView tv_Song;
    //Declare TextView
    TextView tv_Artist;
    //Declare ImageView
    ImageView iv_cover;
    //Assign Bounded to false
    boolean bounded = false;
    //Assign download_finished to false
    boolean download_finished = false;

    /**
     * Override the Oncreate
     * @param savedInstanceState the bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assign receiver to BroadcastReceiver
        myReceiver = new MyBroadcastReceiver();

        //Register the receiver for the IntentFilter
        registerReceiver(myReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        //Assign Buttons to their findViewById counter parts
        btn_play = (Button) findViewById(R.id.btn_play);
        btn_pause = (Button) findViewById(R.id.btn_pause);
        btn_stop = (Button) findViewById(R.id.btn_stop);

        //Assign ImageView to their findViewById counter parts
        iv_cover = (ImageView) findViewById(R.id.iv_cover);

        //Set the imageResource - initial image before song is downloaded
        iv_cover.setImageResource(R.drawable.background_photo);

        //Assign TextViews to their findViewById counter parts
        tv_Artist = (TextView) findViewById(R.id.tv_artistname);
        tv_Song = (TextView) findViewById(R.id.tv_songname);

        //Disable Pause & Stop buttons - unusable when song is not available
        btn_pause.setEnabled(false);
        btn_stop.setEnabled(false);

        /**
         *Set the onclick listener
         */
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If player is not null
                if(player != null) {

                    //Call Play function
                    player.Play();

                    //Set play to not enabled - when song is playing you can't play again
                    btn_play.setEnabled(false);

                    //Set pause to enabled - pause button enabled for use
                    btn_pause.setEnabled(true);

                    //Set stop to enabled - stop button is enabled for use
                    btn_stop.setEnabled(true);
                }
            }
        });

        /**
         * set onclick listener of stop
         */
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //If player is not null
                if(player != null) {

                    //Call Stop Method
                    player.Stop();

                    //Set play to enabled - play button is enabled for use
                    btn_play.setEnabled(true);

                    //Set pause to not enabled
                    btn_pause.setEnabled(false);

                    //Set stop to not enabled
                    btn_stop.setEnabled(false);
                }
            }
        });
        /**
         * set onclick listener of pause
         */
        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //pause the song
                if(player != null) {
                    //Call Pause Method
                    player.Pause();

                    //Set play to enabled - play button is enabled for use
                    btn_play.setEnabled(true);

                    //Set pause to not enabled
                    btn_pause.setEnabled(false);

                    //Set stop to not enabled
                    btn_stop.setEnabled(true);
                }
            }
        });

    }

    /**
     * Assign the Service Connection variable
     */
    private ServiceConnection myConnection = new ServiceConnection() {

        /**
         * Override the service connected
         * @param componentName
         * @param iBinder
         */
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            //Declare object to the iBinder
            PlayingService.LocalBinder binder = (PlayingService.LocalBinder) iBinder;

            //Assign player to the service
            player = binder.getService();

            //Bounded is true - when binder & service connection is made
            bounded = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bounded = false; //Disconnect from the service
        }
    };

    /**
     * This is the onCreate override
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate menu to the actionbar
        getMenuInflater().inflate(R.menu.main_menu, menu);

        //return true
        return true;
    }

    /**
     * Override the selected the item
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        //set the id on the item clicked
        int id = item.getItemId();

        //Switch statement
        switch(id){

            //If the id is the exit id
            case R.id.exit:
                if(bounded){

                    //Stop the music
                    unbindService(myConnection);

                    //player is null
                    player = null;

                    //bounded is false
                    bounded = false;
                }

                //Call finish and break
                finish();
                break;

            //If the id is download
            case R.id.download:

                //Assign Intent for downloading song
                Intent serviceIntent = new Intent(getApplicationContext(), DownloadingService.class);

                //Throw toast to tell user that song is downloading
                Toast.makeText(this, "Song Downloading...", Toast.LENGTH_SHORT).show();

                //Start the service and break
                startService(serviceIntent);
                break;
        }
        //Return the super
        return super.onOptionsItemSelected(item);
    }

    /**
     * The class for broadcast receiver
     */
    class MyBroadcastReceiver extends BroadcastReceiver {

        //Assign the final ID as 2
        final int ID_NOTIFICATION = 2;

        /**
         * Override the OnReceive
         * @param context The context
         * @param intent intent to other
         */
        @Override
        public void onReceive(Context context, Intent intent) {

            //Assign the extras to the intent get extras
            Bundle extras = intent.getExtras();

            //Assign the id
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);

            //Assign object to the get system Service
            DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

            //Declare object to the DownloadManager.Query
            DownloadManager.Query query = new DownloadManager.Query();

            //Set the filter
            query.setFilterById(extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID));

            //Declare object
            Cursor curs = dm.query(query);

            //If the cursor is move to first
            if (curs.moveToFirst()) {

                //Assign the status
                int status = curs.getInt(curs.getColumnIndex(DownloadManager.COLUMN_STATUS));

                //If status is equal to status successful
                if (status == DownloadManager.STATUS_SUCCESSFUL) {

                    //Set title to the title
                    String title = curs.getString(curs.getColumnIndex(DownloadManager.COLUMN_TITLE));

                    //Set nameFile to the file name
                    String nameFile = curs.getString(curs.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

                    //Set download to finished
                    download_finished = true;

                    //Set image
                    iv_cover.setImageResource(R.drawable.albumcover);

                    //Parse the title
                    String[] parse = title.split("/");

                    //Set the text of the song
                    tv_Song.setText(parse[parse.length-1].split("_")[0]);

                    //Set the text of the artist
                    tv_Artist.setText(parse[parse.length-1].split("_")[1]);

                    //Assign object
                    NotificationCompat.Builder build = new NotificationCompat.Builder(getApplicationContext()); //Display notification that DL has completed

                    //Set content Title
                    build.setContentTitle("Music Service");

                    //Set text content
                    build.setContentText("Download finished!");

                    //Set small icon
                    build.setSmallIcon(android.R.drawable.ic_dialog_info);

                    //Assign intent
                    Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);

                    //Assign pending edit
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    //Set action
                    myIntent.setAction("android.intent.action.MAIN");

                    //Set and add category
                    myIntent.addCategory("android.intent.category.LAUNCHER");

                    //Set content intent
                    build.setContentIntent(pendingIntent);

                    //Assign variable
                    Notification notify = build.build();

                    //Assign flags
                    notify.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONLY_ALERT_ONCE ;

                    //Assign object
                    NotificationManager notifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                    //Cancel the notification
                    notifyManager.cancel(1);

                    //Notify the user
                    notifyManager.notify(ID_NOTIFICATION, notify);

                    //Create new intent
                    Intent BoundServiceIntent = new Intent(getApplicationContext(), PlayingService.class);

                    //Create new bundle
                    Bundle bundle = new Bundle();

                    //Put the Strings into the bundle service
                    bundle.putString("File", nameFile);
                    bundle.putString("Artist", parse[parse.length-1].split("_")[0]);
                    bundle.putString("Song", parse[parse.length-1].split("_")[1]);

                    //Put the extras in bundle
                    BoundServiceIntent.putExtras(bundle);

                    //Start the service
                    startService(BoundServiceIntent);

                    //Bind the service
                    bindService(BoundServiceIntent, myConnection, Context.BIND_AUTO_CREATE);
                }
            }
        }
    }
}


