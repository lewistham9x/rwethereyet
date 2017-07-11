package s10171744d.rwethereyet.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Lewis on 11/7/2017.
 */

public class BusStop {

    @SerializedName("BusStopCode") /** helps identify which value within the JSON file to set "code" to **/
    private String code;

    @SerializedName("RoadName")
    private String roadName;

    @SerializedName("Description")
    private String description;

    @SerializedName("Latitude")
    private double lat;

    @SerializedName("Longitude")
    private double lon;
}
