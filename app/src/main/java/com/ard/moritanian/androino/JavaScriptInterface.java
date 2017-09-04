package com.ard.moritanian.androino;

import android.app.Activity;
import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import org.shokai.firmata.ArduinoFirmata;

/**
 * Created by Moritanian on 2017/06/05.
 */

public class JavaScriptInterface {
    Context con;
    WebView webView;

    public JavaScriptInterface(Context c, WebView webView)
    {
        this.con = c;
        this.webView = webView;
    }

    final String HIGH = "HIGH";
    final String LOW = "LOW";
    final String INPUT = "INPUT";
    final String OUTPUT = "OUTPUT";
    final String PWM = "PWM";
    final String ANALOG = "ANALOG";
    final String SERVO = "SERVO";

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

    @JavascriptInterface
    public boolean connectArduino() {
        return ((MainActivity)con).connectArduino();
    }

    @JavascriptInterface
    public void disconnectArduino() {
        ((MainActivity)con).disconnectArduino();
    }

    @JavascriptInterface
    public void pinMode(int port, String mode) {
        callJsFunction(String.format("log('pinmode %d %s')", port, mode));
        switch (mode){
            case OUTPUT:
                ((MainActivity)con).arduino.pinMode(port, ArduinoFirmata.OUTPUT);
                break;

            case INPUT:
                ((MainActivity)con).arduino.pinMode(port, ArduinoFirmata.INPUT);
                break;

            case PWM:
                ((MainActivity)con).arduino.pinMode(port, ArduinoFirmata.PWM);
                break;

            case ANALOG:
                ((MainActivity)con).arduino.pinMode(port, ArduinoFirmata.ANALOG);
                break;

            case SERVO:
                ((MainActivity)con).arduino.pinMode(port, ArduinoFirmata.SERVO);
                break;
        }
    }

    @JavascriptInterface
    public void digitalWrite(int port, String value) {
        callJsFunction(String.format("log('digitalWrite %d %s')", port, value));
        ((MainActivity)con).arduino.digitalWrite(port, value.equals(HIGH));
    }

    @JavascriptInterface
    public String digitalRead(int port) {
        return ((MainActivity)con).arduino.digitalRead(port) ? HIGH : LOW;
    }

    @JavascriptInterface
    public void analogWrite(int port, int value) {
        ((MainActivity)con).arduino.analogWrite(port, value);
    }

    @JavascriptInterface
    public int analogRead(int port) {
        return ((MainActivity)con).arduino.analogRead(port);
    }

    @JavascriptInterface
    public void debugFunc(){
        ((MainActivity)con).arduino.digitalWrite(13, true);
    }

    public void callJsFunction(final String func) {
        ((Activity)con).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:Arduino." + func);
            }
        });
    }


}
