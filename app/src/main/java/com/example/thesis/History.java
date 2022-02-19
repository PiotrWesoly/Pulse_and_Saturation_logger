package com.example.thesis;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class History extends AppCompatActivity {

    ListView timeList, hrList, spo2List;
    TextView test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        timeList = (ListView) findViewById(R.id.timeList);
        hrList = (ListView) findViewById(R.id.hrList);
        spo2List = (ListView) findViewById(R.id.spo2List);
        test = (TextView) findViewById(R.id.test);

        ArrayList<String> time=new ArrayList<>();
        ArrayList<String> hr=new ArrayList<>();
        ArrayList<String> spo2=new ArrayList<>();

        test.setText("BLA");

        MainScreen object;
        object = new MainScreen();

        for(int i=0; i<object.readingsBuffer.size(); i++) {
            hr.add(String.valueOf(object.readingsBuffer.get(i).getHeartRate()));
            spo2.add(String.valueOf(object.readingsBuffer.get(i).getSpO2()));
            time.add((object.readingsBuffer.get(i).getReadingDateTime().toString()));

            Log.d("tag", "buffer[" + i + "] value hr = " + hr.get(i));
        }

//        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, object.readingsBuffer) {
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                View view = super.getView(position, convertView, parent);
//                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
//                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
//
//                text1.setText(hr.get(position));
//                text2.setText(spo2.get(position));
//                return view;
//            }
//        };

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, hr);
        ArrayAdapter arrayAdapter1 = new ArrayAdapter(this, android.R.layout.simple_list_item_1, spo2);
        ArrayAdapter arrayAdapter2 = new ArrayAdapter(this, android.R.layout.simple_list_item_1, time);
//
        hrList.setAdapter(arrayAdapter);
        spo2List.setAdapter(arrayAdapter1);
        timeList.setAdapter(arrayAdapter2);

    }
}