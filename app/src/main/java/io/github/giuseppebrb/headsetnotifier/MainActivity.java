package io.github.giuseppebrb.headsetnotifier;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

import io.github.giuseppebrb.headsetnotifier.controller.FiltersListAdapter;
import io.github.giuseppebrb.headsetnotifier.controller.services.HeadsetListenerService;

/**
 * Entry point of the application.
 */
public class MainActivity extends AppCompatActivity {
    private AlertDialog mAlertDialog;
    private ProgressDialog mProgressDialog;
    private static FiltersListAdapter adapter;
    private static ListView listView;
    private static ArrayList<String> apps;
    private static ArrayList<Drawable> icons;
    private static PackageManager packageManager;
    private static SharedPreferences preferences;

    private boolean isReceiverRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        packageManager = getPackageManager();
        preferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);

        if(!isNotificationServiceEnabled()){
            mAlertDialog = buildNotificationServiceAlertDialog();
            mAlertDialog.show();
        }

        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.mainConstraintLayout);
        listView = (ListView) constraintLayout.findViewById(R.id.filtersListView);

        fetchListValues();

        adapter = new FiltersListAdapter(this, apps, icons);
        listView.setAdapter(adapter);
        listView.setDivider(null);
        listView.setEmptyView(constraintLayout.findViewById(R.id.emptyLayout));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressDialog = new ProgressDialog(MainActivity.this);
                mProgressDialog.setCancelable(false);
                mProgressDialog.setMessage(getString(R.string.loading));
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.show();
                Intent addFilterIntent = new Intent(view.getContext(), FilterAddition.class);
                startActivity(addFilterIntent);
            }
        });

        if(HeadsetListenerService.isHeadsetListenerRunning() == false)
            startService(new Intent(this, HeadsetListenerService.class));

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Progress dialog started when clicked FAB
        if (mProgressDialog!= null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // Remove comment when menu will be used
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Checks if user granted to the application the authorization to manage the notifications.
     * @return true is parmission has been granted, false otherwise
     */
    private boolean isNotificationServiceEnabled(){
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                Constants.ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private AlertDialog buildNotificationServiceAlertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.permission_needed);
        alertDialogBuilder.setMessage(R.string.permission_needed_explanation);
        alertDialogBuilder.setPositiveButton(R.string.notification_settings,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Constants.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                    }
                });
        alertDialogBuilder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // If you choose to not enable the notification listener
                        // the app. will not work as expected
                    }
                });
        return(alertDialogBuilder.create()); // Alert dialog asking to grant notification management permission
    }

    /**
     * Update the list of filters added by the user.
     */
    public static void updateListViewValues(){
        fetchListValues();
        adapter = new FiltersListAdapter(listView.getContext(), apps, icons);
        listView.setAdapter(adapter);
    }

    /**
     * Get the application list selected by the user that should be filtered.
     */
    private static void fetchListValues(){
        apps = new ArrayList<>();
        icons = new ArrayList<>();

        Map<String, ?> allFilters = preferences.getAll();

        for (Map.Entry<String, ?> entry: allFilters.entrySet()){
            apps.add(entry.getKey());

            Gson gson = new Gson();
            String json = preferences.getString(entry.getKey(), null);
            ArrayList<String> values = gson.fromJson(json, ArrayList.class);
            try {
                String packageName = values.get(0); // represents the package name;
                ApplicationInfo app = packageManager.getApplicationInfo(packageName, 0);
                icons.add(packageManager.getApplicationIcon(app));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                icons.add(null);
            }
        }
    }
}
