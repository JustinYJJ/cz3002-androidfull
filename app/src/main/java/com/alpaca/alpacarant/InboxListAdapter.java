package com.alpaca.alpacarant;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by justinyeo on 2/10/15.
 */
public class InboxListAdapter extends ArrayAdapter<HashMap<String, String>>{
    customButtonListener customListner;

    public interface customButtonListener {
        public void onButtonClickListner(int position, HashMap<String, String> value, View v);
    }

    public void setCustomButtonListner(customButtonListener listener) {
        this.customListner = listener;
    }

    private Context context;
    private ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

    public InboxListAdapter(Context context, ArrayList<HashMap<String, String>> dataItem) {
        super(context, R.layout.inbox_listview_layout, dataItem);
        this.data = dataItem;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.inbox_listview_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.imageInboxUser = (ImageView) convertView.findViewById(R.id.inboxImageUser);
            viewHolder.inboxUser = (TextView) convertView.findViewById(R.id.inboxUser);
            viewHolder.inboxContent = (TextView) convertView.findViewById(R.id.inboxContent);
            viewHolder.buttonRead = (Button) convertView.findViewById(R.id.buttonReadInbox);
            convertView.setTag(viewHolder);
        }

        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final HashMap<String, String> temp = getItem(position);

        viewHolder.imageInboxUser.setImageResource(R.drawable.ic_unknown_profile);
        viewHolder.inboxUser.setText(temp.get("sendername") + " posted: ");
        viewHolder.inboxContent.setText(temp.get("contentPartial"));
        viewHolder.buttonRead.setOnClickListener(new View.OnClickListener() {
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
        ImageView imageInboxUser;
        TextView inboxUser;
        TextView inboxContent;
        Button buttonRead;
    }
}
