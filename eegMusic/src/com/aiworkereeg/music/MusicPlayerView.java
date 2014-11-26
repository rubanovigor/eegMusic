package com.aiworkereeg.music;

//public class MusicPlayerView {}

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import com.aiworkereeg.music.MusicService;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;


/**
* GlassView - main game screen
*/
class MusicPlayerView extends SurfaceView implements SurfaceHolder.Callback {
  class MusicPlayerThread extends Thread {
  	float RR; float ZZ;
  	String GameMode="b"; String msgBoard=""; String consoleBoard=""; String consoleLine="";
  	int EEG_launcher_N = 3; int delta = 360/EEG_launcher_N; 
  	int ClusterLeftX = -30; int ClusterCenterX = 0; int ClusterRightX = 30; int ClusterDelta = 0; 
  	String flag; 
  	boolean play_flag = false; boolean stop_flag = false; boolean back_flag = false;
  	boolean next_flag = false; 	boolean Prtscr_flag = false; boolean Picture_flag = false;

  	// -- eeg launcher by default
  	//boolean EegLauncherFlag = true;	boolean MusicPlayerFlag = false;
  	//boolean DnaConsoleFlag = false; boolean CameraFlag = false;
  	// -- camera by default
  	//boolean EegLauncherFlag = false;	boolean MusicPlayerFlag = false;
  	//boolean DnaConsoleFlag = false; boolean CameraFlag = true;
  	// -- terminal by default
  	//boolean EegLauncherFlag = false;	boolean MusicPlayerFlag = false;
  	//boolean DnaConsoleFlag = true; boolean CameraFlag = false;
  	// -- music by default
  	boolean EegLauncherFlag = false;	boolean MusicPlayerFlag = true;
  	boolean DnaConsoleFlag = false; boolean CameraFlag = false;
  	
  	boolean action_cancel_flag = false;
      int At = 50; int Med = 50;   int ApM = 100;    int AmM = 0; 
      int console_length = 4;
      String[] console_str = new String[] {"-","-","-","-"};
      String console_str_action; 
      
      CharSequence TGStatus;
      float alpha = 0;  float alpha1_1=0;
      float scale_obj = 0.7f; float scale_obj_rot = 0.7f; float scale_obj_console = 0.5f;
    //  float CircleRadius = 1f; //280f;   // -- for android phone
      float CircleRadius = 100f;		// -- for google glass
      float accel_alpha = 0f;
      double elapsed = 0;   
      float curr_alpha_obj1 = 0; float curr_alpha_obj2 = 0; float curr_alpha_obj3 = 0; float curr_alpha_obj4 = 0;
      float curr_alpha_obj5 = 0; float curr_alpha_obj6 = 0; float curr_alpha_obj7 = 0; float curr_alpha_obj8 = 0;
      int[] obj1_center; int sel_action_i; float CursorX; float CursorY;
      boolean flag_Cursor = false;  int CursorI = 0; int CursorJ=1;
      float CursorX_delta = 1f; float CursorY_delta = 1f;      float TimeToSelect = 3f;
      boolean flag_submit = false;   
      int GenBinLet1 = -1;        int GenBinLet2 = -1;  String GenLetSucc = ""; 
      
      // -- declare objects for EEG Launcher
      private InterfaceBody LauncherMusicPlayer; 
      private InterfaceBody SelectedIcon; private InterfaceBody LeftBoarderIcon; private InterfaceBody RigthBoarderIcon;
      
      
      // -- declare objects for Music Player
      private InterfaceBody mp_Play;// private InterfaceBody mp_PlayD; 
      private InterfaceBody mp_Stop;//private InterfaceBody mp_StopD;
      private InterfaceBody mp_Next; //private InterfaceBody mp_NextD;
            	
      private float StarR; private float CursorR;  private float R_Gr_sphere_C;  private float R_Gr_sphere_S; 
      
      float Cx_lt_l1; float Cx_rb_l1;   float Cy_lt_l1; float Cy_rb_l1;
      
