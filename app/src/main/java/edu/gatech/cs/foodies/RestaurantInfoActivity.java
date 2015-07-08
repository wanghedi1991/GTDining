package edu.gatech.cs.foodies;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class RestaurantInfoActivity extends ActionBarActivity implements MenuFragment.OnFragmentInteractionListener, LocationFragment.OnFragmentInteractionListener {

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;


    double latitude;
    double longtitude;
    String address;
    String phone;
    FoodLocationEntry entry;
    MenuFragment menuFragment;
    LocationFragment locationFragment;
    Context context;
    ProgressDialog progressDialog;
    boolean goToLocation;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_info);
        entry = new FoodLocationEntry();
        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        context = this;

        Intent intent = getIntent();
        entry.setId(intent.getIntExtra(FoodLocAdapter.RESTAURANT_ID, -1));
        entry.setName(intent.getStringExtra(FoodLocAdapter.RESTAURANT_NAME));
        entry.setImageUrl(intent.getIntExtra(FoodLocAdapter.RESTAURANT_URI, -1));
        goToLocation = intent.getBooleanExtra(FoodLocAdapter.RESTAUNRANT_LOCATION, false);


        actionBar.setTitle(entry.getName());

        mViewPager = (ViewPager) findViewById(R.id.pager);


        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });


        progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        new GetRestaurantInfo().execute(entry.getId() + "");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_restaurant_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(String id) {

    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                if (menuFragment == null) {
                    menuFragment = MenuFragment.newInstance(entry.getName(), entry.getImageUrl());
                }
                return menuFragment;
            } else {
                if (locationFragment == null) {
                    locationFragment = LocationFragment.newInstance(context, address, phone, latitude, longtitude);
                }
                return locationFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_info_menu).toUpperCase(l);
                case 1:
                    return getString(R.string.title_info_location).toUpperCase(l);
            }
            return null;
        }
    }


    private class GetRestaurantInfo extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Getting Location Information...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... ids) {
            try {
                return getRestaurantInfo(ids[0]);
            } catch (Exception e) {
                return "Unable to get restaurants information";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                address = jsonObject.getString("address");
                phone = jsonObject.getString("phone");
                String[] loc = jsonObject.getString("location").split(",");
                latitude = Double.parseDouble(loc[0]);
                longtitude = Double.parseDouble(loc[1]);

                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
                mViewPager.setAdapter(mSectionsPagerAdapter);
                if (goToLocation) {
                    mViewPager.setCurrentItem(1);
                }
                for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
                    actionBar.addTab(
                            actionBar.newTab()
                                    .setText(mSectionsPagerAdapter.getPageTitle(i))
                                    .setTabListener(new ActionBar.TabListener() {
                                        @Override
                                        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                                            mViewPager.setCurrentItem(tab.getPosition());
                                        }

                                        @Override
                                        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

                                        }

                                        @Override
                                        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

                                        }
                                    }));
                }
                locationFragment.setAddress(address);
                locationFragment.setPhone(phone);
                locationFragment.setLatLng(new LatLng(latitude, longtitude));
                progressDialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private String getRestaurantInfo(String id) throws IOException {
            InputStream is = null;
            try {
                URL url = new URL("https://boiling-mesa-3124.herokuapp.com/info/" + id + "/?format=json");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                is = conn.getInputStream();
                StringBuilder builder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                return builder.toString();
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }
    }
}
