package io.github.giuseppebrb.headsetnotifier.controller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.giuseppebrb.headsetnotifier.MainActivity;
import io.github.giuseppebrb.headsetnotifier.R;

/**
 * Adapter for the applications chosen by the user to be filtered.
 */

public class FiltersListAdapter extends BaseAdapter {
    private ArrayList<String> mListItems;
    private ArrayList<Drawable> mAppIcons;
    private LayoutInflater mLayoutInflater;
    private SharedPreferences sharedPreferences;
    private AlertDialog.Builder alertDialog;

    public FiltersListAdapter(Context context, ArrayList<String> appNames, ArrayList<Drawable> appIcons){
        mListItems = appNames;
        mAppIcons = appIcons;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mListItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = mLayoutInflater.inflate(R.layout.filter_item, null);
        ImageView icon = (ImageView) convertView.findViewById(R.id.appIcon);
        TextView names = (TextView) convertView.findViewById(R.id.filterItemText);
        ImageButton deleteButton = (ImageButton) convertView.findViewById(R.id.deleteFilter);
        names.setText(mListItems.get(position));
        icon.setImageDrawable(mAppIcons.get(position));
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog = new AlertDialog.Builder(v.getContext());
                alertDialog.setTitle(R.string.warning);
                alertDialog.setMessage(v.getContext().getString(R.string.delete_message));
                alertDialog.setPositiveButton(v.getContext().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sharedPreferences = alertDialog.getContext().getSharedPreferences(alertDialog.getContext().getString(R.string.app_name), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove(mListItems.get(position));
                        editor.commit();
                        MainActivity.updateListViewValues();
                    }
                });
                alertDialog.setNegativeButton(alertDialog.getContext().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.create().show();
            }
        });
        return convertView;
    }

}
