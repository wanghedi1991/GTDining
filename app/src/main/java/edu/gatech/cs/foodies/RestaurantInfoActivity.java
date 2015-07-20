package edu.gatech.cs.foodies;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;


public class RestaurantInfoActivity extends ActionBarActivity implements MenuFragment.OnFragmentInteractionListener, LocationFragment.OnFragmentInteractionListener {

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;


    double latitude;
    double longtitude;
    String address;
    String imageUrl;
    String menuUrl;
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
        actionBar.setDisplayHomeAsUpEnabled(true);
        context = this;

        Intent intent = getIntent();
        entry.setId(intent.getIntExtra(Constants.RESTAURANT_ID, -1));
        entry.setName(intent.getStringExtra(Constants.RESTAURANT_NAME));
        goToLocation = intent.getBooleanExtra(Constants.RESTAUNRANT_LOCATION, false);


        actionBar.setTitle(entry.getName());

        mViewPager = (ViewPager) findViewById(R.id.pager);


        progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new GetRestaurantInfo().execute(entry.getId() + "");
        } else {
            Toast.makeText(this, "No Internet Access. Please Check Your Connection", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_restaurant_info, menu);
        MenuItem item = menu.findItem(R.id.add_favorite);
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP_NAME, 0);
        Set<String> favorites = sharedPreferences.getStringSet(Constants.FAVORITE_LIST, new HashSet<String>());
        if (!favorites.contains(entry.getId() + "")) {
            item.setTitle("Like");
        } else {
            item.setTitle("Unlike");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        if (id == R.id.add_favorite) {

            SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP_NAME, 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Set<String> favorites = sharedPreferences.getStringSet(Constants.FAVORITE_LIST, new HashSet<String>());
            if (!favorites.contains(entry.getId() + "")) {
                Set<String> editedFavorites = new HashSet<>(favorites);
                editedFavorites.add(entry.getId() + "");
                editor.putStringSet(Constants.FAVORITE_LIST, editedFavorites);
                editor.commit();
                item.setTitle("Unlike");
                Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show();
            } else {
                Set<String> editedFavorites = new HashSet<>(favorites);
                editedFavorites.remove(entry.getId() + "");
                editor.putStringSet(Constants.FAVORITE_LIST, editedFavorites);
                editor.commit();
                item.setTitle("Like");
                Toast.makeText(this, "Removed from Favorites", Toast.LENGTH_SHORT).show();
            }
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
                    menuFragment = MenuFragment.newInstance(menuUrl);
                }
                return menuFragment;
            } else {
                if (locationFragment == null) {
                    locationFragment = LocationFragment.newInstance(context, address, latitude, longtitude, imageUrl);
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
                JSONObject usermap = jsonObject.getJSONObject("usermap");
                address = usermap.getJSONObject("loc").getString("address");
                latitude = usermap.getJSONObject("loc").getDouble("latitude");
                longtitude = usermap.getJSONObject("loc").getDouble("longitude");
                imageUrl = jsonObject.getString("logo");
                menuUrl = jsonObject.getString("menu");
                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
                mViewPager.setAdapter(mSectionsPagerAdapter);

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
                mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        actionBar.setSelectedNavigationItem(position);
                    }
                });
                if (goToLocation) {
                    mViewPager.setCurrentItem(1);
                }
                locationFragment.setAddress(address);
                locationFragment.setLatLng(new LatLng(latitude, longtitude));
                locationFragment.setImageUrl(imageUrl);
                progressDialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private String getRestaurantInfo(String id) throws IOException {
            InputStream is = null;
            try {
                URL url = new URL("https://abracadabrant-fromage-4658.herokuapp.com/resid/" + id + "/?format=json");
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
