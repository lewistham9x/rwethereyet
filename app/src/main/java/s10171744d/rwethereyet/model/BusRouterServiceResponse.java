package s10171744d.rwethereyet.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Lewis on 11/7/2017.
 */

public class BusRouterServiceResponse {
    @SerializedName("1")
    private BusRouterRoute routeOne; //the

    @SerializedName("2")
    private BusRouterRoute routeTwo;

    public BusRouterRoute getRouteOne() {
        return routeOne;
    }

    public BusRouterRoute getRouteTwo() {
        return routeTwo;
    }
}
