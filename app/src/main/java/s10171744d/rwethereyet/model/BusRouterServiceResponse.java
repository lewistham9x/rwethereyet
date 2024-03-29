package s10171744d.rwethereyet.model;

import com.google.gson.annotations.SerializedName;

/**
 * Lewis Tham Jee Peng | Group 9 | S10171744D
 */

public class BusRouterServiceResponse {
    @SerializedName("1")
    private BusRouterRoute routeOne; //the

    @SerializedName("2")
    private BusRouterRoute routeTwo;

    private Integer routeCount;


    public BusRouterRoute getRouteOne() {
        return routeOne;
    }

    public BusRouterRoute getRouteTwo() {
        return routeTwo;
    }

    public Integer getRouteCount()
    {
        routeCount=0;
        if (routeOne.getStops().length>0) //if there are bus stops in array
        {
            routeCount++;
        }
        if (routeTwo.getStops().length>0) //if there are bus stops in array
        {
            routeCount++;
        }
        return routeCount;
    }
}
