package edu.gatech.cs.foodies;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;


public class FoodLocMapFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    ArrayList<MarkerOptions> markers;

    private static View view;

    public static FoodLocMapFragment newInstance() {
        FoodLocMapFragment fragment = new FoodLocMapFragment();
        return fragment;
    }

    public FoodLocMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            view = inflater.inflate(R.layout.fragment_food_loc_map, container, false);
            SupportMapFragment fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.main_map);
            fragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    googleMap.setMyLocationEnabled(true);
                    LatLng latLng = new LatLng(33.773669, -84.397748);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                    if (markers != null) {
                        for (MarkerOptions markerOptions : markers) {
                            googleMap.addMarker(markerOptions);
                        }
                    }
//                    googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//                        @Override
//                        public void onInfoWindowClick(Marker marker) {
//                            if (marker.isInfoWindowShown()) {
//                                marker.hideInfoWindow();
//                            } else {
//                                marker.showInfoWindow();
//                            }
//                        }
//                    });
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
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public ArrayList<MarkerOptions> getMarkers() {
        return markers;
    }

    public void setMarkers(ArrayList<MarkerOptions> markers) {
        this.markers = markers;
    }
}