      float S2P_dist_rt; float S2P_dist_lt; float S2P_dist_rb; float S2P_dist_lb; 
      float P2C_dist;     float S2C_dist;
      float S2P_scale_lt;  float S2P_scale_rt; float S2P_scale_rb;  float S2P_scale_lb;
      float S2P_scale_lt_l2;
      float Scale_Gr_sphere_C = 1;
      
      int Level1_flag;         int Level2_flag;         int Level3_flag;
      double P_S1l1_rr; double P_S2l1_rr; 
      double P_S1l2_rr; double P_S2l2_rr;
      double P_S1l3_rr; double P_S2l3_rr;
      
      	// -- Complex px py
      float pX_C = 0; float pY_C = 0;
      float Z_re = 0; float Z_im = 0; float Z_re_sq=0; float Z_im_sq=0;float Z_re_f=0; float Z_im_f=0;
      
             
      int V_coeff = 4; // Velocity V=At/V_coeff*/
      int ScreenFlag = 1; //control game room(screen)
      
      /* background block */
      private Drawable BackGr_Image; private Drawable BackGr_Image1; private Drawable BackGr_Image2;
      private float BackGr_ImageScaleMax = 0.7f;
      private float BackGr_ImageScale = BackGr_ImageScaleMax;
      private int BackGr_ImageScalePi = 1; //  Pi/BackGr_ImageScalePi
                
      /** X/Y of lander center. */
      double pX = -500;        double pY = -500;
              
      //================================        
      /* State-tracking constants */
      public static final int STATE_LOSE = 1;       public static final int STATE_PAUSE = 2;
      public static final int STATE_READY = 3;      public static final int STATE_RUNNING = 4;
      public static final int STATE_WIN = 5;

      /** UI constants (i.e. the speed & fuel bars) */
     // private static final String KEY_X = "pX";      private static final String KEY_Y = "pY";
      private static final String KEY_play_flag = "play_flag"; 
      //private static final String KEY_DNA = "DnaConsoleFlag";
      //private static final String KEY_MP = "MusicPlayerFlag";

      /** Current height/width of the surface/canvas. @see #setSurfaceSize        */
      private int BackGr_H = 1;        private int BackGr_W = 1;

      /** Message handler used by thread to interact with TextView */
      private Handler mHandler;

      /** Used to figure out elapsed time between frames */
      private long mLastTime;

      /** The state of the game. One of READY, RUNNING, PAUSE, LOSE, or WIN */
      private int mMode;

      /** Indicate whether the surface has been created & is ready to draw */
      private boolean mRun = false;

      /** Handle to the surface manager object we interact with */
      private SurfaceHolder mSurfaceHolder;

      public MusicPlayerThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {
          // get handles to some important objects
          mSurfaceHolder = surfaceHolder;   mHandler = handler;      mContext = context;

          // .setSecure(true);
            
          Resources res = context.getResources();  
          // -- setup objects for EEG Launcher
         // LauncherMusicPlayer = new InterfaceBody(res.getDrawable(R.drawable.icon_musicplayer), scale_obj);
          
          SelectedIcon = new InterfaceBody(res.getDrawable(R.drawable.icon_selected_icon), scale_obj);
          LeftBoarderIcon = new InterfaceBody(res.getDrawable(R.drawable.icon_boarder), scale_obj);
          RigthBoarderIcon = new InterfaceBody(res.getDrawable(R.drawable.icon_boarder), scale_obj);

                     
          // -- setup objects for MusicPlayer
          mp_Stop = new InterfaceBody(res.getDrawable(R.drawable.icon_stop_white), scale_obj); // image,scale
          mp_Play = new InterfaceBody(res.getDrawable(R.drawable.icon_play_white), scale_obj); 
          mp_Next = new InterfaceBody(res.getDrawable(R.drawable.icon_next_white), scale_obj); 
         
          
          StarR = mp_Stop.getImageWidth()/2; // all stars has the same Radius
          StarR = StarR * scale_obj;  // adopt star size to screan using scale
          CursorR = StarR;                 
          
          // cache handles to our key drawables
          BackGr_Image = context.getResources().getDrawable(R.drawable.blacksquare);
          //BackGr_Image = context.getResources().getDrawable(R.drawable.test);
         
      }

