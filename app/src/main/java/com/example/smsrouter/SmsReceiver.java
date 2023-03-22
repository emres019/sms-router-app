package com.example.smsrouter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.provider.Telephony;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.preferences_file_key), Context.MODE_PRIVATE);
            if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION) &&
                    prefs.getBoolean(context.getString(R.string.saved_enabled_key), false)) {
                // Get shared preferences data
                String sender = prefs.getString(context.getString(R.string.saved_sender_key), null);
                if (sender == null) {
                    return;
                }

                String receiver = prefs.getString(context.getString(R.string.saved_receiver_key), null);
                if (receiver == null) {
                    return;
                }

                String messagePattern = prefs.getString(context.getString(R.string.saved_pattern_key), null);
                if (messagePattern == null) {
                    return;
                }

                for (SmsMessage message : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    String strippedSender = PhoneNumberUtils.stripSeparators(sender);
                    String strippedMessageAddress = PhoneNumberUtils.stripSeparators(message.getOriginatingAddress());
                    boolean isMessageFromSender = (PhoneNumberUtils.isGlobalPhoneNumber(strippedMessageAddress)
                            ? strippedMessageAddress : message.getOriginatingAddress()).equals(
                            PhoneNumberUtils.isGlobalPhoneNumber(strippedSender) ? strippedSender : sender
                    );
                    if (isMessageFromSender && context.checkSelfPermission(android.Manifest.permission.SEND_SMS)
                            == PackageManager.PERMISSION_GRANTED) {
                        // Send SMS
                        List<String> matches = getMatches(message.getMessageBody(), messagePattern);
                        if (!matches.isEmpty()) {
                            sendSms(context.getString(R.string.auto_msg_text,
                                    String.join(", ", matches)), receiver);
                        }
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    /**
     * Sends SMS message
     * @param message  Message to send
     * @param receiver Receiver phone number / address
     * @see SmsManager
     */
    @RequiresPermission(android.Manifest.permission.SEND_SMS)
    private void sendSms(String message, String receiver) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(receiver, null, message, null, null);
    }

    /**
     * Gets matches from message
     *
     * @param message        Message to match
     * @param messagePattern Pattern to match
     * @return {@link ArrayList} of matches
     */
    @NonNull
    private List<String> getMatches(String message, String messagePattern) {
        Pattern pattern = Pattern.compile(messagePattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(message);

        List<String> matches = new ArrayList<>();
        while (matcher.find()) {
            matches.add(matcher.group());
        }

        return matches;
    }
}