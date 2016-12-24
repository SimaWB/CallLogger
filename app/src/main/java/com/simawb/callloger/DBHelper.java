package com.simawb.callloger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by VELI on 14.8.2016.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME   = "calloggerDB";
    private static final String TABLE_RECORDS = "records";
    private static final String[] COLUMNS = {"id","call_date","call_number","call_name","call_duration","call_type","send_to_server"};
    private static DBHelper ins;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public static DBHelper getInstance(Context context){
        if (ins == null) {
            ins = new DBHelper(context);
        }
        return ins;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        ins = this;
        String sql = "CREATE TABLE " + TABLE_RECORDS + "(id INTEGER PRIMARY KEY,call_date TEXT,call_number TEXT,call_name TEXT,call_duration INTEGER,call_type INTEGER,send_to_server INTEGER " + ")";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDS);
        onCreate(sqLiteDatabase);
    }

    public void insertCallRecord(CallRecord aRecord) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("call_date", aRecord.getCallDate());
        values.put("call_number", aRecord.getCallNumber());
        values.put("call_name", aRecord.getCallName());
        values.put("call_duration", aRecord.getCallDuration());
        values.put("call_type", aRecord.getCallType());
        values.put("send_to_server", aRecord.getSend2Server());//Kayıt yapıldığı anda "sunucuya gönder" özelliği aktif mi değil mi?

        db.insert(TABLE_RECORDS, null, values);
        db.close();
    }

    public List<CallRecord> getAllRecords() {
        List<CallRecord> records = new ArrayList<CallRecord>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RECORDS, COLUMNS, null, null, null, null, "call_date DESC");
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

                records.add(aRecord);
            }
        } finally {
            cursor.close();
        }

        return records;
    }

    public Cursor getNotSentRecords() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                    TABLE_RECORDS,
                    COLUMNS,
                    "send_to_server=?",
                    new String[]{String.valueOf(1)},
                    null, null,
                    "call_date ASC");
    }

    /**
     * Herhangi bir kayıt bilgisi sunucuya gönderildiğinde onun DBdeki send_to_server değeri
     * 1'den 0'a çekilmeli ki bir sonraki sorguda aynı veri tekrar sunucuya gönderilmesin.
     * @param id
     */
    public void setSend2Server(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("send_to_server", 0);
        db.update(TABLE_RECORDS, cv, "id=?", new String[]{String.valueOf(id)});
    }

}
