package com.example.thesis;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Handler;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class Bluetooth {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
    private boolean scanning;
    private Handler handler = new Handler();
//    ScanCallback leScanCallback;
    SortedSet<BluetoothDevice> scannedDevices = new TreeSet<>();

    ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            scannedDevices.add(result.getDevice());
        }
    };


    /**************************************************
     *               PUBLIC METHODS                   *
     **************************************************/
    public void scan() {
        requestPermission();
        scanLeDevice();
    }

    /*************************************************
    *               PRIVATE METHODS                  *
    **************************************************/

     */
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    private void scanLeDevice() {
        if (!scanning) {
            // Stops scanning after a predefined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanning = false;

                    bluetoothLeScanner.stopScan(leScanCallback);
                }
            }, SCAN_PERIOD);

            scanning = true;
            bluetoothLeScanner.startScan(leScanCallback);
        } else {
            scanning = false;
            bluetoothLeScanner.stopScan(leScanCallback);
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[] {ACCESS_COARSE_LOCATION}, 123);
    }
}
