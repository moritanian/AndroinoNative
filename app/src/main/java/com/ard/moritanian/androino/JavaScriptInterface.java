package com.ard.moritanian.androino;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import org.shokai.firmata.ArduinoFirmata;

/**
 * Created by Moritanian on 2017/06/05.
 */

public class JavaScriptInterface {
    Context con;

    public JavaScriptInterface(Context c) {
        this.con = c;
    }
    final String HIGH = "HIGH";
    final String LOW = "LOW";
    final String INPUT = "INPUT";
    final String OUTPUT = "OUTPUT";

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
    public void connectArduino() {
        ((MainActivity)con).connectArduino();
    }

    @JavascriptInterface
    public void disconnectArduino() {
        ((MainActivity)con).disconnectArduino();
    }

    @JavascriptInterface
    public void pinMode(int port, String mode) {
        ((MainActivity)con).arduino.pinMode(port, mode == INPUT ? ArduinoFirmata.INPUT : ArduinoFirmata.OUTPUT);
    }

    @JavascriptInterface
    public void digitalWrite(int port, String value) {
        ((MainActivity)con).arduino.digitalWrite(port, value == HIGH);
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


}
