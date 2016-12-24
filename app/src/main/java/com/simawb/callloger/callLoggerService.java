package com.simawb.callloger;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;
import java.text.SimpleDateFormat;

import java.util.Date;

public class callLoggerService extends Service {
    boolean isServiceCall = false;

    private void AddToDB(Intent intent, CallRecord CR){
        boolean recordMissedCall = intent.getBooleanExtra("recordMissedCall", true);
        if (!recordMissedCall && CR.getCallType() == CallLog.Calls.MISSED_TYPE)
            return;

        boolean recordZeroLength = intent.getBooleanExtra("recordZeroLength", true);
        if (!recordZeroLength && CR.getCallDuration() == 0 && CR.getCallType() != CallLog.Calls.MISSED_TYPE)
            return;

        int send_to_server = intent.getBooleanExtra("send_to_server", false) ? 1 : 0;
        CR.setSend2Server(send_to_server);

        DBHelper dbHelper = new DBHelper(this);
        dbHelper.insertCallRecord(CR);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isServiceCall = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isServiceCall) {
            int hasPerm = this.getPackageManager().checkPermission("android.permission.READ_CALL_LOG",  this.getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                Cursor cursor = this.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        String CallNumber = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                        String CallName = getContactName(CallNumber);
                        int CallType = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
                        int CallDuration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
                        long CallDate = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String dateString = formatter.format(new Date(CallDate));

                        CallRecord lastCall = new CallRecord(dateString, CallNumber, CallName, CallDuration, CallType, 1);//şimdilik default olarak send_to_server=1 yapılıyor
                        Log.d("LastCall: ", lastCall.toString());

                        AddToDB(intent, lastCall);
                    }
                    cursor.close();
                }
            }
            isServiceCall = false;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private final String getContactName(String phoneNumber) {
        String contactName = null;
        if (phoneNumber != null && phoneNumber.length() > 0) {
            ContentResolver contentResolver = this.getContentResolver();

            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

            String[] projection = new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME };

            Cursor cursor = contentResolver.query(uri, projection, null, null, null);

            if (cursor != null) {
                if (cursor.moveToFirst()){
                    contactName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME));
                }
                cursor.close();
            }
        }
        return contactName;
    }

}
