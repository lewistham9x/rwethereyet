package s10171744d.rwethereyet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import s10171744d.rwethereyet.model.BusStop;
import s10171744d.rwethereyet.util.DataHolder;

public class BusJourney extends AppCompatActivity {

    List<BusStop> busRoute;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_journey);
        //busRoute = DataHolder.getInstance().getData();

        Bundle bundle = getIntent().getExtras();
        int selectedBusStopIndex = bundle.getInt("busStopIndex");

        //Log.d("asf",busRoute.get(0).getName()); //test if the busroute was succesfuly transferred
        Log.d("asf",selectedBusStopIndex+"asdas"); //test if the busroute index was succesfuly transferred

    }
}
