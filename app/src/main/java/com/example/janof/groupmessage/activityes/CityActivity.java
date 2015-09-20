package com.example.janof.groupmessage.activityes;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.janof.groupmessage.R;
import com.example.janof.groupmessage.adapters.CityAdapter;
import com.example.janof.groupmessage.database.models.City;
import com.example.janof.groupmessage.utils.Keys;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;


public class CityActivity extends RealmActivity implements View.OnClickListener {

    private RealmResults<City> cities;
    private MenuItem nextMenuItem;
    private String messageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        findViewById(R.id.city_all).setOnClickListener(this);
        findViewById(R.id.city_none).setOnClickListener(this);

        messageText = getIntent().getStringExtra(Keys.MESSAGE_TEXT);

        ListView listView = (ListView) findViewById(R.id.city_list);
        cities = Realm.getDefaultInstance().where(City.class).findAll();

        RealmList<City> cityRealmList = new RealmList<>();
        cityRealmList.addAll(cities);
        realm.beginTransaction();
        for (City city : cityRealmList) {
            city.setChecked(false);
        }
        realm.commitTransaction();

        listView.setAdapter(new CityAdapter(this, cities, true));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                realm.beginTransaction();
                cities.get(position).setChecked(!cities.get(position).isChecked());
                realm.commitTransaction();
                setMenuItem();
            }
        });

    }

    private void setMenuItem() {
        nextMenuItem.setEnabled(isAnyCityChecked(cities));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_city, menu);
        nextMenuItem = menu.findItem(R.id.city_next);
        setMenuItem();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.city_next) {
            Intent personIntent = new Intent(this, PersonsActivity.class);
            personIntent.putExtra(Keys.IS_FROM_CITIES, true);
            personIntent.putExtra(Keys.MESSAGE_TEXT, messageText);
            startActivity(personIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isAnyCityChecked(RealmResults<City> cities) {
        RealmList<City> cityRealmList = new RealmList<>();
        cityRealmList.addAll(cities);
        for (City city : cityRealmList) {
            if (city.isChecked()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void finish() {
        RealmList<City> cityRealmList = new RealmList<>();
        cityRealmList.addAll(cities);
        realm.beginTransaction();
        for (City city : cityRealmList) {
            city.setChecked(false);
        }
        realm.commitTransaction();
        super.finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.city_all: {
                RealmList<City> cityRealmList = new RealmList<>();
                cityRealmList.addAll(cities);
                realm.beginTransaction();
                for (City city : cityRealmList) {
                    city.setChecked(true);
                }
                realm.commitTransaction();
                nextMenuItem.setEnabled(true);
                break;
            }

            case R.id.city_none: {
                RealmList<City> cityRealmList = new RealmList<>();
                cityRealmList.addAll(cities);
                realm.beginTransaction();
                for (City city : cityRealmList) {
                    city.setChecked(false);
                }
                realm.commitTransaction();
                nextMenuItem.setEnabled(false);
                break;
            }
        }
    }
}
