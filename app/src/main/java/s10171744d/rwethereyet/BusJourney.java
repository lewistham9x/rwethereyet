package s10171744d.rwethereyet;

import android.Manifest;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.Image;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.DetectedActivity;

import java.util.List;

import s10171744d.rwethereyet.model.BusStop;
import s10171744d.rwethereyet.model.Control;
import s10171744d.rwethereyet.model.UpdateData;
import s10171744d.rwethereyet.model.UpdateStop;

public class BusJourney extends AppCompatActivity{

    //public UpdateStopReceiver stopReceiver; //setup receiver from update stop service

    //receiver for broadcasts, to check if the service has updated location yet, so that can set view accordingly
    private DataUpdateReceiver dataUpdateReceiver;

    TextView tv1;
    TextView tv2;

    TextView debug1;
    TextView debug2;

    ImageView ivStop;


    private static final int LOCATION_PERMISSION_ID = 1001; //setting of the location permission id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_journey);


        tv1 = (TextView)findViewById(R.id.textView);
        tv2 = (TextView)findViewById(R.id.textView2);
        debug1 = (TextView)findViewById(R.id.tvDebug);
        debug2 = (TextView)findViewById(R.id.tvDebug2);
        ivStop = (ImageView) findViewById(R.id.ivStop);

        // check location permission
        if (ContextCompat.checkSelfPermission(BusJourney.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(BusJourney.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);
            return;}
        {

            //start the service if location permissions granted
            //setupServiceReceiver();

            Intent i = new Intent(this, UpdateStop.class);
            startService(i);
        }
    }




    //register and unregister listener for updates if the app is in foreground/background to prevent errors
    @Override
    protected void onResume() {
        super.onResume();
        if (dataUpdateReceiver == null) dataUpdateReceiver = new DataUpdateReceiver();
        IntentFilter intentFilter = new IntentFilter("LocationUpdated");
        registerReceiver(dataUpdateReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dataUpdateReceiver != null) unregisterReceiver(dataUpdateReceiver);
    }




    // callback for when data is received from service using datayodatereceiver listener (theres updated data from the service)

    private class DataUpdateReceiver extends BroadcastReceiver { //receiver to check if theres any changes in the thing
        @Override
        public void onReceive(Context context, Intent intent) {  //triggered when data is sent from service
            if (intent.getAction().equals("LocationUpdated")) {
                //show new location (DEBUG)
                showLocation(UpdateData.curLoc);

                Integer status = UpdateData.stopStatus;

                //check the status of bus stop
                if (status == 1)
                {
                    BusStop prevStop = UpdateData.prevStop;

                    String stopinfo = String.format("Last Bus Stop\nName: %s\nCode: %s",prevStop.getName(),prevStop.getCode());
                    tv1.setText(stopinfo);

                    tv2.setText("No, "+UpdateData.stopsLeft +" more stops");

                    ivStop.setVisibility(View.GONE);
                }
                else if (status == 2)
                {
                    BusStop prevStop = UpdateData.prevStop;

                    String stopinfo = String.format("Last Bus Stop\nName: %s\nCode: %s",prevStop.getName(),prevStop.getCode());
                    tv1.setText(stopinfo);

                    tv2.setText("Soon, "+UpdateData.stopsLeft +" more stops");

                    ivStop.setVisibility(View.VISIBLE);

                }
                else if (status == 3)
                {
                    BusStop prevStop = UpdateData.prevStop;

                    String stopinfo = String.format("Last Bus Stop\nName: %s\nCode: %s",prevStop.getName(),prevStop.getCode());
                    tv1.setText(stopinfo);

                    tv2.setText("Yes");

                    ivStop.setVisibility(View.VISIBLE);

                }
                else if (status == 4)
                {
                    BusStop prevStop = UpdateData.prevStop;

                    String stopinfo = String.format("Last Bus Stop\nName: %s\nCode: %s",prevStop.getName(),prevStop.getCode());
                    tv1.setText(stopinfo);

                    tv2.setText("You just started your journey");

                    ivStop.setVisibility(View.GONE);
                }

                else if (status == -1)
                {
                    String stopinfo = String.format("Searching for bus stop...");
                    tv1.setText(stopinfo);

                    tv2.setText("Your journey will start when you are near a bus stop within your chosen route.");

                    ivStop.setVisibility(View.GONE);
                }

            }
        }
    }

    private void showLocation(Location location) {
        if (location != null) {
            final String text = String.format("Latitude %.6f, Longitude %.6f",
                    location.getLatitude(),
                    location.getLongitude());
            debug1.setText(text);

        } else {
            debug1.setText("Null location");
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
        stopService(i);

        super.onDestroy();
    }
}
