package com.example.thesis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import com.example.thesis.Reading;
import static java.security.AccessController.getContext;

public class CustomAdapter extends ArrayAdapter<Reading> {

    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM \nHH:mm:ss");
    Context mContext;

    public CustomAdapter(Context context, ArrayList<Reading> users) {
        super(context, 0, users);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_view_layout, parent, false);
        }

        // Get the data item for this position
        Reading object = getItem(position);

        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.textDate);
        TextView tvHome = (TextView) convertView.findViewById(R.id.textHR);
        TextView tvSpo2 = (TextView) convertView.findViewById(R.id.textSP);
        // Populate the data into the template view using the data object
        tvName.setText(formatter.format(object.getReadingDateTime()));
        tvHome.setText(String.valueOf(object.getHeartRate()) + " BPM");
        tvSpo2.setText(String.valueOf(object.getSpO2()) + "%");

        // Return the completed view to render on screen
        return convertView;
    }
}
