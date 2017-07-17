package s10171744d.rwethereyet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


import java.util.List;

import s10171744d.rwethereyet.model.BusStop;
import s10171744d.rwethereyet.model.Control;

public class BusJourney extends AppCompatActivity {

    TextView tv1;
    TextView tv2;

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


        tv1.setText(busRoute.get(0).getLat()+"");
        tv2.setText(selectedBusStopIndex+"asdas");


    }
}
