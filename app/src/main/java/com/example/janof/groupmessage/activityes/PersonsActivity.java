package com.example.janof.groupmessage.activityes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.janof.groupmessage.R;
import com.example.janof.groupmessage.adapters.PersonsAdapter;
import com.example.janof.groupmessage.database.models.City;
import com.example.janof.groupmessage.database.models.Person;
import com.example.janof.groupmessage.database.models.SentMessage;
import com.example.janof.groupmessage.interfaces.SendListener;
import com.example.janof.groupmessage.utils.Keys;
import com.example.janof.groupmessage.utils.RealmUtils;

import java.util.ArrayList;
import java.util.Calendar;

import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by janof on 25-Jul-15.
 */
public class PersonsActivity extends RealmActivity implements View.OnClickListener, SendListener {


    private RealmResults<Person> persons;

    private MenuItem personSendButton;
    private SentMessage message;
    private String messageText;
    private int currentPersonIndex;
    private ArrayList<Person> personsChecked = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persons);
        findViewById(R.id.person_all).setOnClickListener(this);
        findViewById(R.id.person_none).setOnClickListener(this);

        messageText = getIntent().getStringExtra(Keys.MESSAGE_TEXT);

        RealmResults<City> citiesResult = realm.where(City.class).equalTo("checked", true).findAll();
        RealmList<City> cities = new RealmList<>();
        cities.addAll(citiesResult);
        RealmQuery query = realm.where(Person.class);
        if (getIntent().getBooleanExtra(Keys.IS_FROM_CITIES, false)) {
            for (int i = 0; i < cities.size(); i++) {
                query.equalTo("city.primaryKey", cities.get(i).getPrimaryKey());
                if (i <= cities.size() - 2) {
                    query.or();
                }
            }
        }
        persons = query.findAllSorted("name", true);
        RealmList<Person> personRealmList = new RealmList<>();
        personRealmList.addAll(persons);

        realm.beginTransaction();
        for (Person person : personRealmList) {
            person.setChecked(true);

        }
        realm.commitTransaction();

        ListView listView = (ListView) findViewById(R.id.person_list_view);
        listView.setAdapter(new PersonsAdapter(this, persons, true));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                realm.beginTransaction();
                persons.get(position).setChecked(!persons.get(position).isChecked());
                realm.commitTransaction();
                personSendButton.setEnabled(realm.where(Person.class).equalTo("checked", true).findAll().size() > 0);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_person, menu);
        personSendButton = menu.findItem(R.id.person_send);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.person_send) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(messageText);
            builder.setTitle(R.string.really);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    performSend();
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void performSend() {

        final ProgressDialog progressDialog = new ProgressDialog(PersonsActivity.this);
        progressDialog.setMessage(getString(R.string.sending_messages));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();

        RealmResults<City> citiesResult = realm.where(City.class).equalTo("checked", true).findAll();
        RealmList<City> cities = new RealmList<>();
        cities.addAll(citiesResult);
        RealmQuery query = realm.where(Person.class);
        if (getIntent().getBooleanExtra(Keys.IS_FROM_CITIES, false)) {
            for (int i = 0; i < cities.size(); i++) {
                query.equalTo("city.primaryKey", cities.get(i).getPrimaryKey());
                if (i <= cities.size() - 2) {
                    query.or();
                }
            }
        }

        RealmResults<Person> personsCheckedResult = query.equalTo("checked", true).findAllSorted("name", true);
        RealmList<Person> personsChekedRealmList = new RealmList<>();
        personsChekedRealmList.addAll(personsCheckedResult);

        personsChecked.clear();

        for (Person person : personsChekedRealmList) {
            Person p = new Person();
            p.setPrimaryKey(person.getPrimaryKey());
            p.setChecked(person.isChecked());
            p.setName(person.getName());
            p.setPhoneNr(person.getPhoneNr());
            City city = new City();
            city.setChecked(person.getCity().isChecked());
            city.setPrimaryKey(person.getCity().getPrimaryKey());
            city.setName(person.getCity().getName());
            p.setCity(city);

            personsChecked.add(p);
        }

        realm.beginTransaction();
        SentMessage message = realm.createObject(SentMessage.class);
        message.setPrimaryKey(RealmUtils.generatePrimaryKey(message));
        message.setMessage(messageText);
        message.setCreationDate(Calendar.getInstance().getTime());
        realm.commitTransaction();

        new Thread(new Runnable() {
            @Override
            public void run() {

                if (personsChecked.size() > 0) {
                    Person p = personsChecked.get(0);
                    sendSMS(messageText, p);
                }

                progressDialog.dismiss();

            }
        }).start();
    }

    @Override
    public void finish() {
        RealmList<Person> personRealmList = new RealmList<>();
        personRealmList.addAll(persons);
        realm.beginTransaction();
        for (Person person : personRealmList) {
            person.setChecked(false);
        }
        realm.commitTransaction();
        super.finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.person_all: {
                RealmList<Person> personRealmList = new RealmList<>();
                personRealmList.addAll(persons);
                realm.beginTransaction();
                for (Person city : personRealmList) {
                    city.setChecked(true);
                }
                realm.commitTransaction();
                personSendButton.setEnabled(true);
                break;
            }

            case R.id.person_none: {
                RealmList<Person> personRealmList = new RealmList<>();
                personRealmList.addAll(persons);
                realm.beginTransaction();
                for (Person city : personRealmList) {
                    city.setChecked(false);
                }
                realm.commitTransaction();
                personSendButton.setEnabled(false);
                break;
            }
        }
    }


    private void sendSMS(String message, Person person) {
        String SENT = person.getPrimaryKey();
        String DELIVERED = person.getPrimaryKey();

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        try {

            //---when the SMS has been sent---
            registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context arg0, Intent arg1) {
                    onSendFinished(getResultCode(), arg1, this);
                }
            }, new IntentFilter(SENT));

            //---when the SMS has been delivered---
            registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context arg0, Intent arg1) {
                    onDelivered(getResultCode(), arg1, this);
                }
            }, new IntentFilter(DELIVERED));

        } catch (Exception e) {

        }
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(person.getPhoneNr(), null, message, sentPI, deliveredPI);
    }

    @Override
    public void onSendFinished(int response, Intent intent, BroadcastReceiver sendPendingIntent) {
        String message = "";
        switch (response) {
            case Activity.RESULT_OK:
                message = "SMS sent";
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                message = "Generic failure";
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                message = "No service";
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                message = "Null PDU";
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                message = "Radio off";
                break;
        }
        unregisterReceiver(sendPendingIntent);
        if (++currentPersonIndex < personsChecked.size()) {
            Person person = personsChecked.get(currentPersonIndex);
            sendSMS(messageText, person);
        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.send_finished);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent mainIntent = new Intent(PersonsActivity.this, MainActivity.class);
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                }
            });
            builder.setCancelable(false);
            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    @Override
    public void onDelivered(int response, Intent intent, BroadcastReceiver deliveredPendingIntent) {
        switch (response) {
            case Activity.RESULT_OK:
                Toast.makeText(getBaseContext(), "SMS delivered",
                        Toast.LENGTH_SHORT).show();
                break;
            case Activity.RESULT_CANCELED:
                Toast.makeText(getBaseContext(), "SMS not delivered",
                        Toast.LENGTH_SHORT).show();
                break;
        }
        unregisterReceiver(deliveredPendingIntent);
    }
}
