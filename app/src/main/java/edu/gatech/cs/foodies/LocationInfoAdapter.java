package edu.gatech.cs.foodies;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by Hedi Wang on 2015/7/8.
 */
public class LocationInfoAdapter implements GoogleMap.InfoWindowAdapter {
    Context context;
    String address;
    String imageUrl;
    Drawable drawable;

    public LocationInfoAdapter() {
        context = null;
        address = "";
        drawable = null;
    }

    public LocationInfoAdapter(Context context, String address, String imageUrl) {
        this.context = context;
        this.address = address;
        this.imageUrl = imageUrl;
        drawable = null;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = LayoutInflater.from(context).inflate(R.layout.location_info, null, false);
        String line1, line2;
        final ImageView icon = (ImageView) view.findViewById(R.id.loc_info_icon);
        icon.setImageDrawable(drawable);
//        final Drawable iconDrawable;
        new GetLocationIcon(marker).execute(imageUrl);
//        icon.setImageResource(R.drawable.cross_out);
        if (address != null) {
            int commaIndex = address.indexOf(',');
            if (commaIndex == -1) {
                ((TextView) view.findViewById(R.id.address_line1)).setText(address);
            } else {
                line1 = address.substring(0, commaIndex);
                line2 = address.substring(commaIndex + 1, address.length());
                ((TextView) view.findViewById(R.id.address_line1)).setText(line1);
                ((TextView) view.findViewById(R.id.address_line2)).setText(line2);
            }
        }

        return view;
    }

    private class GetLocationIcon extends AsyncTask<String, Void, Drawable> {

        private Marker marker;

        public GetLocationIcon() {

        }

        public GetLocationIcon(Marker marker) {
            this.marker = marker;
        }

        @Override
        protected Drawable doInBackground(String... params) {
            try {
                InputStream is = (InputStream) new URL(params[0]).getContent();
                Drawable d = Drawable.createFromStream(is, "icon");
                return d;
            } catch (Exception e) {
                e.printStackTrace();
                Drawable d = context.getResources().getDrawable(R.drawable.cross_out);
                return d;
            }
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            LocationInfoAdapter.this.drawable = drawable;
            if (marker.isInfoWindowShown()) {
                marker.showInfoWindow();
            }
        }
    }


}
