package com.a3r0.bettercallerid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

public class Request
{

    public ResponseObject get(String url) throws IOException
    {
        // START TIME
        Long timeStart         = System.currentTimeMillis();

        // URL OBJECT
        URL url_               = new URL(url);

        // OPEN URL CONNECTION
        HttpURLConnection conn = (HttpURLConnection) url_.openConnection();
        //conn.setDoOutput(true);
        conn.setRequestMethod("GET");

        // GET STATUS CODE
        BufferedReader br = null;
        boolean errCode;
        if (100 <= conn.getResponseCode() && conn.getResponseCode() <= 399) {
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            errCode = false;
        } else {
            br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            errCode = true;
        }

        // END TIME
        Long timeEnd                  = System.currentTimeMillis();
        Long elapsedMillis            = timeEnd - timeStart;

        // RETURN RESPONSE OBJECT
        return new ResponseObject(errCode, br.lines().collect(Collectors.joining()), conn.getResponseCode(), elapsedMillis, conn);

    }
}
