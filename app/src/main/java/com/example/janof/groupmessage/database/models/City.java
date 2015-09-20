package com.example.janof.groupmessage.database.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by janof on 25-Jul-15.
 */
public class City extends RealmObject {

    @PrimaryKey
    private String primaryKey;

    private String name;
    private RealmList<Person> persons;
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

    public RealmList<Person> getPersons() {
        return persons;
    }

    public void setPersons(RealmList<Person> persons) {
        this.persons = persons;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}


