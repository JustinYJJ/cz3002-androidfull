package com.alpaca.alpacarant;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;


public class MainPage extends ActionBarActivity {

    DrawerLayout drawerLayout;
    RelativeLayout drawerPane;
    ListView lvNav;

    List<NavItem> listNavItems;
    List<Fragment> listFragments;

    ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.MainPage);
        drawerPane = (RelativeLayout) findViewById(R.id.drawer_panel);
        lvNav = (ListView) findViewById(R.id.nav_list);

        listNavItems = new ArrayList<NavItem>();
        listNavItems.add(new NavItem("Alpaca Rant", R.drawable.ic_home_button));
        listNavItems.add(new NavItem("Friend Suggestions", R.drawable.ic_search_button));
        listNavItems.add(new NavItem("Settings", R.drawable.ic_settings_button));
        listNavItems.add(new NavItem("Logout", R.drawable.ic_logout_button));

        NavItemAdapter navItemAdapter = new NavItemAdapter(getApplicationContext(), R.layout.item_nav_list, listNavItems);

        lvNav.setAdapter(navItemAdapter);

        listFragments = new ArrayList<Fragment>();
        listFragments.add(new SideHome());
        listFragments.add(new SideSearch());
        listFragments.add(new SideSettings());

        //load first fragment as default
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_content, listFragments.get(0)).commit();

        setTitle(listNavItems.get(0).getTitle());
        lvNav.setItemChecked(0, true);
        drawerLayout.closeDrawer(drawerPane);

        //set listener for navigation items
        lvNav.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //replace the fragment with the selection
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_content, listFragments.get(position)).commit();

                setTitle(listNavItems.get(position).getTitle());
                lvNav.setItemChecked(position, true);
                drawerLayout.closeDrawer(drawerPane);
            }
        });

        //create listener for drawer layout
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_opened, R.string.drawer_closed) {
            @Override
            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                invalidateOptionsMenu();
                super.onDrawerClosed(drawerView);
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
    }

    public void onPostRantButtonClick(View v){
        //Get rant text
        EditText editRant = (EditText) findViewById(R.id.editRant);
        String rant = editRant.getText().toString();

        //Get value of spinner
        Spinner spinnerLifetime = (Spinner) findViewById(R.id.spinnerLifetime);
        String lifetime = spinnerLifetime.getSelectedItem().toString();

        Spinner spinnerViewtime = (Spinner) findViewById(R.id.spinnerViewtime);
        String viewtime = spinnerViewtime.getSelectedItem().toString();

        Log.i("Rant: ", rant);
        Log.i("Life: ", lifetime + " " + viewtime);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }
}
