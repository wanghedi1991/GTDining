package edu.gatech.cs.foodies;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;


public class RestaurantInfoFragment extends Fragment {
    String imgUrl;
    String name;
    String detailLocation;
    String monday;
    String tuesday;
    String wednesday;
    String thursday;
    String friday;
    String saturday;
    String sunday;

    public static RestaurantInfoFragment newInstance(String imgUrl, String name, String detailLocation, String monday, String tuesday, String wednesday, String thursday, String friday, String saturday, String sunday) {
        RestaurantInfoFragment fragment = new RestaurantInfoFragment();
        Bundle args = new Bundle();
        args.putString(Constants.RESTAURANT_ICON_URL, imgUrl);
        args.putString(Constants.RESTAUNRANT_LOCATION, imgUrl);
        args.putString(Constants.RESTAURANT_NAME, imgUrl);
        args.putString(Constants.MONDAY, imgUrl);
        args.putString(Constants.TUESDAY, imgUrl);
        args.putString(Constants.WEDNESDAY, imgUrl);
        args.putString(Constants.THURSDAY, imgUrl);
        args.putString(Constants.FRIDAY, imgUrl);
        args.putString(Constants.SATURDAY, imgUrl);
        args.putString(Constants.SUNDAY, imgUrl);
        fragment.setArguments(args);
        return fragment;
    }

    public RestaurantInfoFragment() {
        imgUrl = "";
        name = "";
        detailLocation = "";
        monday = "";
        tuesday = "";
        wednesday = "";
        thursday = "";
        friday = "";
        saturday = "";
        sunday = "";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle args = getArguments();
            imgUrl = args.getString(Constants.RESTAURANT_ICON_URL);
            name = args.getString(Constants.RESTAURANT_NAME);
            detailLocation = args.getString(Constants.RESTAUNRANT_LOCATION);
            monday = args.getString(Constants.MONDAY);
            tuesday = args.getString(Constants.TUESDAY);
            wednesday = args.getString(Constants.WEDNESDAY);
            thursday = args.getString(Constants.THURSDAY);
            friday = args.getString(Constants.FRIDAY);
            saturday = args.getString(Constants.SATURDAY);
            sunday = args.getString(Constants.SUNDAY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant_info, container, false);
        if (!(imgUrl.isEmpty() || name.isEmpty() || detailLocation.isEmpty() || monday.isEmpty() || tuesday.isEmpty() || wednesday.isEmpty() || thursday.isEmpty() || friday.isEmpty() || saturday.isEmpty() || sunday.isEmpty())) {
            new GettingIconTask((ImageView) view.findViewById(R.id.restaurant_icon)).execute(imgUrl);
            ((TextView) view.findViewById(R.id.restaurant_name)).setText(name);
            ((TextView) view.findViewById(R.id.detail_location)).setText(detailLocation);
            ((TextView) view.findViewById(R.id.monday)).setText(monday);
            ((TextView) view.findViewById(R.id.tuesday)).setText(tuesday);
            ((TextView) view.findViewById(R.id.wednesday)).setText(wednesday);
            ((TextView) view.findViewById(R.id.thursday)).setText(thursday);
            ((TextView) view.findViewById(R.id.friday)).setText(friday);
            ((TextView) view.findViewById(R.id.saturday)).setText(saturday);
            ((TextView) view.findViewById(R.id.sunday)).setText(sunday);
        }

        return inflater.inflate(R.layout.fragment_restaurant_info, container, false);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private class GettingIconTask extends AsyncTask<String, Void, Drawable> {

        private ImageView imageView;

        public GettingIconTask() {

        }

        public GettingIconTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Drawable doInBackground(String... params) {
            try {
                InputStream is = (InputStream) new URL(params[0]).getContent();
                Drawable d = Drawable.createFromStream(is, "icon");
                return d;
            } catch (Exception e) {
                e.printStackTrace();
                Drawable d = getActivity().getResources().getDrawable(R.drawable.cross_out);
                return d;
            }
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            imageView.setImageDrawable(drawable);
        }


    }

    public void updateInfo(String imgUrl, String name, String detailLocation, String monday, String tuesday, String wednesday, String thursday, String friday, String saturday, String sunday) {
        View view = getView();
        this.imgUrl = imgUrl;
        this.name = name;
        this.detailLocation = detailLocation;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
        new GettingIconTask((ImageView) view.findViewById(R.id.restaurant_icon)).execute(imgUrl);
        ((TextView) view.findViewById(R.id.restaurant_name)).setText(name);
        ((TextView) view.findViewById(R.id.detail_location)).setText(detailLocation);
        ((TextView) view.findViewById(R.id.monday)).setText(monday);
        ((TextView) view.findViewById(R.id.tuesday)).setText(tuesday);
        ((TextView) view.findViewById(R.id.wednesday)).setText(wednesday);
        ((TextView) view.findViewById(R.id.thursday)).setText(thursday);
        ((TextView) view.findViewById(R.id.friday)).setText(friday);
        ((TextView) view.findViewById(R.id.saturday)).setText(saturday);
        ((TextView) view.findViewById(R.id.sunday)).setText(sunday);
    }

}
