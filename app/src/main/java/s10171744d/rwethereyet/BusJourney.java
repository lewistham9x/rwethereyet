package s10171744d.rwethereyet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;



import java.util.List;

import s10171744d.rwethereyet.model.BusStop;

public class BusJourney extends AppCompatActivity {

    List<BusStop> busRoute;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_journey);

        //SelectedBusJourney bj = Parcels.unwrap(getIntent().getParcelableExtra("busJourney"));

        //List<BusStop> busRoute = bj.getRoute(); //grab the bus stop route from the mainactivity

        //Integer selectedBusStopIndex = bj.getStopIndex(); //grab the selected bus index from mainactivity

        //Log.d("asf",busRoute.get(0).getName()); //test if the busroute was succesfuly transferred
        //Log.d("asf",selectedBusStopIndex+"asdas"); //test if the busroute index was succesfuly transferred

    }
}
