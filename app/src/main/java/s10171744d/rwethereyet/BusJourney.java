package s10171744d.rwethereyet;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;


import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.Geofence;

import java.util.List;

import io.nlopez.smartlocation.OnActivityUpdatedListener;
import io.nlopez.smartlocation.OnGeofencingTransitionListener;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.geofencing.model.GeofenceModel;
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

    List<BusStop> busRoute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_journey);


        tv1 = (TextView)findViewById(R.id.textView);
        tv2 = (TextView)findViewById(R.id.textView2);


        List<BusStop> busRoute = Control.busRoute; //grab the bus stop route from the mainactivity

        Integer selectedBusStopIndex = Control.selectedBusIndex; //grab the selected bus index from mainactivity

        //tv1.setText(busRoute.get(0).getLat()+"");
        //tv2.setText(selectedBusStopIndex+"asdas");


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
        if (withinRadius(1.37060695394614,103.89266808874676,1.37016500002901,103.8953599999,25))
        {
            tv2.setText("25m diff");
        }
        else if (withinRadius(1.37060695394614,103.89266808874676,1.37016500002901,103.8953599999,50))
        {
            tv2.setText("50m diff");
        }
        else if (withinRadius(1.37060695394614,103.89266808874676,1.37016500002901,103.8953599999,100))
        {
            tv2 .setText("100m diff");
        }
        else if (withinRadius(1.37060695394614,103.89266808874676,1.37016500002901,103.8953599999,500))
        {
            tv2 .setText("500m diff");
        }

    }

    private boolean withinRadius(double startLatitude, double startLongitude, double endLatitude, double endLongitude, double radius) //check if 2 coords are within a x radius of each other
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
