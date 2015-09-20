package com.example.janof.groupmessage.database;

import android.app.Activity;

import com.example.janof.groupmessage.R;
import com.example.janof.groupmessage.database.models.City;
import com.example.janof.groupmessage.database.models.MessagePerson;
import com.example.janof.groupmessage.database.models.Person;
import com.example.janof.groupmessage.database.models.SentMessage;
import com.example.janof.groupmessage.utils.RealmUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import io.realm.Realm;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

/**
 * Created by janof on 25-Jul-15.
 */
public class DBBase {

    public static void loadFromStream(final InputStream inputStream, final Activity activity, final LoadFileInterface mCallback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                clearDatabase();

                Realm realm = Realm.getDefaultInstance();
                Workbook workbook = null;
                realm.beginTransaction();
                try {
                    WorkbookSettings settings = new WorkbookSettings();
                    settings.setEncoding("Cp1250");
                    workbook = Workbook.getWorkbook(inputStream, settings);
                    Sheet sheet = workbook.getSheet(0);
                    int rows = sheet.getRows();

                    mCallback.beforeStart(rows);

                    City city = realm.where(City.class).equalTo("name", sheet.getCell(2, 0).getContents()).findFirst();

                    for (int row = 0; row < rows; row++) {
                        String cityName = sheet.getCell(2, row).getContents();
                        cityName = remooveLeadinWhiteSpaces(cityName);
                        String personName = sheet.getCell(1, row).getContents();
                        personName = remooveLeadinWhiteSpaces(personName);
                        String phoneNr = sheet.getCell(0, row).getContents();
                        phoneNr = remooveLeadinWhiteSpaces(phoneNr);

                        if (city == null || !city.getName().matches(cityName)) {
                            city = realm.createObject(City.class);
                            city.setPrimaryKey(RealmUtils.generatePrimaryKey(city));
                            city.setName(cityName);
                        }

                        Person person = realm.createObject(Person.class);
                        person.setPrimaryKey(RealmUtils.generatePrimaryKey(person));
                        person.setName(personName);
                        person.setPhoneNr(phoneNr);
                        person.setCity(city);
                        city.getPersons().add(person);

                        mCallback.onStatusUpdate(row);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (BiffException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCallback.onFileTypeError(activity.getString(R.string.wrong_file));
                        }
                    });

                    return;
                } finally {
                    mCallback.onFinish();
                    realm.commitTransaction();
                    realm.close();
                    if (workbook != null) {
                        workbook.close();
                    }
                }
            }
        }).start();
    }

    public static void loadFromFile(final File file, final Activity activity, final LoadFileInterface mCallback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                clearDatabase();

                Realm realm = Realm.getDefaultInstance();
                Workbook workbook = null;
                realm.beginTransaction();
                try {
                    WorkbookSettings settings = new WorkbookSettings();
                    settings.setEncoding("Cp1250");
                    workbook = Workbook.getWorkbook(file, settings);
                    Sheet sheet = workbook.getSheet(0);
                    int rows = sheet.getRows();

                    mCallback.beforeStart(rows);

                    City city = realm.where(City.class).equalTo("name", sheet.getCell(2, 0).getContents()).findFirst();

                    for (int row = 0; row < rows; row++) {
                        String cityName = sheet.getCell(2, row).getContents();
                        cityName = remooveLeadinWhiteSpaces(cityName);
                        String personName = sheet.getCell(1, row).getContents();
                        personName = remooveLeadinWhiteSpaces(personName);
                        String phoneNr = sheet.getCell(0, row).getContents();
                        phoneNr = remooveLeadinWhiteSpaces(phoneNr);

                        if (city == null || !city.getName().matches(cityName)) {
                            city = realm.createObject(City.class);
                            city.setPrimaryKey(RealmUtils.generatePrimaryKey(city));
                            city.setName(cityName);
                        }

                        Person person = realm.createObject(Person.class);
                        person.setPrimaryKey(RealmUtils.generatePrimaryKey(person));
                        person.setName(personName);
                        person.setPhoneNr(phoneNr);
                        person.setCity(city);
                        city.getPersons().add(person);

                        mCallback.onStatusUpdate(row);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (BiffException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCallback.onFileTypeError(activity.getString(R.string.wrong_file));
                        }
                    });

                    return;
                } finally {
                    mCallback.onFinish();
                    realm.commitTransaction();
                    realm.close();
                    if (workbook != null) {
                        workbook.close();
                    }
                }
            }
        }).start();

    }

    private static void clearDatabase() {

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        realm.where(City.class).findAll().clear();
        realm.where(MessagePerson.class).findAll().clear();
        realm.where(Person.class).findAll().clear();
        realm.where(SentMessage.class).findAll().clear();

        realm.commitTransaction();
        realm.close();

    }

    public static String remooveLeadinWhiteSpaces(String stringToFormat) {
        while (((int) stringToFormat.charAt(0)) == 160 || stringToFormat.startsWith(" ")) {
            stringToFormat = stringToFormat.substring(1);
        }

        while (((int) stringToFormat.charAt(stringToFormat.length() - 1)) == 160 || stringToFormat.endsWith(" ")) {
            stringToFormat = stringToFormat.substring(0, stringToFormat.length() - 1);
        }

        return stringToFormat;
    }

    public interface LoadFileInterface {

        void beforeStart(int rows);

        void onStatusUpdate(int process);

        void onFinish();

        void onFileTypeError(String message);
    }

}
