package s10171744d.rwethereyet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import s10171744d.rwethereyet.model.BusRouterServiceResponse;
import s10171744d.rwethereyet.model.BusStop;
import s10171744d.rwethereyet.network.Network;
import s10171744d.rwethereyet.util.SingleArgumentCallback;

public class MainActivity extends AppCompatActivity {
    List<BusStop> BusStopList;
    TextView responseView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        responseView = (TextView) findViewById(R.id.responseView);

        final Button queryButton = (Button) findViewById(R.id.queryButton);

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
                getBusStopList("166", 1, new SingleArgumentCallback<List<BusStop>>() {//call the callback
                    @Override
                    public void onComplete(List<BusStop> serviceBusStopList) //will execute after callback is complete with data etc
                    {
                        BusStop selected = serviceBusStopList.get(10); //temp test like if bus stop alr selected
                        responseView.setText("Code: "+selected.getCode()+"\nName: "+selected.getName()+"\nLat: "+selected.getLat()+"\nLong: "+selected.getLon());//lists in java uses .get(index) to get index instead of []
                    }
                });
            }
        });
    }
    private void getBusStopList(String busService, final int routeNo, final SingleArgumentCallback<List<BusStop>> callback) //returns list of bus stops for service
    {
       Network.getBusRouterService().listRepos(busService).enqueue(new Callback<BusRouterServiceResponse>() {//enqueue allows for usage of own callback
           @Override
           public void onResponse(Call<BusRouterServiceResponse> call, Response<BusRouterServiceResponse> response) {
               BusRouterServiceResponse yay = response.body();
               String[] result = new String[0];

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

               List<BusStop> busStopsForService = new ArrayList<>();
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
               callback.onComplete(busStopsForService); //when retrieved network info "return" the busStopsForServicec
           }

           @Override
           public void onFailure(Call<BusRouterServiceResponse> call, Throwable t) {

           }
       });
    }
}
