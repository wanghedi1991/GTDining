package edu.gatech.cs.foodies;

import android.animation.Animator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, FilterDialogFragment.FilterDialogListener, GuidedSearchFragment.GuidedSearchTopListener, GuidedSearchSubFragment.GuidedSearchSubListener {


    NavigationDrawerFragment mNavigationDrawerFragment;
    CharSequence mTitle;
    Context context;
    ProgressDialog progressDialog;
    GoogleApiClient googleApiClient;
    Location myLocation;
    FoodLocListFragment foodLocListFragment;
    FoodLocMapFragment foodLocMapFragment;
    Boolean isListShowing;
    ArrayList<FoodLocationEntry> restaurants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        if (restaurants == null) {
            restaurants = new ArrayList<>();
        }

        buildGoogleApiClient();
        googleApiClient.connect();

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, drawerLayout);
        drawerLayout.closeDrawers();

        if (foodLocListFragment == null) {
            foodLocListFragment = FoodLocListFragment.newInstance();
        }
        if (foodLocMapFragment == null) {
            foodLocMapFragment = FoodLocMapFragment.newInstance();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.restaurant_fragment_container, foodLocListFragment);
        fragmentTransaction.commit();
        isListShowing = true;


        MapsInitializer.initialize(context);
        progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Loading data....");
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new GetRestaurantsTask().execute();
        } else {
            Toast.makeText(this, "No Internet Access. Please Check Your Connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP_NAME, 0);
        Set<String> favorites = sharedPreferences.getStringSet(Constants.FAVORITE_LIST, new HashSet<String>());
        ArrayList<FoodLocationEntry> favoriteArray = new ArrayList<>();
        if (restaurants != null) {
            for (FoodLocationEntry entry : restaurants) {
                if (favorites.contains(entry.getId() + "")) {
                    favoriteArray.add(entry);
                }
            }
        }
        FoodLocAdapter adapter = new FoodLocAdapter(restaurants, context);
        FoodLocAdapter adapter1 = new FoodLocAdapter(favoriteArray, context);
        foodLocListFragment.setAdapters(adapter, adapter1);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (position == 0) {
            if (foodLocListFragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.restaurant_fragment_container, foodLocListFragment);
                fragmentTransaction.commit();
                isListShowing = true;
            }
        }
        if (position == 1) {
            GuidedSearchFragment guidedSearchFragment = GuidedSearchFragment.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.restaurant_fragment_container, guidedSearchFragment);
            fragmentTransaction.commit();
        }
        // update the main content by replacing fragments
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
//                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_drawer_search);
                break;
            case 2:
                mTitle = getString(R.string.title_drawer_promo);
                break;
            case 3:
                mTitle = getString(R.string.title_drawer_home);
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.refresh) {
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                new GetRestaurantsTask().execute();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.restaurant_fragment_container, foodLocListFragment);
                transaction.commit();
                isListShowing = true;
            } else {
                Toast.makeText(this, "No Internet Access. Please Check Your Connection", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        if (id == R.id.toggle_view) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (isListShowing) {
                transaction.replace(R.id.restaurant_fragment_container, foodLocMapFragment);
                isListShowing = false;
            } else {
                transaction.replace(R.id.restaurant_fragment_container, foodLocListFragment);
                isListShowing = true;
            }
            transaction.commit();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveClick(boolean diningDollar, boolean mealPlan, boolean buzzFund, int costLevel) {
        String filterString = "tagfilter";
        if (diningDollar) {
            filterString = filterString + "diningdollar";
        }
        if (mealPlan) {
            filterString = filterString + "mealplan";
        }
        if (buzzFund) {
            filterString = filterString + "buzzfund";
        }
        if (costLevel > 0) {
            filterString = filterString + "cost_" + costLevel;
        }
        foodLocListFragment.filter(filterString);
    }

    @Override
    public void searchItemSelected(int index) {
        GuidedSearchSubFragment guidedSearchSubFragment = GuidedSearchSubFragment.newInstance(index);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.restaurant_fragment_container, guidedSearchSubFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void selectGuidedSearchItem(String item) {
        if (foodLocListFragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.restaurant_fragment_container, foodLocListFragment);
            fragmentTransaction.commit();
            isListShowing = true;
            foodLocListFragment.setSearchText(item);
        }
    }


    private class GetRestaurantsTask extends AsyncTask<String, Void, ArrayList<FoodLocationEntry>> {
        ArrayList<MarkerOptions> markers;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected ArrayList<FoodLocationEntry> doInBackground(String... urls) {
            try {
                String s = getRestaurants("https://abracadabrant-fromage-4658.herokuapp.com/resid/?format=json");
                JSONArray jsonArray = new JSONArray(s);

                ArrayList<FoodLocationEntry> restaurants = new ArrayList<>();
                markers = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    FoodLocationEntry temp = new FoodLocationEntry();
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    int id = jsonObject.getJSONObject("usermap").getInt("resid");
                    temp.setId(id);
                    String name = jsonObject.getString("name");
                    temp.setName(name);
                    String imgUrl = jsonObject.getString("logo");
                    temp.setImageUrl(imgUrl);
                    Location restaurantLocation = new Location("");
                    Double latitude = jsonObject.getJSONObject("usermap").getJSONObject("loc").getDouble("latitude");
                    Double longitude = jsonObject.getJSONObject("usermap").getJSONObject("loc").getDouble("longitude");
                    temp.setTag(jsonObject.getString("tags"));
                    restaurantLocation.setLatitude(latitude);
                    restaurantLocation.setLongitude(longitude);
                    temp.setLocation(restaurantLocation);
                    restaurants.add(temp);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(latitude, longitude));
                    markerOptions.title(name);
//                    Drawable d;
//                    try {
//                        InputStream is = (InputStream) new URL(imgUrl).getContent();
//                        d = Drawable.createFromStream(is, "icon");
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        d = context.getResources().getDrawable(R.drawable.cross_out);
//                    }
//                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(((BitmapDrawable) d).getBitmap()));
                    markers.add(markerOptions);
                }
                Collections.sort(restaurants, new Comparator<FoodLocationEntry>() {
                    @Override
                    public int compare(FoodLocationEntry firstLoc, FoodLocationEntry secondLoc) {
                        if (myLocation != null) {
                            double distance0 = firstLoc.getLocation().distanceTo(myLocation);
                            double distance1 = secondLoc.getLocation().distanceTo(myLocation);
                            if (!firstLoc.isDistanceSet()) {
                                firstLoc.setName(firstLoc.getName() + " ( " + ((int) distance0) + " meters away. ");
                                firstLoc.setDistanceSet(true);
                            }
                            if (!secondLoc.isDistanceSet()) {
                                secondLoc.setName(secondLoc.getName() + " ( " + ((int) distance1) + " meters away. )");
                                secondLoc.setDistanceSet(true);
                            }
                            return Double.compare(distance0, distance1);
                        }
                        return 0;
                    }
                });
                MainActivity.this.restaurants = restaurants;
                return restaurants;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<FoodLocationEntry> restaurants) {
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP_NAME, 0);
            Set<String> favorites = sharedPreferences.getStringSet(Constants.FAVORITE_LIST, new HashSet<String>());
            ArrayList<FoodLocationEntry> favoriteArray = new ArrayList<>();
            if (restaurants != null) {
                for (FoodLocationEntry entry : restaurants) {
                    if (favorites.contains(entry.getId() + "")) {
                        favoriteArray.add(entry);
                    }
                }

                FoodLocAdapter adapter = new FoodLocAdapter(restaurants, context);
                FoodLocAdapter adapter1 = new FoodLocAdapter(favoriteArray, context);
                foodLocListFragment.setAdapters(adapter, adapter1);
                foodLocMapFragment.setMarkers(markers);
            }
            progressDialog.dismiss();
        }

        private String getRestaurants(String urlString) throws IOException {
            InputStream is = null;
            try {
                URL url = new URL(urlString);
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

    @Override
    public void onBackPressed() {
        if (foodLocListFragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.restaurant_fragment_container, foodLocListFragment);
            fragmentTransaction.commit();
            foodLocListFragment.clearSearchText();
            isListShowing = true;
        }

    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        myLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.e("Location Log", connectionResult.toString());
                        Toast.makeText(MainActivity.this, "Location Service not available", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(LocationServices.API)
                .build();
    }
}
