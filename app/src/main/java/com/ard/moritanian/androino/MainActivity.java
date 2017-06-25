package com.ard.moritanian.androino;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import android.hardware.Camera;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import android.content.Context;
import android.widget.Toast;

import org.shokai.firmata.ArduinoFirmata;
import org.shokai.firmata.ArduinoFirmataEventHandler;
import java.io.*;
import java.lang.*;
import android.hardware.usb.*;

public class MainActivity extends AppCompatActivity {

    private WebView myWebView;
    private JavaScriptInterface jsInterface;
    final String NATIVE_INTERFACE_NAME = "nativeInterface";

    SurfaceView sv;
    SurfaceHolder sh;
    Camera cam;
    SurfaceHolderCallback shc;
    Context con;

    private static final int PORT = 4680;
    private static final String IP_ADDR = "localhost"; //"192.168.179.7"; // IPアドレス

    ArduinoFirmata arduino;


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        con = this;

        // for webView debug
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        //レイアウトで指定したWebViewのIDを指定する。
        myWebView = (WebView)findViewById(R.id.webView1);


        //jacascriptを許可する
        WebSettings settings = myWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setLoadWithOverviewMode(true);
        myWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        myWebView.setScrollbarFadingEnabled(false);
        settings.setBuiltInZoomControls(true);
        settings.setAllowFileAccess(true);
        settings.setSupportZoom(true);
        settings.setAllowContentAccess(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        myWebView.setWebChromeClient(new WebChromeClient(){
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                request.grant(request.getResources());
            }
        });

