package com.ezraanderson.framebuffer;

import javax.microedition.khronos.egl.EGL10;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;





import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Canvas;


import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;

import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import android.app.Activity;
import com.ziplinegames.moai.*;
//ouya
import tv.ouya.console.api.OuyaController;





/**
    SDL Activity MoaiActivity
*/
public class MoaiActivity extends Activity {

    
    // Main components
    protected static MoaiActivity mSingleton;   
    
    
    protected static  myView mySurface;  
    protected static  ViewGroup mLayout;    


    // EGL objects
    protected static EGLContext  mEGLContext;
    protected static EGLSurface  mEGLSurface;
    protected static EGLDisplay  mEGLDisplay;
    protected static EGLConfig   mEGLConfig;
    
    
    protected static int mGLMajor = 2; 
    protected static int mGLMinor = 0;


    static {
    	

        MoaiLog.i ( "Loading libmoai.so" );  
        		System.loadLibrary ( "moai" );     
        
    }
    
    
    

    // Setup
    @Override
    protected void onCreate(Bundle savedInstanceState) {       
        
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());      


            
  super.onCreate(savedInstanceState);
    

    OuyaController.init(this);


    Log.v("SDL", "trace-0: create SDL()");    


    Moai.onCreate ( this );
    Moai.createContext ();                 
  
  	Moai.init ();   
  	
    Moai.setScreenSize (1280,720 );                
    Moai.setViewSize (1280,720);	  
    
    Moai.startSession ( true );
    Moai.setApplicationState ( Moai.ApplicationState.APPLICATION_RUNNING );
    
    

    mSingleton 	= this;    
    
	mySurface 	= new myView(this.getApplicationContext()); 	
	
	mySurface.getHolder().setFixedSize(1280,720 ); 

	LinearLayoutIMETrap myLayout = MoaiKeyboard.getContainer ();
	
	
	setContentView ( myLayout );

	myLayout.addView ( mySurface,0); 


//*****************
//SET WORKING
Log.v("SDL", "trace-0: Set Path()");    


		try {
		
		ApplicationInfo myApp = getPackageManager ().getApplicationInfo ( getPackageName (), 0 );  
		
				Moai.mount ( "bundle", myApp.publicSourceDir );
				Moai.setWorkingDirectory ( "bundle/assets/lua" );
				
		} catch ( NameNotFoundException e ) {
			
					MoaiLog.e ( "MoaiActivity onCreate: Unable to locate the application bundle" );
					
		}



//************
//SET DOCUMENT    

		if ( getFilesDir () != null ) {       
				Moai.setDocumentDirectory ( getFilesDir ().getAbsolutePath ());    		
		} else {
			    MoaiLog.e ( "MoaiActivity onCreate: Unable to set the document directory" );
		}      
		
				 
			      
                 
}  //end onCreate    
                 


    


  // Events
    @Override
    protected void onPause() {
        Log.v("SDL", "onPause()");
        super.onPause();
        // Don't call MoaiActivity.nativePause(); here, it will be called by SDLSurface::surfaceDestroyed
    }
    
    
    
    
    

    @Override
    protected void onResume() {
       
        super.onResume();
        
          Log.v("SDL", "trace-1: onResume()");
          Log.v("SDL", "Moai.onStart ");          
          		Moai.onStart ();         
          		
    }

    
    
    @Override
    public void onLowMemory() {
        Log.v("SDL", "onLowMemory()");
        super.onLowMemory();
        
    }

    
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("SDL", "onDestroy()");
  	
            // Send a quit message to the application
         
            
        	MoaiLog.i ( "MoaiActivity onDestroy: activity DESTROYED" );   		
    		
    		Moai.stopGame(); 
    		Moai.onDestroy ();    		
    		Moai.finish ();
  		
    		super.onDestroy ();	
    		


    }


    
 
  


////////////////////////////////////////////////////
//run lua scripts
    
    public static void runScripts(String[] strings) {

        for ( String file : strings ) {          
        			MoaiLog.i ( "MoaiRenderer runScripts: Running " + file + " script" );            
        			Moai.runScript ( file );
        }  
      }


    

 
 //*****************************************************************************************************
 //ouya controller   
	@Override
