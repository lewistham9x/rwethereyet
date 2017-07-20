package s10171744d.rwethereyet;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import s10171744d.rwethereyet.model.BusStop;
import s10171744d.rwethereyet.model.UpdateData;
import s10171744d.rwethereyet.model.UpdateStop;

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

                Integer status = UpdateData.stopStatus;

                //check the status of bus stop
                if (status == 1)
                {
                    BusStop prevStop = UpdateData.prevStop;

                    String stopinfo = String.format("%s (%s)",prevStop.getName(),prevStop.getCode());

                    tvReachYet.setText("no");
                    tvStopsLeft.setText(UpdateData.stopsLeft +" more stops");
                    tvPrevStop.setText(stopinfo);

                    ivStop.setVisibility(View.GONE);
                }
                else if (status == 2)
                {
                    BusStop prevStop = UpdateData.prevStop;

                    String stopinfo = String.format("%s (%s)",prevStop.getName(),prevStop.getCode());

                    tvReachYet.setText("soon");
                    tvStopsLeft.setText(UpdateData.stopsLeft +" more stop");
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
                    tvStopsLeft.setText(UpdateData.stopsLeft +" more stops");
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
        stopService(i);

        super.onDestroy();
    }
}
