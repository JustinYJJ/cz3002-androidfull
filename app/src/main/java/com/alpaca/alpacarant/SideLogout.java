package com.alpaca.alpacarant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by justinyeo on 2/10/15.
 */
public class SideLogout extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.side_logout, container, false);

        startActivity(new Intent(v.getContext(), Login.class));
        LocalContext.httpContext = null;

        return v;
    }

}
