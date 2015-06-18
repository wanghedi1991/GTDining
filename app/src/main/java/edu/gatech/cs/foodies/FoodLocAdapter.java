package edu.gatech.cs.foodies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Hedi Wang on 2015/6/18.
 */
public class FoodLocAdapter extends RecyclerView.Adapter<FoodLocAdapter.ViewHolder> {
    private FoodLocationEntry[] entries;

    public FoodLocAdapter(FoodLocationEntry[] entries) {
        this.entries = entries;
    }

    @Override
    public FoodLocAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View entryLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_place_entry, null);
        ViewHolder viewHolder = new ViewHolder(entryLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(entries[position].getTitle());
        holder.promo.setText(entries[position].getPromo());
        holder.icon.setImageResource(entries[position].getImageUrl());
    }

    @Override
    public int getItemCount() {
        return entries.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public ImageView icon;
        public TextView promo;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            title = (TextView) itemLayoutView.findViewById(R.id.entry_title);
            icon = (ImageView) itemLayoutView.findViewById(R.id.entry_icon);
            promo = (TextView) itemLayoutView.findViewById(R.id.entry_promo);
        }
    }
}