        jsInterface = new JavaScriptInterface(this);
        myWebView.addJavascriptInterface(jsInterface, NATIVE_INTERFACE_NAME);
        // カスタムWebViewを設定する
        myWebView.setWebViewClient(new MyWebView());


    }

    @Override
    protected void onResume() {
        super.onResume();

        String url = "http://192.168.179.7:8000";
        SharedPreferences pref = getSharedPreferences("settings_pref",MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
        url = pref.getString(getString(R.string.view_url_key), url);
        Log.d("moritanian2" , url);
        myWebView.loadUrl(url);

       // setupCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // カメラを停止する
        if (cam!= null) {
            //mPreview.setCamera(null);
            cam.release();
            cam = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_reload:
                myWebView.clearCache(true);
                myWebView.reload();
                return true;

            case R.id.home:
                if(myWebView.canGoBack()){
                    myWebView.goBack();
                }
        }

        return super.onOptionsItemSelected(item);
    }

    private class MyWebView extends WebViewClient {

        //ページの読み込み完了
        @Override
        public void onPageFinished(WebView view, String url) {

            // HTML内に埋め込まれている「callJavaScript()」関数を呼び出す
            myWebView.loadUrl("javascript:logFunc('called from native')");
        }
    }

    public void connectArduino(){
        arduino = new ArduinoFirmata(this);
        try{
            arduino.connect();
        }
        catch(IOException e){
            e.printStackTrace();
            finish();
        }
        catch(InterruptedException e){
            e.printStackTrace();
            finish();
        }

    }

    public void disconnectArduino(){
        arduino.close();
    }


    public void setupCamera(){
       // getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
       // requestWindowFeature(Window.FEATURE_NO_TITLE);
        Log.d("moritanian2","setupCamera");
       // FrameLayout fl = new FrameLayout(this);
       // setContentView(fl);
    /*
        sv = new SurfaceView(this);
        sh = sv.getHolder();
        shc = new SurfaceHolderCallback();
        sh.addCallback(shc);
    */
        int cameraId = findFrontFacingCamera();
        if(cameraId < 0){
            Toast.makeText(con, "No camera found", Toast.LENGTH_LONG).show();
            return;
        }
        cam = Camera.open(cameraId);
        Camera.Parameters param = cam.getParameters();
        List<Camera.Size> ss = param.getSupportedPictureSizes();
        Camera.Size pictSize = ss.get(0);

        param.setPictureSize(pictSize.width, pictSize.height);
        cam.setParameters(param);
        cam.startPreview();
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                Log.d("moritanian2", "Camera found " + String.valueOf(cameraId) );
                break;
            }
        }
        return cameraId;
    }

    public void stopCamera(){
        shc.surfaceDestroyed(sh);
    }

    public void takeCamera(){
        //shc.takePicture();
        Log.d("moritanian2", "takecamera");
        cam.setPreviewCallback(editPreviewImage);

        /*
            // カメラのスクリーンショットの取得
        cam.takePicture(null, null,new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data,Camera camera) {
                //sendData( getContext(), data);
                Log.d("moritanian2", "On Picture Taken");
                sendData( getApplicationContext(), data);
            }
        });
        */
    }

    private void sendData(Context context, byte[] data){
        // ソケットの作成
        Socket socket;
        BufferedOutputStream out;
        try{
            socket = new Socket(IP_ADDR, PORT);
            Log.d("moritanian2", "sendData");
            out = new BufferedOutputStream(socket.getOutputStream());
            out.write(data);
            if(out != null) out.close();
            if(socket != null) socket.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private final Camera.PreviewCallback editPreviewImage =
            new Camera.PreviewCallback() {

                public void onPreviewFrame(byte[] data, Camera camera) {
                    Log.d("moritanian2", "onpreviewFrame");
                    cam.setPreviewCallback(null);  // プレビューコールバックを解除

                    cam.stopPreview();
                    // 画像の保存処理

                    sendData(con, data);
                    cam.startPreview();
                }
            };

    class SurfaceHolderCallback implements SurfaceHolder.Callback {
        private static final int WIDTH  = 480;
        private static final int HEIGHT = 320;

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.d("moritanian2", "surfaceCreated");
            int cameraId = findFrontFacingCamera();
            if(cameraId < 0){
                Toast.makeText(con, "No camera found", Toast.LENGTH_LONG).show();
                return;
            }
            cam = Camera.open(cameraId);
            Camera.Parameters param = cam.getParameters();
            List<Camera.Size> ss = param.getSupportedPictureSizes();
            Camera.Size pictSize = ss.get(0);

            param.setPictureSize(pictSize.width, pictSize.height);
            cam.setParameters(param);
        }
        @Override
        public void surfaceChanged(SurfaceHolder holder, int f, int w, int h) {
            try {
                cam.setDisplayOrientation(0);
                cam.setPreviewDisplay(sv.getHolder());

                Camera.Parameters param = cam.getParameters();
                List<Camera.Size> previewSizes =
                        cam.getParameters().getSupportedPreviewSizes();
                Camera.Size pre = previewSizes.get(0);
                param.setPreviewSize(pre.width, pre.height);

                Toolbar.LayoutParams lp = new Toolbar.LayoutParams(pre.width, pre.height);
                sv.setLayoutParams(lp);

                cam.setParameters(param);
                cam.startPreview();
            } catch (Exception e) { }
        }
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            cam.stopPreview();
            cam.release();
        }

        public void takePicture() {
            // カメラのスクリーンショットの取得
            cam.takePicture(null, null,new Camera.PictureCallback() {
                public void onPictureTaken(byte[] data,Camera camera) {
                    //sendData( getContext(), data);
                    sendData( getApplicationContext(), data);
                }
            });
        }

        private void sendData(Context context, byte[] data){
            // ソケットの作成
            Socket socket;
            BufferedOutputStream out;
            try{
                socket = new Socket(IP_ADDR, PORT);
                Log.d("moritanian2", "sendData");
                out = new BufferedOutputStream(socket.getOutputStream());
                out.write(data);
                if(out != null) out.close();
                if(socket != null) socket.close();
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }

        private int findFrontFacingCamera() {
            int cameraId = -1;
            // Search for the front facing camera
            int numberOfCameras = Camera.getNumberOfCameras();
            for (int i = 0; i < numberOfCameras; i++) {
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(i, info);
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    Log.d("moritanian2", "Camera found");
                    cameraId = i;
                    break;
                }
            }
            return cameraId;
        }
    }
}
