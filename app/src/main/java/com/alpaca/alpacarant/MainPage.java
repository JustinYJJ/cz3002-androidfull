package com.alpaca.alpacarant;

import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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
        listNavItems.add(new NavItem("Profile", R.drawable.ic_settings_button));
        listNavItems.add(new NavItem("Logout", R.drawable.ic_logout_button));

        NavItemAdapter navItemAdapter = new NavItemAdapter(getApplicationContext(), R.layout.item_nav_list, listNavItems);

        lvNav.setAdapter(navItemAdapter);

        listFragments = new ArrayList<Fragment>();
        listFragments.add(new SideHome());
        listFragments.add(new SideSearch());
        listFragments.add(new SideSettings());
        listFragments.add(new SideLogout());

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
        String annonymous;

        //Get rant text
        EditText editRant = (EditText) findViewById(R.id.editRant);
        String rant = editRant.getText().toString();

        //Get value of spinner
        Spinner spinnerLifetime = (Spinner) findViewById(R.id.spinnerLifetime);
        String lifetime = "" + (Integer.parseInt(spinnerLifetime.getSelectedItem().toString()) * 3600);

        Spinner spinnerViewtime = (Spinner) findViewById(R.id.spinnerViewtime);
        String viewtime = spinnerViewtime.getSelectedItem().toString();

        CheckBox checkBoxAnnonymous = (CheckBox) findViewById(R.id.checkBoxAnnonymous);
        if (checkBoxAnnonymous.isChecked()){
            annonymous = "true";
        }
        else{
            annonymous = "false";
        }

        sendPostRantRequest(lifetime, viewtime, annonymous, rant);
    }

    public void onPostMessageButtonClick(View v){
        startActivity(new Intent(getApplicationContext(), MessageFriend.class));
    }

    private void sendPostRantRequest(String lifetime, String viewtime, String annonymous, String rant) {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String paramLifeTime = params[0];
                String paramViewTime = params[1];
                String paramAnnonymous = params[2];
                String paramRant = params[3];

                //instantiates httpclient to make request
                DefaultHttpClient httpClient = new DefaultHttpClient();

                //url with the post data
                HttpPost httpPost = new HttpPost("http://nturant.me/rant/");
                httpPost.setHeader("accept", "application/json");

                //create values to be passed into POST request
                BasicNameValuePair lifetimeBasicNameValuePair = new BasicNameValuePair("lifetime", paramLifeTime);
                BasicNameValuePair viewtimeBasicNameValuePair = new BasicNameValuePair("viewtime", paramViewTime);
                BasicNameValuePair annonymousBasicNameValuePair = new BasicNameValuePair("annonymous", paramAnnonymous);
                BasicNameValuePair contentBasicNameValuePair = new BasicNameValuePair("content", paramRant);

                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(lifetimeBasicNameValuePair);
                nameValuePairList.add(viewtimeBasicNameValuePair);
                nameValuePairList.add(annonymousBasicNameValuePair);
                nameValuePairList.add(contentBasicNameValuePair);

                try{
                    //convert value to UrlEncodedFormEntity
                    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);

                    //hands the entity to the request
                    httpPost.setEntity(urlEncodedFormEntity);

                    try{
                        HttpResponse httpResponse = httpClient.execute(httpPost, LocalContext.httpContext);

                        //get HttpResponse content
                        InputStream inputStream = httpResponse.getEntity().getContent();

                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                        StringBuilder stringBuilder = new StringBuilder();

                        String bufferedStrChunk = null;

                        while((bufferedStrChunk = bufferedReader.readLine()) != null){
                            stringBuilder.append(bufferedStrChunk);
                        }

                        System.out.println(httpResponse.getStatusLine().getStatusCode());
                        if (httpResponse.getStatusLine().getStatusCode() == 200){
                            System.out.println("Rant posted");
                        }

                        if (httpResponse.getEntity() != null){
                            Log.i("Entity: ", "Not null");
                            httpResponse.getEntity().consumeContent();
                        }

                        return stringBuilder.toString();
                    }   catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                startActivity(new Intent(getApplicationContext(), MainPage.class));
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(lifetime, viewtime, annonymous, rant);
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
