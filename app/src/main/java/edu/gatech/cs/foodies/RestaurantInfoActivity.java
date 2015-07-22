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
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;


public class RestaurantInfoActivity extends ActionBarActivity {


    double latitude;
    double longtitude;
    String address;
    String imageUrl;
    String menuUrl;
    String name;
    String detailLocation;
    String monday;
    String tuesday;
    String wednesday;
    String thursday;
    String friday;
    String saturday;
    String sunday;

    FoodLocationEntry entry;

    MenuFragment menuFragment;
    LocationFragment locationFragment;
    RestaurantInfoFragment restaurantInfoFragment;

    Context context;
    ProgressDialog progressDialog;

    boolean goToLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_info);
        entry = new FoodLocationEntry();
        context = this;
        longtitude = 0;
        latitude = 0;
        address = "";
        imageUrl = "";
        menuUrl = "";
        name = "";
        detailLocation = "";
        monday = "";
        tuesday = "";
        wednesday = "";
        thursday = "";
        friday = "";
        saturday = "";
        sunday = "";
        context = this;
        Intent intent = getIntent();
        entry.setId(intent.getIntExtra(Constants.RESTAURANT_ID, -1));
        entry.setName(intent.getStringExtra(Constants.RESTAURANT_NAME));
        goToLocation = intent.getBooleanExtra(Constants.RESTAUNRANT_LOCATION, false);

        if (menuFragment == null) {
            menuFragment = MenuFragment.newInstance(menuUrl);
        }
        if (restaurantInfoFragment == null) {
            restaurantInfoFragment = RestaurantInfoFragment.newInstance(imageUrl, name, detailLocation, monday, tuesday, wednesday, thursday, friday, saturday, sunday);
        }
        if (locationFragment == null) {
            locationFragment = LocationFragment.newInstance(context, address, latitude, longtitude, imageUrl);
        }
        Button info = (Button) findViewById(R.id.restaurant_info);
        Button menu = (Button) findViewById(R.id.restaurant_menu);
        Button locationButton = (Button) findViewById(R.id.restaurant_location);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.info_activity_container, RestaurantInfoFragment.newInstance(imageUrl, name, detailLocation, monday, tuesday, wednesday, thursday, friday, saturday, sunday));
                fragmentTransaction.commit();
            }
        });
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menuFragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.info_activity_container, MenuFragment.newInstance(menuUrl));
                    fragmentTransaction.commit();
                }
            }
        });
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationFragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.info_activity_container, locationFragment);
                    fragmentTransaction.commit();
                }
            }
        });
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
                name = jsonObject.getString("name");
                detailLocation = usermap.getJSONObject("loc").getString("details");
                JSONObject hours = usermap.getJSONObject("hours");
                if (hours.getBoolean("m")) {
                    monday = hours.getString("open_time_m") + " - " + hours.getString("close_time_m");
                } else {
                    monday = "Closed";
                }
                if (hours.getBoolean("t")) {
                    tuesday = hours.getString("open_time_t") + " - " + hours.getString("close_time_t");
                } else {
                    tuesday = "Closed";
                }
                if (hours.getBoolean("m")) {
                    wednesday = hours.getString("open_time_w") + " - " + hours.getString("close_time_w");
                } else {
                    wednesday = "Closed";
                }
                if (hours.getBoolean("m")) {
                    thursday = hours.getString("open_time_r") + " - " + hours.getString("close_time_r");
                } else {
                    thursday = "Closed";
                }
                if (hours.getBoolean("m")) {
                    friday = hours.getString("open_time_f") + " - " + hours.getString("close_time_f");
                } else {
                    friday = "Closed";
                }
                if (hours.getBoolean("m")) {
                    saturday = hours.getString("open_time_s") + " - " + hours.getString("close_time_s");
                } else {
                    saturday = "Closed";
                }
                if (hours.getBoolean("m")) {
                    sunday = hours.getString("open_time_h") + " - " + hours.getString("close_time_h");
                } else {
                    sunday = "Closed";
                }

                if (locationFragment != null) {
                    locationFragment.setAddress(address);
                    locationFragment.setLatLng(new LatLng(latitude, longtitude));
                    locationFragment.setImageUrl(imageUrl);
                } else {
                    locationFragment = LocationFragment.newInstance(context, address, latitude, longtitude, imageUrl);
                }
                if (goToLocation) {
                    findViewById(R.id.restaurant_location).performClick();
                } else {
                    findViewById(R.id.restaurant_info).performClick();
                }
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

