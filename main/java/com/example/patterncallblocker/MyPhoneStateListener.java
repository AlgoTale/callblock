package com.example.patterncallblocker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class MyPhoneStateListener extends PhoneStateListener {

    public Context context;

    public MyPhoneStateListener(Context context) {
        super();
        this.context = context;
    }

    @SuppressLint("MissingPermission")
    public void onCallStateChanged(int state, String incomingNumber) {

        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                incomingNumber = incomingNumber.substring(1);
                for(Pattern pattern: AppDatabase.getDatabase(context).patternDao().getAll()) {
                    if (java.util.regex.Pattern.matches(pattern.regexPattern, incomingNumber)) {
                        try {
                            TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                telecomManager.endCall();
                                Toast.makeText(context, "Call Declined - " + incomingNumber, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
                break;
        }
    }
}
