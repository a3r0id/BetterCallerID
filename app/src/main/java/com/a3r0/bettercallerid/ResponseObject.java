package com.a3r0.bettercallerid;
import java.net.HttpURLConnection;

public class ResponseObject
{
    boolean error;
    String text;
    int status;
    Long elapsed;
    HttpURLConnection HttpURLConnectionObject;

    public ResponseObject(boolean error, String responseText, int statusCode, Long elapsedTime, HttpURLConnection HttpURLConnectionObject){
        this.error                   = error;
        this.text                    = responseText;
        this.status                  = statusCode;
        this.elapsed                 = elapsedTime;
        this.HttpURLConnectionObject = HttpURLConnectionObject;
    }
}