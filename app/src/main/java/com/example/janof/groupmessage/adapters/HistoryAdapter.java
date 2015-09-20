package com.example.janof.groupmessage.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.janof.groupmessage.database.models.SentMessage;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

/**
 * Created by janof on 25-Jul-15.
 */
public class HistoryAdapter extends RealmBaseAdapter<SentMessage> {

    public HistoryAdapter(Context context, RealmResults<SentMessage> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.two_line_list_item, parent, false);
        }

        ((TextView) convertView.findViewById(android.R.id.text1)).setText(realmResults.get(position).getMessage());
        ((TextView) convertView.findViewById(android.R.id.text1)).setTextColor(Color.BLACK);
        ((TextView) convertView.findViewById(android.R.id.text1)).setSingleLine();
        ((TextView) convertView.findViewById(android.R.id.text2)).setText(realmResults.get(position).getCreationDate().toString());

        return convertView;
    }
}
