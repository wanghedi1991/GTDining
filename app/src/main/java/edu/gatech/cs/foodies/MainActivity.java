package edu.gatech.cs.foodies;

import android.animation.Animator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {


    private NavigationDrawerFragment mNavigationDrawerFragment;
    private RecyclerView restaurantList;
    private RecyclerView favoriteList;
    private CharSequence mTitle;
    Context context;
    ProgressDialog progressDialog;
    private GoogleApiClient googleApiClient;
    private Location myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buildGoogleApiClient();
        googleApiClient.connect();
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, drawerLayout);
        drawerLayout.closeDrawers();
        restaurantList = (RecyclerView) findViewById(R.id.nearby_list);
        favoriteList = (RecyclerView) findViewById(R.id.favorite_list);
        restaurantList.setLayoutManager(new LinearLayoutManager(this));
        favoriteList.setLayoutManager(new LinearLayoutManager(this));
        restaurantList.setItemAnimator(new DefaultItemAnimator());
        favoriteList.setItemAnimator(new DefaultItemAnimator());
        favoriteList.setVisibility(View.GONE);
        final Button toggle = (Button) findViewById(R.id.toggle_favorite);
        toggle.setText("Show Favorites");
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (restaurantList.getVisibility() == View.VISIBLE) {
                    restaurantList.animate().translationY(restaurantList.getHeight()).alpha(0).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            restaurantList.setVisibility(View.GONE);
                            favoriteList.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {


                        }
                    });
                    toggle.setText("Hide Favorites");
                } else {
                    restaurantList.animate().translationY(0).alpha(1.0f).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            restaurantList.setVisibility(View.VISIBLE);
                            favoriteList.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {

                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    });

                    toggle.setText("Show Favorites");
                }
            }
        });
        ((EditText) findViewById(R.id.search_text)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ((FoodLocAdapter) restaurantList.getAdapter()).getFilter().filter(s);
                ((FoodLocAdapter) favoriteList.getAdapter()).getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        context = this;
        progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Loading data....");

    }

    @Override
    protected void onResume() {
        super.onResume();
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
    public void onNavigationDrawerItemSelected(int position) {
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
//        restoreActionBar();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.refresh) {
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                new GetRestaurantsTask().execute();
            } else {
                Toast.makeText(this, "No Internet Access. Please Check Your Connection", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class GetRestaurantsTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            try {

                return getRestaurants("https://abracadabrant-fromage-4658.herokuapp.com/resid/?format=json");
            } catch (Exception e) {
                return "Unable to get restaurants information";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONArray jsonArray = new JSONArray(s);

                SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP_NAME, 0);
                Set<String> favorites = sharedPreferences.getStringSet(Constants.FAVORITE_LIST, new HashSet<String>());
                ArrayList<FoodLocationEntry> restaurants = new ArrayList<>();
                ArrayList<FoodLocationEntry> favoriteArray = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    FoodLocationEntry temp = new FoodLocationEntry();
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    int id = jsonObject.getJSONObject("usermap").getInt("resid");
                    temp.setId(id);
                    temp.setName(jsonObject.getString("name"));
                    temp.setImageUrl(jsonObject.getString("logo"));
                    Location restaurantLocation = new Location("");
                    restaurantLocation.setLatitude(jsonObject.getJSONObject("usermap").getJSONObject("loc").getDouble("latitude"));
                    restaurantLocation.setLongitude(jsonObject.getJSONObject("usermap").getJSONObject("loc").getDouble("longitude"));
                    temp.setLocation(restaurantLocation);
                    restaurants.add(temp);
                    if (favorites.contains(id + "")) {
                        favoriteArray.add(temp);
                    }
                }
                Collections.sort(restaurants, new Comparator<FoodLocationEntry>() {
                    @Override
                    public int compare(FoodLocationEntry firstLoc, FoodLocationEntry secondLoc) {
                        if (myLocation != null) {
                            double distance0 = firstLoc.getLocation().distanceTo(myLocation);
                            double distance1 = secondLoc.getLocation().distanceTo(myLocation);
                            return Double.compare(distance0, distance1);
                        }
                        return 0;
                    }
                });
                FoodLocAdapter adapter = new FoodLocAdapter(restaurants, context);
                FoodLocAdapter adapter1 = new FoodLocAdapter(favoriteArray, context);
                restaurantList.setAdapter(adapter);
                favoriteList.setAdapter(adapter1);
                adapter.notifyDataSetChanged();
                adapter1.notifyDataSetChanged();
                progressDialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
