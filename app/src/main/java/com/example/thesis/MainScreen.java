package com.example.thesis;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainScreen extends Activity {
    private static final String TAG = "BlueTest5-Controlling";
    private int mMaxChars = 50000;//Default//change this to string..........
    private UUID mDeviceUUID;
    public BluetoothSocket mBTSocket;
    private ReadInput mReadThread = null;
    private boolean mIsUserInitiatedDisconnect = false;
    private boolean mIsBluetoothConnected = false;
    private BluetoothDevice mDevice;

    public static List<Reading> readingsBuffer = new ArrayList<>();
    Reading object;

    CardView history, graphs;
    private ProgressDialog progressDialog;
    TextView mTextView, mTextView1;
    public String strInput, strInput2;
    public String x;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ActivityHelper.initialize(this);
        mTextView = findViewById(R.id.bpm);
        mTextView1 = findViewById(R.id.spo2);
        history = (CardView) findViewById(R.id.history);
        graphs = (CardView) findViewById(R.id.graph);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        mDevice = b.getParcelable(BluetoothSettings.DEVICE_EXTRA);
        mDeviceUUID = UUID.fromString(b.getString(BluetoothSettings.DEVICE_UUID));
        mMaxChars = b.getInt(BluetoothSettings.BUFFER_SIZE);

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
                Intent intent = new Intent(MainScreen.this, History.class);
                startActivity(intent);
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
                    byte[] buffer = new byte[256];
                    if (inputStream.available() > 0) {
                        inputStream.read(buffer);
                        int i = 0;
                        for (i = 0; i < buffer.length && buffer[i] != 0; i=i+6) {
                            object = new Reading(Byte.toUnsignedInt(buffer[i]), Byte.toUnsignedInt(buffer[i+1]), (((0xFF & buffer[i+2]) << 24) | ((0xFF & buffer[i+3]) << 16) |
                                    ((0xFF & buffer[i+4]) << 8) | (0xFF & buffer[i+5])));
                            readingsBuffer.add(object);
                        }
                        strInput = new String(String.valueOf(Byte.toUnsignedInt(buffer[0]))+" bpm");
                        strInput2 = new String(String.valueOf(Byte.toUnsignedInt(buffer[1]))+" %");

                        Log.d(TAG, "run: " + strInput);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTextView.setText(strInput);
                                mTextView1.setText(strInput2);
//                                for(int i=0; i<readingsBuffer.size(); i++)
//                                {
//                                    Log.d(TAG, "run: i= "+String.valueOf(i) + "value=" + String.valueOf(readingsBuffer.get(i).getHeartRate()));
//                                }
                            }
                        });
                    }
                    Thread.sleep(500);
                }
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
// TODO Auto-generated catch block
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
// TODO Auto-generated catch block
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
            new DisConnectBT().execute();
        }
        Log.d(TAG, "Paused");
        super.onPause();
    }
    @Override
    protected void onResume() {
        if (mBTSocket == null || !mIsBluetoothConnected) {
            new ConnectBT().execute();
        }
        Log.d(TAG, "Resumed");
        super.onResume();
    }
    @Override
    protected void onStop() {
        Log.d(TAG, "Stopped");
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
            progressDialog = ProgressDialog.show(MainScreen.this, "Hold on", "Connecting");// http://stackoverflow.com/a/11130220/1287554
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
                Toast.makeText(getApplicationContext(), "Could not connect to device.Please turn on your Hardware", Toast.LENGTH_LONG).show();
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
                mReadThread = new ReadInput(); // Kick off input reader
            }
            progressDialog.dismiss();
        }
    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}