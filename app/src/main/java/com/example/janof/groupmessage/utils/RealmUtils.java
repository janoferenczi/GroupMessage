package com.example.janof.groupmessage.utils;

import java.util.Calendar;

/**
 * Created by janof on 25-Jul-15.
 */
public class RealmUtils {

    public static String generatePrimaryKey(Object objects) {
        Calendar calendar = Calendar.getInstance();
        String key = objects.hashCode() + "-" + calendar.getTimeInMillis();
        return key;
    }
}
