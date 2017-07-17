package s10171744d.rwethereyet.util;

import java.util.List;

import s10171744d.rwethereyet.model.BusStop;

/**
 * Created by Lewis on 18/7/2017.
 */

public class DataHolder {
    private List<BusStop> busRoute;
    public List<BusStop> getData()
    {
        return busRoute;
    }
    public void setData(List<BusStop> busRoute)
    {
        this.busRoute = busRoute;
    }

    private static final DataHolder holder = new DataHolder();
    public static DataHolder getInstance() {return holder;}
}
