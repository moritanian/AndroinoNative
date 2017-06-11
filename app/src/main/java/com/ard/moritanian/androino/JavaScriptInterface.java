package com.ard.moritanian.androino;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

/**
 * Created by Moritanian on 2017/06/05.
 */

public class JavaScriptInterface {
    Context con;

    public JavaScriptInterface(Context c) {
        this.con = c;
    }

    @JavascriptInterface
    public void showToast(String messege) {
        messege += "[ここからはJavaで付加されたテキストです]";
        Toast.makeText(this.con, messege, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void startCamera() {
        ((MainActivity)con).setupCamera();
    }

    @JavascriptInterface
    public void takeCamera() {
        ((MainActivity)con).takeCamera();
    }
}
