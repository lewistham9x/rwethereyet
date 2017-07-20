package s10171744d.rwethereyet.model;

import android.location.Location;

import java.util.List;

/**
 * Lewis Tham Jee Peng | Group 9 | S10171744D
 */

//class allows for all activities to view/modify the same variables - for transferring data from the service to the busjourney activity
//could not use sharedpref to store objects like busstop and location

public class UpdateData {
    public static int stopStatus;
    public static BusStop prevStop;
    public static BusStop destStop;
    public static int stopsLeft;
    public static Location curLoc;
}
