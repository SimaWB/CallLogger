package com.simawb.callloger;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;

/**
 * Created by VELI on 14.8.2016.
 */
public class CallRecord implements Serializable {

    private int id;
    private String callDate;
    private String callNumber;
    private String callName;
    private int callDuration;
    private int callType;
    private int send2Server;

    public CallRecord() {
        super();
    }

    public CallRecord(String callDate, String callNumber, String callName, int callDuration, int callType, int send2Server) {
        super();
        this.callDate = callDate;
        this.callNumber = callNumber;
        this.callName = callName;
        this.callDuration = callDuration;
        this.callType = callType;
        this.send2Server = send2Server;
    }

    @Override
    public String toString() {
        return String.format("Date:%s, Number:%s, Name:%s, Duration:%d, Direction:%d", callDate, callNumber, callName, callDuration, callType);
    }

    public String toJSON() {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("call_date", callDate);
            jsonObj.put("call_number", callNumber);
            jsonObj.put("call_name", callName);
            jsonObj.put("call_duration", callDuration);
            jsonObj.put("call_type", callType);
            return jsonObj.toString();
        } catch (JSONException e) {
            Log.e("CallLogger", "Error while record to toJSON transformation");
            return "";
        }
    }

    public int getCallType() {
        return callType;
    }

    public void setCallType(int callType) {
        this.callType = callType;
    }

    public int getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(int callDuration) {
        this.callDuration = callDuration;
    }

    public String getCallName() {
        return callName;
    }

    public void setCallName(String callName) {
        this.callName = callName;
    }

    public String getCallNumber() {
        return callNumber;
    }

    public void setCallNumber(String callNumber) {
        this.callNumber = callNumber;
    }

    public String getCallDate() {
        return callDate;
    }

    public void setCallDate(String callDate) {
        this.callDate = callDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSend2Server() {
        return send2Server;
    }

    public void setSend2Server(int send2Server) {
        this.send2Server = send2Server;
    }

}
