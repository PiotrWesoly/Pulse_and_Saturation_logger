package com.example.thesis;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class History extends AppCompatActivity {

    ListView timeList, hrList, spo2List;
    TextView test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        timeList = (ListView) findViewById(R.id.timeList);
        hrList = (ListView) findViewById(R.id.hrList);
        spo2List = (ListView) findViewById(R.id.spo2List);

        ArrayList<String> time=new ArrayList<>();
        ArrayList<String> hr=new ArrayList<>();
        ArrayList<String> spo2=new ArrayList<>();


        MainScreen object;
        object = new MainScreen();

        for(int i=0; i<object.readingsBuffer.size(); i++) {
            String timeStr;

            /* WORKS */
            timeStr = formatter.format(object.readingsBuffer.get(i).getReadingDateTime());
            hr.add(String.valueOf(object.readingsBuffer.get(i).getHeartRate()) + " bpm");
            spo2.add(String.valueOf(object.readingsBuffer.get(i).getSpO2()) + "%");
            time.add(timeStr);

        }


        /* WORKS */
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, hr);
        ArrayAdapter arrayAdapter1 = new ArrayAdapter(this, android.R.layout.simple_list_item_1, spo2);
        ArrayAdapter arrayAdapter2 = new ArrayAdapter(this, android.R.layout.simple_list_item_1, time);

        hrList.setAdapter(arrayAdapter);
        spo2List.setAdapter(arrayAdapter1);
        timeList.setAdapter(arrayAdapter2);

    }
}