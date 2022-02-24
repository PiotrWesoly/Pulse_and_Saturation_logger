package com.example.thesis;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class History extends AppCompatActivity {

    private static final String TAG = "History";
    ListView timeList;
    CardView dateFrom, dateTo;
    TextView timeFromText, dateFromText, timeToText, dateToText;
    Button   saveBtn, backBtn;
    public long millisFrom, millisTo;
    Calendar fromDate, toDate;
    int mYear, mMonth, mDay, mHour, mMinute;
    ArrayList<Reading> objects = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        fromDate = Calendar.getInstance();
        toDate = Calendar.getInstance();

        MainScreen object;
        object = new MainScreen();

        timeList = (ListView) findViewById(R.id.timeList);
        dateFrom = (CardView) findViewById(R.id.dateFrom);
        dateTo = (CardView) findViewById(R.id.dateTo);
        timeToText = (TextView) findViewById(R.id.timeToText);
        timeFromText = (TextView) findViewById(R.id.timeFromText);
        dateFromText = (TextView) findViewById(R.id.dateFromText);
        dateToText = (TextView) findViewById(R.id.dateToText);

        millisFrom = object.readingsBuffer.get(0).getReadingDateTimeMillis();
        millisTo = object.readingsBuffer.get(object.readingsBuffer.size()-1).getReadingDateTimeMillis();

        dateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCal(dateFrom, timeFromText, dateFromText, object);
            }
        });

        dateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCal(dateTo, timeToText, dateToText, object);
            }
        });

        for(int i=0; i<object.readingsBuffer.size(); i++) {
            if(object.readingsBuffer.get(i).getReadingDateTimeMillis()>=millisFrom && object.readingsBuffer.get(i).getReadingDateTimeMillis()<= millisTo) {
                objects.add(object.readingsBuffer.get(i));
            }
        }

        CustomAdapter adapter = new CustomAdapter(this, objects);
        timeList.setAdapter(adapter);

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");

        timeFromText.setText(formatter.format(objects.get(0).getReadingDateTime()));
        timeToText.setText(formatter.format(objects.get(objects.size()-1).getReadingDateTime()));
        formatter = new SimpleDateFormat("dd-MM-YYYY");
        dateFromText.setText(formatter.format(objects.get(0).getReadingDateTime()));
        dateToText.setText(formatter.format(objects.get(objects.size()-1).getReadingDateTime()));

    }

    private void openCal(CardView button, TextView timeText, TextView dateText, MainScreen object) {
        String defaultDateYear, defaultDateMonth, defaultDateDay ;
        long time, date;


        final LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.activity_time, null);

        TimePicker timePicker = (TimePicker) linearLayout.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        DatePicker datePicker = (DatePicker) linearLayout.findViewById(R.id.datePicker);

        final AlertDialog builder = new AlertDialog.Builder(this)
                .setView(linearLayout)
                .setCancelable(false)
                .create();
        builder.show();

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

                mHour = hourOfDay;
                mMinute = minute;

                if (hourOfDay < 10 && minute < 10) {
                    timeText.setText("0" + String.valueOf(hourOfDay) + ":" + "0" + String.valueOf(minute));
                } else if (hourOfDay < 10) {
                    timeText.setText("0" + String.valueOf(hourOfDay) + ":" + String.valueOf(minute));
                } else if (minute < 10) {
                    timeText.setText(String.valueOf(hourOfDay) + ":" + "0" + String.valueOf(minute));
                } else
                    timeText.setText(String.valueOf(hourOfDay) + ":" + String.valueOf(minute));
            }

        });

        datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear++;

                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
                if(dayOfMonth<10 && monthOfYear<10){
                    dateText.setText("0"+dayOfMonth+"-"+"0"+monthOfYear+"-"+year);
                }
                else if(dayOfMonth<10){
                    dateText.setText("0"+dayOfMonth+"-"+monthOfYear+"-"+year);
                }
                else if(monthOfYear<10) {
                    dateText.setText(dayOfMonth + "-" + "0" + monthOfYear + "-" + year);
                }
                else {
                    dateText.setText(dayOfMonth + "-" + monthOfYear + "-" + year);
                }
            }
        });

        if(button == dateFrom) {
            fromDate.set(mYear, mMonth, mDay, mHour, mMinute);
            millisFrom = fromDate.getTimeInMillis();
        }else{
            toDate.set(mYear, mMonth, mDay, mHour, mMinute);
            millisTo = toDate.getTimeInMillis();
        }

        for(int i=0; i<object.readingsBuffer.size(); i++) {
            if(object.readingsBuffer.get(i).getReadingDateTimeMillis()>=millisFrom && object.readingsBuffer.get(i).getReadingDateTimeMillis()<= millisTo) {
                objects.add(object.readingsBuffer.get(i));
            }
        }

        saveBtn = linearLayout.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                builder.dismiss();
            }
        });
        backBtn = linearLayout.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                builder.dismiss();
            }
        });

    }

}