      /** Starts the game, setting parameters for the current difficulty.  */
      public void doStart() {
          synchronized (mSurfaceHolder) {
              Level1_flag = 0;  Level2_flag = 0;  Level3_flag = 0; 
                                    
              
              // pick a convenient initial location for the lander sprite
              /* ===set random initial shuttle X coordinate=== */
             // BackGr_H = 1600; BackGr_W = 1000;
             // pY = -BackGr_H/2;  pX = BackGr_W/2;
              //pY = 800;  pX = 500;
              
              CursorX = (float) pX - CursorR;
              CursorY_delta = BackGr_ImageScale*BackGr_W/2;
              //CircleRadius = BackGr_ImageScale*BackGr_W / 2.7f;
              CircleRadius = (float) ((pX)/1.85);
              CursorX_delta = StarR*2f;
            //==========InterfaceBody===========
             
              	// 3 left-top
	          Cx_lt_l1 = (float)pX;   
	          //Cy_lt_l1 = (float)(pY - CircleRadius);
	          Cy_lt_l1 = (float)(pY);
              mp_Play.setCenterCoordinates(Cx_lt_l1, Cy_lt_l1); mp_Play.setScale(scale_obj); mp_Play.setAlpha(0f);	                
	                
	            	// 4 right-bottom
	          Cx_rb_l1 = (float)pX;   
              Cy_rb_l1 = (float)(pY + CircleRadius);  
              
             // float iconXc = (float)(pX - CircleRadius - 1.5f*StarR); 
              //float iconYc = (float)(pY - CircleRadius);
              mp_Next.setCenterCoordinates(Cx_rb_l1, Cy_rb_l1); mp_Next.setScale(scale_obj); mp_Next.setAlpha(0f);
              
              mLastTime = System.currentTimeMillis() + 100;
              setState(STATE_RUNNING);
          }
      }

      /** Pauses the physics update & animation.   */
      public void pause() {
          synchronized (mSurfaceHolder) {
              if (mMode == STATE_RUNNING) setState(STATE_PAUSE);
          }
      }

      /**
       * Restores game state from the indicated Bundle. Typically called when
       * the Activity is being restored after having been previously
       * destroyed.
       *
       * @param savedState Bundle containing the game state
       */
      public synchronized void restoreState(Bundle savedState) {
          synchronized (mSurfaceHolder) {
              setState(STATE_PAUSE);
             // mRotating = 0;
            /*  pX = savedState.getDouble(KEY_X);
              pY = savedState.getDouble(KEY_Y);*/
              
              play_flag = savedState.getBoolean(KEY_play_flag);
            /*  DnaConsoleFlag = savedState.getBoolean(KEY_DNA);
              MusicPlayerFlag = savedState.getBoolean(KEY_MP);*/
          }
      }

      @Override
      public void run() {
          while (mRun) {
              Canvas c = null;
              try {
                  c = mSurfaceHolder.lockCanvas(null);
                  synchronized (mSurfaceHolder) {
                      if (mMode == STATE_RUNNING) updatePhysics();
                      doDraw(c);
                  }
              } finally {
                  // do this in a finally so that if an exception is thrown
                  // during the above, we don't leave the Surface in an
                  // inconsistent state
                  if (c != null) {
                      mSurfaceHolder.unlockCanvasAndPost(c);
                      
                  }
              }
          }
      }

      /**
       * Dump game state to the provided Bundle. Typically called when the
       * Activity is being suspended.
       *
       * @return Bundle with this view's state
       */
      public Bundle saveState(Bundle map) {
          synchronized (mSurfaceHolder) {
              if (map != null) {
              /*    map.putDouble(KEY_X, Double.valueOf(pX));
                  map.putDouble(KEY_Y, Double.valueOf(pY)); */
            	  
                  //map.putBoolean(KEY_play_flag, Boolean.valueOf(play_flag));
                  map.putBoolean(KEY_play_flag, Boolean.valueOf(true));
              /*    map.putBoolean(KEY_DNA, Boolean.valueOf(DnaConsoleFlag));
                  map.putBoolean(KEY_MP , Boolean.valueOf(MusicPlayerFlag));*/
              }
          }
          return map;
      }
      
