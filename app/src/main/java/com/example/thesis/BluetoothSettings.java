package com.example.thesis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import static android.content.ContentValues.TAG;

public class BluetoothSettings extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private int mMaxChars = 50000;//Default//change this to string..........
    private UUID mDeviceUUID;
    public BluetoothSocket mBTSocket;
    public String x;
    private ReadInput mReadThread = null;
    private boolean mIsUserInitiatedDisconnect = false;
    private boolean mIsBluetoothConnected = false;
    private BluetoothDevice mDevice;
    int seconds, minutes, hours;



    private ProgressDialog progressDialog;
    Button start, menu, button14,button15, czas, wykres;
    TextView HR_TextView, mTextView1, zmiennaA, zmiennaV;
    public String strInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_settings);

        HR_TextView = findViewById(R.id.bpm);

        try {
            mBTSocket.getOutputStream().write((int)1);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private class ReadInput implements Runnable {

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
                    byte[] buffer = new byte[12000];
                    if (inputStream.available() > 0) {
                        inputStream.read(buffer);
                        int i = 0;

                        strInput = new String(buffer, 0, i);
                        Log.d(TAG, strInput);
                        x = strInput;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                HR_TextView.setText(x);
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
}

