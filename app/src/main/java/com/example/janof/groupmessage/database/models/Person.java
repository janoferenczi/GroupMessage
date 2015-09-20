package com.example.janof.groupmessage.database.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by janof on 25-Jul-15.
 */
public class Person extends RealmObject {

    @PrimaryKey
    private String primaryKey;

    private String name;
    private String phoneNr;
    private City city;
    private boolean checked;

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNr() {
        return phoneNr;
    }

    public void setPhoneNr(String phoneNr) {
        this.phoneNr = phoneNr;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