      /** Set current Attention.   */
      public void setAttention(int Attention) {
          synchronized (mSurfaceHolder) {
              At = Attention;
          }
      }
      
      /** Set current Meditation.  */
      public void setMeditation(int Meditation) {
          synchronized (mSurfaceHolder) {
              Med = Meditation;
          }
      }
      
      /** Set current Meditation.  */
      public void setGameMode(String GM) {
          synchronized (mSurfaceHolder) {
              GameMode = GM;
              //flag = String.valueOf(GameMode);
          }
      }
      
      /** Set TG status.  */
      public void setTGStatus(CharSequence TGStatus_l) {
          synchronized (mSurfaceHolder) {
          	TGStatus = TGStatus_l;
          }
      }

      /**
       * Used to signal the thread whether it should be running or not.
       * Passing true allows the thread to run; passing false will shut it
       * down if it's already running. Calling start() after this was most
       * recently called with false will result in an immediate shutdown.
       *
       * @param b true to run, false to shut down
       */
      public void setRunning(boolean b) {
          mRun = b;
      }

      /**
       * Sets the game mode. That is, whether we are running, paused etc.
       *
       * @see #setState(int, CharSequence)
       * @param mode one of the STATE_* constants
       */
      public void setState(int mode) {
          synchronized (mSurfaceHolder) {
              setState(mode, null);
          }
      }

      /**
       * Sets the game mode. That is, whether we are running, paused, in the
       * failure state, in the victory state, etc.
       *
       * @param mode one of the STATE_* constants
       * @param message string to add to screen or null
       */
      public void setState(int mode, CharSequence message) {
          /*
           * This method optionally can cause a text message to be displayed
           * to the user when the mode changes. Since the View that actually
           * renders that text is part of the main View hierarchy and not
           * owned by this thread, we can't touch the state of that View.
           * Instead we use a Message + Handler to relay commands to the main
           * thread, which updates the user-text View.
           */
          synchronized (mSurfaceHolder) {           	
              mMode = mode;
              CharSequence str ="";
              if (mMode == STATE_RUNNING) {
              	str =  "";
              	//+ "CursorI = "+ delta + "  \n "
              	//+ "CursorI = "+ String.valueOf(CursorI) + "  \n ";
              	
              } else {
                  Resources res = mContext.getResources();      str = "";
                  
                  if (message != null) {str = message + "\n" + str; }
                }
                  Message msg = mHandler.obtainMessage();
                  Bundle b = new Bundle();
                  b.putString("text", str.toString());
                  b.putInt("viz", View.VISIBLE);
                  msg.setData(b);
                  mHandler.sendMessage(msg);
              //}
          }
      }

      /* Callback invoked when the surface dimensions change. */
      public void setSurfaceSize(int width, int height) {
          // synchronized to make sure these all change atomically
          synchronized (mSurfaceHolder) {
              BackGr_W = width;
              BackGr_H = height;

              // don't forget to resize the background image
             // mBackgroundImage = Bitmap.createScaledBitmap(mBackgroundImage, width, height, true);
          }
      }

      /** Resumes from a pause.    */
      public void unpause() {
          // Move the real time clock up to now
          synchronized (mSurfaceHolder) {
              mLastTime = System.currentTimeMillis() + 100;
          }
          setState(STATE_RUNNING);
      }


