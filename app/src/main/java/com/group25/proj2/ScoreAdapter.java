package com.group25.proj2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by kevinwong on 2017-04-03.
 */

public class ScoreAdapter extends ArrayAdapter<ScoreObject> {
    private Context mContext;

    /**
     * Declare the elements of how the Pin should look
     */
    public static class ViewHolder {
        TextView score;
        TextView date;
    }


    public ScoreAdapter(Context context, ArrayList<ScoreObject> scores) {
        super(context, 0, scores);
        mContext = context;
    }

    /**
     * Logic for adding text to the title, subTitle and description fields of the PinMessage
     *
     * @param position
     * @param convertView
     * @param parent
     * @return the View
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the PinMessage that is being created
        ScoreObject score = getItem(position);
        // Create the viewHolder that stores the Pins
        ViewHolder viewHolder;
        // if there is nothing on PinBoard, add a new viewHolder
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_score, parent, false);

            viewHolder.score = (TextView) convertView.findViewById(R.id.item_score);
            viewHolder.date = (TextView) convertView.findViewById(R.id.item_date);

            convertView.setTag(viewHolder);
        } else {
            // If convertView is not null, simply update
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate data into template view using data object
        viewHolder.score.setText("Score: " + Integer.toString(score.getScore()));
        viewHolder.date.setText(score.getDate());

        return convertView;
    }
}
