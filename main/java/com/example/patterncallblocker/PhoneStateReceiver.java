package com.example.patterncallblocker;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.lang.reflect.Method;

public class PhoneStateReceiver extends BroadcastReceiver {

    TelephonyManager telephony;

    @Override
    public void onReceive(Context context, Intent intent) {
        MyPhoneStateListener phoneListener = new MyPhoneStateListener(context);
        telephony = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
    }
}
