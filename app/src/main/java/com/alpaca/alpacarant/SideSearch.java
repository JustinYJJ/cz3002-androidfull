package com.alpaca.alpacarant;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by justinyeo on 17/9/15.
 */
public class SideSearch extends Fragment {
    String[] countries = new String[] {
            "India",
            "Pakistan",
            "Sri Lanka",
            "China",
            "Bangladesh",
            "Nepal",
            "Afghanistan",
            "North Korea",
            "South Korea",
            "Japan"
    };

    int[] flags = new int[]{
            R.drawable.ic_unknown_profile,
            R.drawable.ic_unknown_profile,
            R.drawable.ic_unknown_profile,
            R.drawable.ic_unknown_profile,
            R.drawable.ic_unknown_profile,
            R.drawable.ic_unknown_profile,
            R.drawable.ic_unknown_profile,
            R.drawable.ic_unknown_profile,
            R.drawable.ic_unknown_profile,
            R.drawable.ic_unknown_profile
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.side_search, container, false);

        Log.i("HttpGet: ", "Beginning");
        sendGetRequest();
        List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();

        for(int i=0;i<10;i++){
            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("txt", "Country : " + countries[i]);
            hm.put("flag", Integer.toString(flags[i]) );
            aList.add(hm);
        }

        // Keys used in Hashmap
        String[] from = { "flag","txt","cur" };

        // Ids of views in listview_layout
        int[] to = { R.id.profile_pic,R.id.name};

        // Instantiating an adapter to store each items
        // R.layout.listview_layout defines the layout of each item
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), aList, R.layout.listview_layout, from, to);

        // Getting a reference to listview of main.xml layout file
        ListView listView = (ListView) v.findViewById(R.id.listView);

        // Setting the adapter to the listView
        listView.setAdapter(adapter);
        return v;
    }

    private void sendGetRequest() {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                //instantiates httpclient to make request
                HttpClient httpClient = new DefaultHttpClient();

                //url with the post data
                HttpGet httpGet = new HttpGet("http://nturant.me/users/friendSuggestion");
                httpGet.setHeader("accept", "application/json");

                try{
                    try{
                        HttpResponse httpResponse = httpClient.execute(httpGet);

                        //get HttpResponse content
                        InputStream inputStream = httpResponse.getEntity().getContent();

                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                        StringBuilder stringBuilder = new StringBuilder();

                        String bufferedStrChunk = null;

                        while((bufferedStrChunk = bufferedReader.readLine()) != null){
                            stringBuilder.append(bufferedStrChunk);
                        }

                        Log.i("Response: ", stringBuilder.toString());
                        Log.i("Status: ", "" + httpResponse.getStatusLine().getStatusCode());
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
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }
}
