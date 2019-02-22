package com.yunk.floatingvideoview.View;

import android.content.Context;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by bradley on 2019/2/22.
 */

public class WebCamView extends WebView {
    private String camIP = "114.32.10.82";
    private String camPort = "83";
    private String camHost = camIP+":"+camPort;
    private String username = "root";
    private String password = "root";
    private String fullURL = "http://"+username+":"+password+"@"+camHost+"/mjpg/video.mjpg";

    public WebCamView(Context context) {
        super(context);
        this.getSettings().setLoadWithOverviewMode(true);
        this.getSettings().setUseWideViewPort(true);
        this.getSettings().setJavaScriptEnabled(true);
        this.setWebViewClient(new WebViewClient());
    }

    public void setCamAddress(String ipAddress, String port){
        camIP = ipAddress;
        camPort = port;
    }

    public void setCamAccount(String id, String pw){
        username = id;
        password = pw;
    }
    public void setCamAccount(String ipAddress, String id, String pw){
        camIP = ipAddress.split(":")[0];
        camPort = ipAddress.split(":")[1];
        camHost = ipAddress;
        username = id;
        password = pw;
    }
    public void setCamAccount(String host, String port, String id, String pw){
        camHost = host+":"+port;
        username = id;
        password = pw;
    }

    public void playCam(){
        fullURL = "http://"+username+":"+password+"@"+camHost+"/mjpg/video.mjpg";
        this.loadUrl(fullURL);
    }
}
