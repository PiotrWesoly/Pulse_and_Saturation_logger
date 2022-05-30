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
    private long readingDateTimeMillis;

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

    public long getReadingDateTimeMillis() {
        return readingDateTimeMillis;
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

    public void setReadingDateTimeMillis(long readingDateTimeMillis) {
        this.readingDateTimeMillis = readingDateTimeMillis;
    }

    public Date convertReadingTime(int sampleMilis) {

        long time= System.currentTimeMillis();
        Date ret = new Date();
        long y, x;

        y=time - sampleMilis;
        ret.setTime(y);
        setReadingDateTimeMillis(y);

        return ret;
    }
}
