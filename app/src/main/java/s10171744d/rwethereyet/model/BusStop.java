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
}
