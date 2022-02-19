package com.example.thesis;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Reading {

    private int heartRate;
    private int spO2 = 0;
    private int sampleMilis = 0;
    private Date readingDateTime;

    Reading(int hr, int spO2, int sampleMilis)
    {
        this.heartRate = hr;
        this.spO2 = spO2;
        this.sampleMilis = sampleMilis;
        this.readingDateTime = convertReadingTime(sampleMilis);
    }

    public int getHeartRate() {
        return heartRate;
    }

    public int getSpO2() {
        return spO2;
    }

    public int getSampleMilis() {
        return sampleMilis;
    }

    public Date getReadingDateTime() {
        return readingDateTime;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public void setSampleMilis(int sampleMilis) {
        this.sampleMilis = sampleMilis;
    }

    public void setSpO2(int spO2) {
        this.spO2 = spO2;
    }

    public void setReadingDateTime(Date readingDateTime) {
        this.readingDateTime = readingDateTime;
    }

    public Date convertReadingTime(int sampleMilis) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date dateTime = Calendar.getInstance().getTime();
        Date currentDateTime = Calendar.getInstance().getTime();
        Date recordDate = new Date(sampleMilis);
        String currentString = formatter.format(currentDateTime);
        String recordString = formatter.format(recordDate);
        Log.d("Reading", "Current: " + currentString);
        Log.d("Reading", "Record: " + recordString);


        return dateTime;
    }
}
