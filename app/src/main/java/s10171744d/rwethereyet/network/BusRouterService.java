package s10171744d.rwethereyet.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import s10171744d.rwethereyet.model.BusRouterServiceResponse;
import s10171744d.rwethereyet.model.BusStop;

/**
 * Lewis Tham Jee Peng | Group 9 | S10171744D
 */

public interface BusRouterService {
    @GET("data/2/bus-services/{id}.json")
    Call<BusRouterServiceResponse> listRepos(@Path("id") String user);

    @GET("data/2/bus-stops.json")
    Call<List<BusStop>> listAllStops(); //no need for wrapping
}
