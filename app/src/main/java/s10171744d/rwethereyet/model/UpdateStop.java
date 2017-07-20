package s10171744d.rwethereyet.model;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.view.View;

import com.google.android.gms.location.DetectedActivity;

import java.util.List;

import br.com.goncalves.pugnotification.notification.PugNotification;
import io.nlopez.smartlocation.OnActivityUpdatedListener;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationParams;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;
import s10171744d.rwethereyet.R;

/**
 * Created by Lewis on 20/7/2017.
 */

public class UpdateStop extends IntentService implements OnLocationUpdatedListener, OnActivityUpdatedListener {

    private LocationGooglePlayServicesProvider provider;

    Integer StopsTilAlert;

    Integer FirstStopIndex; //first stop found by updater to trim list
    Integer PrevStopIndex;

    Integer LastStopIndex; //destination index in main list
    List<BusStop> busRoute; //the list of bus stops provided by busjoruney\


    String notiftit = "Are we there yet?";
    String notifmsg = "No";

    Integer status;

    Intent intent; //for accessing intent from busjourney in other methods
    /*
        status returns an integer based on the status of the busstop update, used for returning to busjourney
        0 = no difference
        1 = a stop has been reached
        2 = user is reaching destination
        3 = user has reached destination
        4 = the first stop has been found
    */


    // Must create a default constructor
    public UpdateStop() {
        // Used to name the worker thread, important only for debugging.
        super("updatestop-service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // This describes what will happen when service is triggered
        this.intent = intent;
    }


    @Override
    public void onCreate() {
        super.onCreate(); // if you override onCreate(), make sure to call super().
        // If a Context object is needed, call getApplicationContext() here.
        FirstStopIndex = null;
        StopsTilAlert = 1; // default value for number of stops before alerting user to get off

        //busRoute = busRoute;//////!!!!<<<<<<<need to change to grab the busroute     from the activity
        //LastStopIndex = 69420; ///////!!!!<<<<need to change to grab the selected value from the activity

        LastStopIndex = Control.selectedBusIndex;
        busRoute = Control.busRoute; //grab the bus stop route from the mainactivity
        busRoute = busRoute.subList(0, LastStopIndex+1); //trim the busroute to end with destination


        startLocation();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopLocation();
    }

    @Override
    public void onLocationUpdated(Location location) {

        //will need to return a value back to the mainactivity based off here

        status = 0;

        if (FirstStopIndex == null)
        {
            if (findFirstStop(location,busRoute)) //if first stop is finally found, will trim
            {
                busRoute = busRoute.subList(FirstStopIndex, busRoute.size());//trim the bus list to only include the first stop (destination stop alr trimmed)
                PrevStopIndex = 0;
                status = 4;
            }
            /*
            else
            {
                //bus stop isnt found yet
                status = 0;
            }
            */

        }
        else//lol this is p unoptimised - this is run after first stop has been found, and will keep check+updating everytime location is updated
        {
            int stopStatus = updatePreviousStop(busRoute,location);// update the previous bus stop

            if (stopStatus==1) //check if user has reached next stop
            {

            }
            else if (stopStatus==2) //user is reaching destination
            {
                //build a notification to alert
                notifmsg="You're reaching in <" + StopsTilAlert +" stops";
                buildNotification(notifmsg);
            }
            else if (stopStatus==3)//check is user has reached destination
            {
                //build a notification to alert
                notifmsg="Yes";
                buildNotification(notifmsg);
                stopLocation(); //end the location updates
            }

            status = stopStatus;
        }

        //send call to busjourney
        ResultReceiver rec = intent.getParcelableExtra("receiver");
        // To send a message to the Activity, create a pass a Bundle
        Bundle bundle = new Bundle();
        bundle.putInt("UpdateStatus",status);
        // Here we call send passing a resultCode and the bundle of extras
        rec.send(Activity.RESULT_OK,bundle);

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

        if (dist[0]<=radius)
        {
            return true;
        }
        else
        {
            return false;
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

    }


    private void buildNotification(String msg)
    {
        PugNotification.with(this)
                .load()
                .title(notiftit)
                .message(notifmsg)
                //.bigTextStyle(notifBTxt)
                .smallIcon(R.mipmap.ic_launcher)
                .largeIcon(R.mipmap.ic_launcher)
                .flags(Notification.DEFAULT_ALL)
                .simple()
                .build();
    }


}