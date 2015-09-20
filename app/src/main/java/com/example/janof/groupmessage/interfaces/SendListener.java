package com.example.janof.groupmessage.interfaces;

import android.content.BroadcastReceiver;
import android.content.Intent;

/**
 * Created by janof on 26-Jul-15.
 */
public interface SendListener {

    void onSendFinished(int response, Intent intent, BroadcastReceiver sendPendingIntent);

    void onDelivered(int response, Intent intent, BroadcastReceiver deliveredPendingIntent);

}
