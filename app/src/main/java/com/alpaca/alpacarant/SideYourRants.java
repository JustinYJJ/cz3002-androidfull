package com.alpaca.alpacarant;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by justinyeo on 17/9/15.
 */
public class SideYourRants extends Fragment implements YourRantsListAdapter.customButtonListener{
    private ArrayList<HashMap<String, String>> mylist;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.side_your_rants, container, false);

        getAllRantsRequest(view);
        return view;
    }

    private void getAllRantsRequest(final View v) {
        class SendPostReqAsyncTask extends AsyncTask<String, String, JSONArray> {

            @Override
            protected JSONArray doInBackground(String... params) {

                //instantiates httpclient to make request
                HttpClient httpClient = new DefaultHttpClient();

                //url with the post data
                HttpGet httpGet = new HttpGet("http://nturant.me/rant/");
                httpGet.setHeader("accept", "application/json");

                try {
                    try {
                        HttpResponse httpResponse = httpClient.execute(httpGet, LocalContext.httpContext);

                        //get HttpResponse content
                        InputStream inputStream = httpResponse.getEntity().getContent();

                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                        StringBuilder stringBuilder = new StringBuilder();

                        String bufferedStrChunk = null;

                        while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
                            stringBuilder.append(bufferedStrChunk);
                        }
                        String result = stringBuilder.toString();

                        JSONArray jArray = new JSONArray(result);

                        Log.i("Response: ", stringBuilder.toString());
                        Log.i("Status: ", "" + httpResponse.getStatusLine().getStatusCode());

                        return jArray;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONArray result) {
                super.onPostExecute(result);

                mylist = new ArrayList<HashMap<String, String>>();
                try {

                    for (int i = 0; i < result.length(); i++) {
                        HashMap<String, String> map = new HashMap<String, String>();
                        JSONObject name = result.getJSONObject(i);
                        map.put("ownername", name.getString("ownername"));
                        map.put("content", name.getString("content"));
                        if (name.getString("content").length() >= 5) {
                            map.put("contentPartial", name.getString("content").substring(0, 5) + "...");
                        }
                        else {
                            map.put("contentPartial", "...");
                        }
                        map.put("rantid", name.getString("_id"));
                        map.put("viewtime", name.getString("viewtime"));
                        map.put("lifetime", name.getString("lifetime"));
                        mylist.add(map);
                    }

                    // Instantiating an adapter to store each items
                    // R.layout.rant_listview_layout defines the layout of each item
                    YourRantsListAdapter adapter = new YourRantsListAdapter(v.getContext(), mylist);
                    adapter.setCustomButtonListner(SideYourRants.this);

                    // Getting a reference to listview of main.xml layout file
                    ListView listView = (ListView) v.findViewById(R.id.listViewProfile);

                    // Setting the adapter to the listView
                    if (adapter != null) {
                        listView.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }

    @Override
    public void onButtonClickListner(int position, HashMap<String, String> value, View v) {
        if (v.getTag().toString().equals("Edit")){
            Intent intent = new Intent(v.getContext(), EditRant.class);
            intent.putExtra("hashmap", value);
            startActivity(intent);
        }
        else if (v.getTag().toString().equals("Remove")){
            sendDeleteRantRequest(value.get("rantid"));
            getAllRantsRequest(view);
        }
    }

    private void sendDeleteRantRequest(final String rantid) {
        class SendPostReqAsyncTask extends AsyncTask<String, String, String> {

            @Override
            protected String doInBackground(String... params) {

                //instantiates httpclient to make request
                HttpClient httpClient = new DefaultHttpClient();

                //url with the post data
                HttpDelete httpDelete = new HttpDelete("http://nturant.me/rant/" + rantid);
                httpDelete.setHeader("accept", "application/json");

                try {
                    try {
                        HttpResponse httpResponse = httpClient.execute(httpDelete, LocalContext.httpContext);

                        //get HttpResponse content
                        InputStream inputStream = httpResponse.getEntity().getContent();

                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                        StringBuilder stringBuilder = new StringBuilder();

                        String bufferedStrChunk = null;

                        while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
                            stringBuilder.append(bufferedStrChunk);
                        }
                        String result = stringBuilder.toString();

                        Log.i("Response: ", stringBuilder.toString());
                        Log.i("Status: ", "" + httpResponse.getStatusLine().getStatusCode());

                        return result;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(rantid);
    }
}
