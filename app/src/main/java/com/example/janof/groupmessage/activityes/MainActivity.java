package com.example.janof.groupmessage.activityes;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.janof.groupmessage.R;
import com.example.janof.groupmessage.database.DBBase;
import com.example.janof.groupmessage.database.models.SentMessage;
import com.example.janof.groupmessage.utils.Keys;
import com.example.janof.groupmessage.utils.RealmUtils;
import com.ipaulpro.afilechooser.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;


public class MainActivity extends RealmActivity implements View.OnClickListener, TextWatcher {

    public static final String MESSAGE_ID = "messageID";

    private static final int REQUEST_CHOOSER = 15;
    private ProgressDialog progressDialog;
    private EditText messageEditText;
    private MenuItem nextMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.file_chooser).setOnClickListener(this);
        findViewById(R.id.main_history).setOnClickListener(this);


        try {
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getResources().getString(R.string.data_loading));
            progressDialog.show();

            InputStream is = getAssets().open("telefonkonyv.xls");
            DBBase.loadFromStream(is, this, new DBBase.LoadFileInterface() {
                @Override
                public void onStatusUpdate(int process) {
                    progressDialog.setProgress(process);
                }

                @Override
                public void onFinish() {
                    progressDialog.dismiss();
                }

                @Override
                public void beforeStart(int rows) {
                    progressDialog.setMax(rows);
                }

                @Override
                public void onFileTypeError(String message) {
                    progressDialog.dismiss();
                    showAlertDialog(message);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }


        messageEditText = (EditText) findViewById(R.id.message_edit_text);
        messageEditText.addTextChangedListener(this);

        String messageKey = getIntent().getStringExtra(Keys.MESSAGE_KEY);
        if (messageKey != null) {
            SentMessage message = realm.where(SentMessage.class).equalTo("primaryKey", messageKey).findFirst();
            messageEditText.setText(message.getMessage());
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_CHOOSER:
                if (resultCode == RESULT_OK) {

                    final Uri uri = data.getData();

                    String path = FileUtils.getPath(this, uri);

                    if (path == null) {
                        showAlertDialog(getString(R.string.only_local_files));
                        return;
                    }

                    File file = new File(path);
                    progressDialog = new ProgressDialog(this);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage(getResources().getString(R.string.data_loading));
                    progressDialog.show();
                    DBBase.loadFromFile(file, this, new DBBase.LoadFileInterface() {
                        @Override
                        public void onStatusUpdate(int process) {
                            progressDialog.setProgress(process);
                        }

                        @Override
                        public void onFinish() {
                            progressDialog.dismiss();
                        }

                        @Override
                        public void beforeStart(int rows) {
                            progressDialog.setMax(rows);
                        }

                        @Override
                        public void onFileTypeError(String message) {
                            progressDialog.dismiss();
                            showAlertDialog(message);
                        }
                    });

                }
                break;
        }
    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        nextMenuItem = menu.findItem(R.id.main_next);
        nextMenuItem.setEnabled(messageEditText.getText().length() > 5);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.main_next) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.choose_send_methdo);
            builder.setPositiveButton(R.string.cityes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    moveToCityActivity();
                }
            });
            builder.setNegativeButton(R.string.persons, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    moveToPersonActivity();
                }
            });

            builder.create().show();

        }
        return super.onOptionsItemSelected(item);
    }

    private void moveToCityActivity() {
        Intent cityIntent = new Intent(MainActivity.this, CityActivity.class);
        cityIntent.putExtra(Keys.MESSAGE_TEXT, messageEditText.getText().toString());
        startActivity(cityIntent);
    }

    private void moveToPersonActivity() {
        Intent personIntent = new Intent(MainActivity.this, PersonsActivity.class);
        personIntent.putExtra(Keys.MESSAGE_TEXT, messageEditText.getText().toString());
        startActivity(personIntent);
    }

    private String saveMessage(EditText messageEditText) {
        SentMessage message = new SentMessage();
        message.setPrimaryKey(RealmUtils.generatePrimaryKey(message));
        message.setMessage(messageEditText.getText().toString());
        message.setCreationDate(Calendar.getInstance().getTime());

        realm.beginTransaction();
        realm.copyToRealm(message);
        realm.commitTransaction();

        return message.getPrimaryKey();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.file_chooser: {
                Intent getContentIntent = FileUtils.createGetContentIntent();
                Intent intent = Intent.createChooser(getContentIntent, "Select a file");
                startActivityForResult(intent, REQUEST_CHOOSER);
                break;
            }

            case R.id.main_history: {
                Intent historyIntent = new Intent(this, HistoryActivity.class);
                startActivity(historyIntent);
                break;
            }

        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (nextMenuItem != null) {
            nextMenuItem.setEnabled(s.length() > 5);
        }
    }
}
