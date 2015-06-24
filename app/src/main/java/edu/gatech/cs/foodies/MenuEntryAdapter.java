package edu.gatech.cs.foodies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Hedi Wang on 2015/6/24.
 */
public class MenuEntryAdapter extends RecyclerView.Adapter<MenuEntryAdapter.MenuViewHolder> {
    private MenuEntry[] entries;
    private Context context;

    public MenuEntryAdapter(MenuEntry[] entries, Context context) {
        this.entries = entries;
        this.context = context;
    }


    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View menuView = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_entry, parent, false);
        MenuViewHolder menuViewHolder = new MenuViewHolder(menuView);
        return menuViewHolder;
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        holder.name.setText(entries[position].getName());
        holder.price.setText(entries[position].getPrice() + "");
        holder.calorie.setText(entries[position].getCalorie() + "");
    }

    @Override
    public int getItemCount() {
        return entries.length;
    }

    public static class MenuViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        TextView name;
        TextView price;
        TextView calorie;

        public MenuViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            name = (TextView) itemView.findViewById(R.id.name);
            price = (TextView) itemView.findViewById(R.id.price);
            calorie = (TextView) itemView.findViewById(R.id.calorie);
        }
    }
}
