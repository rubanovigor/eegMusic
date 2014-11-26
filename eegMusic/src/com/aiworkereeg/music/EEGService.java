package com.aiworkereeg.music;

import com.neurosky.thinkgear.TGDevice;
import com.neurosky.thinkgear.TGEegPower;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class EEGService extends IntentService{
	public static final String StartTG = "com.aiworkereeg.music.action.StartTG";
	final static String TAG = "EEG Service";
	// -- used to write to the system log from this class.
    public static final String LOG_TAG = "EEG Service";
    // -- defines and instantiates an object for handling status updates.
    private BroadcastNotifier mBroadcaster = new BroadcastNotifier(this);
    
    /*Bitmap mDummyAlbumArt;
    NotificationManager mNotificationManager;
    Notification mNotification = null;
    final int NOTIFICATION_ID = 1;*/
    	
	// -- BT and TG
	private BluetoothAdapter bluetoothAdapter;
	TGDevice tgDevice;
	private static final boolean RAW_ENABLED = false; // false by default
	private String BTstatus; 
	
	private int At = 50;   private int Med = 50;
	
	/*public static final String BTstatus_key = "BTstatus";
	private int result = Activity.RESULT_CANCELED;
	public static final String RESULT = "result";
	//public static final String URL = "urlpath";
	public static final String FILENAME = "filename";
	//public static final String FILEPATH = "filepath";
	public static final String NOTIFICATION = "com.aiworkereeg.music";*/
	
    /**
     * An IntentService must always have a constructor that calls the super constructor. The
     * string supplied to the super constructor is used to give a name to the IntentService's
     * background thread.
     */
    public EEGService() {   super("EEGService");   }
		  
	 
	/** In an IntentService, onHandleIntent is run on a background thread.  As it
     * runs, it broadcasts its current status using the LocalBroadcastManager.
     * @param workIntent The Intent that starts the IntentService. This Intent contains the
     * URL of the web site from which the RSS parser gets data.
     */
    @Override
    protected void onHandleIntent(Intent workIntent) {
    	//Toast.makeText(this, "onHandleIntent", Toast.LENGTH_SHORT).show();
    	processStartTG();
    	
                // Broadcasts an Intent indicating that processing has started.
                //mBroadcaster.broadcastIntentWithState(Constants.STATE_ACTION_STARTED);
                
                // Reports that the service is about to connect to the RSS feed
                //mBroadcaster.broadcastIntentWithState(Constants.STATE_CONNECTING);      
                
             if (BTstatus == "connected") {           
                // Reports that the feed retrieval is complete.
               // mBroadcaster.broadcastIntentWithState(Constants.STATE_CONNECTED);
             }
       
       
    }
    
    
    void processStartTG() {
        /* Checking BT and connecting to the TG device */
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {	// Alert user that Bluetooth is not available
          // Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_SHORT).show();
            //finish();
        	BTstatus = "bt turned off";
            return;
        } else { // create the TGDevice 
            tgDevice = new TGDevice(bluetoothAdapter, handler);
           // updateNotification("disconnected");
            StartEEG();
        	// Toast.makeText(this, "Bluetooth available", Toast.LENGTH_SHORT).show();
             
        }
	}

	public void StartEEG() {
		//Toast.makeText(this, "connecting...", Toast.LENGTH_SHORT).show();
		if (tgDevice.getState() != TGDevice.STATE_CONNECTING && tgDevice.getState() != TGDevice.STATE_CONNECTED) {
		    tgDevice.connect(RAW_ENABLED);
		}
	}
	
	
	// -- Handles messages from TGDevice 
	private final Handler handler = new Handler() {
	   @Override
	   public void handleMessage(Message msg) {               
	               switch (msg.what) {
		                case TGDevice.MSG_STATE_CHANGE:
		                    /*display message according to state change type */
		                    switch (msg.arg1) {
		                    case TGDevice.STATE_IDLE:
		                        break;
		                    case TGDevice.STATE_CONNECTING:
		                    	//Toast.makeText(this, "connecting...", Toast.LENGTH_SHORT).show();
		                    	BTstatus = "connecting...";
		                    	mBroadcaster.broadcastIntentWithState(Constants.STATE_CONNECTING);  
		                        break;
		                    case TGDevice.STATE_CONNECTED:
		                        tgDevice.start();
		                        //Toast.makeText(this, "connected", Toast.LENGTH_SHORT).show();
		                        BTstatus = "connected";
		                        mBroadcaster.broadcastIntentWithState(Constants.STATE_CONNECTED);
		                        break;
		                    case TGDevice.STATE_NOT_FOUND:
		                    	//Toast.makeText(this, "neurosky mindwave mobile was not found", Toast.LENGTH_SHORT).show();
		                    	BTstatus = "not found";
		                    	mBroadcaster.broadcastIntentWithState(Constants.STATE_EEGnotFound); 
		                        break;
		                    case TGDevice.STATE_NOT_PAIRED:
		                    	//Toast.makeText(this, "neurosky mindwave mobile not paired !", Toast.LENGTH_SHORT).show();
		                    	BTstatus = "not paired";
		                    	mBroadcaster.broadcastIntentWithState(Constants.STATE_EEGnotPaired); 
		                        break;
		                    case TGDevice.STATE_DISCONNECTED:
		                    	//Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show();
		                    	BTstatus = "disconnected";
		                    	mBroadcaster.broadcastIntentWithState(Constants.STATE_Disconnected); 
		                    	break;
		                    }

		                    break;
		                    
		                case TGDevice.MSG_POOR_SIGNAL:
		                      //int TGState = msg.arg1;
		                    break;
		                case TGDevice.MSG_RAW_DATA:
		                	//raw1 = msg.arg1;
		                    //tv.append("Got raw: " + msg.arg1 + "\n");                  
		                    break;
		                case TGDevice.MSG_HEART_RATE:
		                    //tv.append("Heart rate: " + msg.arg1 + "\n");
		                    break;
		                case TGDevice.MSG_ATTENTION:
		                    // -- First send Attention data to the backend in async way
		                	//APIClient.collectAttention(null, msg.arg1);
		                	
		                		 	               
		                    At = msg.arg1;       
		                    mBroadcaster.broadcastIntentWithA(At);  
		                    //setUpAsForeground("Att: " + String.valueOf(At) + "||" + " Med: " + String.valueOf(Med) );
	                		
		                    //tv_Att.setText(String.valueOf(At));
		                    //mMusicPlayerThread.setAttention(At);
		                          
		                    // --saving data to file
		                    /* String filename ="so_v2_<date_time>.csv";
		                    Time now = new Time();
		                    now.setToNow();
		                    String date_time = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));                    
		                    filename = "so_v2_" + date_time + ".csv";
		                    
		                   // writeToExternalStoragePublic(filename, user_g, now, At, Med);
		                    */
		                    break;
		                    
		                case TGDevice.MSG_MEDITATION:
		                	//APIClient.collectMeditation(null, msg.arg1);

		                    Med = msg.arg1;
		                    mBroadcaster.broadcastIntentWithM(Med);  
		                    //tv_Med.setText(String.valueOf(Med));
		                    //mMusicPlayerThread.setMeditation(Med);
		                    
		                    break;
		                case TGDevice.MSG_BLINK:
		                    //tv.append("Blink: " + msg.arg1 + "\n");
		                    break;
		                case TGDevice.MSG_RAW_COUNT:
		                    //tv.append("Raw Count: " + msg.arg1 + "\n");
		                    break;
		                case TGDevice.MSG_LOW_BATTERY:
		                    Toast.makeText(getApplicationContext(), "Low battery!", Toast.LENGTH_SHORT).show();
		                    break;
		                case TGDevice.MSG_RAW_MULTI:
		                    //TGRawMulti rawM = (TGRawMulti)msg.obj;
		                    //tv.append("Raw1: " + rawM.ch1 + "\nRaw2: " + rawM.ch2);
		                
		                case TGDevice.MSG_SLEEP_STAGE:
		                	//sleep_stage = msg.arg1;
		                	break;
		                case TGDevice.MSG_EEG_POWER:
		                    TGEegPower eegPower = (TGEegPower) msg.obj;
		                    APIClient.collectEEGPower(null, eegPower);
		                    
		                   /* delta = eegPower.delta;
		                    high_alpha = eegPower.highAlpha;
		                    high_beta = eegPower.highBeta;
		                    low_alpha = eegPower.lowAlpha;
		                    low_beta = eegPower.lowBeta;
		                    low_gamma = eegPower.lowGamma;
		                    mid_gamma = eegPower.midGamma;
		                    theta = eegPower.theta;*/
		                    break;
		                default:
		                    break;
	                }

	          }
	};

	
	
	
	 /** Updates the notification. */
   /* void updateNotification(String text) {
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), MainEEGmusic.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        mNotification.setLatestEventInfo(getApplicationContext(), "EEG Service", " Status:  " + text, pi);
        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }*/
    
    /**
     * Configures service as a foreground service. A foreground service is a service that's doing
     * something the user is actively aware of (such as playing music), and must appear to the
     * user as a notification. That's why we create the notification here.
     */
   /* void setUpAsForeground(String text) {
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), MainEEGmusic.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        mNotification = new Notification();
        mNotification.tickerText = text;
        mNotification.icon = R.drawable.eeg_service;
        mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
        mNotification.setLatestEventInfo(getApplicationContext(), "EEG Service", " " + text, pi);
        startForeground(NOTIFICATION_ID, mNotification);
    }*/
	



}
