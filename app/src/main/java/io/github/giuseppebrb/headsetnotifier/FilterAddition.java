package io.github.giuseppebrb.headsetnotifier;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.giuseppebrb.headsetnotifier.controller.CustomSpinnerAdapter;
import io.github.giuseppebrb.headsetnotifier.model.Application;

/**
 *  Handles the behaviour of the activity where user can choose what apps need to be filtered.
 */
public class FilterAddition extends AppCompatActivity {

    private Application applicationSelected;
    private AlertDialog.Builder alertDialog;
    private SharedPreferences sharedPreferences;
    private Spinner soundSelectorSpinner;

    private int idSoundResourceSelected = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_addition);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        alertDialog = new AlertDialog.Builder(this);
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        // Retrieve information of every app installed
        PackageManager packageManager = getPackageManager();
        List<ApplicationInfo> applicationInfoList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        final ArrayList<Application> applications = new ArrayList<>();
        for(ApplicationInfo applicationInfo : applicationInfoList)
            applications.add(new Application(applicationInfo.loadLabel(packageManager).toString(),
                    packageManager.getApplicationIcon(applicationInfo),
                    applicationInfo.packageName));
        Collections.sort(applications);

        ArrayList<String> appNames = new ArrayList<>();
        final ArrayList<Drawable> appIcons = new ArrayList<>();
        for (Application app: applications){
            appNames.add(app.getName());
            appIcons.add(app.getIcon());
        }

        Spinner applicationChooser = (Spinner) findViewById(R.id.applicationChooserSpinner);
        CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(this, appNames, appIcons);
        applicationChooser.setAdapter(customSpinnerAdapter);
        applicationChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String appNameSelected = (String) parent.getItemAtPosition(position);
                for (Application app: applications)
                    if (app.getName().equalsIgnoreCase(appNameSelected)){
                        applicationSelected = app;
                        return;
                    }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        configureSoundSelector();
        ImageButton previewButton = (ImageButton) findViewById(R.id.previewButton);
        previewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer mp = MediaPlayer.create(v.getContext(), idSoundResourceSelected);
                mp.start();
            }
        });


        Button saveButton = (Button) findViewById(R.id.saveButton);
        Button cancelButton = (Button) findViewById(R.id.cancelButton);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((applicationSelected == null)){
                    alertDialog.setTitle(R.string.error);
                    alertDialog.setMessage(getString(R.string.incomplete_values));
                    alertDialog.setPositiveButton(R.string.ok,new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.create().show();
                } else {
                    Gson gson = new Gson();
                    ArrayList<String> values = new ArrayList<>();
                    values.add(applicationSelected.getPackageName());
                    values.add(String.valueOf(idSoundResourceSelected));
                    String json = gson.toJson(values);
                    editor.putString(applicationSelected.getName(), json);
                    editor.commit();
                    editor.apply();
                    MainActivity.updateListViewValues();
                    finish();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIfValuesEmpty();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                checkIfValuesEmpty();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        checkIfValuesEmpty();
    }

    /**
     * Check if values in the UI have been modified by the user.
     * If so an alert dialog appears.
     */
    private void checkIfValuesEmpty(){
        if ((applicationSelected != null)){
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
    }

    /**
     * Configure the sound selector in the UI
     */
    private void configureSoundSelector(){
        soundSelectorSpinner = (Spinner) findViewById(R.id.sound_selector_spinner);
        // Configuring the adapter
        ArrayAdapter<CharSequence> notificationSoundsAdapter = ArrayAdapter.createFromResource(this,
                R.array.notification_sounds, android.R.layout.simple_spinner_item);
        notificationSoundsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Specifying the behaviour when an item is selected
        soundSelectorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
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
    }
}
