package com.aiworkereeg.music;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList; 
import java.util.Locale;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aiworkereeg.music.MusicService;
import com.aiworkereeg.music.MusicPlayerView;
import com.aiworkereeg.music.MusicPlayerView.MusicPlayerThread;
import com.aiworkereeg.music.R;

import com.neurosky.thinkgear.TGDevice;
import com.neurosky.thinkgear.TGEegPower;
import com.neurosky.thinkgear.TGRawMulti;



public class MainEEGmusic extends Activity implements SurfaceHolder.Callback{
// public class MainEEGmusic extends Activity {
	
	private BluetoothAdapter bluetoothAdapter;
	TGDevice tgDevice;
	private static final boolean RAW_ENABLED = false; // false by default
	
	TextView tv_info;	TextView tv_TimeToSel; 
	TextView tv_consoleBoard; TextView tv_consoleLine;
	private int At = 42;     private int Med = 42;
	TextView tv_Med;    TextView tv_Att;    TextView tv_Vel;    TextView tv_AmM;    
    	/** A handle to the thread that's actually running the animation. */
    private MusicPlayerThread mMusicPlayerThread;   
    	/** A handle to the View in which the game is running. */
    private MusicPlayerView mMusicPlayerView;
    
   /* final int ActivityTwoRequestCode = 0;
    Bitmap myBitmap;
    
    // -- camera 
    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    boolean previewing = false;
    LayoutInflater controlInflater = null;
    private boolean flag_camera = true; 
    private int cameraId = 0; */
    public String filename = "empty";
    
    String KEY_play_flag; 
    boolean play_flag_local = false;
    String play_flag_l = "false";
    
    // -- an instance of the status broadcast receiver
    DownloadStateReceiver mDownloadStateReceiver;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
      //  getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // -- tell system to use the layout defined in our XML file
        setContentView(R.layout.activity_main_eegmusic);
                       
        // -- get the between instance stored values (status of music player)
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        if (preferences.getString(KEY_play_flag, null) != null){
        	play_flag_l =  preferences.getString(KEY_play_flag, null);
        }
        
        // -- link thread to view     
        mMusicPlayerView = (MusicPlayerView) findViewById(R.id.mp);
        mMusicPlayerThread = mMusicPlayerView.getThread();

        // -- give the MusicPlayerView a handle to the TextView used for messages
        mMusicPlayerView.setTextView((TextView) findViewById(R.id.text));
		tv_info = (TextView) findViewById(R.id.text);
        
		// -- give the GlassView a handle to the TextView used for messages
        tv_Att = (TextView) findViewById(R.id.Att_text);
        tv_Med = (TextView) findViewById(R.id.Med_text);       
        tv_Vel = (TextView) findViewById(R.id.Vel_text);
        tv_AmM = (TextView) findViewById(R.id.AmM_text);
        
        tv_TimeToSel = (TextView) findViewById(R.id.TimeToSel);
        tv_consoleBoard = (TextView) findViewById(R.id.console_info);
        tv_consoleLine = (TextView) findViewById(R.id.console_line);
        
