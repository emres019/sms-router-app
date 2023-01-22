package com.example.smsreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = SmsReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.preferences_file_key), Context.MODE_PRIVATE);
        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION) &&
                prefs.getBoolean(context.getString(R.string.saved_enabled_key), false)) {
            // Get the SMS attributes
            Bundle bundle = intent.getExtras();
            String format = bundle.getString("format");

            byte[][] pdus = (byte[][]) bundle.get("pdus");
            if (pdus != null) {
                // Log sms receive
                Log.i(LOG_TAG, "Got " + pdus.length + " SMS");

                // Get shared preferences data
                String sender = prefs.getString(context.getString(R.string.saved_sender_key), null);
                if (sender == null) {
                    Log.e(LOG_TAG, "Sender is null");
                    return;
                }

                String receiver = prefs.getString(context.getString(R.string.saved_receiver_key), null);
                if (receiver == null) {
                    Log.e(LOG_TAG, "Receiver is null");
                    return;
                }

                String messagePattern = prefs.getString(context.getString(R.string.saved_pattern_key), null);
                if (messagePattern == null) {
                    Log.e(LOG_TAG, "Pattern is null");
                    return;
                }

                for (byte[] bytes : pdus) {
                    SmsMessage message = SmsMessage.createFromPdu(bytes, format);
                    if (message.getOriginatingAddress().equals(sender) &&
                            context.checkSelfPermission(android.Manifest.permission.SEND_SMS)
                                    == PackageManager.PERMISSION_GRANTED) {

                        // Send SMS
                        List<String> matches = getMatches(message.getMessageBody(), messagePattern);
                        sendSms(context.getString(R.string.auto_msg_text)
                                + String.join(", ", matches), receiver);
                    }
                }
            }
        }
    }

    /***
     * Sends SMS message
     * @param message Message to send
     * @param receiver Receiver phone number / address
     */
    @RequiresPermission(android.Manifest.permission.SEND_SMS)
    private void sendSms(String message, String receiver) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(receiver, null, message, null, null);
    }

    /***
     * Gets matches from message
     * @param message Message to match
     * @param messagePattern Pattern to match
     * @return LinkedList of matches
     */
    @NonNull
    private List<String> getMatches(String message, String messagePattern) {
        Pattern pattern = Pattern.compile(messagePattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(message);

        LinkedList<String> matches = new LinkedList<>();
        while (matcher.find()) {
            matches.add(matcher.group());
        }

        return matches;
    }
}