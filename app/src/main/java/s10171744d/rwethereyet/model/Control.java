package s10171744d.rwethereyet.model;

import java.util.List;

/**
 * Created by Lewis on 18/7/2017.
 */

//class allows for all activities to view/modify the same variables - meant for transferring data from main to busjourney
//could not use sharedpref to store objects
public class Control {
    public static List<BusStop> busRoute;
    public static int selectedBusIndex;
}