public boolean onKeyDown (int keyCode, KeyEvent event) {

		boolean handled = OuyaController.onKeyDown(keyCode, event);
		int player = OuyaController.getPlayerNumByDeviceId(event.getDeviceId());
		
		
	     if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
	    	 Log.v("SDL", "dosn'et work");   
	     }

	     
	     
		Moai.buttonDown(keyCode, 1);
		//MoaiOuya.NotifyOuyaButtonDown(keyCode, player);
		
 
		
 
return handled || super.onKeyDown(keyCode, event);
}


	
	
@Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
	
	
        boolean handled = OuyaController.onKeyUp(keyCode, event);
        
        	int player = OuyaController.getPlayerNumByDeviceId(event.getDeviceId());
        	
        	Moai.buttonUp(keyCode, 1);	
        	
        	//MoaiOuya.NotifyOuyaButtonUp(keyCode, player);
        
        return handled || super.onKeyUp(keyCode, event);
    }




@Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        boolean handled = OuyaController.onGenericMotionEvent(event);
      
        int player = OuyaController.getPlayerNumByDeviceId(event.getDeviceId());


   
        if ((event.getSource() & InputDevice.SOURCE_CLASS_POINTER) != 0){

                float touchpadX = event.getX();
                float touchpadY = event.getY();

              //  MoaiOuya.NotifyOuyaMotionEventTouchpad( touchpadX, touchpadY, player);
        }
  
        else{
                float leftAxisX = event.getAxisValue(OuyaController.AXIS_LS_X);
                float leftAxisY = event.getAxisValue(OuyaController.AXIS_LS_Y);
                float rightAxisX = event.getAxisValue(OuyaController.AXIS_RS_X);
                float rightAxisY = event.getAxisValue(OuyaController.AXIS_RS_Y);
                float l2Axis = event.getAxisValue(OuyaController.AXIS_L2);
                float r2Axis = event.getAxisValue(OuyaController.AXIS_R2);

                boolean callNotification = false;
                float c_minStickDistance = OuyaController.STICK_DEADZONE * OuyaController.STICK_DEADZONE;

                if (leftAxisX * leftAxisX + leftAxisY * leftAxisY < c_minStickDistance){
                    leftAxisX = leftAxisY = 0.0f;
                }
                else{
                	
                    callNotification = true;
                }

                if (rightAxisX * rightAxisX + rightAxisY * rightAxisY < c_minStickDistance){
                    rightAxisX = rightAxisY = 0.0f;
                    callNotification = true;
                }
                else{
                    callNotification = true;
                }

                if (l2Axis > 0.0f || r2Axis > 0.0f ){
                    callNotification = true;
                }
                
                if ( callNotification ){
                	
                			Moai.buttonMotion(leftAxisX, leftAxisY, rightAxisX, rightAxisY, 0);
                	
                    }
        }

        return handled || super.onGenericMotionEvent(event);
    }


 


   
    
    
 //******************                  
 }//end activity
                
                
                
                
                



class myView extends SurfaceView implements SurfaceHolder.Callback
{

    private GL10 gl;
	

	public myView(Context context)
    {          
         super(context);
         
         SurfaceHolder holder = getHolder();         
         getHolder().addCallback(this);   
         //setFocusable(true);
    }


    public void surfaceCreated(SurfaceHolder holder) {  
    	 Log.v("SDL", "trace-2: surfaceCreated()");
    }

    

    
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		 Log.v("SDL", "trace-3: surfaceChanged()");
		 
		 
		   //es type     
	        //MoaiActivity.mGLMajor    = 2;
	        //MoaiActivity.mGLMinor    = 1;
	        
    	 
//EGL INSTANCE
        final EGL10 egl = (EGL10)EGLContext.getEGL();

        
//DEFAULT DISPLAY
        MoaiActivity.mEGLDisplay = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
       
