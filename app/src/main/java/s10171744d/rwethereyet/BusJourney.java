package s10171744d.rwethereyet;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import s10171744d.rwethereyet.model.BusStop;
import s10171744d.rwethereyet.model.UpdateData;
import s10171744d.rwethereyet.model.UpdateStop;

/**
 * Lewis Tham Jee Peng | Group 9 | S10171744D
 */

public class BusJourney extends AppCompatActivity{

    //public UpdateStopReceiver stopReceiver; //setup receiver from update stop service

    //receiver for broadcasts, to check if the service has updated location yet, so that can set view accordingly
    private DataUpdateReceiver dataUpdateReceiver;

    TextView tvReachYet;
    TextView tvStopsLeft;
    TextView tvPrevStop;

    ImageView ivStop;

    private static final int LOCATION_PERMISSION_ID = 1001; //setting of the location permission id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_journey);


        tvReachYet = (TextView)findViewById(R.id.tvReachYet);
        tvStopsLeft = (TextView)findViewById(R.id.tvStopsLeft);
        tvPrevStop = (TextView)findViewById(R.id.tvPrevStop);
        ivStop = (ImageView) findViewById(R.id.ivStop);

        // check location permission before running service
        checkPermission();
    }

    private void checkPermission() //check if location permission granted, and start service if it is, if not, request for perm.
    {
        Boolean succ;
        succ = (ContextCompat.checkSelfPermission(BusJourney.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        if (!succ)
        {
            ActivityCompat.requestPermissions(BusJourney.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);
        }
        else
        {
            //start the service if location permissions granted
            Intent i = new Intent(this, UpdateStop.class);
            startService(i);
        }
    }

    //@android.support.annotation.RequiresApi(api = Build.VERSION_CODES.M)
    @Override //check when user has selected whether or not to allow permissions
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        /* commented out as it is hard to do
        //if user selects don't ask again..
        if (!shouldShowRequestPermissionRationale(permissions[0]))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Need location permissions for proper functionality");
            builder.setMessage("Are we there yet requires the location permission in order to detect what bus stop you are at. Please allow permissions from settings in order for the app to work.");
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                }
            });
            builder.setNegativeButton("exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish(); //will go to ondestroy, then from there stop the service
                }
            });
            builder.show();
        }

        */
        checkPermission(); //check permission again once option from request has been selected
    }


    //register and unregister listener for updates if the app is in foreground/background to prevent errors
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("lc","resumed");
        updateView();
        if (dataUpdateReceiver == null) dataUpdateReceiver = new DataUpdateReceiver();
        IntentFilter intentFilter = new IntentFilter("LocationUpdated");
        registerReceiver(dataUpdateReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("lc","paused");
        if (dataUpdateReceiver != null) unregisterReceiver(dataUpdateReceiver);
    }


    // callback for when data is received from service using datayodatereceiver listener (theres updated data from the service)

    private class DataUpdateReceiver extends BroadcastReceiver { //receiver to check if theres any changes in the thing
        @Override
        public void onReceive(Context context, Intent intent) {  //triggered when data is sent from service
            if (intent.getAction().equals("LocationUpdated")) {
                updateView();
            }
        }
    }

    private void updateView(){
        Integer status = UpdateData.stopStatus;

        //check the status of bus stop
        if (status == 1)
        {
            BusStop prevStop = UpdateData.prevStop;

            String stopinfo = String.format("%s (%s)",prevStop.getName(),prevStop.getCode());

            tvReachYet.setText("no");
            tvStopsLeft.setText(UpdateData.stopsLeft +" more stops to " +UpdateData.destStop.getName());
            tvPrevStop.setText(stopinfo);

            ivStop.setVisibility(View.GONE);
        }
        else if (status == 2)
        {
            BusStop prevStop = UpdateData.prevStop;

            String stopinfo = String.format("%s (%s)",prevStop.getName(),prevStop.getCode());

            tvReachYet.setText("soon");
            tvStopsLeft.setText(UpdateData.stopsLeft +" more stops to " +UpdateData.destStop.getName());
            tvPrevStop.setText(stopinfo);

            ivStop.setVisibility(View.VISIBLE);

        }
        else if (status == 3)
        {
            BusStop prevStop = UpdateData.prevStop;

            String stopinfo = String.format("%s (%s)",prevStop.getName(),prevStop.getCode());

            tvReachYet.setText("yes");
            tvStopsLeft.setText("");
            tvPrevStop.setText(stopinfo);

            ivStop.setVisibility(View.VISIBLE);

        }
        else if (status == 4)
        {
            BusStop prevStop = UpdateData.prevStop;

            String stopinfo = String.format("%s (%s)",prevStop.getName(),prevStop.getCode());

            tvReachYet.setText("!!!");
            tvStopsLeft.setText(UpdateData.stopsLeft +" more stops to " +UpdateData.destStop.getName());
            tvPrevStop.setText(stopinfo);

            ivStop.setVisibility(View.GONE);
        }

        else if (status == -1)
        {
            tvReachYet.setText("...");
            tvStopsLeft.setText("searching for bus stop");
            tvPrevStop.setText("your journey will start once you are near a bus stop within your selected route");

            ivStop.setVisibility(View.GONE);
        }
    }

    //Method to display location, used for debugging
    private void showLocation(Location location) {
        if (location != null) {
            final String text = String.format("Latitude %.6f, Longitude %.6f",
                    location.getLatitude(),
                    location.getLongitude());

        }
        else
        {

        }
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("End Journey");
        builder.setMessage("Would you like to end your journey?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish(); //will go to ondestroy, then from there stop the service
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    @Override
    protected void onDestroy() {
        //end the service
        Intent i = new Intent(this, UpdateStop.class);
        stopService(i); //for proper android garbage collection, end service so it doesnt consume resources

        super.onDestroy();
    }
}
