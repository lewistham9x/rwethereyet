package s10171744d.rwethereyet;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import s10171744d.rwethereyet.model.BusRouterServiceResponse;
import s10171744d.rwethereyet.model.BusStop;
import s10171744d.rwethereyet.network.Network;
import s10171744d.rwethereyet.util.SingleArgumentCallback;

public class MainActivity extends AppCompatActivity {
    List<BusStop> BusStopList;
    ListView busRouteListView1;
    ListView busRouteListView2;
    EditText busServiceNo;
    ToggleButton btnToggleRoute;

    Integer routecount;

    List<BusStop> selectedServiceBusStopList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button queryButton = (Button) findViewById(R.id.queryButton);
        final EditText busServiceNo = (EditText) findViewById(R.id.txtServiceNo);
        busRouteListView1 = (ListView)findViewById(R.id.busRouteListView1);
        busRouteListView2 = (ListView)findViewById(R.id.busRouteListView2);
        btnToggleRoute = (ToggleButton)findViewById(R.id.btnToggleRoute);

        selectedServiceBusStopList = new ArrayList<>();


        Network.getBusRouterService().listAllStops().enqueue(new Callback<List<BusStop>>() {
            @Override
            public void onResponse(Call<List<BusStop>> call, Response<List<BusStop>> response) {
                BusStopList = response.body();//add all the bus stops in singapore as objects to the list
            }
            @Override
            public void onFailure(Call<List<BusStop>> call, Throwable t) {

            }
        });


        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (routecount == 1)
                {

                }
                else if (routecount == 2)
                {

                }
                else if (routecount == 3)
                {

                }

                getBusStopList(busServiceNo.getText()+"", routeno, new SingleArgumentCallback<List<BusStop>>() {//call the callback
                    @Override
                    public void onComplete(final List<BusStop> serviceBusStopList) //will execute after callback is complete with data etc
                    {
                        selectedServiceBusStopList = serviceBusStopList;//add busroute 1 bus stops to list
                    }
                });
                BusRouteListViewAdapter adapter = new BusRouteListViewAdapter(selectedServiceBusStopList);
                busRouteListView1.setAdapter(adapter);
                busRouteListView1.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
                    {
                        String busstopname = selectedServiceBusStopList.get(position).getName();
                        Log.d("asf",busstopname);
                    }
                });

            }
        });
    }
    private void getBusStopList(String busService, final int routeNo, final SingleArgumentCallback<List<BusStop>> callback) //returns list of bus stops for service
    {
        Network.getBusRouterService().listRepos(busService).enqueue(new Callback<BusRouterServiceResponse>() {//enqueue allows for usage of own callback
            List<BusStop> busStopsForService = new ArrayList<>();
            @Override
            public void onResponse(Call<BusRouterServiceResponse> call, Response<BusRouterServiceResponse> response) {
                if (response.isSuccessful()) //if response is successful (such a bus stop exists)
                {
                    BusRouterServiceResponse yay = response.body();
                    String[] result = new String[0];

                    //counting routes - need to count number of routes in order to display accordingly
                    result = yay.getRouteOne().getStops();
                    routecount = 0;
                    if (result != null)
                    {
                        routecount++;
                    }
                    result = yay.getRouteTwo().getStops();
                    routecount = 0;
                    if (result != null)
                    {
                        routecount++;
                    }
                    result = yay.getRouteThree().getStops();
                    routecount = 0;
                    if (result != null)
                    {
                        routecount++;
                    }
                    //end of route counting

                    switch (routeNo) { //neater way for if else
                        case 1:
                            result = yay.getRouteOne().getStops();
                            break;
                        case 2:
                            result = yay.getRouteTwo().getStops();
                            break;
                        case 3:
                            result = yay.getRouteThree().getStops();
                            break;
                    }

                    for (String s : result)
                    {
                        for (BusStop bs : BusStopList)
                        {
                            if (bs.getCode().equals(s))
                            {
                                busStopsForService.add(bs);
                                break;
                            }
                        }
                    }
                }
                else
                {
                    //create error bus stop object for non existent bus service
                    BusStop error = new BusStop();
                    error.error(1);
                    busStopsForService.add(error);
                }
                callback.onComplete(busStopsForService); //when retrieved network info "return" the busStopsForService
            }

            @Override
            public void onFailure(Call<BusRouterServiceResponse> call, Throwable t) {
                //create error bus stop object for no connection
                BusStop error = new BusStop();
                error.error(2);
                busStopsForService.add(error);
                callback.onComplete(busStopsForService); //when retrieved network info "return" the busStopsForService
            }
        });
    }
}