        //CLAUSE
        if ( MoaiActivity.mEGLDisplay == EGL10.EGL_NO_DISPLAY) {
        	Log.v("SDL", "trace-: DISPLAY FAILED");
        }

        //INFO
          Log.d("SDL", "trace-EGL vendor: " + 		egl.eglQueryString( MoaiActivity.mEGLDisplay, EGL10.EGL_VENDOR));
          Log.d("SDL", "trace-EGL version: " + 		egl.eglQueryString( MoaiActivity.mEGLDisplay, EGL10.EGL_VERSION));
          Log.d("SDL", "trace-EGL extensions: " + 	egl.eglQueryString( MoaiActivity.mEGLDisplay, EGL10.EGL_EXTENSIONS)); 
        
//CONFIGS   
  	
        int[] version = new int[2];        
        egl.eglInitialize(MoaiActivity.mEGLDisplay, version);


        
       // int EGL_OPENGL_ES2_BIT = 4;
        /*  
    	int[] my_attribs = new int[] {            		                     
    		            EGL10.EGL_RED_SIZE,      8,
    		            EGL10.EGL_GREEN_SIZE,    8,
    		            EGL10.EGL_BLUE_SIZE,     8,
    		            EGL10.EGL_ALPHA_SIZE,    8,
    		            EGL10.EGL_DEPTH_SIZE,   16,
    		            //EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
    		            EGL10.EGL_NONE,
    		            //0x303B, // EGL10.EGL_MIN_SWAP_INTERVAL,
    		           // 0x303C, // EGL10.EGL_MAX_SWAP_INTERVAL,
    		            };
    	 */ 
        
        /*
    	  int[] my_attribs = {
                  EGL10.EGL_DEPTH_SIZE,   16,
                  EGL10.EGL_NONE
          };
         */
        
        final int EGL_OPENGL_ES2_BIT = 4;
        
        int[] my_attribs = new int[]{
        	      EGL10.EGL_STENCIL_SIZE, 1,  /* Don't change this position in array! */
        	      EGL10.EGL_RED_SIZE, 8,
        	      EGL10.EGL_GREEN_SIZE, 8,
        	      EGL10.EGL_BLUE_SIZE, 8,
        	      EGL10.EGL_ALPHA_SIZE, 8,
        	      EGL10.EGL_DEPTH_SIZE, 16,
        	      //EGL10.EGL_SAMPLE_BUFFERS, 2,
        	    EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
        	      EGL10.EGL_NONE,
        	    // EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
        	    };

        
        
   
      
        EGLConfig[] configs = new EGLConfig[1];
        int[] num_config = new int[1]; 
        
        
        egl.eglChooseConfig(MoaiActivity.mEGLDisplay, my_attribs, configs, 1, num_config);
        
  
   //config     
        EGLConfig config = configs[0];  
        MoaiActivity.mEGLConfig  = config;

       
        
   
        
        

    
        
   
   		//this.gl = (GL11)MoaiActivity.mEGLContext.getGL();
   		
   		
        
//CREATE SURFACE // PASS TO ezSURFACE       
        MoaiActivity.mEGLSurface = egl.eglCreateWindowSurface(MoaiActivity.mEGLDisplay, MoaiActivity.mEGLConfig,MoaiActivity.mySurface, null);

        if (egl.EGL_NO_SURFACE ==  MoaiActivity.mEGLSurface) 
        	{
        			Log.v("SDL", "trace-: surface FAILED");
        	}
        
        

      
        
        
//CREATE CONTEXT         
        int EGL_CONTEXT_CLIENT_VERSION=0x3098; 

         int contextAttrs[] = new int[] { 
         			EGL_CONTEXT_CLIENT_VERSION, 
         			MoaiActivity.mGLMajor, 
         			EGL10.EGL_NONE 
 			 };
        
