package com.example.janof.groupmessage.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.example.janof.groupmessage.database.models.City;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

/**
 * Created by janof on 25-Jul-15.
 */
public class CityAdapter extends RealmBaseAdapter<City> {

    public CityAdapter(Context context, RealmResults<City> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_list_item_multiple_choice, parent, false);
        }
        ((CheckedTextView) convertView).setText(realmResults.get(position).getName());
        ((CheckedTextView) convertView).setChecked(realmResults.get(position).isChecked());


        return convertView;
    }
}
