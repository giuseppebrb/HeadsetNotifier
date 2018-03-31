package io.github.giuseppebrb.headsetnotifier.controller;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.giuseppebrb.headsetnotifier.R;

/**
 * Adapter for the list of applications installed on the current device.
 * It's used when user choose a new application to filter.
 */

public class CustomSpinnerAdapter extends BaseAdapter {

    Context context;
    ArrayList<Drawable> appIcons;
    ArrayList<String> appNames;
    LayoutInflater inflter;

    public  CustomSpinnerAdapter(Context context, ArrayList<String> appNames, ArrayList<Drawable> appIcons){
        this.appIcons = appIcons;
        this.appNames = appNames;
        this.context = context;
        inflter = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return appIcons.size();
    }

    @Override
    public Object getItem(int position) {
        return appNames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflter.inflate(R.layout.spinner_row, null);
        ImageView icon = (ImageView) convertView.findViewById(R.id.appIcon);
        TextView names = (TextView) convertView.findViewById(R.id.appName);
        icon.setImageDrawable(appIcons.get(position));
        names.setText(appNames.get(position));
        return convertView;
    }
}
