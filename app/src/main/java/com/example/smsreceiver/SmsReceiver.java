package com.example.smsreceiver;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;

public class SmsReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = SmsReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            // Get the SMS message
            Bundle bundle = intent.getExtras();
            SmsMessage[] messages;
            StringBuilder strMessage = new StringBuilder();
            String format = bundle.getString("format");

            // Retrieve the SMS message received
            byte[][] pdus = (byte[][]) bundle.get("pdus");
            if (pdus != null) {
                // Log sms receive
                Log.i(LOG_TAG, "Got " + pdus.length + " SMS");

                // Fill the array
                messages = new SmsMessage[pdus.length];
                for (int i = 0; i < messages.length; i++) {
                    // TODO: Check SMS was came from the sender
                    messages[i] = SmsMessage.createFromPdu(pdus[i], format);

                    // Build the message to show.
                    // TODO: Remove strMessage
                    strMessage.append("SMS from ").append(messages[i].getOriginatingAddress())
                            .append(": ").append(messages[i].getMessageBody()).append("\n");
                }

                Log.i(LOG_TAG, "SMS: " + strMessage.toString());
            }
        }
    }
}