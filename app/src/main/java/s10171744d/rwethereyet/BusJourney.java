package s10171744d.rwethereyet;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


import com.google.android.gms.location.DetectedActivity;

import java.util.List;

import io.nlopez.smartlocation.OnActivityUpdatedListener;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationParams;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;
import s10171744d.rwethereyet.model.BusStop;
import s10171744d.rwethereyet.model.Control;

public class BusJourney extends AppCompatActivity implements OnLocationUpdatedListener, OnActivityUpdatedListener {

    TextView tv1;
    TextView tv2;

    private LocationGooglePlayServicesProvider provider;

    private static final int LOCATION_PERMISSION_ID = 1001;

    Integer LastStopIndex;
    Integer PrevStopIndex;

    List<BusStop> busRoute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_journey);


        tv1 = (TextView)findViewById(R.id.textView);
        tv2 = (TextView)findViewById(R.id.textView2);

        LastStopIndex = Control.selectedBusIndex;
        PrevStopIndex = null;

        List<BusStop> busRoute = Control.busRoute; //grab the bus stop route from the mainactivity


        //trim the busroute to end with destination
        busRoute.subList(0, LastStopIndex);



        // check location permission
        if (ContextCompat.checkSelfPermission(BusJourney.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(BusJourney.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);
            return;}
        {
            startLocation();
        }
    }


    private void startLocation() {

        provider = new LocationGooglePlayServicesProvider();
        provider.setCheckLocationSettings(true);

        SmartLocation smartLocation = new SmartLocation.Builder(this).logging(true).build();


        smartLocation.with(this).location(provider).start(this);
        smartLocation.location(provider).config(LocationParams.NAVIGATION).start(this);
        smartLocation.activity().start(this);


    }

    private void stopLocation() {
        SmartLocation.with(this).location().stop();
        SmartLocation.with(this).activity().stop();

    }

    @Override
    public void onActivityUpdated(DetectedActivity detectedActivity) {
        //showActivity(detectedActivity)
    }

    @Override
    public void onLocationUpdated(Location location) { //whenever update location
        showLocation(location);
        //withinRadius(1.37060695394614,103.89266808874676,1.37016500002901,103.8953599999,25);
        if (PrevStopIndex == null)
        {
            findFirstStop(location);
        }
        else//lol this is p unoptimised
        {

        }
    }

    private void findFirstStop(Location currentlocation)//Boolean findFirstStop(Location currentlocation)
    {
        double stoplat;
        double stoplon;
        double curLat = currentlocation.getLatitude();
        double curLon = currentlocation.getLongitude();

        int stopIndex=0;
        boolean succ = false; //track whether bus stop found

        for (BusStop bs : busRoute)
        {
            stoplat = bs.getLat();
            stoplon = bs.getLon();
            if (withinRadius(stoplat,stoplon,curLat,curLon,10))
            {
                //succ = true;
                PrevStopIndex = stopIndex; //no need for succ, since if this is set to sth that means the value no longer null<<<
            }
            stopIndex++;
        }
        //return succ; //if successful, will stop searching for first stop
    }

    private boolean isAtStop(List<BusStop> busRoute, Integer nextStop, Location currentlocation) //if user is at bus stop location
    {
        double stoplat = busRoute.get(nextStop).getLat();
        double stoplon = busRoute.get(nextStop).getLon();
        double curLat = currentlocation.getLatitude();
        double curLon = currentlocation.getLongitude();

        if (withinRadius(stoplat,stoplon,curLat,curLon,10))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean withinRadius(double startLatitude, double startLongitude, double endLatitude, double endLongitude, double radius) //check if 2 coords are within a x of each other
    {
        float[] dist  = new float[1];
        Location.distanceBetween(startLatitude,startLongitude,endLatitude,endLongitude,dist);

        Log.d("dist",dist[0]+"");
        if (dist[0]<=radius)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private void showLocation(Location location) {
        if (location != null) {
            final String text = String.format("Latitude %.6f, Longitude %.6f",
                    location.getLatitude(),
                    location.getLongitude());
            tv1.setText(text);

        } else {
            tv1.setText("Null location");
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
                finish();
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
    protected void onStop() {
        stopLocation();
        super.onStop();
        Log.d("detected", "onStopevent");
    }

    @Override
    protected void onDestroy() {
        stopLocation();
        super.onDestroy();
        Log.d("detected", "onDestroyevent");
    }

}
