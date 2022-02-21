package com.example.thesis;

import android.util.Log;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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
        convertReadingTime(sampleMilis);
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

        long time= System.currentTimeMillis();
        Date ret = new Date();
        long y, x;
        long year, month, day, hours, minutes, seconds;

        y=time - sampleMilis;

//        y = sampleMilis/1000;

        x=(long)y/1000;
        seconds = x%60;
        x/=60;
        minutes = x%60;
        x/=60;
        hours=x%24;
        x/=24;
        day=(x+5*(x/30/12)+(x/30/12/4))%30;
        x/=30;
        month=x%12;
        x/=12;
        year=x+1970;

        ret.setTime(y);

//        Log.d("Reading", "Record: " + recordString);

        return ret;
    }
}
