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
 * Created by justinyeo on 16/9/15.
 */
public class TabInbox extends Fragment implements InboxListAdapter.customButtonListener {
    private ArrayList<HashMap<String, String>> mylist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.tab_inbox, container, false);

        setGetMessageRequest(v);
        return v;
    }

    /**
     * Method to send a GET request to get all messages
     * @param v View of current context
     */
    private void setGetMessageRequest(final View v) {
        class SendPostReqAsyncTask extends AsyncTask<String, String, JSONArray> {

            @Override
            protected JSONArray doInBackground(String... params) {

                //instantiates httpclient to make request
                HttpClient httpClient = new DefaultHttpClient();

                //url with the post data
                HttpGet httpGet = new HttpGet("http://nturant.me/users/message");
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
                        map.put("sender", name.getString("sender"));
                        map.put("sendername", name.getString("sendername"));
                        map.put("content", name.getString("content"));
                        if (name.getString("content").length() >= 5) {
                            map.put("contentPartial", name.getString("content").substring(0, 5) + "...");
                        }
                        else {
                            map.put("contentPartial", "...");
                        }
                        map.put("messageid", name.getString("_id"));
                        mylist.add(map);
                    }

                    // Instantiating an adapter to store each items
                    // R.layout.rant_listview_layout defines the layout of each item
                    InboxListAdapter adapter = new InboxListAdapter(v.getContext(), mylist);
                    adapter.setCustomButtonListner(TabInbox.this);

                    // Getting a reference to listview of main.xml layout file
                    ListView listView = (ListView) v.findViewById(R.id.listViewInbox);

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

    /**
     * Method for button listener
     * @param position  Attribute for position in list view
     * @param value     Attribute for user data
     * @param v         View of current context
     */
    @Override
    public void onButtonClickListner(int position, HashMap<String, String> value, View v) {
        Intent intent = new Intent(v.getContext(), ReplyMessage.class);
        intent.putExtra("hashmap", value);
        startActivity(intent);
    }


}
