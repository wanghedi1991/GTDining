package edu.gatech.cs.foodies;

import android.animation.Animator;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class FoodLocListFragment extends Fragment {

    RecyclerView restaurantList;
    RecyclerView favoriteList;
    FoodLocAdapter nearbyAdapter;
    FoodLocAdapter favoriteAdapter;
    boolean searchTextSet;
    String presetSearchText;

    public static FoodLocListFragment newInstance() {

        FoodLocListFragment fragment = new FoodLocListFragment();
        return fragment;
    }

    public FoodLocListFragment() {
        searchTextSet = false;
        presetSearchText = "";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        if (nearbyAdapter == null) {
            nearbyAdapter = new FoodLocAdapter();
        }
        if (favoriteAdapter == null) {
            favoriteAdapter = new FoodLocAdapter();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_loc_list, container, false);
        restaurantList = (RecyclerView) view.findViewById(R.id.nearby_list);
        favoriteList = (RecyclerView) view.findViewById(R.id.favorite_list);
        final Button toggle = (Button) view.findViewById(R.id.toggle_favorite);
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
        ((EditText) view.findViewById(R.id.search_text)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        Button filterButton = (Button) view.findViewById(R.id.filter_result);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText) getView().findViewById(R.id.search_text)).setText("");
                FilterDialogFragment filterDialog = new FilterDialogFragment();
                filterDialog.show(getFragmentManager(), "filter");
            }
        });
        if (nearbyAdapter != null) {
            restaurantList.setAdapter(nearbyAdapter);
        }
        if (favoriteAdapter != null) {
            favoriteList.setAdapter(favoriteAdapter);
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

    @Override
    public void onResume() {
        super.onResume();
        if (searchTextSet) {
            ((EditText) getView().findViewById(R.id.search_text)).setText(presetSearchText);
            searchTextSet = false;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        restaurantList.setLayoutManager(new LinearLayoutManager(getActivity()));
        favoriteList.setLayoutManager(new LinearLayoutManager(getActivity()));
        restaurantList.setItemAnimator(new DefaultItemAnimator());
        favoriteList.setItemAnimator(new DefaultItemAnimator());
        favoriteList.setVisibility(View.GONE);
    }

    public void filter(CharSequence s) {
        if (restaurantList != null && restaurantList.getAdapter() != null) {
            ((FoodLocAdapter) restaurantList.getAdapter()).getFilter().filter(s);
            ((FoodLocAdapter) favoriteList.getAdapter()).getFilter().filter(s);
        }
    }

    public void setAdapters(FoodLocAdapter nearbyAdapter, FoodLocAdapter favoriteAdapter) {
        this.nearbyAdapter = nearbyAdapter;
        this.favoriteAdapter = favoriteAdapter;
        restaurantList.setAdapter(this.nearbyAdapter);
        favoriteList.setAdapter(this.favoriteAdapter);
        this.nearbyAdapter.notifyDataSetChanged();
        this.favoriteAdapter.notifyDataSetChanged();
    }

    public void setSearchText(String text) {
        searchTextSet = true;
        presetSearchText = text;
    }

    public void clearSearchText() {
        if (getView() != null) {
            ((EditText) getView().findViewById(R.id.search_text)).setText("");
        }
    }
}
