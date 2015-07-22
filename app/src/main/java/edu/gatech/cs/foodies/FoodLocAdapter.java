package edu.gatech.cs.foodies;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Hedi Wang on 2015/6/18.
 */
public class FoodLocAdapter extends RecyclerView.Adapter<FoodLocAdapter.FoodLocViewHolder> implements Filterable {
    private ArrayList<FoodLocationEntry> entries;
    private ArrayList<FoodLocationEntry> entriesCopy;
    private Context context;

    public FoodLocAdapter() {
        entries = new ArrayList<>();
        entriesCopy = new ArrayList<>();
        context = null;
    }

    public FoodLocAdapter(ArrayList<FoodLocationEntry> entries, Context context) {
        this.entries = entries;
        entriesCopy = new ArrayList<>(entries);
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
                i.putExtra(Constants.RESTAURANT_ID, entries.get(position).getId());
                i.putExtra(Constants.RESTAURANT_NAME, entries.get(position).getName());
                i.putExtra(Constants.RESTAURANT_ICON_URL, entries.get(position).getImageUrl());
                i.putExtra(Constants.RESTAUNRANT_LOCATION, false);
                context.startActivity(i);
            }
        });
        holder.title.setText(entries.get(position).getName());
        String iconUrl = entries.get(position).getImageUrl();
        new DisplayingIconTask(holder.icon).execute(iconUrl);
        holder.goToLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, RestaurantInfoActivity.class);
                i.putExtra(Constants.RESTAURANT_ID, entries.get(position).getId());
                i.putExtra(Constants.RESTAURANT_NAME, entries.get(position).getName());
                i.putExtra(Constants.RESTAURANT_ICON_URL, entries.get(position).getImageUrl());
                i.putExtra(Constants.RESTAUNRANT_LOCATION, true);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    results.values = entriesCopy;
                    results.count = entriesCopy.size();
                } else {
                    ArrayList<FoodLocationEntry> resultEntries = new ArrayList<>();
                    for (FoodLocationEntry entry : entriesCopy) {
                        if (entry.getName().trim().toLowerCase().contains(constraint.toString().trim().toLowerCase())) {
                            resultEntries.add(entry);
                        }
                        if (entry.getTag().toLowerCase().contains(constraint.toString().trim().toLowerCase())) {

                            resultEntries.add(entry);
                        }
                        Boolean checkTag = false;
                        if (constraint.toString().contains("tagfilter")) {
                            checkTag = true;
                        }
                        if (checkTag) {
                            boolean pass = true;
                            String tags = entry.getTag().trim().toLowerCase();
                            String filterTag = constraint.toString();
                            if (filterTag.contains("diningdollar")) {
                                if (!tags.contains("diningdollar")) {
                                    pass = false;
                                }
                            }
                            if (filterTag.contains("mealplan")) {
                                if (!tags.contains("mealplan")) {
                                    pass = false;
                                }
                            }
                            if (filterTag.contains("buzzfund")) {
                                if (!tags.contains("buzzfund")) {
                                    pass = false;
                                }
                            }
                            if (filterTag.contains("cost_1")) {
                                if (!tags.contains("cost_1")) {
                                    pass = false;
                                }
                            }
                            if (filterTag.contains("cost_2")) {
                                if (!tags.contains("cost_2")) {
                                    pass = false;
                                }
                            }
                            if (filterTag.contains("cost_3")) {
                                if (!tags.contains("cost_3")) {
                                    pass = false;
                                }
                            }
                            if (pass) {
                                resultEntries.add(entry);
                            }
                        }
                    }
                    results.values = resultEntries;
                    results.count = resultEntries.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                entries = (ArrayList<FoodLocationEntry>) results.values;
                notifyDataSetChanged();
            }
        };
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

    private class DisplayingIconTask extends AsyncTask<String, Void, Drawable> {

        private ImageView imageView;

        public DisplayingIconTask() {

        }

        public DisplayingIconTask(ImageView imageView) {
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
                Drawable d = context.getResources().getDrawable(R.drawable.cross_out);
                return d;
            }
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            imageView.setImageDrawable(drawable);
        }
    }

    public ArrayList<FoodLocationEntry> getEntries() {
        return entries;
    }

    public void setEntries(ArrayList<FoodLocationEntry> entries) {
        this.entries = entries;
    }
}
