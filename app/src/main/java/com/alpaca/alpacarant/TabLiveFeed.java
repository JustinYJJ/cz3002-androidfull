package com.alpaca.alpacarant;


        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;

        import org.apache.http.HttpResponse;
        import org.apache.http.NameValuePair;
        import org.apache.http.client.ClientProtocolException;
        import org.apache.http.client.HttpClient;
        import org.apache.http.client.entity.UrlEncodedFormEntity;
        import org.apache.http.client.methods.HttpPost;
        import org.apache.http.impl.client.DefaultHttpClient;
        import org.apache.http.message.BasicNameValuePair;

        import java.io.IOException;
        import java.io.UnsupportedEncodingException;
        import java.util.ArrayList;
        import java.util.List;

/**
 * Created by justinyeo on 16/9/15.
 */
public class TabLiveFeed extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.tab_live_feed, container, false);


        return v;
    }
}
