package edu.gatech.cs.foodies;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.Button;
import android.widget.Toast;

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


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {


    private NavigationDrawerFragment mNavigationDrawerFragment;
    private RecyclerView restaurantList;
    //    private RecyclerView favoriteList;
    private CharSequence mTitle;
    Context context;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        restaurantList = (RecyclerView) findViewById(R.id.nearby_list);
//        favoriteList = (RecyclerView) findViewById(R.id.favorite_list);

//        FoodLocationEntry entries0[] = {new FoodLocationEntry("Panda00", "Promo00", R.drawable.panda),
//                new FoodLocationEntry("Panda01", "Promo01", R.drawable.panda),
//                new FoodLocationEntry("Panda02", "Promo02", R.drawable.panda),
//                new FoodLocationEntry("Panda03", "Promo03", R.drawable.panda)};


        ArrayList<RestaurantEntry> test = new ArrayList<>();
        test.add(new RestaurantEntry(3, "Test", "test"));
        test.add(new RestaurantEntry(4, "Test1", "test1"));
        restaurantList.setLayoutManager(new LinearLayoutManager(this));
//        favoriteList.setLayoutManager(new LinearLayoutManager(this));
//        FoodLocAdapter nearbyAdapter = new FoodLocAdapter(entries0,this);
//        FoodLocAdapter favoriteAdapter = new FoodLocAdapter(entries1, this);
//        favoriteList.setAdapter(favoriteAdapter);
        restaurantList.setItemAnimator(new DefaultItemAnimator());
//        favoriteList.setItemAnimator(new DefaultItemAnimator());
        Button button = (Button) findViewById(R.id.search_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        context = this;
        progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
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

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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
            progressDialog.setMessage("Loading data....");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                return getRestaurants("https://boiling-mesa-3124.herokuapp.com/dining/?format=json");
            } catch (Exception e) {
                return "Unable to get restaurants information";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONArray jsonArray = new JSONArray(s);
                ArrayList<FoodLocationEntry> restaurants = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    FoodLocationEntry temp = new FoodLocationEntry();
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    temp.setId(jsonObject.getInt("restaurantID"));
                    temp.setName(jsonObject.getString("restaurantName"));
                    temp.setOwner(jsonObject.getString("owner"));
                    restaurants.add(temp);
                }
                FoodLocAdapter adapter = new FoodLocAdapter(restaurants, context);
                progressDialog.dismiss();
                restaurantList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
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
}
