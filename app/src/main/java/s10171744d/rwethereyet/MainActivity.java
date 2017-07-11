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

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import s10171744d.rwethereyet.model.BusRouterServiceResponse;
import s10171744d.rwethereyet.network.Network;

public class MainActivity extends AppCompatActivity {

    TextView responseView;
    static final String API_KEY = "qYVYhFVvQZuDj2KI3w8lBw==";
    static final String API_URL = "http://datamall2.mytransport.sg/ltaodataservice/LTABusService?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        responseView = (TextView) findViewById(R.id.responseView);

        Button queryButton = (Button) findViewById(R.id.queryButton);
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Response<BusRouterServiceResponse> response = Network.getBusRouterService().listRepos("166").execute();
                    BusRouterServiceResponse yay = response.body();
//                    yay.getRouteOne();
                    responseView.setText(yay.getRouteOne().getStops()[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
