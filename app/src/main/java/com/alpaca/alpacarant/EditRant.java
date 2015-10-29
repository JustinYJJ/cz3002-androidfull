package com.alpaca.alpacarant;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
import java.util.HashMap;
import java.util.List;

public class EditRant extends ActionBarActivity {
    /**
     * Hashmap for storing rant data attributes
     */
    private HashMap<String, String> hashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_rant);

        Intent intent = getIntent();
        hashMap = (HashMap<String, String>)intent.getSerializableExtra("hashmap");

        TextView username = (TextView) findViewById(R.id.editProfileUser);
        username.setText(hashMap.get("ownername") + ":");

        EditText message = (EditText) findViewById(R.id.editProfileContent);
        message.setText(hashMap.get("content"));
    }

    /**
     * Method for cancel button
     * @param v View of current context
     */
    public void onCancelEditButtonClick(View v){
        finish();
    }

    /**
     * Method for save button
     * @param v View of current context
     */
    public void onSaveButtonClick(View v){
        String rantid = hashMap.get("rantid");

        EditText message = (EditText) findViewById(R.id.editProfileContent);
        String editedRant = message.getText().toString();

        String viewtime = hashMap.get("viewtime");
        String lifetime = hashMap.get("lifetime");

        String anonymous;
        if (hashMap.get("ownername").equals("Anonymous")) {
            anonymous = "true";
        }
        else {
            anonymous = null;
        }

        sendEditRantRequest(rantid, editedRant, viewtime, lifetime, anonymous, v);
    }

    /**
     * Method to send a POST request to edit rant
     * @param rantid        Rant ID attribute
     * @param editedRant    Edited rant
     * @param viewtime      New view time
     * @param lifetime      New life time
     * @param annonymous    True if anonymous, else false
     * @param v             View of current context
     */
    private void sendEditRantRequest(final String rantid, String editedRant, String viewtime, String lifetime, String annonymous, final View v) {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String paramRantId = params[0];
                String paramEditedRant= params[1];
                String paramViewTime = params[2];
                String paramLifeTime = params[3];
                String paramAnnonymous = params[4];

                //instantiates httpclient to make request
                DefaultHttpClient httpClient = new DefaultHttpClient();

                //url with the post data
                HttpPost httpPost = new HttpPost("http://nturant.me/rant/" + rantid);
                httpPost.setHeader("accept", "application/json");

                //create values to be passed into POST request
                BasicNameValuePair lifetimeBasicNameValuePair = new BasicNameValuePair("lifetime", paramLifeTime);
                BasicNameValuePair viewtimeBasicNameValuePair = new BasicNameValuePair("viewtime", paramViewTime);
                BasicNameValuePair annonymousBasicNameValuePair = new BasicNameValuePair("anonymous", paramAnnonymous);
                BasicNameValuePair contentBasicNameValuePair = new BasicNameValuePair("content", paramEditedRant);
                BasicNameValuePair rantidBasicNameValuePair = new BasicNameValuePair("rant-id", paramRantId);

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
                            System.out.println("Rant Updated");
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
                startActivity(new Intent(v.getContext(), MainPage.class));
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(rantid, editedRant, viewtime, lifetime, annonymous);
    }
}
