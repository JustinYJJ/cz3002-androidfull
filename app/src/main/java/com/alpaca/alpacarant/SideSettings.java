package com.alpaca.alpacarant;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
 * Created by justinyeo on 17/9/15.
 */
public class SideSettings extends Fragment {
    private ArrayList<HashMap<String, String>> mylist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.side_settings, container, false);

        getCurrentUser(v);
        return v;
    }

    private void getCurrentUser(final View v) {
        class SendPostReqAsyncTask extends AsyncTask<String, String, JSONObject> {

            @Override
            protected JSONObject doInBackground(String... params) {

                //instantiates httpclient to make request
                HttpClient httpClient = new DefaultHttpClient();

                //url with the post data
                HttpGet httpGet = new HttpGet("http://nturant.me/users/");
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

                        JSONObject jObject = new JSONObject(result);

                        Log.i("Response: ", stringBuilder.toString());
                        Log.i("Status: ", "" + httpResponse.getStatusLine().getStatusCode());

                        return jObject;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject result) {
                super.onPostExecute(result);

                mylist = new ArrayList<HashMap<String, String>>();
                try {

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("displayname", result.getString("displayname"));
                    mylist.add(map);

                    EditText editProfileName = (EditText) v.findViewById(R.id.editProfileName);
                    editProfileName.setText(mylist.get(0).get("displayname"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }
}
