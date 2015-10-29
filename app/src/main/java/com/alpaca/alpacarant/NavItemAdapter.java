package com.alpaca.alpacarant;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by justinyeo on 17/9/15.
 */
public class NavItemAdapter extends ArrayAdapter<NavItem> {

    Context context;
    int resLayout;
    List<NavItem> listNavItems;

    /**
     * Constructor for navigation item adapter
     * @param context       Attribute for context
     * @param resLayout     Attribute for res layout
     * @param listNavItems  Attribute for list of navigation items
     */
    public NavItemAdapter(Context context, int resLayout, List<NavItem> listNavItems) {
        super(context, resLayout, listNavItems);

        this.context = context;
        this.resLayout = resLayout;
        this.listNavItems = listNavItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = View.inflate(context, resLayout, null);

        TextView tvTitle = (TextView) v.findViewById(R.id.title);
        ImageView navIcon = (ImageView) v.findViewById(R.id.nav_icon);

        NavItem navItem = listNavItems.get(position);

        tvTitle.setText(navItem.getTitle());
        navIcon.setImageResource(navItem.getResIcon());

        return v;
    }
}
