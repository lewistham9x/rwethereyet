package s10171744d.rwethereyet.model;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

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
 * Lewis Tham Jee Peng | Group 9 | S10171744D
 */

public class UpdateStop extends Service implements OnLocationUpdatedListener, OnActivityUpdatedListener {

    private LocationGooglePlayServicesProvider provider;

    Integer FirstStopIndex; //first stop found by updater to trim list
    Integer PrevStopIndex;

    Integer LastStopIndex; //destination index in main list
    List<BusStop> busRoute; //the list of bus stops provided by busjoruney\


    String notiftit = "Are we there yet?";
    String notifmsg = "No";

    Integer radius = 50;//radius (in m) for detecting bus stop (change for different sensitivity)



    // Must create a default constructor
    public UpdateStop() {
        // Used to name the worker thread, important only for debugging.
        //super("updatestop-service");
    }


    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("Event","OnCreate");
        FirstStopIndex = null;

        LastStopIndex = Control.selectedBusIndex;
        busRoute = Control.busRoute; //grab the bus stop route from the mainactivity
        UpdateData.destStop=busRoute.get(LastStopIndex); //get the data of the destination stop
        busRoute = busRoute.subList(0, LastStopIndex+1); //trim the busroute to end with destination

        startLocation();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocation(); //for stopping the location check once service ends
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationUpdated(Location location) {
        Log.d("tit","Location service is running");

        //for testing purposes (set the user location to be the first stop of the list)
        /*
            location.setLatitude(busRoute.get(0).getLat());
            location.setLongitude(busRoute.get(0).getLon());
        */

        Integer stopStatus = 0;
        /*
            returns an integer based on the status of the busstop update, used for returning to busjourney
            0 = no updates;
            1 = a stop has been reached
            -1 = first stop hasnt been found
        */

        if (FirstStopIndex == null)
        {
            if (findFirstStop(location,busRoute)) //if first stop is finally found, will trim
            {
                busRoute = busRoute.subList(FirstStopIndex, busRoute.size());//trim the bus list to only include the first stop (destination stop alr trimmed)
                PrevStopIndex = 0;
                UpdateData.prevStop=busRoute.get(PrevStopIndex);
                UpdateData.stopsLeft=countStopsAway(busRoute,PrevStopIndex);
                stopStatus = 1;

                UpdateData.prevStop=busRoute.get(PrevStopIndex);
                UpdateData.stopsLeft=countStopsAway(busRoute,PrevStopIndex);

            }
            else
            {
                //journey hasnt started
                stopStatus = -1;
            }
        }
        else//this is run after first stop has been found, and will keep check+updating everytime location is updated
        {
            if (UpdateData.stopsLeft>0)
            {
                if (isAtStop(busRoute,PrevStopIndex+1,location))
                {
                    stopStatus = 1;
                    PrevStopIndex++;
                }
            }
            UpdateData.prevStop=busRoute.get(PrevStopIndex);
            UpdateData.stopsLeft=countStopsAway(busRoute,PrevStopIndex);
        }
        UpdateData.stopStatus = stopStatus;

        if (stopStatus>0) //if there has been a change in bus stop/first stop alr found
        {
            if (UpdateData.stopsLeft==0)
            {
                //build a notification to alert
                notifmsg="Yes";
                buildNotification(notifmsg);
            }
            else if (UpdateData.stopsLeft<= Settings.getStopsToAlert())
            {
                //build a notification to alert
                if (Settings.getStopsToAlert()==1)
                {
                    notifmsg="You are reaching in <" + Settings.getStopsToAlert() +" stop";
                }
                else
                {
                    notifmsg="You are reaching in <" + Settings.getStopsToAlert() +" stops";
                }
                buildNotification(notifmsg);
                stopSelf();// end the service once destination reached
            }
        }

        UpdateData.curLoc=location;

        sendBroadcast(new Intent("LocationUpdated"));
    }


    private boolean findFirstStop(Location currentlocation,List<BusStop> busRoute)
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
            if (withinRadius(stoplat,stoplon,curLat,curLon,radius))//50 is minimum for proper tracking
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

        if (withinRadius(stoplat,stoplon,curLat,curLon, Settings.getSensitivity())) //last param is the radius sensitivity of bus stop locating
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
        else //error cos the previous stop has past by the final stop??
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

        smartLocation.with(getApplicationContext())
                .location(provider)
                .config(LocationParams.NAVIGATION)
                .start(this);
    }

    private void stopLocation() {
        SmartLocation.with(getApplicationContext()).location().stop();
    }

    @Override
    public void onActivityUpdated(DetectedActivity detectedActivity) {

    }


    private void buildNotification(String msg)
    {
        PugNotification.with(this)
                .load()
                .title(notiftit)
                .message(msg)
                //.bigTextStyle(notifBTxt)
                .smallIcon(R.drawable.ic_notif)
                .largeIcon(R.mipmap.ic_launcher)
                .priority(NotificationCompat.PRIORITY_HIGH) //set peeking??
                .flags(Notification.DEFAULT_ALL)
                .simple()
                .build();
    }


}