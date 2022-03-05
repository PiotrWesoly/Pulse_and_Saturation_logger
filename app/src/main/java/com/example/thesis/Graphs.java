package com.example.thesis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM;


public class Graphs extends AppCompatActivity {

    static ArrayList<Reading> objects = new ArrayList<>();
    ArrayList<String> xAxisValues;
    private ScatterChart chart;
    MainScreen object;
    long substruct = 1640000000000L;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphs);

        chart = (ScatterChart) findViewById(R.id.hrChart);
        object = new MainScreen();

        xAxisValues = new ArrayList<String>();
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd\nHH:mm");

        for(int i=0; i<object.readingsBuffer.size();i++){

            xAxisValues.add(formatter.format(object.readingsBuffer.get(i).getReadingDateTime()));
        }


        chart.getDescription().setEnabled(false);
//        chart.setOnChartValueSelectedListener(this);

        chart.setDrawGridBackground(false);
        chart.setTouchEnabled(true);
        chart.setMaxHighlightDistance(30f);
        chart.setVisibleXRangeMaximum(10);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setExtraLeftOffset(15);
        chart.setExtraRightOffset(15);

        chart.setMaxVisibleValueCount(200);
        chart.setPinchZoom(true);

        Legend l = chart.getLegend();
        chart.getLegend().setEnabled(false);


        YAxis yl = chart.getAxisLeft();
//        yl.setTypeface(tfLight);
        yl.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        chart.getAxisRight().setEnabled(false);

        XAxis xl = chart.getXAxis();
        xl.setGranularity(1f);
        xl.setCenterAxisLabels(true);
        xl.setEnabled(true);
        xl.setDrawGridLines(false);
        xl.setPosition(BOTTOM);
//        xl.setTypeface(tfLight);
        xl.setDrawGridLines(true);

        xl.setPosition(BOTTOM);
        xl.setAxisLineColor(Color.BLUE);

        setData();

        chart.getXAxis().setValueFormatter(new MyAxisFormatter());
        chart.getXAxis().setLabelRotationAngle(45);
    }


    public void setData( ) {
        ArrayList<Entry> values1 = new ArrayList<>();

        for (int i = 0; i <object.readingsBuffer.size(); i++) {
            float val = (float) object.readingsBuffer.get(i).getHeartRate();
            values1.add(new Entry(object.readingsBuffer.get(i).getReadingDateTimeMillis()-substruct, val));
        }

        // create a dataset and give it a type
        ScatterDataSet set1 = new ScatterDataSet(values1, "HR");
        set1.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        set1.setColor(ColorTemplate.COLORFUL_COLORS[0]);

        set1.setScatterShapeSize(12f);

        ArrayList<IScatterDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the data sets

        // create a data object with the data sets
        ScatterData data = new ScatterData(dataSets);
//        data.setValueTypeface(tfLight);

        chart.setData(data);
        chart.invalidate();
    }

    private class MyAxisFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {

            for(int i=0; i<object.readingsBuffer.size(); i++) {
                if (value == (float)(object.readingsBuffer.get(i).getReadingDateTimeMillis() - substruct)){
                    return xAxisValues.get(i);
                }
            }
            return "g";
        }
    }

}