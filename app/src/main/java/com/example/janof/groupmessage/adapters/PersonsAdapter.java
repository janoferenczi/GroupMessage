package com.example.janof.groupmessage.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.example.janof.groupmessage.database.models.Person;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

/**
 * Created by janof on 25-Jul-15.
 */
public class PersonsAdapter extends RealmBaseAdapter<Person> {


    public PersonsAdapter(Context context, RealmResults<Person> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Person person = realmResults.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_list_item_multiple_choice, parent, false);
        }
        ((CheckedTextView) convertView).setText(person.getName() + " (" + person.getPhoneNr() + ")");
        ((CheckedTextView) convertView).setChecked(person.isChecked());
        return convertView;
    }
}
