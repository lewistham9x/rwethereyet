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
import android.widget.LinearLayout;
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

    Integer routecount;

    //List<BusStop> selectedServiceBusStopList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button queryButton = (Button) findViewById(R.id.queryButton);
        final EditText busServiceNo = (EditText) findViewById(R.id.txtServiceNo);
        busRouteListView1 = (ListView)findViewById(R.id.busRouteListView1);
        busRouteListView2 = (ListView)findViewById(R.id.busRouteListView2);
        routecount = 0;

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

                    //set view hide and weight

                    getBusStopList(busServiceNo.getText()+"", 1, new SingleArgumentCallback<List<BusStop>>() {//call the callback
                        @Override
                        public void onComplete(final List<BusStop> serviceBusStopList) //will execute after callback is complete with data etc
                        {
                            BusRouteListViewAdapter adapter = new BusRouteListViewAdapter(serviceBusStopList);
                            busRouteListView1.setAdapter(adapter);
                            busRouteListView1.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
                                {
                                    String busstopname = serviceBusStopList.get(position).getName();
                                    Log.d("asf",busstopname);//need to pass through the route num too
                                }
                            });
                        }
                    });

                }
                else
                {

                    //set view hide and weight

                    getBusStopList(busServiceNo.getText()+"", 1, new SingleArgumentCallback<List<BusStop>>() {//call the callback
                        @Override
                        public void onComplete(final List<BusStop> serviceBusStopList) //will execute after callback is complete with data etc
                        {
                            BusRouteListViewAdapter adapter = new BusRouteListViewAdapter(serviceBusStopList);
                            busRouteListView1.setAdapter(adapter);
                            busRouteListView1.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
                                {
                                    String busstopname = serviceBusStopList.get(position).getName();
                                    Log.d("asf",busstopname);//need to pass through the route num too
                                }
                            });
                        }
                    });

                    getBusStopList(busServiceNo.getText()+"", 2, new SingleArgumentCallback<List<BusStop>>() {//call the callback
                        @Override
                        public void onComplete(final List<BusStop> serviceBusStopList) //will execute after callback is complete with data etc
                        {
                            BusRouteListViewAdapter adapter = new BusRouteListViewAdapter(serviceBusStopList);
                            busRouteListView2.setAdapter(adapter);
                            busRouteListView2.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
                                {
                                    String busstopname = serviceBusStopList.get(position).getName();
                                    Log.d("asf",busstopname);//need to pass through the route num too
                                }
                            });
                        }
                    });
                }

            }
        });
    }


    private void getBusStopList(final String busService, final int routeNo, final SingleArgumentCallback<List<BusStop>> callback) //returns list of bus stops for service
    {
        Network.getBusRouterService().listRepos(busService).enqueue(new Callback<BusRouterServiceResponse>() {//enqueue allows for usage of own callback
            List<BusStop> busStopsForService = new ArrayList<>();
            @Override
            public void onResponse(Call<BusRouterServiceResponse> call, Response<BusRouterServiceResponse> response) {
                if (response.isSuccessful()) //if response is successful (such a bus stop exists)
                {
                    BusRouterServiceResponse yay = response.body();
                    String[] result = new String[0];

                    //test count routes
                    routecount = 0;
                    if (yay.getRouteOne().getStops().length>0) //if there are bus stops in array
                    {
                        routecount++;
                    }
                    if (yay.getRouteTwo().getStops().length>0) //if there are bus stops in array
                    {
                        routecount++;
                    }
                    Log.d("routecount",routecount+"");



                    if (routeNo == 1)
                    {
                        result = yay.getRouteOne().getStops();
                    }
                    else
                    {
                        result = yay.getRouteTwo().getStops();
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
