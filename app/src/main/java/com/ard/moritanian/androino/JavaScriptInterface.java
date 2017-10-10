package com.ard.moritanian.androino;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;


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

    /*
    final byte US_DISTANCE_MEASUREMENT_REQUEST_COMMAND = 0B00100000;
    final byte US_DISTANCE_MEASUREMENT_RESULT_COMMAND = 0B00100001;
*/

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
    public void pinMode(int pin, String mode) {
        callJsFunction(String.format("log('pinmode %d %s')", pin, mode));
        switch (mode){
            case OUTPUT:
                ((MainActivity)con).arduino.pinMode(pin, ArduinoFirmata.OUTPUT);
                break;

            case INPUT:
                ((MainActivity)con).arduino.pinMode(pin, ArduinoFirmata.INPUT);
                break;

            case PWM:
                ((MainActivity)con).arduino.pinMode(pin, ArduinoFirmata.PWM);
                break;

            case ANALOG:
                ((MainActivity)con).arduino.pinMode(pin, ArduinoFirmata.ANALOG);
                break;

            case SERVO:
                ((MainActivity)con).arduino.pinMode(pin, ArduinoFirmata.SERVO);
                break;
        }
    }

    @JavascriptInterface
    public void digitalWrite(int pin, String value) {
        callJsFunction(String.format("log('digitalWrite %d %s')", pin, value));
        ((MainActivity)con).arduino.digitalWrite(pin, value.equals(HIGH));
    }

    @JavascriptInterface
    public String digitalRead(int pin) {
        return ((MainActivity)con).arduino.digitalRead(pin) ? HIGH : LOW;
    }

    @JavascriptInterface
    public void analogWrite(int pin, int value) {
        ((MainActivity)con).arduino.analogWrite(pin, value);
    }

    @JavascriptInterface
    public int analogRead(int pin) {
        return ((MainActivity)con).arduino.analogRead(pin);
    }

    /*
    @JavascriptInterface
    public boolean usDistanceMeasure(int trigPin, int echoPin) {
        if(trigPin < 0 || trigPin > 127 || echoPin < 0 || echoPin > 127){
            return false;
        }

        byte data[] = {(byte)trigPin, (byte)echoPin};
        arduino.sysex(US_DISTANCE_MEASUREMENT_REQUEST_COMMAND, data);

        return true;
    }
*/

    @JavascriptInterface
    public void sendSysex(byte command, byte[] data) {
        Log.i("sendSysex", String.format("%d %d", data.length, data[0]));
        ((MainActivity)con).arduino.sysex(command, data);
    }

    @JavascriptInterface
    public void debugFunc(){
        ((MainActivity)con).arduino.digitalWrite(13, false);
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
