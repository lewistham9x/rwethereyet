package s10171744d.rwethereyet.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import s10171744d.rwethereyet.BuildConfig;

/**
 * Lewis Tham Jee Peng | Group 9 | S10171744D
 */

public class Network {
    public static Retrofit.Builder getRetrofitBuilder()  {
        HttpLoggingInterceptor  interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BASIC : HttpLoggingInterceptor.Level.NONE);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        return new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).client(client);
    }

    public static BusRouterService getBusRouterService() {
        return getRetrofitBuilder().baseUrl("https://busrouter.sg/")
                .build()
                .create(BusRouterService.class);
    }
}
