package s10171744d.rwethereyet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import s10171744d.rwethereyet.model.BusStop;

/**
 * Lewis Tham Jee Peng | Group 9 | S10171744D
 */

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
        BusStop bs = busRoute.get(position);
        if (bs.getCode().equals("#ERROR1")) //error handling for non existent bus stop
        {
            code.setText("Bus stop does not exist");
            name.setVisibility(View.GONE);
        }
        else if (bs.getCode().equals("#ERROR2")) //error handling for non existent bus stop
        {
            code.setText("A connection error occured");
            name.setVisibility(View.GONE);
        }
        else
        {
            code.setText("Code: " + bs.getCode());
            name.setText("" + bs.getName());
        }
        return rowView;
    }
}
