package edu.gatech.cs.foodies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Hedi Wang on 2015/7/8.
 */
public class LocationInfoAdapter implements GoogleMap.InfoWindowAdapter {
    Context context;
    String address;
    String phone;

    public LocationInfoAdapter() {
        context = null;
        address = "";
        phone = "";
    }

    public LocationInfoAdapter(Context context, String address, String phone) {
        this.context = context;
        this.address = address;
        this.phone = phone;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = LayoutInflater.from(context).inflate(R.layout.location_info, null, false);
        String line1, line2;
        ((ImageView) view.findViewById(R.id.loc_info_icon)).setImageResource(R.drawable.panda);
        if (address != null) {
            int commaIndex = address.indexOf(',');
            line1 = address.substring(0, commaIndex);
            line2 = address.substring(commaIndex + 1, address.length());
            ((TextView) view.findViewById(R.id.address_line1)).setText(line1);
            ((TextView) view.findViewById(R.id.address_line2)).setText(line2);
        }
        if (phone != null) {
            ((TextView) view.findViewById(R.id.phone_number)).setText(phone);
        }
        return view;
    }
}