         MoaiActivity.mEGLContext = egl.eglCreateContext(MoaiActivity.mEGLDisplay, config,EGL10.EGL_NO_CONTEXT, contextAttrs);         
        
//MAKE CURRENT
        
        
      //  egl.eglMakeCurrent(MoaiActivity.mEGLDisplay, surface, surface, MoaiActivity.mEGLContext);           
        if (false == egl.eglMakeCurrent(MoaiActivity.mEGLDisplay,  MoaiActivity.mEGLSurface ,  MoaiActivity.mEGLSurface , MoaiActivity.mEGLContext))
        	{
        			Log.v("SDL", "trace-: eglMakeCurrent FAILED");
        	}
        
        
        
//kills everything but it doesn't  
//BUT DON"T        
        //egl.eglDestroyContext(MoaiActivity.mEGLDisplay,  MoaiActivity.mEGLContext);
        
        
      
        
        GL10 gl = (GL10) MoaiActivity.mEGLContext.getGL();
        Log.d("SDL", "trace-OpenGL vendor: " + gl.glGetString(GL10.GL_VENDOR));
        Log.d("SDL", "trace-OpenGL version: " + gl.glGetString(GL10.GL_VERSION));
        Log.d("SDL", "trace-OpenGL renderer: " + gl.glGetString(GL10.GL_RENDERER));
        Log.d("SDL", "trace-OpenGL extensions: " + gl.glGetString(GL10.GL_EXTENSIONS));
        
//DETECT
	 		Log.v("SDL", "trace-3-context-->Moai.detectGraphicsContext");   
	 		
     				Moai.detectGraphicsContext ();
     				
     	        
//RUN
      	 	Log.v("SDL", "trace-4-context-->RUNNING LUA EZRA");            
      	 	runScripts ( new String [] { "../init.lua", "main.lua" } );
              

    
         	
//GAMELOOP   
      	 	
     /*
         Thread myThread = new Thread(new Runnable() {
             @Override
             public void run() {
            	 
	  		       while (true) 
	  		       {
	  		    		EGL10 egl = (EGL10)EGLContext.getEGL();
	  		    		
	    		
	  		    		egl.eglWaitNative(EGL10.EGL_CORE_NATIVE_ENGINE, null);
	  		      	  
	  		
	  		      			Moai.update ();  //MOAIRenderMgr::Get ().Render (); GETS CALLED OF UPDATE  
	  		 
	  		      		egl.eglSwapBuffers(MoaiActivity.mEGLDisplay,MoaiActivity.mEGLSurface);
	  		      		egl.eglWaitGL();
	  		      		
	  		 
	  		 		 	
			  		 		  	try {
			  						Thread.sleep(5);
			  					} catch (InterruptedException e) {
			  						// TODO Auto-generated catch block
			  						e.printStackTrace();
			  					}  
			  		          
	  		       } 
            	 
	  		
	  		        
	  		        
             }
         });
         
   
         myThread.start();
        
        */
      	 	
      
      	  final Handler handler = new Handler();
            Runnable myDraw = new Runnable() {
            	
                @Override
                public void run() {
                	
               		//egl.eglWaitNative(EGL10.EGL_CORE_NATIVE_ENGINE, null);
                	
             		egl.eglSwapBuffers(MoaiActivity.mEGLDisplay,MoaiActivity.mEGLSurface);
             		Moai.update (); 
             		egl.eglWaitGL();
        
                  handler.post(this);
                }
              };
              handler.post(myDraw);
           
      
  
		 
		 
	}
	
	
/////////////////////////////////////////////////////////////////
    
public static void runScripts(String[] strings) {

		for ( String file : strings ) {          
		MoaiLog.i ( "MoaiRenderer runScripts: Running " + file + " script" );            
		Moai.runScript ( file );
		}  
}

	
	

    @Override
    protected void onAttachedToWindow() {       

        super.onAttachedToWindow();
      	 Log.v("SDL", "trace-1-context-->WINDOW");   	
    }

    
    // unused
    @Override
    public void onDraw(Canvas canvas) {
    			
    			 Log.v("SDL", "DRAW");   
    		}






	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		 Log.v("SDL", "trace-12-context-->DESTROIED");   
		
	}
}



















  
