package com.example.janof.groupmessage.activityes;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by janof on 25-Jul-15.
 */
public class RealmActivity extends ActionBarActivity {

    protected Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .name("telefonkonyv.realm").build();
        realm = Realm.getInstance(config);
        realm.setDefaultConfiguration(config);
    }

    @Override
    protected void onDestroy() {
        realm.close();
        super.onDestroy();
    }
}
