package edu.gatech.cs.foodies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Hedi Wang on 2015/6/18.
 */
public class FoodLocAdapter extends RecyclerView.Adapter<FoodLocAdapter.FoodLocViewHolder> {
    private ArrayList<FoodLocationEntry> entries;
    private Context context;
    public static String RESTAURANT_ID = "RESTAURANT_ID";
    public static String RESTAURANT_NAME = "RESTAURANT_NAME";
    public static String RESTAURANT_URI = "RESTAURANT_URI";
    public static String RESTAUNRANT_LOCATION = "RESTAUNRANT_LOCATION";

    public FoodLocAdapter(ArrayList<FoodLocationEntry> entries, Context context) {
        this.entries = entries;
        this.context = context;
    }

    @Override
    public FoodLocViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View entryLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_place_entry, parent, false);
        FoodLocViewHolder foodLocViewHolder = new FoodLocViewHolder(entryLayoutView);
        return foodLocViewHolder;
    }


    @Override
    public void onBindViewHolder(FoodLocViewHolder holder, final int position) {
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, RestaurantInfoActivity.class);
                i.putExtra(RESTAURANT_ID, entries.get(position).getId());
                i.putExtra(RESTAURANT_NAME, entries.get(position).getName());
                i.putExtra(RESTAURANT_URI, entries.get(position).getImageUrl());
                i.putExtra(RESTAUNRANT_LOCATION, false);
                context.startActivity(i);
            }
        });
        holder.title.setText(entries.get(position).getName());
        holder.icon.setImageResource(entries.get(position).getImageUrl());
        holder.goToLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, RestaurantInfoActivity.class);
                i.putExtra(RESTAURANT_ID, entries.get(position).getId());
                i.putExtra(RESTAURANT_NAME, entries.get(position).getName());
                i.putExtra(RESTAURANT_URI, entries.get(position).getImageUrl());
                i.putExtra(RESTAUNRANT_LOCATION, true);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public static class FoodLocViewHolder extends RecyclerView.ViewHolder {

        LinearLayout linearLayout;
        TextView title;
        ImageView icon;
        ImageView goToLocation;

        public FoodLocViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            linearLayout = (LinearLayout) itemLayoutView.findViewById(R.id.food_place_entry);
            title = (TextView) itemLayoutView.findViewById(R.id.entry_title);
            icon = (ImageView) itemLayoutView.findViewById(R.id.entry_icon);
            goToLocation = (ImageView) itemLayoutView.findViewById(R.id.food_loc_button);
        }


    }
}
