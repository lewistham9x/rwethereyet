package s10171744d.rwethereyet;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import s10171744d.rwethereyet.model.BusRouterServiceResponse;
import s10171744d.rwethereyet.model.BusStop;
import s10171744d.rwethereyet.network.Network;

public class MainActivity extends AppCompatActivity {

    TextView responseView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        responseView = (TextView) findViewById(R.id.responseView);

        Button queryButton = (Button) findViewById(R.id.queryButton);
        new initBusStopList().execute(); //convert all bus stops from json into bus stop objects
        //Network.getBusRouterService().listAllStops().enqueue(new Callback<List<BusStop>>() {
        //    @Override
        //    public void onResponse(Call<List<BusStop>> call, Response<List<BusStop>> response) {
        //        response.body();
        //    }
//
        //    @Override
        //    public void onFailure(Call<List<BusStop>> call, Throwable t) {
//
        //    }
        //});

        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RetrieveBusStopIDs().execute("166","1");
            }
        });
    }

    private List<BusStop> getBusStopList(String BusService) //returns list of bus stops for service
    {
        List<BusStop> busStopsForsService = new ArrayList<>();
        new RetrieveBusStopIDs() {
            @Override
            protected void onPostExecute(String[] result) { //override so can scan the whole list and add the bus stop objects to the list based on the stringarray
                for (String s : result)
                {

                }
            }
        }.execute(BusService,"1");

    }

    class RetrieveBusStopIDs extends AsyncTask<String, Void, String[]> {//params, progress,result

        private Exception exception;

        protected void onPreExecute() {
        }

        protected String[] doInBackground(String... args) { //returns array of strings of bus stop ids
            try {
                Response<BusRouterServiceResponse> response =
                        Network.getBusRouterService().listRepos(args[0]).execute();
                BusRouterServiceResponse yay = response.body();

                if (args[1].equals("1"))
                {
                    return yay.getRouteOne().getStops();
                }
                else if (args[1]=="2")
                {
                    return yay.getRouteTwo().getStops();
                }
                else
                {
                    return yay.getRouteThree().getStops();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
        }
    }


    class initBusStopList extends AsyncTask<Void, Void, List<BusStop>> {

        private Exception exception;

        protected void onPreExecute() {
        }

        protected List<BusStop> doInBackground(Void... urls) {
            try {
                List<BusStop> BusStopList = Network.getBusRouterService().listAllStops().execute().body();
                return BusStopList;
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(List<BusStop> response) {
            List<BusStop> = response;
        }
    }
}
