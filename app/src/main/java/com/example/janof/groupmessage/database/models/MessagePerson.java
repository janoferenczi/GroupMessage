package com.example.janof.groupmessage.database.models;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by janof on 25-Jul-15.
 */
public class MessagePerson extends RealmObject {

    @PrimaryKey
    private String primaryKey;

    private Person person;
    private SentMessage sentMessage;
    private Date sendDate;
    private Date deliveryDate;

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public SentMessage getSentMessage() {
        return sentMessage;
    }

    public void setSentMessage(SentMessage sentMessage) {
        this.sentMessage = sentMessage;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
}
