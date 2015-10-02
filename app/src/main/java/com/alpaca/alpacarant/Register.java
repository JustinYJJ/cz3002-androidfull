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
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Register extends ActionBarActivity {
    EditText editEmail, editName, editPassword, editReEnterPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void onCancelButtonClick(View v){
        finish();
    }

    public void onRegisterButtonClick(View v){
        editEmail           = (EditText) findViewById(R.id.textEmail);
        editName            = (EditText) findViewById(R.id.textName);
        editPassword        = (EditText) findViewById(R.id.textPassword);
        editReEnterPassword = (EditText) findViewById(R.id.textReEnterPassword);

        String email            = editEmail.getText().toString();
        String name             = editName.getText().toString();
        String password         = editPassword.getText().toString();
        String reEnterPassword  = editReEnterPassword.getText().toString();

        if (email.isEmpty()){
            Toast.makeText(getApplicationContext(), "Email field cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (name.isEmpty()){
            Toast.makeText(getApplicationContext(), "Name field cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (password.isEmpty()){
            Toast.makeText(getApplicationContext(), "Password field cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (reEnterPassword.isEmpty()){
            Toast.makeText(getApplicationContext(), "Re-enter password field cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (!password.equals(reEnterPassword)){
            Toast.makeText(getApplicationContext(), "Password does not match", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            sentPostRequest(email, name, password);
        }
    }

    private void sentPostRequest(String email, String name, String password) {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                String paramEmail = params[0];
                String paramDisplayName = params[1];
                String paramPassword = params[2];

                //instantiates httpclient to make request
                HttpClient httpClient = new DefaultHttpClient();
                Log.i("HttpClient: ", "Beginning");

                //url with the post data
                HttpPost httpPost = new HttpPost("http://nturant.me/signup");
                httpPost.setHeader("accept", "application/json");
                Log.i("HttpPost: ", "Beginning");

                //create values to be passed into POST request
                BasicNameValuePair emailBasicNameValuePair = new BasicNameValuePair("email", paramEmail);
                BasicNameValuePair displayNameBasicNameValuePair = new BasicNameValuePair("name", paramDisplayName);
                BasicNameValuePair passwordBasicNameValuePair = new BasicNameValuePair("password", paramPassword);

                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(emailBasicNameValuePair);
                nameValuePairList.add(displayNameBasicNameValuePair);
                nameValuePairList.add(passwordBasicNameValuePair);
                Log.i("Namevaluepair: ", nameValuePairList.toString());

                try{
                    //convert value to UrlEncodedFormEntity
                    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);
                    Log.i("Urlencoded: ", urlEncodedFormEntity.toString());

                    //hands the entity to the request
                    httpPost.setEntity(urlEncodedFormEntity);

                    try{
                        HttpResponse httpResponse = httpClient.execute(httpPost);

                        //get HttpResponse content
                        InputStream inputStream = httpResponse.getEntity().getContent();

                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                        StringBuilder stringBuilder = new StringBuilder();

                        String bufferedStrChunk = null;

                        while((bufferedStrChunk = bufferedReader.readLine()) != null){
                            stringBuilder.append(bufferedStrChunk);
                        }

                        Log.i("HttpResponse: ", httpResponse.toString());
                        Log.i("Http Status: ", "" + httpResponse.getStatusLine().getStatusCode());

                        if (httpResponse.getStatusLine().getStatusCode() == 200){
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Account created successfully!", Toast.LENGTH_SHORT).show();
                                }
                            });
                            finish();
                        }
                        else if (httpResponse.getStatusLine().getStatusCode() == 400){
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Email is already used", Toast.LENGTH_SHORT).show();
                                }
                            });
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
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(email, name, password);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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
