package com.alpaca.alpacarant;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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

public class MessageFriend extends ActionBarActivity {
    private HashMap<String, String> hashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_friend);

        Intent intent = getIntent();
        hashMap = (HashMap<String, String>)intent.getSerializableExtra("hashmap");

        TextView username = (TextView) findViewById(R.id.MessageUser);
        username.setText("To " + hashMap.get("name") + ":");
    }

    public void onCancelMessageButtonClick(View v){
        finish();
    }

    public void onSendMessageButtonClick(View v){
        EditText message = (EditText) findViewById(R.id.editMessage);
        String message1 = message.getText().toString();

        sendMessageRequest(message1, hashMap.get("username"), v);
    }

    private void sendMessageRequest(String message1, String username, final View v) {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String paramMessage = params[0];
                String paramUsername= params[1];

                //instantiates httpclient to make request
                DefaultHttpClient httpClient = new DefaultHttpClient();

                //url with the post data
                HttpPost httpPost = new HttpPost("http://nturant.me/users/message");
                httpPost.setHeader("accept", "application/json");

                //create values to be passed into POST request
                BasicNameValuePair receiverBasicNameValuePair = new BasicNameValuePair("receiver", paramUsername);
                BasicNameValuePair messageBasicNameValuePair = new BasicNameValuePair("message", paramMessage);

                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(receiverBasicNameValuePair);
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
                Toast.makeText(v.getContext(), "Message has been sent", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(v.getContext(), MainPage.class));
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(message1, username);
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
}
