package com.yunk.floatingvideoview;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import com.yunk.floatingvideoview.View.WebCamView;
import com.yunk.floatingvideoview.View.WindowFrameLayout;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_CODE = 1;
    String host;
    WebCamView camView;
    VideoView videoView;
    WindowFrameLayout windowFrameLayout;
    Button btn_popupweb, btn_popupvideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_popupweb = (Button) findViewById(R.id.btn_show_web);
        btn_popupvideo = (Button) findViewById(R.id.btn_show_video);
        host = "114.32.10.82:83";
        btn_popupweb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkDrawOverlayPermission()) {
                    camView = new WebCamView(MainActivity.this);
                    camView.setCamAccount(host, "root", "root");
                    windowFrameLayout = new WindowFrameLayout(MainActivity.this, camView);
                    windowFrameLayout.playWindowView("", camView);
                    camView.playCam();
                    //finish();
                }
            }
        });
        btn_popupvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkDrawOverlayPermission()) {
                    videoView = new VideoView(MainActivity.this);
                    videoView.setMediaController(new MediaController(MainActivity.this));
                    videoView.setVideoPath("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
                    windowFrameLayout = new WindowFrameLayout(MainActivity.this, videoView);
                    windowFrameLayout.playWindowView("", videoView);
                    videoView.start();
                    //finish();
                }
            }
        });

    }

    public boolean checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_CODE);
            return false;
        } else {
            return true;
        }
    }
}
