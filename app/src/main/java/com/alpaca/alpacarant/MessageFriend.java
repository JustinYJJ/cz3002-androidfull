package com.alpaca.alpacarant;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class MessageFriend extends ActionBarActivity implements MessageFriendListAdapter.customButtonListener {
    int[] pictures = new int[]{
            R.drawable.ic_unknown_profile,
    };

    ArrayList<String> usernames = new ArrayList<String>();
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        message = getIntent().getStringExtra("Message");
        setContentView(R.layout.activity_message_friend);

        getFriendsRequest();
    }

    private void getFriendsRequest() {
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
                    MessageFriendListAdapter adapter1 = new MessageFriendListAdapter(getApplicationContext(), mylist);
                    adapter1.setCustomButtonListner(MessageFriend.this);

                    // Getting a reference to listview of main.xml layout file
                    ListView listView = (ListView) findViewById(R.id.listViewMessage);

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

    public void onSendMessageButtonClick(View v) {
        for (String user : usernames) {
            sendFriendMessageRequest(user, message);
        }
    }

    private void sendFriendMessageRequest(String user, String message) {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String paramUser = params[0];
                String paramMessage = params[1];

                //instantiates httpclient to make request
                DefaultHttpClient httpClient = new DefaultHttpClient();

                //url with the post data
                HttpPost httpPost = new HttpPost("http://nturant.me/users/message");
                httpPost.setHeader("accept", "application/json");

                //create values to be passed into POST request
                BasicNameValuePair userBasicNameValuePair = new BasicNameValuePair("receiver", paramUser);
                BasicNameValuePair messageBasicNameValuePair = new BasicNameValuePair("message", paramMessage);

                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(userBasicNameValuePair);
                nameValuePairList.add(messageBasicNameValuePair);

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
                            System.out.println("Message sent");
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
        sendPostReqAsyncTask.execute(user, message);
    }

    public void onCancelMessageButtonClick(View v) {
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_message_friend, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onButtonClickListner(int position, HashMap<String, String> value, View v) {
        usernames.add(value.get("username"));
    }
}
