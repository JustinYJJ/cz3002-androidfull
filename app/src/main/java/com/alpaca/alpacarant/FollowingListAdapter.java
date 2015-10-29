package com.alpaca.alpacarant;

import android.content.Context;
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
public class FollowingListAdapter extends ArrayAdapter<HashMap<String, String>> {
    customButtonListener customListner;

    /**
     * Interface for custom button listener
     */
    public interface customButtonListener {
        public void onButtonClickListner(int position, HashMap<String, String> value, View v);
    }

    /**
     * Method for custom button listener
     * @param listener Custom button listener
     */
    public void setCustomButtonListner(customButtonListener listener) {
        this.customListner = listener;
    }

    private Context context;
    private ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

    public FollowingListAdapter(Context context, ArrayList<HashMap<String, String>> dataItem) {
        super(context, R.layout.following_listview_layout, dataItem);
        this.data = dataItem;
        this.context = context;
    }

    /**
     * Method to set list view of following members
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.following_listview_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.profile_picFollowing);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.nameFollowing);
            viewHolder.buttonUnfollow = (Button) convertView.findViewById(R.id.buttonUnfollow);
            viewHolder.buttonMessage = (Button) convertView.findViewById(R.id.buttonMessageFriend);
            convertView.setTag(viewHolder);
        }

        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final HashMap<String, String> temp = getItem(position);

        viewHolder.imageView.setImageResource(R.drawable.ic_unknown_profile);
        viewHolder.textView.setText(temp.get("name"));
        viewHolder.buttonUnfollow.setTag("Unfollow");
        viewHolder.buttonMessage.setTag("Message");
        viewHolder.buttonUnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customListner != null) {
                    customListner.onButtonClickListner(position, temp, v);
                }
            }
        });
        viewHolder.buttonMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customListner != null){
                    customListner.onButtonClickListner(position, temp, v);
                }
            }
        });

        return convertView;
    }

    public class ViewHolder {
        ImageView imageView;
        TextView textView;
        Button buttonUnfollow;
        Button buttonMessage;
    }
}