      /**
       * Draws the shuttle, space bodies and background to the provided Canvas.
       */
      private void doDraw(Canvas canvas) {
          // Draw the background image. Operations on the Canvas accumulate so this is like clearing the screen.
                   
          //moving one image on background
          canvas.save();
          canvas.scale(BackGr_ImageScale, BackGr_ImageScale, (float)BackGr_W/2f , (float)BackGr_H/2f); // scale
          BackGr_Image.setBounds(0, (int)(BackGr_H/2f - BackGr_W/2f), BackGr_W, (int)(BackGr_H/2f + BackGr_W/2f));          
          BackGr_Image.draw(canvas);        
          canvas.restore();   
                  
          LeftBoarderIcon.drawTo(canvas);  RigthBoarderIcon.drawTo(canvas); 

          if (play_flag == false){ mp_Play.drawTo(canvas); }
          if (play_flag == true) { mp_Stop.drawTo(canvas); }
	      mp_Next.drawTo(canvas); 
                       
          SelectedIcon.drawTo(canvas);             
          
          
  }

      /**
       * Figures the objects state (x, y, ...) based on the passage of
       * realtime. Does not invalidate(). Called at the start of draw().
       * Detects the end-of-game and sets the UI to the next state.
       */
      private void updatePhysics() {
          long now = System.currentTimeMillis();

          // -- Do nothing if mLastTime is in the future.
          // -- This allows the game-start to delay the start of the physics by 100ms or whatever.
          if (mLastTime > now) return;
          // -- double elapsed = (now - mLastTime) / 1000.0;
          //elapsed = (now - mLastTime) / 1000.0;
          elapsed = (now - mLastTime) / 1000.0;
          
          /* -- =========== -- */
          ApM = At+Med;                       
          AmM = At-Med;
          	// 1s: 10 * 0.015 = 0.15 
         /* if (accel_alpha>=2.5f) {accel_alpha = 2.5f; scale_obj = scale_obj_rot; }  // -- limit rotational speed
          if (accel_alpha>=2f && accel_alpha<=2.5f) {scale_obj = scale_obj_rot; }
          if (accel_alpha>=1.5f && accel_alpha<2.0f) {scale_obj = scale_obj_rot; }
          if (accel_alpha>=1f && accel_alpha<1.5f) {scale_obj = scale_obj_rot; }          
          if (accel_alpha<=0f){ accel_alpha = 0f; scale_obj = scale_obj_rot;  }*/
          
          if (accel_alpha>=2.5f) {accel_alpha = 2.5f; scale_obj = scale_obj_rot; }  // -- limit rotational speed
          if (accel_alpha>=2f && accel_alpha<=2.5f) {scale_obj = scale_obj_rot; }
          if (accel_alpha>=1.5f && accel_alpha<2.0f) {scale_obj = scale_obj_rot; }
          if (accel_alpha>=1f && accel_alpha<1.5f) {scale_obj = scale_obj_rot; }          
          if (accel_alpha<=0f){ accel_alpha = 0f; scale_obj = scale_obj_rot;  }
          
          	// -- time to action selection
          if (accel_alpha<=0f && CursorI != 0){ TimeToSelect = TimeToSelect - 0.015f; action_cancel_flag = false;}
          	else {
          		if (TimeToSelect>0f && TimeToSelect<=2f && accel_alpha>-5f && accel_alpha<=1f){ action_cancel_flag = true;}	
          		TimeToSelect = 3f;
          		}
          if (TimeToSelect == 3f && accel_alpha>1f){ action_cancel_flag = false;}	
         
          if (TimeToSelect<=0f){ TimeToSelect = 0f;}
          
          //ClusterLeftX = -30; ClusterCenterX = 0; ClusterRightX = 30; ClusterDelta = 0;
          	// -- update accel_alpha and alpha
          if (At-Med >= ClusterLeftX-ClusterDelta &&
          	At-Med <= ClusterRightX+ClusterDelta &&
          	accel_alpha > 0) 
          				{ accel_alpha = accel_alpha - 0.35f*(float)elapsed ; alpha = alpha + accel_alpha; }
          else {
              if (At-Med > ClusterRightX+ClusterDelta )
              			{ accel_alpha = accel_alpha + 0.35f*(float)elapsed ; alpha = alpha + accel_alpha; } 
              else {
              	if (At-Med < ClusterLeftX-ClusterDelta )
              			{ accel_alpha = accel_alpha + 0.35f*(float)elapsed ; alpha = alpha + accel_alpha; }
              }                    
          }       
          if (alpha >=360) {alpha = alpha-360; }
              // -- reset flag if acceleration increase above 1 
          if (accel_alpha>1f && CursorI<=console_length) {flag_Cursor=true; }
          	
          if (CursorI == 0 && accel_alpha>0.5f) {CursorI = 1; }                    
          
          SelectedIcon.setScale(0f); 
          
          if (MusicPlayerFlag == true) {EEG_launcher_N = 2; }
          delta = 360/EEG_launcher_N;
            // - up to 4 items could be added to GUI
          curr_alpha_obj1 = alpha; curr_alpha_obj2 = alpha + delta; 
          curr_alpha_obj3 = alpha + 2*delta; curr_alpha_obj4 = alpha + 3*delta;

          
          if (curr_alpha_obj1 > 360) {curr_alpha_obj1=curr_alpha_obj1 - 360;}
          if (curr_alpha_obj2 > 360) {curr_alpha_obj2=curr_alpha_obj2 - 360;}
          if (curr_alpha_obj3 > 360) {curr_alpha_obj3=curr_alpha_obj3 - 360;}
          if (curr_alpha_obj4 > 360) {curr_alpha_obj4=curr_alpha_obj4 - 360;}
          
          LeftBoarderIcon.updatePhysics(delta/2, 360 - delta/2, CircleRadius, pX, pY);
          RigthBoarderIcon.updatePhysics(-delta/2, delta/2, CircleRadius, pX, pY);
          
          
          
          /* -- ======MusicPlayer===== -- */
          	consoleBoard = " "; 
	            float[] alpha_rot = new float[] {0.0f, 0.0f, 0.8f, 0.8f, 1f, 0.9f };  Random rs1 =new Random();
	            if (play_flag == true){
	            	mp_Stop.updatePhysics(alpha_rot[rs1.nextInt(2)], curr_alpha_obj1, CircleRadius, pX, pY); 
	            	mp_Stop.setScale(scale_obj);          
	            }
	            if (play_flag == false){
	            	mp_Play.updatePhysics(alpha_rot[rs1.nextInt(1)], curr_alpha_obj1, CircleRadius, pX, pY); 
	            	mp_Play.setScale(scale_obj);     
	            }
	            mp_Next.updatePhysics(alpha_rot[rs1.nextInt(2)], curr_alpha_obj2, CircleRadius, pX, pY); 
	            mp_Next.setScale(scale_obj);                       
             
	            // -- set coordinates of Icon SelectedIcon  
	            if (accel_alpha <= 0 && flag_Cursor == true && TimeToSelect<3){ 
	            	SelectedIcon.setScale(scale_obj*1.4f);
	            	
	            	// -- play / stop
	            	//if (curr_alpha_obj1 > 360 - delta/2  ||  curr_alpha_obj1 <= delta/2){ 
	            	if (curr_alpha_obj1 > delta/2  &&  curr_alpha_obj1 <= 360-delta/2){ 
	            		SelectedIcon.updatePhysics(alpha_rot[rs1.nextInt(2)], curr_alpha_obj1, CircleRadius, pX, pY); 
	            	}            		            	
	            	// -- play next
	            	//if (curr_alpha_obj2 > 360 - delta/2  ||  curr_alpha_obj2 <= delta/2){
	            	if (curr_alpha_obj2 > delta/2  &&  curr_alpha_obj2 <= 360-delta/2){ 
	            		SelectedIcon.updatePhysics(alpha_rot[rs1.nextInt(2)], curr_alpha_obj2, CircleRadius, pX, pY); 
	            	}           	            	                  	
	
	            }
	                             
	            // -- manage what was selected  
	            if (accel_alpha <= 0 && flag_Cursor == true && TimeToSelect<=0){            	
         	
	            	// -- play / stop
	            	//if (curr_alpha_obj1 > 360 - delta/2  ||  curr_alpha_obj1 <= delta/2){ 
	            	if (curr_alpha_obj1 > delta/2  &&  curr_alpha_obj1 <= 360-delta/2){ 
	            		if (play_flag == false)	{ play_flag = true; stop_flag = false; sel_action_i = 1; msgBoard = "play";}
	            		else          			{ play_flag = false; stop_flag = true; sel_action_i = 2; msgBoard = "stop";}	            		
	            	} 
	            		            	
	            	// -- play next
	            	//if (curr_alpha_obj2 > 360 - delta/2  ||  curr_alpha_obj2 <= delta/2){
	            	if (curr_alpha_obj2 > delta/2  &&  curr_alpha_obj2 <= 360-delta/2){ 
	            		next_flag = true; play_flag = true; stop_flag = false; sel_action_i = 3; 	msgBoard = "play next";
	            	}	            	
	            	                  	            			            	
	            	flag_Cursor = false;            	
	
          		MusicPlayerFlag = false; EegLauncherFlag = true;
	            }
	            
          /* -- ======END MusicPlayer===== -- */          
                
          
 
                   
          setState(STATE_RUNNING);  //set game status and update and print message
          
          mLastTime = now;
          
      }
             
