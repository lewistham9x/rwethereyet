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

    TextView debug1;

    private LocationGooglePlayServicesProvider provider;

    private static final int LOCATION_PERMISSION_ID = 1001;

    Integer LastStopIndex; //destination
    Integer FirstStopIndex;
    Integer PrevStopIndex;

    Integer StopsTilAlert;

    List<BusStop> busRoute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_journey);


        tv1 = (TextView)findViewById(R.id.textView);
        tv2 = (TextView)findViewById(R.id.textView2);
        debug1 = (TextView)findViewById(R.id.tvDebug);

        LastStopIndex = Control.selectedBusIndex;
        FirstStopIndex = null;

        StopsTilAlert = 1; // default value for number of stops before alerting user to get off

        busRoute = Control.busRoute; //grab the bus stop route from the mainactivity


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
    public void onLocationUpdated(Location location) { //whenever update location[

        //test set location to dover stn



        showLocation(location);

        if (FirstStopIndex == null)
        {
            if (findFirstStop(location,busRoute)) //if first stop is finally found, will trim
            {
                busRoute.subList(FirstStopIndex, busRoute.size());//trim the bus list to only include the first stop (destination stop alr trimmed)
                PrevStopIndex = 0;
                tv2.setText("Found starting bus stop: "+busRoute.get(0).getName());
            }
            else
            {
                tv2.setText("Searching for bus stop...");
            }

        }
        else//lol this is p unoptimised - will keep check+updating everytime location is updated -- is it???
        {
            int stopStatus = updatePreviousStop(busRoute,PrevStopIndex,location);

            if (stopStatus==1) //check if user has reached next stop
            {
                //display the last stop if the previous stop has been updated
                showPreviousStop(busRoute,PrevStopIndex);

                if (isReaching(busRoute,StopsTilAlert)) //if bus stops left are within set value
                {
                    Log.d("TESTING","You are reaching the destination in 1 stop");
                    tv1.setText("You are reaching the destination in " + StopsTilAlert +"stops");
                    //alert user that that they are within the alert distance from destination
                }
                else
                {
                    //display the number of stops left to destination
                    tv1.setText("Stops Left: " + countStopsAway(busRoute));
                }


            }
            else if (stopStatus==2)//check is user has reached destination
            {
                showPreviousStop(busRoute,PrevStopIndex);
                //send notification to alert user that they reached
                tv1.setText("You have reached your destination");

            }
        }
    }

    private boolean findFirstStop(Location currentlocation,List<BusStop> busRoute)//Boolean findFirstStop(Location currentlocation)
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
                succ = true;
                FirstStopIndex = stopIndex;
            }
            stopIndex++;
        }
        return succ; //if successful, will stop searching for first stop
    }

    private boolean isAtStop(List<BusStop> busRoute, Integer stop, Location currentlocation) //if user is at bus stop location
    {
        double stoplat = busRoute.get(stop).getLat();
        double stoplon = busRoute.get(stop).getLon();
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

        //Log.d("dist",dist[0]+"");
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
            debug1.setText(text);

        } else {
            debug1.setText("Null location");
        }
    }
    private int updatePreviousStop(List<BusStop> busRoute, Integer prevStopIndex, Location location) //returns a int value if previous stop is updated/reached destination
    //0 = no change in bus stop, 1 = change in bus stop, 2= destination has been reached
    {
        if (isAtStop(busRoute,prevStopIndex+1,location)) //if user gps is near the next bus stop
        {
            Integer stopsAway = countStopsAway(busRoute);
            if (stopsAway==0)//if the previous stop was destination stop
            {
                return 2;
            }
            else if (stopsAway>0) //if its not at destination, increase prevstopindex
            {
                PrevStopIndex++;
                return 1;
            }
            else //if theres error in which previous stop index is past the destination
            {
                Log.d("ERROR","Last stop index is past destination??");
                return -1;
            }
        }
        return 0;
    }

    private Integer countStopsAway(List<BusStop> busRoute)
    {
        int stopsleft;
        if (PrevStopIndex <= busRoute.size()) // if the previous stop index is less than the last stop index (handle null reference)
        {
            stopsleft = busRoute.size() - PrevStopIndex;
        }
        else //error cos the previous stop has past by the final stop
        {
            //handle errors?
            stopsleft = -1;
        }
        return stopsleft;
    }


    private void showPreviousStop(List<BusStop> busRoute, Integer prevStopIndex)
    {
        BusStop prevStop = busRoute.get(prevStopIndex);
        String code = prevStop.getCode();
        String name = prevStop.getName();
        //double lat = prevStop.getLat();
        //double lon = prevStop.getLon();

        String output = String.format("Last Bus Stop: Bus Stop %o\n\nCode: %s\nName: %s\n",prevStopIndex,code,name);

        tv2.setText(output);
    }


    private Boolean isReaching(List<BusStop> busRoute, Integer stopsAway) //check if the user is reaching the destination (based on no. of stops away)
    {
        //count how many stops to destination (to prevent null reference and for alerting when 1 stop away)
        int stopsleft = countStopsAway(busRoute);
        if (stopsleft != -1) // if the previous stop index is less than the last stop index (handle null reference)
        {
            if (stopsleft<=stopsAway)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else //error cos the previous stop has past by the final stop
        {
            //handle errors?
            return false;
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

    //@Override //commented out since onstop can be triggered when send to home
    //protected void onStop() {
    //    stopLocation();
    //    super.onStop();
    //    Log.d("detected", "onStopevent");
    //}

    @Override
    protected void onDestroy() {
        stopLocation();
        super.onDestroy();
        Log.d("detected", "onDestroyevent");
    }

}
