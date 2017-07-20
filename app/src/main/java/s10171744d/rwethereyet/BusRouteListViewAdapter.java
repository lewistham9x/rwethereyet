package s10171744d.rwethereyet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import s10171744d.rwethereyet.model.BusStop;


public class BusRouteListViewAdapter extends BaseAdapter{


    List<BusStop> busRoute;

    BusRouteListViewAdapter(List<BusStop> busRoute){
        this.busRoute = busRoute;
    }

    @Override
    public int getCount() {
        return busRoute.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.busroute,parent,false);
        TextView code = (TextView)rowView.findViewById(R.id.code);
        TextView name = (TextView)rowView.findViewById(R.id.name);
        TextView lat = (TextView)rowView.findViewById(R.id.lat);
        TextView lon = (TextView)rowView.findViewById(R.id.lon);
        BusStop bs = busRoute.get(position);
        if (bs.getCode().equals("#ERROR1")) //error handling for non existent bus stop
        {
            code.setText("Bus stop does not exist");
            name.setVisibility(View.GONE);
            lat.setVisibility(View.GONE);
            lon.setVisibility(View.GONE);
        }
        else if (bs.getCode().equals("#ERROR2")) //error handling for non existent bus stop
        {
            code.setText("A connection error occured");
            name.setVisibility(View.GONE);
            lat.setVisibility(View.GONE);
            lon.setVisibility(View.GONE);
        }
        else
        {
            code.setText("Stop Code: " + bs.getCode());
            name.setText("" + bs.getName());
            lat.setText("Latitude: " + bs.getLat());
            lon.setText("Longitude: " + bs.getLon());
        }
        return rowView;
    }
}