      private void ClearConsole() {
  		msgBoard = "C";
  		console_str[0] = "-"; console_str[1] = "-"; console_str[2] = "-"; console_str[3] = "-";
  		CursorI = 1; consoleBoard = "C - clear";
      }
  }

  /** Handle to the application context, used to e.g. fetch Drawables. */
  private Context mContext;

  /** Pointer to the text view to display "Paused.." etc. */
  private TextView mStatusText;

  /** The thread that actually draws the animation */
  private MusicPlayerThread thread;

  public MusicPlayerView(Context context, AttributeSet attrs) {
      super(context, attrs);

      // register our interest in hearing about changes to our surface
      SurfaceHolder holder = getHolder();
      holder.addCallback(this);

      // create thread only; it's started in surfaceCreated()
      thread = new MusicPlayerThread(holder, context, new Handler() {
          @Override
          public void handleMessage(Message m) {
              mStatusText.setVisibility(m.getData().getInt("viz"));
              mStatusText.setText(m.getData().getString("text"));
          }
      });
      

      setFocusable(true); // make sure we get key events
  }

  /**
   * Fetches the animation thread corresponding to this MusicPlayerView.
   *
   * @return the animation thread
   */
  public MusicPlayerThread getThread() {
      return thread;
  }

  /**
   * Standard window-focus override. Notice focus lost so we can pause on
   * focus lost. e.g. user switches to take a call.
   */
  @Override
  public void onWindowFocusChanged(boolean hasWindowFocus) {
      if (!hasWindowFocus) thread.pause();
  }

