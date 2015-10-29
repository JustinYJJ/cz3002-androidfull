package com.alpaca.alpacarant;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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
 * Created by justinyeo on 16/9/15.
 */
public class TabLiveFeed extends Fragment implements RantListAdapter.customButtonListener {
	private ArrayList<HashMap<String, String>> mylist;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.tab_live_feed, container, false);
		setGetRantRequest(v);
		return v;
	}

	/**
	 * Method to send a GET request for all rants
	 * @param v
	 */
	private void setGetRantRequest(final View v) {
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
						mylist.add(map);
					}

					// Instantiating an adapter to store each items
					// R.layout.rant_listview_layout defines the layout of each item
					RantListAdapter adapter = new RantListAdapter(v.getContext(), mylist);
					adapter.setCustomButtonListner(TabLiveFeed.this);

					// Getting a reference to listview of main.xml layout file
					ListView listView = (ListView) v.findViewById(R.id.listViewRant);

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
	 * @param position	Attribute for position in list view
	 * @param value		Attribute for user data
	 * @param v			View of current context
	 */
	@Override
	public void onButtonClickListner(int position, HashMap<String, String> value, View v) {
		sendViewRantRequest(value, v, position);
		ListView rant = (ListView) getActivity().findViewById(R.id.listViewRant);
		View view = rant.getChildAt(position - rant.getFirstVisiblePosition());

		TextView textRant = (TextView) view.findViewById(R.id.rantContent);
		textRant.setText(value.get("content"));

		Button button = (Button) view.findViewById(R.id.buttonReadRant);
		button.setVisibility(View.GONE);
	}

	/**
	 * Method to send a POST request to view rant
	 * @param value		Attribute for rant data
	 * @param v			View of current context
	 * @param position	Position of rant in list view
	 */
	private void sendViewRantRequest(final HashMap<String, String> value, final View v, final int position) {
		class SendPostReqAsyncTask extends AsyncTask<HashMap<String, String>, Void, String>{

			@Override
			protected String doInBackground(HashMap<String, String>... params) {
				HashMap<String, String> paramRantId = params[0];

				//instantiates httpclient to make request
				DefaultHttpClient httpClient = new DefaultHttpClient();

				//url with the post data
				HttpPost httpPost = new HttpPost("http://nturant.me/rant/viewRant");
				httpPost.setHeader("accept", "application/json");

				//create values to be passed into POST request
				BasicNameValuePair rantIdBasicNameValuePair = new BasicNameValuePair("id", paramRantId.get("rantid"));

				List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
				nameValuePairList.add(rantIdBasicNameValuePair);

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
							System.out.println("Rant viewed");
							System.out.println(stringBuilder.toString());
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
				super.onPostExecute(result);
			}
		}

		SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
		sendPostReqAsyncTask.execute(value);
	}
}
