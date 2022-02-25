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
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class Graphs extends AppCompatActivity {

    static ArrayList<Reading> objects = new ArrayList<>();
    List<String> xAxisValues;
    private ScatterChart chart;
    private SeekBar seekBarX, seekBarY;
    private TextView tvX, tvY;
    MainScreen object;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphs);

        chart = (ScatterChart) findViewById(R.id.hrChart);
        object = new MainScreen();

        xAxisValues = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm");


        for(int i=0; i<object.readingsBuffer.size();i++){

            xAxisValues.add(formatter.format(object.readingsBuffer.get(i).getReadingDateTime()));
        }

        MainScreen object;
        object = new MainScreen();

        setTitle("Graphs");

//        chart = findViewById(R.id.chart1);
        chart.getDescription().setEnabled(false);
//        chart.setOnChartValueSelectedListener(this);

        chart.setDrawGridBackground(false);
        chart.setTouchEnabled(true);
        chart.setMaxHighlightDistance(50f);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        chart.setMaxVisibleValueCount(200);
        chart.setPinchZoom(true);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
//        l.setTypeface(tfLight);
        l.setXOffset(5f);

        YAxis yl = chart.getAxisLeft();
//        yl.setTypeface(tfLight);
        yl.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        chart.getAxisRight().setEnabled(false);

        XAxis xl = chart.getXAxis();
        xl.setCenterAxisLabels(true);
        xl.setEnabled(true);
        xl.setDrawGridLines(false);
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xl.setTypeface(tfLight);
        xl.setDrawGridLines(false);

        chart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xAxisValues));
        setData(5);
    }


    public void setData( int numberOfRec) {


        ArrayList<Entry> values1 = new ArrayList<>();


        for (int i = object.readingsBuffer.size()-1-numberOfRec; i <object.readingsBuffer.size(); i++) {
            float val = (float) object.readingsBuffer.get(i).getHeartRate();
            values1.add(new Entry(object.readingsBuffer.get(i).getReadingDateTimeMillis(), val));
        }


        // create a dataset and give it a type
        ScatterDataSet set1 = new ScatterDataSet(values1, "HR");
        set1.setScatterShape(ScatterChart.ScatterShape.SQUARE);
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


}