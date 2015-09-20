package com.example.janof.groupmessage.activityes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.janof.groupmessage.R;
import com.example.janof.groupmessage.adapters.HistoryAdapter;
import com.example.janof.groupmessage.database.models.SentMessage;
import com.example.janof.groupmessage.utils.Keys;

import io.realm.RealmResults;

public class HistoryActivity extends RealmActivity {

    private RealmResults<SentMessage> messages;
    private boolean isLongTapped = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        messages = realm.where(SentMessage.class).findAllSorted("creationDate", false);
        ListView listView = (ListView) findViewById(R.id.history_list_view);
        listView.setAdapter(new HistoryAdapter(this, messages, true));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                if (!isLongTapped) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
                    builder.setMessage(messages.get(position).getMessage());
                    builder.setPositiveButton(R.string.forward, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            Intent mainIntent = new Intent(HistoryActivity.this, MainActivity.class);
                            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            mainIntent.putExtra(Keys.MESSAGE_KEY, messages.get(position).getPrimaryKey());
                            startActivity(mainIntent);

                        }
                    });

                    builder.setNeutralButton(R.string.persons, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton(R.string.cityes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                isLongTapped = true;
                AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
                builder.setMessage(R.string.delete_message);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        realm.beginTransaction();
                        messages.get(position).removeFromRealm();
                        realm.commitTransaction();
                        dialog.dismiss();
                        isLongTapped = false;
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        isLongTapped = false;
                    }
                });

                builder.create().show();
                return false;
            }
        });
    }

}
