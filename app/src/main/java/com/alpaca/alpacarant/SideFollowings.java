package com.alpaca.alpacarant;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by justinyeo on 17/9/15.
 */
public class SideFollowings extends Fragment implements FriendListAdapter.customButtonListener, FollowingListAdapter.customButtonListener {
    int[] pictures = new int[]{
            R.drawable.ic_unknown_profile,
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.side_following, container, false);
        sendGetFollowersRequest(v);
        return v;
    }

    private void sendGetFollowersRequest(final View v) {
        class SendPostReqAsyncTask extends AsyncTask<String, String, JSONArray> {

            @Override
            protected JSONArray doInBackground(String... params) {

                //instantiates httpclient to make request
                HttpClient httpClient = new DefaultHttpClient();

                //url with the post data
                HttpGet httpGet = new HttpGet("http://nturant.me/users/followings");
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
                ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();

                try {

                    for (int i = 0; i < result.length(); i++) {
                        HashMap<String, String> map = new HashMap<String, String>();
                        JSONObject name = result.getJSONObject(i);
                        map.put("name", name.getString("displayname"));
                        map.put("picture", Integer.toString(pictures[0]));
                        map.put("username", name.getString("username"));
                        mylist.add(map);
                    }

                    // Instantiating an adapter to store each items
                    // R.layout.friend_listview_layout defines the layout of each item
                    FollowingListAdapter adapter1 = new FollowingListAdapter(v.getContext(), mylist);
                    adapter1.setCustomButtonListner(SideFollowings.this);

                    // Getting a reference to listview of main.xml layout file
                    ListView listView = (ListView) v.findViewById(R.id.listViewFollowings);

                    // Setting the adapter to the listView
                    listView.setAdapter(adapter1);
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
        Button button1 = (Button) v.findViewById(R.id.buttonUnfollow);
        Button button = (Button) v.findViewById(R.id.buttonMessageFriend);
        ListView followings = (ListView) getActivity().findViewById(R.id.listViewFollowings);
        View view = followings.getChildAt(position - followings.getFirstVisiblePosition());

        if (button1 != null) {
            if (button1.getText().toString().equals("Follow")) {
                sendFollowFriendRequest(value.get("username"), view);
            } else {
                sendUnfollowFriendRequest(value.get("username"), view);
            }
        }
        if (v.getTag().toString().equals("Message")){
            Intent intent = new Intent(v.getContext(), MessageFriend.class);
            intent.putExtra("hashmap", value);
            startActivity(intent);
        }
    }

    private void sendFollowFriendRequest(String name, final View v) {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String>{

            @Override
            protected String doInBackground(String... params) {

                String paramUsername = params[0];

                //instantiates httpclient to make request
                DefaultHttpClient httpClient = new DefaultHttpClient();

                //url with the post data
                HttpPost httpPost = new HttpPost("http://nturant.me/users/addFriend");
                httpPost.setHeader("accept", "application/json");

                //create values to be passed into POST request
                BasicNameValuePair usernameBasicNameValuePair = new BasicNameValuePair("username", paramUsername);

                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(usernameBasicNameValuePair);

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
                        }

                        if (httpResponse.getEntity() != null){
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
                Button button1 = (Button) v.findViewById(R.id.buttonUnfollow);
                Button message = (Button) v.findViewById(R.id.buttonMessageFriend);

                if (button1 != null) {
                    button1.setText("Unfollow");
                }
                message.setVisibility(View.VISIBLE);
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(name);
    }

    private void sendUnfollowFriendRequest(String username, final View v) {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String>{

            @Override
            protected String doInBackground(String... params) {

                String paramUsername = params[0];

                //instantiates httpclient to make request
                DefaultHttpClient httpClient = new DefaultHttpClient();

                //url with the post data
                HttpPost httpPost = new HttpPost("http://nturant.me/users/unfollow");
                httpPost.setHeader("accept", "application/json");

                //create values to be passed into POST request
                BasicNameValuePair usernameBasicNameValuePair = new BasicNameValuePair("username", paramUsername);

                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(usernameBasicNameValuePair);

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
                        }

                        if (httpResponse.getEntity() != null){
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
                Button button1 = (Button) v.findViewById(R.id.buttonUnfollow);
                Button message = (Button) v.findViewById(R.id.buttonMessageFriend);

                if (button1 != null) {
                    button1.setText("Follow");
                }

                message.setVisibility(View.GONE);
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(username);
    }
}
