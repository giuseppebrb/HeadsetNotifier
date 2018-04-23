package io.github.giuseppebrb.headsetnotifier;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

import io.github.giuseppebrb.headsetnotifier.model.Application;

/**
 * Activity that handles edits on a filter previously added by the user.
 */

public class FilterEdit extends AppCompatActivity {

    private Application applicationSelected;
    private AlertDialog.Builder alertDialog;
    private SharedPreferences sharedPreferences;
    private int idSoundResourceSelected = 0;
    private Spinner soundSelectorSpinner;
    private ApplicationInfo app = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        alertDialog = new AlertDialog.Builder(this);
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        final TextView appName = (TextView) findViewById(R.id.app_name);
        ImageView appIcon = (ImageView) findViewById(R.id.app_icon);
        Button saveButton = (Button) findViewById(R.id.saveButton);
        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        Button deleteButton = (Button) findViewById(R.id.deleteButton);
        ImageButton previewButton = (ImageButton) findViewById(R.id.previewButton);

        previewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer mp = MediaPlayer.create(v.getContext(), idSoundResourceSelected);
                mp.start();
            }
        });

        Intent intent = getIntent();
        final String appPackage = intent.getStringExtra(Constants.APP_PACKAGE);

        try {
            app = getPackageManager().getApplicationInfo(appPackage, 0);
            appIcon.setImageDrawable(getPackageManager().getApplicationIcon(appPackage));
            appName.setText(getPackageManager().getApplicationLabel(app));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        configureSoundSelector();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

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
                        editor.remove(appName.getText().toString());
                        editor.commit();
                        MainActivity.updateListViewValues();
                        finish();
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

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                ArrayList<String> values = new ArrayList<>();
                values.add(appPackage);
                values.add(String.valueOf(idSoundResourceSelected));
                String json = gson.toJson(values);
                try {
                    editor.putString(getPackageManager().getApplicationInfo(appPackage, 0).loadLabel(getPackageManager()).toString(), json);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                editor.commit();
                editor.apply();
                MainActivity.updateListViewValues();
                finish();
            }
        });
    }

    /**
     * Configure the sound selector in the UI
     */
    private void configureSoundSelector() {
        soundSelectorSpinner = (Spinner) findViewById(R.id.sound_selector_spinner);
        // Configuring the adapter
        ArrayAdapter<CharSequence> notificationSoundsAdapter = ArrayAdapter.createFromResource(this,
                R.array.notification_sounds, android.R.layout.simple_spinner_item);
        notificationSoundsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Specifying the behaviour when an item is selected
        soundSelectorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        // Case "Long Expected" sound
                        idSoundResourceSelected = R.raw.long_expected;
                        break;
                    case 1:
                        // Case "Quite Impressed" sound
                        idSoundResourceSelected = R.raw.quite_impressed;
                        break;
                    case 2:
                        // Case "Slow Spring Board" sound
                        idSoundResourceSelected = R.raw.slow_spring_board;
                        break;
                    case 3:
                        // Case "Solemn" sound
                        idSoundResourceSelected = R.raw.solemn;
                        break;
                    case 4:
                        // Case "To The Point"
                        idSoundResourceSelected = R.raw.to_the_point;
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                idSoundResourceSelected = R.raw.long_expected;
            }
        });
        soundSelectorSpinner.setAdapter(notificationSoundsAdapter);
        soundSelectorSpinner.setSelection(getSpinnerIdResource());
    }

    /**
     * Pair the notification sound resource of selected app with the values in the spinner
     *
     * @return position in spinner list of the notification sound
     */
    private int getSpinnerIdResource() {
        int id = -1;
        Gson gson = new Gson();
        String json = sharedPreferences.getString(app.loadLabel(getPackageManager()).toString(), null);
        ArrayList<String> values = gson.fromJson(json, ArrayList.class);

        switch (Integer.valueOf(values.get(1))) {
            case R.raw.long_expected:
                id = 0;
                break;
            case R.raw.quite_impressed:
                id = 1;
                break;
            case R.raw.slow_spring_board:
                id = 2;
                break;
            case R.raw.solemn:
                id = 3;
                break;
            case R.raw.to_the_point:
                id = 4;
                break;
        }
        return id;
    }

    /**
     * Handles the behaviour when user cancel the edit operation.
     */
    private void cancel() {
        alertDialog.setTitle(R.string.warning);
        alertDialog.setMessage(getString(R.string.filter_will_not_be_saved));
        alertDialog.setPositiveButton(R.string.leave, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.create().show();
    }

    @Override
    public void onBackPressed() {
        cancel();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                cancel();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
