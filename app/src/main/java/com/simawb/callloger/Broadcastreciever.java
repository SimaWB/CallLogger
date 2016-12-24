package com.simawb.callloger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class Broadcastreciever extends BroadcastReceiver {
    boolean isPhoneRinging = false;
    boolean isPhoneCalling = false;
    Context mContext;

    public Broadcastreciever() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        try {
            TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            isPhoneRinging = true;
            isPhoneCalling = true;

            PhoneStateListener callStateListener = new PhoneStateListener() {
                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    if (state == TelephonyManager.CALL_STATE_RINGING) {
                        isPhoneRinging = false;
                    }
                    if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                        isPhoneCalling = false;
                    }
                    if (state == TelephonyManager.CALL_STATE_IDLE) {
                        if (isPhoneRinging) {
                            if (isPhoneCalling) {
                                Intent myIntent = new Intent(mContext, callLoggerService.class);
                                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                mContext.stopService(myIntent);
                                Handler handler = new Handler();

                                handler.postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        Intent myIntent = new Intent(mContext, callLoggerService.class);
                                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
                                        myIntent.putExtra("recordMissedCall", prefs.getBoolean("pref_checkbox_missedCall", true));
                                        myIntent.putExtra("recordZeroLength", prefs.getBoolean("pref_checkbox_zeroLength", true));
                                        myIntent.putExtra("send_to_server", prefs.getBoolean("pref_checkbox_send2Server", false));
                                        mContext.startService(myIntent);
                                    }

                                }, 1000);

                                isPhoneCalling = false;
                                isPhoneRinging = false;
                            }
                        }
                    }
                }
            };
            telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        } catch (Exception e) {
            Log.e("Phone Receive Error", " " + e);
        }
    }

}
