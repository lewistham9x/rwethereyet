package s10171744d.rwethereyet;

import android.Manifest;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.Image;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.gms.location.DetectedActivity;

import java.util.List;

import br.com.goncalves.pugnotification.notification.PugNotification;
import io.nlopez.smartlocation.OnActivityUpdatedListener;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationParams;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;
import s10171744d.rwethereyet.model.BusStop;
import s10171744d.rwethereyet.model.Control;

public class    BusJourney extends AppCompatActivity implements OnLocationUpdatedListener, OnActivityUpdatedListener {

    TextView tv1;
    TextView tv2;

    TextView debug1;
    TextView debug2;

    ImageView ivStop;

    private LocationGooglePlayServicesProvider provider;

    private static final int LOCATION_PERMISSION_ID = 1001;

    Integer LastStopIndex; //destination
    Integer FirstStopIndex;
    Integer PrevStopIndex;
    Integer StopsTilAlert;

    Integer updatecount; //for debugging

    String notifTit;
    String notifBTxt;

    List<BusStop> busRoute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_journey);


        tv1 = (TextView)findViewById(R.id.textView);
        tv2 = (TextView)findViewById(R.id.textView2);
        debug1 = (TextView)findViewById(R.id.tvDebug);
        debug2 = (TextView)findViewById(R.id.tvDebug2);
        ivStop = (ImageView) findViewById(R.id.ivStop);

        LastStopIndex = Control.selectedBusIndex;
        FirstStopIndex = null;

        StopsTilAlert = 1; // default value for number of stops before alerting user to get off

        busRoute = Control.busRoute; //grab the bus stop route from the mainactivity

        notifTit="Are we there yet?";
        notifBTxt="";




        updatecount=0;


        //trim the busroute to end with destination
        busRoute = busRoute.subList(0, LastStopIndex+1);



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

        /* DEBUGGING
        //test set location to dover stn - same coords as json
        location.setLatitude(1.31167951129602);
        location.setLongitude(103.77868390552867);

        //test set location to end of stn
        location.setLatitude(1.3142989);
        location.setLongitude(103.7784209);



        //set location to the first bus stop in list
        BusStop bs = busRoute.get(0);
        location.setLatitude(bs.getLat());
        location.setLongitude(bs.getLon());
        */

        showLocation(location);


        /*
        //show distance to next stop - most likely to be removed since this shows displacement, not distance
        //BusStop nextStop = busRoute.get(PrevStopIndex);
        float[] dist  = new float[1];

        Location.distanceBetween(1.33239231,103.77785882,1.33256841,103.77786468,dist);
        debug2.setText(dist[0]+"");
        */

        updatecount++;
        debug1.setText(updatecount+"");

        if (FirstStopIndex == null)
        {
            if (findFirstStop(location,busRoute)) //if first stop is finally found, will trim
            {
                //debug2.setText(FirstStopIndex.toString() );

                busRoute = busRoute.subList(FirstStopIndex, busRoute.size());//trim the bus list to only include the first stop (destination stop alr trimmed)
                PrevStopIndex = 0;
                tv2.setText("Found starting bus stop: "+busRoute.get(0).getName());

                //need to run stuff to be similar to update bus stop since this is also updating, but doesnt call the update method


                //display the last stop if the previous stop has been updated
                showPreviousStop(busRoute,PrevStopIndex);

                //display the number of stops left to destination
                tv1.setText("Stops Left: " + countStopsAway(busRoute,PrevStopIndex));

            }
            else
            {
                tv2.setText("Searching for bus stop...");
            }

        }
        else//lol this is p unoptimised - this is run after first stop has been found, and will keep check+updating everytime location is updated
        {
            int stopStatus = updatePreviousStop(busRoute,location);// update the previous bus stop

            if (stopStatus==1) //check if user has reached next stop
            {
                //display the last stop if the previous stop has been updated
                showPreviousStop(busRoute,PrevStopIndex);

                //display the number of stops left to destination
                tv1.setText("Stops Left: " + countStopsAway(busRoute,PrevStopIndex));

                /*
                //show distance to next stop - most likely to be removed since this shows displacement, not distance
                BusStop nextStop = busRoute.get(PrevStopIndex+1);
                float[] dist  = new float[1];

                Location.distanceBetween(nextStop.getLat(),nextStop.getLon(),location.getLatitude(),location.getLongitude(),dist);
                debug2.setText(dist[0]+"");
                */
                ivStop.setVisibility(View.GONE);

            }
            else if (stopStatus==2) //user is reaching destination
            {
                //display the last stop if the previous stop has been updated
                showPreviousStop(busRoute,PrevStopIndex);



                //alert user that that they are within the alert distance from destination
                String msg;
                if (StopsTilAlert>1)
                {
                    msg = "You are reaching the destination in " + StopsTilAlert + " stops";
                }
                else
                {
                    msg = "You are reaching the destination in " + StopsTilAlert + " stop";
                }
                tv1.setText(msg);

                ivStop.setVisibility(View.VISIBLE);

                buildNotification(msg);
            }
            else if (stopStatus==3)//check is user has reached destination
            {
                showPreviousStop(busRoute,PrevStopIndex);
                //send notification to alert user that they reached -maybe change activity to ad?
                String msg="You have reached your destination";
                tv1.setText(msg);

                ivStop.setVisibility(View.VISIBLE);

                buildNotification(msg);
                stopLocation(); //end the location updates
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

        if (withinRadius(stoplat,stoplon,curLat,curLon,50)) //10 is too small radius for checking nearby bus stop, 50 is perfect
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
    private int updatePreviousStop(List<BusStop> busRoute, Location location) //returns a int value if previous stop is updated/reached destination
    //0 = no change in bus stop, 1 = change in bus stop, 2= reaching destination, 3= destination has been reached
    {
        if (isAtStop(busRoute,PrevStopIndex,location)) //if user gps is near the next bus stop <---error, when reach the end of the list, cant ++ prevstop anymore
        {
            if (countStopsAway(busRoute,PrevStopIndex)==0) //if the stop is the destination
            {
                return 3;
            }
            else if (countStopsAway(busRoute,PrevStopIndex+1)<=StopsTilAlert) //if the stop is the within the range of alerting
            {
                PrevStopIndex++;
                return 2;
            }
            else//if next stop found
            {
                PrevStopIndex++;
                return 1;
            }
        }
        else
        {
            return 0; //do nothing if theres no change in previous bus stop
        }
    }

    private Integer countStopsAway(List<BusStop> busRoute, Integer stopIndex)
    {
        int stopsleft;
        if (stopIndex <= busRoute.size()-1) // if the previous stop index is less than the last stop index (handle null reference)
        {
            stopsleft = busRoute.size()-1 - stopIndex;
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

    private void buildNotification(String msg)
    {
        PugNotification.with(this)
                .load()
                .title(notifTit)
                .message(msg)
                //.bigTextStyle(notifBTxt)
                .smallIcon(R.mipmap.ic_launcher)
                .largeIcon(R.mipmap.ic_launcher)
                .flags(Notification.DEFAULT_ALL)
                .simple()
                .build();
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
