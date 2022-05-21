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
    static ArrayList<Reading> objects = new ArrayList<>();
    public static CustomAdapter adapter;
    boolean isDatechanged = false, isTimechanged = false;

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
            if(objects.contains(object.readingsBuffer.get(i)) == true)
            {
                continue;
            }
            objects.add(object.readingsBuffer.get(i));
        }

        adapter = new CustomAdapter(this, objects);
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

        SimpleDateFormat formatterYear = new SimpleDateFormat("YYYY");
        SimpleDateFormat formatterMonth = new SimpleDateFormat("MM");
        SimpleDateFormat formatterDay = new SimpleDateFormat("dd");
        SimpleDateFormat formatterHour = new SimpleDateFormat("hh");
        SimpleDateFormat formatterMin = new SimpleDateFormat("mm");

        if(objects.size()!=0) {
            datePicker.updateDate(Integer.parseInt(formatterYear.format(objects.get(0).getReadingDateTime())), Integer.parseInt(formatterMonth.format(objects.get(0).getReadingDateTime())) - 1, Integer.parseInt(formatterDay.format(objects.get(0).getReadingDateTime())));
            timePicker.setHour(Integer.parseInt(formatterHour.format(objects.get(0).getReadingDateTime())));
            timePicker.setMinute(Integer.parseInt(formatterMin.format(objects.get(0).getReadingDateTime())));

        }
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
                isTimechanged = true;

            }

        });

        datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear++;

                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
                isDatechanged = true;
            }
        });

        saveBtn = linearLayout.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(button == dateFrom) {
                    fromDate.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getHour(), timePicker.getMinute());
                    millisFrom = fromDate.getTimeInMillis();
                }else{
                    toDate.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getHour(), timePicker.getMinute());
                    millisTo = toDate.getTimeInMillis();
                }
                objects.clear();
                for(int i=0; i<object.readingsBuffer.size(); i++) {
                    if(object.readingsBuffer.get(i).getReadingDateTimeMillis()>=millisFrom && object.readingsBuffer.get(i).getReadingDateTimeMillis()<= millisTo) {
                        objects.add(object.readingsBuffer.get(i));
                    }
                }

                if(isTimechanged == true) {
                    setTime(timeText);
                    isTimechanged = false;
                }
                if(isDatechanged == true) {
                    setDate(dateText);
                    isDatechanged = false;
                }

                adapter.notifyDataSetChanged();
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

    private void setDate(TextView dateText){

        if(mDay<10 && mMonth<10){
            dateText.setText("0"+mDay+"-"+"0"+mMonth+"-"+mYear);
        }
        else if(mDay<10){
            dateText.setText("0"+mDay+"-"+mMonth+"-"+mYear);
        }
        else if(mMonth<10) {
            dateText.setText(mDay + "-" + "0" + mMonth + "-" + mYear);
        }
        else {
            dateText.setText(mDay + "-" + mMonth + "-" + mYear);
        }
    }


    protected void setTime(TextView timeText) {

        if (mHour < 10 && mMinute < 10) {
            timeText.setText("0" + String.valueOf(mHour) + ":" + "0" + String.valueOf(mMinute));
        } else if (mHour < 10) {
            timeText.setText("0" + String.valueOf(mHour) + ":" + String.valueOf(mMinute));
        } else if (mMinute < 10) {
            timeText.setText(String.valueOf(mHour) + ":" + "0" + String.valueOf(mMinute));
        } else
            timeText.setText(String.valueOf(mHour) + ":" + String.valueOf(mMinute));
    }
}