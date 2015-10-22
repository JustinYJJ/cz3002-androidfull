package com.alpaca.alpacarant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by justinyeo on 15/10/15.
 */
public class ProfileListAdapter extends ArrayAdapter<HashMap<String, String>> {
    customButtonListener customListner;

    public interface customButtonListener {
        public void onButtonClickListner(int position, HashMap<String, String> value, View v);
    }

    public void setCustomButtonListner(customButtonListener listener) {
        this.customListner = listener;
    }

    private Context context;
    private ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

    public ProfileListAdapter(Context context, ArrayList<HashMap<String, String>> dataItem) {
        super(context, R.layout.profile_listview_layout, dataItem);
        this.data = dataItem;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.profile_listview_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.profileUser = (TextView) convertView.findViewById(R.id.profileUser);
            viewHolder.profileContent = (TextView) convertView.findViewById(R.id.profileContent);
            viewHolder.buttonEdit = (Button) convertView.findViewById(R.id.buttonEdit);
            viewHolder.buttonRemove = (Button) convertView.findViewById(R.id.buttonRemove);
            convertView.setTag(viewHolder);
        }

        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final HashMap<String, String> temp = getItem(position);

        viewHolder.profileUser.setText(temp.get("ownername") + ": ");
        viewHolder.profileContent.setText(temp.get("content"));
        viewHolder.buttonEdit.setTag("Edit");
        viewHolder.buttonRemove.setTag("Remove");
        viewHolder.buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customListner != null) {
                    customListner.onButtonClickListner(position, temp, v);
                }
            }
        });
        viewHolder.buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customListner != null) {
                    customListner.onButtonClickListner(position, temp, v);
                }
            }
        });

        return convertView;
    }

    public class ViewHolder {
        TextView profileUser;
        TextView profileContent;
        Button buttonEdit;
        Button buttonRemove;
    }
}
