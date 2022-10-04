package com.example.thesis;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM;


public class Graphs extends AppCompatActivity {

    private static final String TAG = "Graphs";
    ArrayList<String> xAxisValues;
    private LineChart chartHR;
    private LineChart chartSpo2;
    MainScreen object;
    public LineData data;
    public static int iter=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphs);

        chartHR = (LineChart) findViewById(R.id.hrChart);
        chartSpo2 = (LineChart) findViewById(R.id.spo2Chart);

        setUpChart(chartHR);
        setUpChart(chartSpo2);

        addDate();

        setData(chartHR);
        setData(chartSpo2);
//        chartHR.setVisibleXRangeMaximum(10f);
//        chartSpo2.setVisibleXRangeMaximum(10f);

    }

    private void setUpChart(LineChart chart) {

        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setTouchEnabled(true);
//        chart.setMaxHighlightDistance(30f);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setExtraLeftOffset(15);
        chart.setExtraRightOffset(15);

        chart.setMaxVisibleValueCount(10);
        chart.setPinchZoom(true);
        chart.getLegend().setEnabled(false);
//        chart.setVisibleXRange(5,20);
//        chart.moveViewToX(10);

        YAxis yl = chart.getAxisLeft();
        yl.setAxisMinimum(10f); // this replaces setStartAtZero(true)
        yl.setAxisMaximum(150f);

        chartSpo2.getAxisLeft().setAxisMinimum(80);
        chartSpo2.getAxisLeft().setAxisMaximum(100);

        chartHR.getAxisLeft().setAxisMinimum(50);
        chartHR.getAxisLeft().setAxisMaximum(150);

        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setLabelCount(5, true);


        XAxis xl = chart.getXAxis();
//        xl.setCenterAxisLabels(false);
        xl.setEnabled(true);
        xl.setDrawLabels(true);
//        xl.setLabelCount(20, true);
        xl.setSpaceMin(10f);
        xl.setGranularity(1f);

        xl.setPosition(BOTTOM);
        xl.setDrawGridLines(true);
        xl.setAxisLineColor(Color.RED);

        xl.setValueFormatter(new MyAxisFormatter());
        xl.setLabelRotationAngle(45);
        xl.setTextSize(10f);
//        xl.setLabelCount(5, true);
    }

    private void addDate() {
        object = new MainScreen();
        xAxisValues = new ArrayList<String>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM\nHH:mm");

        for(int i=0; i<object.readingsBuffer.size();i++){

            xAxisValues.add(formatter.format(object.readingsBuffer.get(i).getReadingDateTime()));
        }
    }

    public void setData(LineChart chart ) {
        ArrayList<Entry> values1 = new ArrayList<>();
        float timeDifference = 0;
        float val = 0;

        for (int i = 0; i <object.readingsBuffer.size(); i++) {
            if(chart == chartHR) {
                val = (float) object.readingsBuffer.get(i).getHeartRate();
            }
            else{
                val = (float) object.readingsBuffer.get(i).getSpO2();
            }
            timeDifference = ((object.readingsBuffer.get(i).getReadingDateTimeMillis() - object.readingsBuffer.get(0).getReadingDateTimeMillis() )/1000);
            Log.i(TAG, "i: "+ i + "timeDiff:" + timeDifference);
            values1.add(new Entry(timeDifference, val));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(values1, "HR");
        set1.disableDashedLine();
        set1.setColor(Color.DKGRAY);
        set1.setCircleColor(Color.DKGRAY);
        set1.setLineWidth(4f);
        if (chart == chartHR) {
            set1.setColor(Color.RED, 40);
        }
        else{
            set1.setColor(Color.BLUE, 40);
        }
        set1.setCircleRadius(2f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(0f);
        set1.setDrawFilled(true);

        if(chart == chartHR){
            set1.setFillDrawable(getDrawable(R.drawable.gradient_graph_hr));
        }
        else
        {
            set1.setFillDrawable(getDrawable(R.drawable.gradient_graph_spo2));
        }
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the data sets

        // create a data object with the data sets
        data = new LineData(dataSets);

        chart.getXAxis().setValueFormatter(new MyAxisFormatter());

        chart.setData(data);
        chart.invalidate();

        chart.setVisibleXRange(10f,300f);
//        chart.moveViewToX(chart.getXChartMax());
    }


    private class MyAxisFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            for (int i = 0; i < object.readingsBuffer.size(); i++) {
////                Log.i(TAG, "value:"+ value + " timeDiff:" + ((object.readingsBuffer.get(i).getReadingDateTimeMillis() - object.readingsBuffer.get(0).getReadingDateTimeMillis() )/1000));
////                Log.i(TAG, " timesWithoutDividing:" + (object.readingsBuffer.get(i).getReadingDateTimeMillis() - object.readingsBuffer.get(0).getReadingDateTimeMillis() ));
////                Log.i(TAG, "value:"+ value + " timeTo:" + (object.readingsBuffer.get(i).getReadingDateTimeMillis()));
                if ((float)value - ( (float) ((object.readingsBuffer.get(i).getReadingDateTimeMillis() - object.readingsBuffer.get(0).getReadingDateTimeMillis()) / 1000))<10) {
                    return xAxisValues.get(i);
                }
            }
            return " ";
        }
    }
}