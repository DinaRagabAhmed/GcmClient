package com.iti.gcmpushnotification;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class SecondActivity extends AppCompatActivity {

    Button accept;
    Button deny;

    String API_KEY = "AAAAHMYLWE0:APA91bG4sFOSqjTjmdEFPiQIr_RqMn5RX3MQ46ZySroG9bHcLzVoHQU6HKGr3qvxZtwJS3dRXLYxQhqezDesEvU3CvsJaYqFLB5Z2H4w2fw1789g6AxEQG9601fS-6_SwSXgvekfVmKU";
    //String to= "c7DqVclnvoM:APA91bHWuXUJ7Rnu6Ul32tmbUK9xR2iDhapENkiSr_91J5chzYQ_ZVazDHRtUCrxiWTxcuINSD9_0XEHdsYvxPD30TrnEm7CDwHpzKTm847dTLasfTfmMzvRWtf135gVSry69780XakT";

    //String to ="/topics/manager";
    //String API_KEY = "AAAAHMYLWE0:APA91bG4sFOSqjTjmdEFPiQIr_RqMn5RX3MQ46ZySroG9bHcLzVoHQU6HKGr3qvxZtwJS3dRXLYxQhqezDesEvU3CvsJaYqFLB5Z2H4w2fw1789g6AxEQG9601fS-6_SwSXgvekfVmKU";
    //String to= "c7DqVclnvoM:APA91bHWuXUJ7Rnu6Ul32tmbUK9xR2iDhapENkiSr_91J5chzYQ_ZVazDHRtUCrxiWTxcuINSD9_0XEHdsYvxPD30TrnEm7CDwHpzKTm847dTLasfTfmMzvRWtf135gVSry69780XakT";

    String to ="/topics/manager";
    String time;
    String title;

    TextView msg;
    TextView managerTime;

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent=getIntent();

        //time=intent.getStringExtra("DINA");
         time=intent.getExtras().getString("time");
         title=intent.getExtras().getString("title");
        msg.setText("you got ride called "+title+" do you want to accept it?!");
        managerTime.setText("manager send this request at "+time);

        Log.i("time in intent ",time);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_second);
        Log.i("result","on create");
        msg=(TextView)findViewById(R.id.textView);
        managerTime=(TextView)findViewById(R.id.managerTime);
        accept=(Button)findViewById(R.id.accept);
        Log.i("result","on create");




        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(isNetworkAvailable()) {

                    HashMap rides = GCMPushReceiverService.rides;
                    if (rides.get(time).equals("available")) {
                        Log.i("result", "on cbuttin");
                        Thread background = new Thread(new Runnable() {
                            @Override
                            public void run() {

                                Log.i("result", "on run thread");
                                try {
                                    // Prepare JSON containing the GCM message content. What to send and where to send.
                                    JSONObject jGcmData = new JSONObject();
                                    JSONObject jData = new JSONObject();
                                    jData.put("title",title);
                                    //Log.i("time in emo",new GCMPushReceiverService().notificationTime);
                                    jData.put("acceptedToken", GCMRegistrationIntentService.token);
                                    Log.i("time in accept thread", time);
                                    jData.put("time", time);

                                    jGcmData.put("to", to);

                                    // What to send in GCM message.
                                    jGcmData.put("data", jData);

                                    // Create connection to send GCM Message request.
                                    URL url = new URL("https://android.googleapis.com/gcm/send");
                                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                    conn.setRequestProperty("Authorization", "key=" + API_KEY);
                                    conn.setRequestProperty("Content-Type", "application/json");
                                    conn.setRequestMethod("POST");
                                    conn.setDoOutput(true);

                                    Log.i("result", "made conn");
                                    // Send GCM message content.
                                    OutputStream outputStream = conn.getOutputStream();
                                    outputStream.write(jGcmData.toString().getBytes());

                                    // Read GCM response.
                                    InputStream inputStream = conn.getInputStream();
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                                    Log.i("result", "4");
                                    StringBuilder str = new StringBuilder();
                                    String line = null;
                                    Log.i("result", "5");

                                    while ((line = reader.readLine()) != null) {
                                        Log.i("result", "reading before appeand");
                                        str.append(line);
                                        //Log.i("result","reading");
                                    }
                                    Log.i("result", "6");
                                    String resultFromWs = str.toString();
                                    Log.i("result", resultFromWs);
                                } catch (IOException e) {
                                    System.out.println("Unable to send GCM message.");
                                    System.out.println("Please ensure that API_KEY has been replaced by the server " +
                                            "API key, and that the device's registration token is correct (if specified).");
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }


                        });
                        background.start();


                        finish();

                    } else {
                        Toast.makeText(getApplicationContext(), "sorry ride has been already token", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();


                }
            }
        });

    }

    public  boolean isNetworkAvailable() {

        ConnectivityManager connectivigetyManager = (ConnectivityManager) getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivigetyManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
