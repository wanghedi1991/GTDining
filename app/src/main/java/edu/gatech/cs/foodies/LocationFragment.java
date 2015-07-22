package edu.gatech.cs.foodies;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationFragment extends Fragment {

    private Context context;
    private String address;
    private LatLng latLng;
    private String imageUrl;
    private static View view;

    public static LocationFragment newInstance(Context context, String address, double latitude, double longitude, String imageUrl) {
        LocationFragment fragment = new LocationFragment();
        Bundle args = new Bundle();
        fragment.setContext(context);
        args.putString(Constants.RESTAURANT_ADDRESS, address);
        args.putDouble(Constants.RESTAURANT_LATITUDE, latitude);
        args.putDouble(Constants.RESTAURANT_LONGITUDE, longitude);
        args.putString(Constants.RESTAURANT_ICON_URL, imageUrl);
        return fragment;
    }

    public LocationFragment() {
        // Required empty public constructor
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle args = getArguments();
            address = args.getString(Constants.RESTAURANT_ADDRESS);
            latLng = new LatLng(args.getDouble(Constants.RESTAURANT_LATITUDE), args.getDouble(Constants.RESTAURANT_LONGITUDE));
            imageUrl = args.getString(Constants.RESTAURANT_ICON_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
        }
        try {
            view = inflater.inflate(R.layout.fragment_location, container, false);
            SupportMapFragment fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            fragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    googleMap.setMyLocationEnabled(true);
                    if (latLng != null) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
                        googleMap.addMarker(markerOptions);
                    }
                    googleMap.setInfoWindowAdapter(new LocationInfoAdapter(context, address, imageUrl));
                    googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {
                            if (marker.isInfoWindowShown()) {
                                marker.hideInfoWindow();
                            } else {
                                marker.showInfoWindow();
                            }
                        }
                    });
                }
            });
        } catch (Exception e) {

        }

        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
