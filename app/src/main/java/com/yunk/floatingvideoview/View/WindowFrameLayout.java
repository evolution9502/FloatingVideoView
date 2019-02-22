package com.yunk.floatingvideoview.View;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.VideoView;

import static android.R.attr.progressBarStyleLarge;
import static android.content.Context.WINDOW_SERVICE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

/**
 * Created by bradley on 2019/2/22.
 */

public class WindowFrameLayout extends FrameLayout {
    public final static String TAG = "WindowFrame";
    private Context mContext;
    private WindowManager wm = null;
    private WindowManager.LayoutParams wmParams = null;
    private float xDownInSmallWindow, yDownInSmallWindow;
    private static float lastX, lastY = 0;;
    public static int WIDTH = 1024, HEIGHT = 552;

    private WebView SiemensCamView;
    private VideoView VideoCamView;
    private View playView;
    public ImageButton cancelVideo;
    public static ProgressBar mSpinner;
    private LayoutParams ImageLParams, LoadingParams;

    public WindowFrameLayout(@NonNull Context context, WebView avv) {
        super(context);
        Log.d(TAG, "AiCamWindowFrame: AiCamWindowFrame Constructor initialized !!!!! ^___^");
        this.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        mContext = context;
        SiemensCamView = avv;
        SiemensCamView.setLayoutParams(new ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        playView = SiemensCamView;
        initWindow(playView);
        cancelVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: cancelVideo");
                removeFrameFromWM(playView);
                SiemensCamView.destroy();
            }
        });
        touchEvent (SiemensCamView);
    }

    public WindowFrameLayout(@NonNull Context context, VideoView avv) {
        super(context);
        Log.d(TAG, "AiCamWindowFrame: AiCamWindowFrame Constructor initialized !!!!! ^___^");
        this.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        mContext = context;
        VideoCamView = avv;
        VideoCamView.setLayoutParams(new ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        initWindow(VideoCamView);
        cancelVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: cancelVideo");
                removeFrameFromWM(VideoCamView);
                VideoCamView.stopPlayback();
            }
        });
        touchEvent (VideoCamView);
    }

    private void touchEvent (View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            float mTouchStartX;
            float mTouchStartY;
            float x,y;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 獲取相對螢幕的坐標，即以螢幕左上角為原點
                x = event.getRawX();
                y = event.getRawY() - 25; // 25是系統狀態欄的高度
                //Log.i(TAG, "currX" + x + "====currY" + y);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 獲取相對View的坐標，即以此View左上角為原點
                        mTouchStartX = event.getX();
                        mTouchStartY = event.getY();
                        //Log.i("startP", "startX" + mTouchStartX+ "====startY" + mTouchStartY);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //Updates windowView position parameter
                        wmParams.x=(int)( x-mTouchStartX);
                        wmParams.y=(int) (y-mTouchStartY);
                        updateView(wmParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        //Updates windowView position parameter
                        wmParams.x=(int)( x-mTouchStartX);
                        wmParams.y=(int) (y-mTouchStartY);
                        updateView(wmParams);
                        //可以在此記錄最後一次的位置
                        mTouchStartX = mTouchStartY = 0;
                        break;
                }
                return true;
            }

        });
    }


    public void initWindow(View view) {
        Log.d(TAG, "initWindow: Start initialize WindowManager %%%%%#####");
        // WindowManager initialize
        wm = (WindowManager) mContext.getSystemService(WINDOW_SERVICE);
        // Set the LayoutParams (global variable) related parameters
        wmParams = new WindowManager.LayoutParams();
        /**
         * The following are the relevant properties of WindowManager.LayoutParams
         */
        //wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;//WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;//WindowManager.LayoutParams.TYPE_SYSTEM_ERROR; /* */ // 设置window type
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){//6.0
            wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else {
            wmParams.type =  WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        // Set the image format, the effect is transparent background
        wmParams.format = PixelFormat.RGBA_8888; //PixelFormat.TRANSPARENT;
        // 设置Window flag
        wmParams.flags = FLAG_NOT_FOCUSABLE | FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        //| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;*/
		/*
		 * //The effect of the following flags attribute is "locked".
		 * //Floating windows can not be touched, do not accept any events, and do not affect the subsequent event response.
		 * wmParams.flags=LayoutParams.FLAG_NOT_TOUCH_MODAL |
		 * LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCHABLE;
		 */
        // get screen size
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int w = displayMetrics.widthPixels;
        int h = displayMetrics.heightPixels;

        wmParams.gravity = Gravity.LEFT | Gravity.TOP; // Adjust the window to the upper left corner
        // 以屏幕左上角为原点，设置x、y初始值
        WIDTH = mContext.getResources().getDisplayMetrics().widthPixels;  //size.x; //currentDisplay.getWidth();
        HEIGHT = mContext.getResources().getDisplayMetrics().heightPixels;  //size.y; //currentDisplay.getHeight();
        wmParams.x = 10; //(WIDTH - VIEW_WIDTH) / 2;
        Log.d(TAG, "##### The wmParams.x is: "+wmParams.x+" #####");
        wmParams.y = 10; //HEIGHT-500;
        // Set the floating window parameters
        wmParams.width =  w;//WRAP_CONTENT;//480;//VIEW_WIDTH; //(int)(w/2.5);
        Log.d(TAG, "##### The wmParams.width is: "+wmParams.width+" #####");
        wmParams.height =  h;//WRAP_CONTENT; //360; //VIEW_HEIGHT;  //(int)(h/2.5);
        Log.d(TAG, "##### The wmParams.height is: "+wmParams.height+" #####");

        cancelVideo = new ImageButton(mContext);
        cancelVideo.setImageResource(android.R.drawable.ic_delete);
        if(this.isActivated() || this.isShown()){
            cancelVideo.setVisibility(VISIBLE);
        }else {
            cancelVideo.setVisibility(INVISIBLE);
        }

        mSpinner = new ProgressBar(mContext, null, progressBarStyleLarge);
        mSpinner.setVisibility(VISIBLE);
        LoadingParams = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        LoadingParams.gravity = Gravity.CENTER;

        ImageLParams = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        ImageLParams.gravity = Gravity.RIGHT | Gravity.TOP;
        ImageLParams.setLayoutDirection(view.getLayoutDirection());
    }

    public void playWindowView(String media, View view){
        if(view==null){
            Log.e(TAG, "WindowFrame: CamView is null !!");
        }else{
            //aiCamVideoView.createPlayer(media);
            Log.d(TAG, "WindowFrame: CamView is OK, add view to windowmanager");
            this.addView(view);
            this.addView(mSpinner, LoadingParams);
            this.addView(cancelVideo, ImageLParams);
            wm.addView(this, wmParams);
        }
        cancelVideo.setVisibility(VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSpinner.setVisibility(View.GONE);
            }
        }, 4500);
    }

    private void updateView(WindowManager.LayoutParams params){
        wm.updateViewLayout(this, params);
    }

    public void removeFrameFromWM(View view){
        this.removeView(view);
        //this.removeView(cancelVideo);
        try{
            wm.removeView(this);
        }catch (IllegalArgumentException e){
            Log.e(TAG, "removeFrameFromWM: no view to canel");
        }
    }
}
