package com.simawb.callloger;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.os.Message;
import android.os.Handler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class JobSchedulerService extends JobService {

    private String ServerUrl;
    private String UserName;
    private String Password;

    private Handler mJobHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            new Thread(new Runnable() {

                @Override
                public void run() {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    boolean onlyWifi = prefs.getBoolean("pref_checkbox_wifi", false);
                    boolean sendTo = prefs.getBoolean("pref_checkbox_send2Server", false);

                    if (sendTo) {
                        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
                        if (isConnected) {
                            boolean connIsWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
                            if (connIsWiFi || (!onlyWifi) ) {
                                ServerUrl = prefs.getString("server_name", "");
                                UserName = prefs.getString("user_name", "");
                                Password = prefs.getString("password", "");
                                if (ServerUrl.isEmpty()) {
                                    Log.e("CallLogger", "Sunucu ismi boş!");
                                } else {
                                    sendRecords();
                                }
                            }
                        }
                    }
                }
            }).start();

            jobFinished((JobParameters)msg.obj, false);
            return true;
        }
    });

    private void sendRecords() {
        DBHelper dbHelper = DBHelper.getInstance(this);
        Cursor cursor = dbHelper.getNotSentRecords();
        try {
            while (cursor.moveToNext()) {
                CallRecord aRecord = new CallRecord();
                aRecord.setId(cursor.getInt(0));
                aRecord.setCallDate(cursor.getString(1));
                aRecord.setCallNumber(cursor.getString(2));
                aRecord.setCallName(cursor.getString(3));
                aRecord.setCallDuration(cursor.getInt(4));
                aRecord.setCallType(cursor.getInt(5));
                aRecord.setSend2Server(cursor.getInt(6));
                if (sendToServer(aRecord.toJSON())) {
                    dbHelper.setSend2Server(aRecord.getId());
                    Log.d("CallLogger", "Kayıt sunucuya gönderildi");
                } else {
                    Log.e("CallLogger", "Kayıt sunucuya gönderilemedi!");
                }
            }
        } finally {
            cursor.close();
        }
    }

    /**
     * data'yı sunucuya gönderir
     * @param data Post edilecek JSON formatındaki veri
     * @return Gönderim başarılı ise true değilse false
     */
    private boolean sendToServer(String data) {
        if (data == "") return false;
        try {
            URL url = new URL(ServerUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setRequestProperty("Authorization", userNamePasswordBase64());
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.connect();

            OutputStream outputStream = connection.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
            outputStreamWriter.write(data);
            outputStreamWriter.flush();
            outputStreamWriter.close();

            //Log.d("CallLogger", "sendToServer: " + connection.getResponseCode());
            return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
        }catch (IOException e) {
            Log.e("CallLogger", e.getMessage());
            return false;
        }
    }

    /**
     * Username ve Password'u kullanarak Http basic auth için header oluştur
     * @return
     */
    private String userNamePasswordBase64() {
        String authStr = UserName + ":" + Password;
        return "Basic "+ Base64.encodeToString(authStr.getBytes(), Base64.NO_WRAP);
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        mJobHandler.sendMessage( Message.obtain(mJobHandler, 1, jobParameters) );
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        mJobHandler.removeMessages(1);
        return false;
    }

}