        // =========================================
        // -- in dev
        StartEEGService();
        
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);

        float density  = getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels / density;
        float dpWidth  = outMetrics.widthPixels / density;
               
        Log.d(getString(R.string.app_name), "ir_d: starting thread");
        
        mMusicPlayerThread.doStart(); 
        mMusicPlayerView.getThread().play_flag = Boolean.valueOf(play_flag_l);
        mMusicPlayerView.getThread().pX = dpWidth*1.5;
        mMusicPlayerView.getThread().pY = dpHeight*1.5;

        // tv_Vel.setText(String.valueOf(20));
        //tv_info.setText("STATE_CONNECTING");
        
       // =========================================
        
        
        /* Checking BT and connecting to the TG device */
      /*  bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {	// Alert user that Bluetooth is not available
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        } else {
            tgDevice = new TGDevice(bluetoothAdapter, handler);
            //Toast.makeText(this, "Bluetooth is available ir", Toast.LENGTH_LONG).show();
           // doStuff();
        }	*/
        
        
                           
	}

	@Override
	public void onStart() {    	       
		super.onStart();   
		Log.d(getString(R.string.app_name), "ir_d onStart()");
	}	      
	@Override
	public void onResume() {
		super.onResume();
		mMusicPlayerView.getThread().unpause(); 
        mMusicPlayerView.getThread().setRunning(true);
        
	    Log.d(getString(R.string.app_name), "ir_d onResume()");
	}
    @Override
    public void onPause() {        
        super.onPause();
                
    	mMusicPlayerView.getThread().pause(); // pause game when Activity pauses
        mMusicPlayerView.getThread().setRunning(false); //correctly destroy SurfaceHolder  
       // mMusicPlayerThread.interrupt();
        
        // -- store values between instances here  
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);  
        SharedPreferences.Editor editor = preferences.edit();  // Put the values from the UI 
        	//editor.putBoolean(KEY_play_flag, true); // value to store  
        editor.putString(KEY_play_flag, String.valueOf(mMusicPlayerView.getThread().play_flag));
        	// -- commit to storage 
        editor.commit(); 
           
       
        finish();
        
        Log.d(getString(R.string.app_name), "ir_d onPause()");
    }
    @Override
	public void onStop() { 
		super.onStop();
       /* try {
            if (tgDevice != null) {
                tgDevice.close();
            }

        } catch (NullPointerException e) { }*/
        
        Log.d(getString(R.string.app_name), "ir_d onStop()");
	}
    @Override
	public void onDestroy() {  
    	super.onDestroy();  
        /*try {
            if (tgDevice != null) {
                tgDevice.close();
            }
        } catch (NullPointerException e) { } */

       // mMusicPlayerView.getThread().pause(); // pause game when Activity pauses
       // mMusicPlayerView.getThread().setRunning(false); //correctly destroy SurfaceHolder, ir   
           
        /*// If the DownloadStateReceiver still exists, unregister it and set it to null
        if (mDownloadStateReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mDownloadStateReceiver);
            mDownloadStateReceiver = null;
        }*/
        
	    Log.d(getString(R.string.app_name), "ir_d onDestroy()");
	}    
   
	
	    /**
	     * Notification that something is about to happen, to give the Activity a
	     * chance to save state.
	     * * @param outState a Bundle into which this Activity should save its state
	     */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	        // just have the View's thread save its state into our Bundle
	        super.onSaveInstanceState(outState);
	        //play_flag_local = mMusicPlayerView.getThread().play_flag;
	        //outState.putBoolean(KEY_play_flag, Boolean.valueOf(mMusicPlayerView.getThread().play_flag));
	       // outState.putBoolean(KEY_play_flag, true);
	        //mMusicPlayerView.getThread().saveState(outState);
	}
	   
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
	   super.onRestoreInstanceState(savedInstanceState);
	   	   
       //if (savedInstanceState == null && String.valueOf(savedInstanceState.getBoolean(KEY_play_flag)) != null) {
    	if (savedInstanceState == null) {
          //	play_flag_local = savedInstanceState.getBoolean(KEY_play_flag);
       	
           // we were just launched: set up a new game
          // mGlassThread.setState(GlassThread.STATE_READY);
         //  mMusicPlayerThread.setState(MusicPlayerThread.STATE_READY);
           //Log.w(this.getClass().getName(), "SIS is null");
       } else {
           // we are being restored: resume a previous game
          // mMusicPlayerThread.restoreState(savedInstanceState);        	
           //Log.w(this.getClass().getName(), "SIS is nonnull");
       }
    	//tv_Vel.setText(String.valueOf(true)) ;
	}
	
		// -- tgDevice State
	public void doStuff() {
	   //Toast.makeText(this, "connecting...", Toast.LENGTH_SHORT).show();
	   if (tgDevice.getState() != TGDevice.STATE_CONNECTING && tgDevice.getState() != TGDevice.STATE_CONNECTED) {
	       tgDevice.connect(RAW_ENABLED);
	   }
	}
	    
	// -- Start EEG service in background
	public void StartEEGService() {
        /* Creates an intent filter for DownloadStateReceiver that intercepts broadcast Intents */
        
        // The filter's action is BROADCAST_ACTION
        IntentFilter statusIntentFilter = new IntentFilter(Constants.BROADCAST_ACTION);
        
        // -- sets the filter's category to DEFAULT
        statusIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);
                
        // -- instantiates a new DownloadStateReceiver
        mDownloadStateReceiver = new DownloadStateReceiver();
        
        // -- registers the DownloadStateReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mDownloadStateReceiver, statusIntentFilter);
        
        Intent intent = new Intent(this, EEGService.class);
    	startService(intent);
    		    
	    Log.d(getString(R.string.app_name), "ir_d StartEEGService()");
	}
		
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	        switch (keyCode) {
	            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
	            case KeyEvent.KEYCODE_HOME:
	            {
	 	          // android.os.Process.killProcess(android.os.Process.myPid());
	 	        }
	            case KeyEvent.KEYCODE_HEADSETHOOK:
	                startService(new Intent(MusicService.ACTION_TOGGLE_PLAYBACK));
	                return true;
	        }
	        
	        return super.onKeyDown(keyCode, event);
	}
	   	    
	 /**
     * This class uses the BroadcastReceiver framework to detect and handle status messages from
     * the service that downloads URLs.
     */
    private class DownloadStateReceiver extends BroadcastReceiver {
    	
        private DownloadStateReceiver() {
            
            // prevents instantiation by other packages.
        }
        /**
         *
         * This method is called by the system when a broadcast Intent is matched by this class'
         * intent filters
         *
         * @param context An Android context
         * @param intent The incoming broadcast Intent
         */
       
        @Override
        public void onReceive(Context context, Intent intent) {
        	//tv_info.setText("on Receive");
        	Log.d(getString(R.string.app_name), "State: STARTED");
            /*
             * Gets the status from the Intent's extended data, and chooses the appropriate action
             */
            switch (intent.getIntExtra(Constants.EXTENDED_DATA_STATUS,
                    Constants.STATE_ACTION_STARTED)) {
                
                // Logs "started" state
                case Constants.STATE_ACTION_STARTED:
                    if (Constants.LOGD) {                        
                        Log.d(getString(R.string.app_name), "State: STARTED");
                    }
                    break;
                // Logs "connecting to network" state
                case Constants.STATE_CONNECTING:
                	//tv_info.setText("STATE_CONNECTING");
                	tv_consoleLine.setText("CONNECTING");
                    if (Constants.LOGD) {                        
                        Log.d(getString(R.string.app_name), "State: CONNECTING");
                    }
                    break;

                // Starts displaying data when the RSS download is complete
                case Constants.STATE_CONNECTED:
                    // Logs the status
                	tv_consoleLine.setText("STATE_CONNECTED");
                     
                    if (Constants.LOGD) {                     
                        Log.d(getString(R.string.app_name), "STATE: CONNECTED");
                    }

                default:
                    break;
            }
            
            // -- if the incoming Intent is a sending A/M
            //if (intent.getAction().equals(Constants.EXTENDED_DATA_A)) {
               // tv_Vel.setText(intent.getIntExtra(Constants.EXTENDED_DATA_A, At));
            	At = intent.getIntExtra(Constants.EXTENDED_DATA_A, At);
            	Med = intent.getIntExtra(Constants.EXTENDED_DATA_M, Med);
            	
            	mMusicPlayerThread.setAttention(At);
            	mMusicPlayerThread.setMeditation(Med);
            	//String capital = intent.getStringExtra(Constants.EXTRA_CAPITAL);
                //mTvCapital.setText("Capital : " + capital);
                
            	// --display Att/Med and they difference
            	tv_Att.setText(String.valueOf(At));
            	tv_Med.setText(String.valueOf(Med));
            	tv_AmM.setText(String.valueOf(At-Med)); // display Att-Med
            	// -- change size and color of Att-Med text view                   
	            if (Math.abs(At-Med) <= 15)	{tv_AmM.setTextSize(22); tv_AmM.setTextColor(Color.GRAY);}
	            	else if (Math.abs(At-Med) <= 30) {tv_AmM.setTextSize(22); tv_AmM.setTextColor(Color.GRAY); }
	            
	            if (At-Med < -45 || At-Med > 45) {tv_AmM.setTextSize(25); tv_AmM.setTextColor(Color.GREEN);}
	            	else if (At-Med < -30 || At-Med > 30) {tv_AmM.setTextSize(25); tv_AmM.setTextColor(Color.GREEN); }
	           
	            
		      /*  // -- do appropriate action for music player
	            	// --play/stop/playnext
	            if (mMusicPlayerView.getThread().play_flag == true)
	        		{ 
	            	 startService(new Intent(MusicService.ACTION_PLAY)); 
	            	}
	            if (mMusicPlayerView.getThread().stop_flag == true)
	    			{ 
	            	 startService(new Intent(MusicService.ACTION_STOP));
	    			}            
	            if (mMusicPlayerView.getThread().next_flag == true)
	    			{ 
	                 startService(new Intent(MusicService.ACTION_STOP));
	            	 startService(new Intent(MusicService.ACTION_SKIP));
	    			 mMusicPlayerView.getThread().next_flag = false;
	    			}
	            
	        	// -- display velocity based on accel_alpha [0..2.5]
                float vel = mMusicPlayerView.getThread().accel_alpha;
                if (vel>=2f) {tv_Vel.setText("4");}
                if (vel>=1.5f && vel<2f) {tv_Vel.setText("3");}
                if (vel>=1f && vel<1.5f) {tv_Vel.setText("2");}
                if (vel>=0.5f && vel<1f) {tv_Vel.setText("1");}
                if (vel<0.5f) {tv_Vel.setText("0");}  
	            

            	// -- display time to action selection
	            tv_TimeToSel.setTextColor(Color.WHITE);
	            float tts = mMusicPlayerView.getThread().TimeToSelect;
	            tts = tts*10f;
	            tts = Math.round(tts);
	            tts = tts/10f;
	            //if (tts < 3f && vel<=0 && mMusicPlayerView.getThread().flag_Cursor)
	            //if (mMusicPlayerView.getThread().action_cancel_flag){tv_TimeToSel.setText("cancel");}
	            if (tts < 3f &&  mMusicPlayerView.getThread().flag_Cursor) {
	            	tv_TimeToSel.setTextSize(20); tv_TimeToSel.setText(String.valueOf(Math.round(tts)) ); 
	            	mMusicPlayerView.getThread().msgBoard = " ";}
	            else {
	            	tv_TimeToSel.setTextSize(20); tv_TimeToSel.setText(mMusicPlayerView.getThread().msgBoard);
	            	}
	            if (mMusicPlayerView.getThread().action_cancel_flag){
	            	tv_TimeToSel.setTextSize(20);tv_TimeToSel.setText("cancel");
	            	mMusicPlayerView.getThread().msgBoard = "";
	            	}
	            */
	            // -- setup Board/Console text view
	            tv_consoleBoard.setTextSize(15);tv_consoleBoard.setText(mMusicPlayerView.getThread().consoleBoard);
	            tv_consoleLine.setTextSize(30); tv_consoleLine.setText(mMusicPlayerView.getThread().consoleLine);
            
            //}
            
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
	                    	//mGlassThread.setTGStatus("Connecting...");
	                    	//tv_info.setText("Connecting...");
	                    	//releaseCamera();
	                        break;
	                    case TGDevice.STATE_CONNECTED:
	                        tgDevice.start();
	                        //tv_info.setText("Connected");
	                        // -- start thread with eeg_launcher
	                       // mMusicPlayerThread.doStart(); 
	                        mMusicPlayerView.getThread().play_flag = Boolean.valueOf(play_flag_l);
	                        break;
	                    case TGDevice.STATE_NOT_FOUND:
	                    	//tv_info.setText("neurosky mindwave mobile was not found");
	                        break;
	                    case TGDevice.STATE_NOT_PAIRED:
	                    	//tv_info.setText("neurosky mindwave mobile not paired !!!!!");
	                        break;
	                    case TGDevice.STATE_DISCONNECTED:
	                    	//tv_info.setText("Disconnected");
	                    	//doStuff();
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
	                    //APIClient.postData(null, "attention", String.valueOf(msg.arg1), null); // old
	                	APIClient.collectAttention(null, msg.arg1);
	                	
	                	//Log.e(getString(R.string.app_name), "camera.takePicture()");  
	                		 	               
	                    At = msg.arg1;         
	                    tv_Att.setText(String.valueOf(At));
	                    mMusicPlayerThread.setAttention(At);
	                    
	                    // -- do appropriate action for music player
	                    	// --play/stop/playnext
	                    if (mMusicPlayerView.getThread().play_flag == true)
                    		{ 
	                    	 startService(new Intent(MusicService.ACTION_PLAY)); 
	                    	}
	                    if (mMusicPlayerView.getThread().stop_flag == true)
                			{ 
	                    	 startService(new Intent(MusicService.ACTION_STOP));
                			}
	                    
	                    if (mMusicPlayerView.getThread().next_flag == true)
                			{ 
	                    	 startService(new Intent(MusicService.ACTION_SKIP));
                			 mMusicPlayerView.getThread().next_flag = false;
                			}
	                    
	                   /* if (mMusicPlayerView.getThread().back_flag == true)
            				{ 
	                    	 //startService(new Intent(MusicService.ACTION_PAUSE));
	                    	 startService(new Intent(MusicService.ACTION_STOP)); 
	                    	 mMusicPlayerView.getThread().back_flag = false;
            				// onBackPressed();
            				} */
	                    	                    
	                    	// -- display velocity based on accel_alpha [0..2.5]
	                    float vel = mMusicPlayerView.getThread().accel_alpha;
	                    if (vel>=2f) {tv_Vel.setText("4");}
	                    if (vel>=1.5f && vel<2f) {tv_Vel.setText("3");}
	                    if (vel>=1f && vel<1.5f) {tv_Vel.setText("2");}
	                    if (vel>=0.5f && vel<1f) {tv_Vel.setText("1");}
	                    if (vel<0.5f) {tv_Vel.setText("0");}  
	                    
	                    // -- for testing
	                    //tv_Vel.setText(String.valueOf(mMusicPlayerView.getThread().play_flag)) ;
	                    
	                    	// -- display time to action selection
	                    tv_TimeToSel.setTextColor(Color.WHITE);
	                    float tts = mMusicPlayerView.getThread().TimeToSelect;
	                    tts = tts*10f;
	                    tts = Math.round(tts);
	                    tts = tts/10f;
	                    //if (tts < 3f && vel<=0 && mMusicPlayerView.getThread().flag_Cursor)
	                    //if (mMusicPlayerView.getThread().action_cancel_flag){tv_TimeToSel.setText("cancel");}
	                    if (tts < 3f &&  mMusicPlayerView.getThread().flag_Cursor) {
	                    	tv_TimeToSel.setTextSize(20); tv_TimeToSel.setText(String.valueOf(Math.round(tts)) ); 
	                    	mMusicPlayerView.getThread().msgBoard = " ";}
	                    else {
	                    	tv_TimeToSel.setTextSize(20); tv_TimeToSel.setText(mMusicPlayerView.getThread().msgBoard);
	                    	}
	                    if (mMusicPlayerView.getThread().action_cancel_flag){
	                    	tv_TimeToSel.setTextSize(20);tv_TimeToSel.setText("cancel");
	                    	mMusicPlayerView.getThread().msgBoard = "";
	                    	}
	                    
	                    tv_consoleBoard.setTextSize(15);tv_consoleBoard.setText(mMusicPlayerView.getThread().consoleBoard);
	                    tv_consoleLine.setTextSize(30); tv_consoleLine.setText(mMusicPlayerView.getThread().consoleLine);
	                    	                    		                    
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
	                    //APIClient.postData(null, "meditation", String.valueOf(msg.arg1), null); //old code
	                	APIClient.collectMeditation(null, msg.arg1);

	                    Med = msg.arg1;
	                    tv_Med.setText(String.valueOf(Med));
	                    mMusicPlayerThread.setMeditation(Med);
	                    	 
	                    tv_AmM.setText(String.valueOf(At-Med)); // display Att-Med
	                    	// -- change size and color of Att-Med text view                   
	                    if (Math.abs(At-Med) <= 15)	{tv_AmM.setTextSize(22); tv_AmM.setTextColor(Color.GRAY);}
	                    	else if (Math.abs(At-Med) <= 30) {tv_AmM.setTextSize(22); tv_AmM.setTextColor(Color.GRAY); }
	                    
	                    if (At-Med < -45 || At-Med > 45) {tv_AmM.setTextSize(25); tv_AmM.setTextColor(Color.GREEN);}
	                    	else if (At-Med < -30 || At-Med > 30) {tv_AmM.setTextSize(25); tv_AmM.setTextColor(Color.GREEN); }
	                                                           
	                    
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

@Override
public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	// TODO Auto-generated method stub
	
}

@Override
public void surfaceCreated(SurfaceHolder arg0) {
	// TODO Auto-generated method stub
	
}

@Override
public void surfaceDestroyed(SurfaceHolder arg0) {
	// TODO Auto-generated method stub
	
}

	
	
}
