package com.example.thesis;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class MainScreen extends Activity {
    private static final String TAG = "Logger-dbg";
    private static final String CHANNEL_ID = "1";
    private int mMaxChars = 50000;//Default//change this to string..........
    private UUID mDeviceUUID;
    public BluetoothSocket mBTSocket;
    private ReadInput mReadThread = null;
    private boolean mIsUserInitiatedDisconnect = false;
    private boolean mIsBluetoothConnected = false;
    private BluetoothDevice mDevice;

    private static final byte CURRENT_READING_MSG = 1;
    private static final byte HISTORY_MSG = 2;
    private static final int MINUTE = 60000;

    public static List<Reading> readingsBuffer = new ArrayList<>();
    Reading object;

    CardView history, graphs, startBtn;
    private ProgressDialog progressDialog;
    TextView mTextView, mTextView1, startText;
    public String strInput, strInput2, x;
    public static boolean start = true;
    int type =0;
    static public boolean wasNotified = false;

    BluetoothSettings testObject = new BluetoothSettings();

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ActivityHelper.initialize(this);
        mTextView = findViewById(R.id.bpm);
        mTextView1 = findViewById(R.id.spo2);
        startText = findViewById(R.id.startText);
        history = (CardView) findViewById(R.id.history);
        graphs = (CardView) findViewById(R.id.graph);
        startBtn = (CardView) findViewById(R.id.start);

//        loadData();
        pushNotification(140);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(testObject.isTest == false) {
            mDevice = b.getParcelable(BluetoothSettings.DEVICE_EXTRA);
            mDeviceUUID = UUID.fromString(b.getString(BluetoothSettings.DEVICE_UUID));
            mMaxChars = b.getInt(BluetoothSettings.BUFFER_SIZE);
        }

        /*******TEST*******/
        if(testObject.isTest == true) {

            int minHR = 75;
            int maxHR = 79;
            int minSpo2 = 97;
            int maxSpo2 = 99;
            int random_hr = 73;
            int random_spo2 = 98;
            int random_interval = (int)Math.floor(Math.random()*(10-5+1)+5);

            //Generate random int value from 50 to 10


            for(int i=0; i<1400; i++) {
                random_interval = (int)Math.floor(Math.random()*(10-5+1)+5);
                if(i%random_interval==0)
                {
                random_hr = (int)Math.floor(Math.random()*(maxHR-minHR+1)+minHR);
                random_spo2 = (int)Math.floor(Math.random()*(maxSpo2-minSpo2+1)+minSpo2);
                }
                if(i > 1200 && i<1400)
                {
                    Reading testObject = new Reading(random_hr+25, random_spo2, 1800000-65000*i);
                    readingsBuffer.add(testObject);
                }
                else {
                    Reading testObject = new Reading(random_hr, random_spo2, 1800000 - 65000 * i);
                    readingsBuffer.add(testObject);
                }
            }


            mTextView.setText("100 BPM");
            mTextView1.setText("97%");
        }
        /*******************/


        if(start == false){
            startText.setText("Start");
        }else startText.setText("Stop");

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainScreen.this, History.class);
                startActivity(intent);
            }
        });

        graphs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainScreen.this, Graphs.class);
                startActivity(intent);
            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(start == false){
                    startText.setText("Stop");
                    onResume();
                    String text = "1";
                    try {
                        mBTSocket.getOutputStream().write(text.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    start = true;
                }else{
                    startText.setText("Start");
                    start = false;
                    String text = "2";
                    try {
                        mBTSocket.getOutputStream().write(text.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    onPause();
                }
            }
        });

        Log.d(TAG, "Ready");
    }

    class ReadInput implements Runnable {

        private boolean bStop = false;
        private Thread t;

        public ReadInput() {
            t = new Thread(this, "Input Thread");
            t.start();
        }

        public boolean isRunning() {
            return t.isAlive();
        }
        @Override
        public void run() {
            InputStream inputStream;

            try {
                inputStream = mBTSocket.getInputStream();
                while (!bStop) {
                    byte[] buffer = new byte[1000000];
                    if (inputStream.available() > 0) {

                        inputStream.read(buffer);

                        for (int i = 0; i < buffer.length && buffer[i] != 0; i=i+7) {

                            object = new Reading(Byte.toUnsignedInt(buffer[i]), Byte.toUnsignedInt(buffer[i+1]), (((0xFF & buffer[i+2]) << 24) |
                                    ((0xFF & buffer[i+3]) << 16) | ((0xFF & buffer[i+4]) << 8) | (0xFF & buffer[i+5])));

                            if(object.getHeartRate() > 140 && wasNotified == false)
                            {
                                pushNotification(object.getHeartRate());
                                wasNotified = true;
                            }

                            if(readingsBuffer.size() == 0)
                            {
                                readingsBuffer.add(object);
                            }else if(readingsBuffer.size()!=0 && (object.getReadingDateTimeMillis()-readingsBuffer.get(readingsBuffer.size()-1).getReadingDateTimeMillis())>(MINUTE/2)) {
                                readingsBuffer.add(object);
                            }
                            type = buffer[i+6];
                        }
                        strInput = String.valueOf(Byte.toUnsignedInt(buffer[0]))+" bpm";
                        strInput2 = String.valueOf(Byte.toUnsignedInt(buffer[1]))+" %";

                        Log.d(TAG, "run: " + strInput);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(type == CURRENT_READING_MSG) {
                                    mTextView.setText(strInput);
                                    mTextView1.setText(strInput2);
                                }
                            }
                        });
                    }
                    Thread.sleep(500);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void stop() {
            bStop = true;
        }
    }


    private class DisConnectBT extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected Void doInBackground(Void... params) {
            if (mReadThread != null) {
                mReadThread.stop();
                while (mReadThread.isRunning())
                    ; // Wait until it stops
                mReadThread = null;
            }
            try {
                mBTSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mIsBluetoothConnected = false;
            if (mIsUserInitiatedDisconnect) {
                finish();
            }
        }
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        if (mBTSocket != null && mIsBluetoothConnected) {
            if(start != true) {
                new DisConnectBT().execute();
            }
        }
        Log.d(TAG, "Paused");
        super.onPause();
    }

    @Override
    protected void onResume() {
//        if (mBTSocket == null || !mIsBluetoothConnected) {
            if(testObject.isTest == false)
                new ConnectBT().execute();
//        }
//        else
//        {
//            String text = "2";
//            try {
//                mBTSocket.getOutputStream().write(text.getBytes());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        Log.d(TAG, "Resumed");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "Stopped");
//        saveData();
        String text = "2";
        if(testObject.isTest == false) {
            try {
                mBTSocket.getOutputStream().write(text.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean mConnectSuccessful = true;
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MainScreen.this, "Hold on", "Connecting");
        }
        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (mBTSocket == null || !mIsBluetoothConnected) {
                    mBTSocket = mDevice.createInsecureRfcommSocketToServiceRecord(mDeviceUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    mBTSocket.connect();
                }
            } catch (IOException e) {
                mConnectSuccessful = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!mConnectSuccessful) {
                Toast.makeText(getApplicationContext(), "Could not connect to device.Please turn on your sensor", Toast.LENGTH_LONG).show();
                finish();
            } else {
                msg("Connected to device");
                mIsBluetoothConnected = true;
                String text = "1";
                try {
                    mBTSocket.getOutputStream().write(text.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mReadThread = new ReadInput();
            }
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void saveData() {
        // method for saving the data in array list.
        // creating a variable for storing data in
        // shared preferences.
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);

        // creating a variable for editor to
        // store data in shared preferences.
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // creating a new variable for gson.
        Gson gson = new Gson();

        // getting data from gson and storing it in a string.
        String json = gson.toJson(readingsBuffer);

        // below line is to save data in shared
        // prefs in the form of string.
        editor.putString("readings", json);

        // below line is to apply changes
        // and save data in shared prefs.
        editor.apply();

        // after saving data we are displaying a toast message.
        Toast.makeText(this, "Saved Array List to Shared preferences. ", Toast.LENGTH_SHORT).show();
    }

    private void loadData() {
        // method to load arraylist from shared prefs
        // initializing our shared prefs with name as
        // shared preferences.
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);

        // creating a variable for gson.
        Gson gson = new Gson();

        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        String json = sharedPreferences.getString("readings", null);

        // below line is to get the type of our array list.
        Type type = new TypeToken<ArrayList<Reading>>() {}.getType();

        // in below line we are getting data from gson
        // and saving it to our array list
        readingsBuffer = gson.fromJson(json, type);

        // checking below if the array list is empty or not
        if (readingsBuffer == null) {
            // if the array list is empty
            // creating a new array list.
            readingsBuffer = new ArrayList<>();
        }
    }

    public void pushNotification( int value) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.watch_icon)
                .setContentTitle("Pulse and Saturation logger")
                .setContentText("Your pulse is dangerously high, value: "+value + "bmp")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "notification";
            String description = "Notify user";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            notificationManager.notify(1, builder.build());
        }
    }

}