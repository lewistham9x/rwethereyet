package s10171744d.rwethereyet.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import s10171744d.rwethereyet.model.BusRouterServiceResponse;

/**
 * Created by Lewis on 11/7/2017.
 */

public interface BusRouterService {
    @GET("data/2/bus-services/{id}.json")
    Call<BusRouterServiceResponse> listRepos(@Path("id") String user);
}
