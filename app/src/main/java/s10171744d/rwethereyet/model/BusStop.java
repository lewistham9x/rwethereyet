package s10171744d.rwethereyet.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Lewis on 11/7/2017.
 */

public class BusStop {

    @SerializedName("no") /** helps identify which value within the JSON file to set "code" to **/
    private String code;

    @SerializedName("name")
    private String name;

    @SerializedName("lat")
    private double lat;

    @SerializedName("lng")
    private double lon;

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    //method is needed to create special bus stops (like bus stop to indicate error)
    public void error(int error) //create error bus stop object for non existent bus stop
    {
        code = "#ERROR"+error;
        name = "";
        lat = 0;
        lon = 0;
    }
}
