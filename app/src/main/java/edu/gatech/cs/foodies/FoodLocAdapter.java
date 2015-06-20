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

/**
 * Created by Hedi Wang on 2015/6/18.
 */
public class FoodLocAdapter extends RecyclerView.Adapter<FoodLocAdapter.ViewHolder> {
    private FoodLocationEntry[] entries;
    private Context context;
    public static String RESTAURANT_NAME = "RESTAURANT_NAME";
    public static String RESTAURANT_URI = "RESTAURANT_URI";

    public FoodLocAdapter(FoodLocationEntry[] entries, Context context) {
        this.entries = entries;
        this.context = context;
    }

    @Override
    public FoodLocAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View entryLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_place_entry, parent, false);
        ViewHolder viewHolder = new ViewHolder(entryLayoutView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.getItemLayoutView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, RestaurantInfo.class);
                i.putExtra(RESTAURANT_NAME, entries[position].getTitle() );
                i.putExtra(RESTAURANT_URI, entries[position].getImageUrl());
                context.startActivity(i);
            }
        });
        holder.title.setText(entries[position].getTitle());
        holder.promo.setText(entries[position].getPromo());
        holder.icon.setImageResource(entries[position].getImageUrl());
    }

    @Override
    public int getItemCount() {
        return entries.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View itemLayoutView;
        public TextView title;
        public ImageView icon;
        public TextView promo;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            this.itemLayoutView = itemLayoutView;
            title = (TextView) itemLayoutView.findViewById(R.id.entry_title);
            icon = (ImageView) itemLayoutView.findViewById(R.id.entry_icon);
            promo = (TextView) itemLayoutView.findViewById(R.id.entry_promo);
        }

        public View getItemLayoutView() {
            return itemLayoutView;
        }
    }
}
