package s10171744d.rwethereyet;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;


import java.util.ArrayList;
import java.util.List;

import br.com.goncalves.pugnotification.notification.PugNotification;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import s10171744d.rwethereyet.model.BusRouterServiceResponse;
import s10171744d.rwethereyet.model.BusStop;
import s10171744d.rwethereyet.model.Control;
import s10171744d.rwethereyet.network.Network;
import s10171744d.rwethereyet.util.SingleArgumentCallback;

public class MainActivity extends AppCompatActivity {
    List<BusStop> BusStopList;
    ListView busRouteListView1;
    ListView busRouteListView2;
    EditText busServiceNo;
    Button queryButton;

    //List<BusStop> selectedServiceBusStopList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queryButton = (Button) findViewById(R.id.queryButton);
        busServiceNo = (EditText) findViewById(R.id.txtServiceNo);
        busRouteListView1 = (ListView) findViewById(R.id.busRouteListView1);
        busRouteListView2 = (ListView) findViewById(R.id.busRouteListView2);

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
                Network.getBusRouterService().listRepos(busServiceNo.getText()+"").enqueue(new Callback<BusRouterServiceResponse>() {
                    @Override
                    public void onResponse(Call<BusRouterServiceResponse> call, Response<BusRouterServiceResponse> response) {
                        if (response.isSuccessful()) //if response is successful (such a bus stop exists)
                        {
                            BusRouterServiceResponse yay = response.body(); //need to cos privatised
                            Integer routecount = yay.getRouteCount();//get the route count
                            Log.d("routecount", routecount+"");//pass through the serviceBusStopList

                            if (routecount == 1) {
                                Log.d("detected", "there is 1 route");//pass through the serviceBusStopList


                                getBusStopList(busServiceNo.getText() + "", 1, new SingleArgumentCallback<List<BusStop>>() {//call the callback
                                    @Override
                                    public void onComplete(final List<BusStop> serviceBusStopList) //will execute after callback is complete with data etc
                                    {
                                        BusRouteListViewAdapter adapter = new BusRouteListViewAdapter(serviceBusStopList);
                                        busRouteListView1.setAdapter(adapter);
                                        busRouteListView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                                String busstopname = serviceBusStopList.get(position).getName();
                                                Log.d("asf", busstopname);//pass through the serviceBusStopList

                                                Control.busRoute = serviceBusStopList;
                                                Control.selectedBusIndex = position;

                                                Intent intent = new Intent(MainActivity.this, BusJourney.class);

                                                startActivity(intent);
                                            }
                                        });
                                    }
                                });
                                LinearLayout.LayoutParams param1 = (LinearLayout.LayoutParams)
                                        busRouteListView1.getLayoutParams();
                                param1.weight = 100;
                                busRouteListView1.setLayoutParams(param1);

                                LinearLayout.LayoutParams param2 = (LinearLayout.LayoutParams)
                                        busRouteListView2.getLayoutParams();
                                param2.weight = 0;
                                busRouteListView2.setLayoutParams(param2);


                                busRouteListView2.setVisibility(View.GONE);


                            }
                            else
                            {
                                getBusStopList(busServiceNo.getText() + "", 1, new SingleArgumentCallback<List<BusStop>>() {//call the callback
                                    @Override
                                    public void onComplete(final List<BusStop> serviceBusStopList) //will execute after callback is complete with data etc
                                    {
                                        Log.d("completed", "firstroute");//pass through the serviceBusStopList
                                        BusRouteListViewAdapter adapter1 = new BusRouteListViewAdapter(serviceBusStopList);
                                        busRouteListView1.setAdapter(adapter1);

                                        busRouteListView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                                String busstopname = serviceBusStopList.get(position).getName();
                                                Log.d("asf", busstopname);//pass through the serviceBusStopList

                                                Control.busRoute = serviceBusStopList;
                                                Control.selectedBusIndex = position;

                                                Intent intent = new Intent(MainActivity.this, BusJourney.class);

                                                startActivity(intent);
                                            }
                                        });
                                    }
                                });

                                getBusStopList(busServiceNo.getText() + "", 2, new SingleArgumentCallback<List<BusStop>>() {//call the callback
                                    @Override
                                    public void onComplete(final List<BusStop> serviceBusStopList) //will execute after callback is complete with data etc
                                    {
                                        Log.d("completed", "secondroute");//pass through the serviceBusStopList
                                        BusRouteListViewAdapter adapter2 = new BusRouteListViewAdapter(serviceBusStopList);
                                        busRouteListView2.setAdapter(adapter2);
                                        busRouteListView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                                String busstopname = serviceBusStopList.get(position).getName();
                                                Log.d("asf", busstopname);//pass through the serviceBusStopList

                                                Control.busRoute = serviceBusStopList;
                                                Control.selectedBusIndex = position;

                                                Intent intent = new Intent(MainActivity.this, BusJourney.class);

                                                startActivity(intent);
                                            }
                                        });

                                    }
                                });
                                Log.d("detected", "there are 2 routes");//pass through the serviceBusStopList

                                //set weight to 50 for both listviews
                                LinearLayout.LayoutParams param1 = (LinearLayout.LayoutParams)
                                        busRouteListView1.getLayoutParams();
                                param1.weight = 50;

                                busRouteListView1.setLayoutParams(param1);


                                LinearLayout.LayoutParams param2 = (LinearLayout.LayoutParams)
                                        busRouteListView2.getLayoutParams();
                                param2.weight = 50;
                                busRouteListView2.setLayoutParams(param2);

                                busRouteListView2.setVisibility(View.VISIBLE);


                            }
                        }
                        else
                        {
                            Log.d("detected", "there is no route");//pass through the serviceBusStopList


                            LinearLayout.LayoutParams param1 = (LinearLayout.LayoutParams)
                                    busRouteListView1.getLayoutParams();
                            param1.weight = 100;
                            busRouteListView1.setLayoutParams(param1);

                            LinearLayout.LayoutParams param2 = (LinearLayout.LayoutParams)
                                    busRouteListView2.getLayoutParams();
                            param2.weight = 0;
                            busRouteListView2.setLayoutParams(param2);
                            busRouteListView2.setVisibility(View.GONE);


                            //create error bus stop object for non existent bus service (for error handling)
                            BusStop error = new BusStop();
                            error.error(1);
                            List<BusStop> errorBusStopList = new ArrayList<BusStop>(){};
                            errorBusStopList.add(error);

                            BusRouteListViewAdapter erroradapter = new BusRouteListViewAdapter(errorBusStopList);
                            busRouteListView1.setAdapter(erroradapter);


                        }
                    }
                    @Override
                    public void onFailure(Call<BusRouterServiceResponse> call, Throwable t) {
                        //handle errors here instead?
                        //create error bus stop object for non existent bus service (for error handling)
                        BusStop error = new BusStop();
                        error.error(2);
                        List<BusStop> errorBusStopList = new ArrayList<BusStop>(){};
                        errorBusStopList.add(error);

                        BusRouteListViewAdapter erroradapter = new BusRouteListViewAdapter(errorBusStopList);
                        busRouteListView1.setAdapter(erroradapter);
                    }
                });
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
                callback.onComplete(busStopsForService); //when retrieved network info "return" the busStopsForService
            }

            @Override
            public void onFailure(Call<BusRouterServiceResponse> call, Throwable t) {
                //error handling moved to routecount
            }
        });
    }
}
