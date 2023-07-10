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
import android.telephony.SubscriptionManager;

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
            SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.prefs_file_path), Context.MODE_PRIVATE);
            if (!intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
                    || !sharedPreferences.getBoolean(context.getString(R.string.prefs_key_app_enabled), false)) {
                return;
            }

            String savedSender = sharedPreferences.getString(context.getString(R.string.prefs_key_sender), null);
            if (savedSender == null) {
                return;
            }

            String receiver = sharedPreferences.getString(context.getString(R.string.prefs_key_receiver), null);
            if (receiver == null) {
                return;
            }

            String messagePattern = sharedPreferences.getString(context.getString(R.string.prefs_key_pattern), null);
            if (messagePattern == null) {
                return;
            }

            for (SmsMessage message : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {

                String messageSender = message.getOriginatingAddress();
                String plainSavedSender = PhoneNumberUtils.stripSeparators(savedSender);
                String plainMessageSender = PhoneNumberUtils.stripSeparators(messageSender);
                boolean isSavedSenderPhoneNumber = PhoneNumberUtils.isGlobalPhoneNumber(plainSavedSender);
                boolean isMessageSenderPhoneNumber = PhoneNumberUtils.isGlobalPhoneNumber(plainMessageSender);

                boolean isMessageFromSender;
                if (isMessageSenderPhoneNumber && isSavedSenderPhoneNumber) {
                    isMessageFromSender = plainSavedSender.equals(plainMessageSender);
                } else {
                    isMessageFromSender = savedSender.equals(messageSender);
                }

                if (isMessageFromSender && context.checkSelfPermission(android.Manifest.permission.SEND_SMS)
                        == PackageManager.PERMISSION_GRANTED) {
                    List<String> matches = getMatches(message.getMessageBody(), messagePattern);
                    if (!matches.isEmpty()) {
                        sendSms(context.getString(R.string.auto_msg_text,
                                String.join(", ", matches)), receiver);
                    }
                }
            }
        } catch (Exception ignored) {

        }
    }

    /**
     * @see SmsManager
     */
    @RequiresPermission(android.Manifest.permission.SEND_SMS)
    private void sendSms(String message, String to) {

        int smsSubscriptionId = SmsManager.getDefaultSmsSubscriptionId();
        if (smsSubscriptionId == SubscriptionManager.INVALID_SUBSCRIPTION_ID) {
            return;
        }

        SmsManager smsManager = SmsManager.getSmsManagerForSubscriptionId(smsSubscriptionId);
        smsManager.sendTextMessage(to, null, message, null, null);
    }

    /**
     * Gets matches that fit the pattern in the message
     *
     * @return {@link ArrayList} of matches
     */
    @NonNull
    private List<String> getMatches(String message, String pattern) {

        Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(message);

        List<String> matches = new ArrayList<>();
        while (matcher.find()) {
            matches.add(matcher.group());
        }

        return matches;
    }
}