  /**     * Installs a pointer to the text view used for messages.     */
  public void setTextView(TextView textView) {
      mStatusText = textView;
  }

  /* Callback invoked when the surface dimensions change. */
  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
      thread.setSurfaceSize(width, height);
  }

  /*
   * Callback invoked when the Surface has been created and is ready to be
   * used.
   */
  public void surfaceCreated(SurfaceHolder holder) {
      // start the thread here so that we don't busy-wait in run()
      // waiting for the surface to be created
      if(thread.getState() == Thread.State.TERMINATED)
      {
      //MusicPlayerThread state TERMINATED..make new...under CheckCreateThread
      	thread = new MusicPlayerThread(holder, mContext, new Handler());
      } 
      
      thread.setRunning(true);
      thread.start();
  }

  /*
   * Callback invoked when the Surface has been destroyed and must no longer
   * be touched. WARNING: after this method returns, the Surface/Canvas must
   * never be touched again!
   */
  public void surfaceDestroyed(SurfaceHolder holder) {
      // we have to tell thread to shut down & wait for it to finish, or else
      // it might touch the Surface after we return and explode
      boolean retry = true;
      
      
      thread.setRunning(false);
      while (retry) {
          try {
              thread.join();
              retry = false;
          } catch (InterruptedException e) {
          }
      }
  }
}

