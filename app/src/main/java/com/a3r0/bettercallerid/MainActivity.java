package com.a3r0.bettercallerid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

public class MainActivity extends AppCompatActivity
{

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // SYSTEM
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // CREATE NOTIFICATION CHANNEL
        NotificationChannel channel = new NotificationChannel(
                Globals.channelID,
                "Better CallerID",
                NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription("Better than your average CallerID");
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

        // ASK FOR READ_CALL_LOG PERMISSIONS
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            Log.d("PERMISSIONS", "ACCEPTED");
        }
        else
        {
            Log.d("PERMISSIONS", "ASKING");
            // You can directly ask for the permission.
            requestPermissions(
                    new String[] { Manifest.permission.READ_CALL_LOG },
                    Globals.requestCode);
        }


        TextView textViewApiKey = findViewById(R.id.textViewApiKey);
        SharedPreferences sp = getSharedPreferences("text", 0);
        String getKeyInit = sp.getString("apiKey", null);

        Log.d(Globals.logging.apiKey, getKeyInit);

        getKeyInit = (getKeyInit == null) ? "N/A" : getKeyInit;

        textViewApiKey.setText("Current API Key:\n" + getKeyInit);

        // SETUP PHONE LISTENER
        TelephonyManager telephony = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(new PhoneStateListener()
        {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                Log.d("CALL STATE", String.valueOf(state));
                super.onCallStateChanged(state, incomingNumber);

                Log.d("INCOMING CALL", incomingNumber);
                Log.d("INCOMING CALL", "State: " + String.valueOf(state));

                //CHECK IF INCOMING CALL
                if (incomingNumber.length() > 2 && state < 2)
                {

                    new SystemNotification("Better CallerID", incomingNumber, "Incoming call from " + incomingNumber, getApplicationContext());

                    SharedPreferences sp = getSharedPreferences("text", 0);
                    String key = sp.getString("apiKey", null);

                    Log.d("INCOMING CALL", incomingNumber);
                    Log.d("INCOMING CALL", "Using API key: " + key);

                    if (key != null)
                    {
                        String url    = "http://apilayer.net/api/validate?access_key="
                                + key
                                + "&number="
                                + incomingNumber;

                        Thread thread = new Thread(new Runnable() {

                            @Override
                            public void run() {

                                Request request = new Request();
                                try {
                                    ResponseObject response = request.get(url);
                                    JSONObject json = new JSONObject(response.text);

                                    String presentation = "";

                                    if (!json.getBoolean("valid")){
                                        new SystemNotification("Better CallerID", incomingNumber, "⚠INVALID NUMBER⚠", getApplicationContext());

                                    }

                                    presentation += json.getString("location") + ", " + json.getString("country_code");

                                    String lineType = "N/A";

                                    if (json.getString("line_type").length() > 2){
                                        lineType = json.getString("line_type");
                                    }

                                    String carrier = "ℹ️ Unknown Carrier";
                                    carrier = (json.getString("carrier").length() > 1) ? json.getString("carrier") : carrier;

                                    presentation += " [" + lineType + "] " + carrier;

                                    new SystemNotification("Better CallerID", incomingNumber, presentation, getApplicationContext());

                                } catch (JSONException | IOException ioException) {
                                    ioException.printStackTrace();
                                }

                            }
                        });
                        thread.start();
                    }
                    else{
                        // HOOK TO UI THREAD LOOPER TO TOAST
                        new Thread(new Runnable(){
                            public void run() {
                                Looper.prepare();
                                Toast.makeText(getApplicationContext(), String.format("Better CallerID: Numverify API key is not set!", Globals.apiKey), Toast.LENGTH_LONG).show();
                                Looper.loop();
                            }
                        }).start();
                    }
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);
    }


    // Button on clicked
    @SuppressLint("SetTextI18n")
    public void btnSubmit(View v)
    {
        TextView thisTextInput = findViewById(R.id.apiKeyInput);
        String tvString = thisTextInput.getText().toString();
        if (tvString.length() != 32){
            // HOOK TO UI THREAD LOOPER TO TOAST
            new Thread(new Runnable(){
                public void run() {
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), "Invalid Key!", Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }).start();
            return;
        }

        sharedPref = getSharedPreferences("text", 0);
        sharedPref.edit().putString("apiKey", tvString).apply();

        TextView textViewApiKey = findViewById(R.id.textViewApiKey);
        textViewApiKey.setText("Current API Key: " + tvString);

        // HOOK TO UI THREAD LOOPER TO TOAST
        new Thread(new Runnable(){
            public void run() {
                Looper.prepare();
                Toast.makeText(getApplicationContext(), String.format("API key set to %s!", Globals.apiKey), Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }).start();
    }

}