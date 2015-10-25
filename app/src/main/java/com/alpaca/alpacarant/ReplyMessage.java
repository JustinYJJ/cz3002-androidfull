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
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReplyMessage extends ActionBarActivity {
    private HashMap<String, String> hashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_message);

        Intent intent = getIntent();
        hashMap = (HashMap<String, String>)intent.getSerializableExtra("hashmap");
        String msgid = hashMap.get("messageid");

        System.out.println("Entering view message request");
        sendViewMessageRequest(msgid);
        System.out.println("Exiting view message request");

        TextView username = (TextView) findViewById(R.id.replyUser);
        username.setText(hashMap.get("sendername") + ":");

        TextView message = (TextView) findViewById(R.id.replyContent);
        message.setText(hashMap.get("content"));
    }

    public void onCancelReplyButtonClick(View v){
        finish();
    }

    public void onSendReplyButtonClick(View v){
        EditText reply = (EditText) findViewById(R.id.editReply);
        sendReplyRequest(hashMap.get("sender"), reply.getText().toString(), v);
    }

    private void sendReplyRequest(String sender, String s, final View v) {
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

                try {
                    //convert value to UrlEncodedFormEntity
                    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);

                    //hands the entity to the request
                    httpPost.setEntity(urlEncodedFormEntity);

                    try {
                        HttpResponse httpResponse = httpClient.execute(httpPost, LocalContext.httpContext);

                        //get HttpResponse content
                        InputStream inputStream = httpResponse.getEntity().getContent();

                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                        StringBuilder stringBuilder = new StringBuilder();

                        String bufferedStrChunk = null;

                        while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
                            stringBuilder.append(bufferedStrChunk);
                        }

                        System.out.println(httpResponse.getStatusLine().getStatusCode());
                        if (httpResponse.getStatusLine().getStatusCode() == 200) {
                            System.out.println("Message sent");
                        }

                        if (httpResponse.getEntity() != null) {
                            Log.i("Entity: ", "Not null");
                            httpResponse.getEntity().consumeContent();
                        }

                        return stringBuilder.toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
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
        sendPostReqAsyncTask.execute(sender, s);
    }

    private void sendViewMessageRequest(String msgid) {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                String paramMsgId = params[0];

                //instantiates httpclient to make request
                DefaultHttpClient httpClient = new DefaultHttpClient();

                //url with the post data
                HttpPost httpPost = new HttpPost("http://nturant.me/users/readmessage");
                httpPost.setHeader("accept", "application/json");

                //create values to be passed into POST request
                BasicNameValuePair messageIdBasicNameValuePair = new BasicNameValuePair("msgid", paramMsgId);

                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(messageIdBasicNameValuePair);

                try {
                    //convert value to UrlEncodedFormEntity
                    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);

                    //hands the entity to the request
                    httpPost.setEntity(urlEncodedFormEntity);

                    try {
                        HttpResponse httpResponse = httpClient.execute(httpPost, LocalContext.httpContext);

                        //get HttpResponse content
                        InputStream inputStream = httpResponse.getEntity().getContent();

                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                        StringBuilder stringBuilder = new StringBuilder();

                        String bufferedStrChunk = null;

                        while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
                            stringBuilder.append(bufferedStrChunk);
                        }

                        System.out.println(httpResponse.getStatusLine().getStatusCode());
                        System.out.println("Status code: " + httpResponse.getStatusLine().getStatusCode());
                        if (httpResponse.getStatusLine().getStatusCode() == 200) {
                            System.out.println("Message viewed");
                            System.out.println(stringBuilder.toString());
                        }

                        if (httpResponse.getEntity() != null) {
                            httpResponse.getEntity().consumeContent();
                        }

                        return stringBuilder.toString();
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
        sendPostReqAsyncTask.execute(msgid);
    }
}
