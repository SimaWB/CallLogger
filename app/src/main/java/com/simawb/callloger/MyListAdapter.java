package com.simawb.callloger;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.provider.CallLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

/**
 * Created by VELI on 14.8.2016.
 */
public class MyListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<CallRecord> recordList;

    public MyListAdapter(Activity activity, List<CallRecord> records) {
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        recordList = records;
    }

    @Override
    public int getCount() {
        return recordList.size();
    }

    @Override
    public Object getItem(int i) {
        return recordList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View vi = view;
        if (view == null)
            vi = inflater.inflate(R.layout.rowlayout, viewGroup, false); // create layout from

        TextView textViewName = (TextView)vi.findViewById(R.id.name);
        TextView textViewNumber = (TextView)vi.findViewById(R.id.number);
        TextView textViewDate = (TextView)vi.findViewById(R.id.date);
        TextView textViewDuration = (TextView)vi.findViewById(R.id.duration);
        ImageView imageViewCalltype = (ImageView)vi.findViewById(R.id.calltype);

        CallRecord CR = recordList.get(i);
        if (CR != null) {
            textViewName.setText(CR.getCallName());
            textViewNumber.setText(CR.getCallNumber());
            textViewDate.setText(CR.getCallDate());
            int Duration = CR.getCallDuration();
            textViewDuration.setText(String.format("%02d:%02d:%02d", Duration / 3600, (Duration % 3600) / 60, (Duration % 60)));

            int color = Color.GREEN, resId=0;

            switch (CR.getCallType()) {
                case CallLog.Calls.MISSED_TYPE:
                case CallLog.Calls.REJECTED_TYPE:
                    color = Color.RED;
                    resId = R.drawable.ic_call_missed;
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    resId = R.drawable.ic_call_received;
                    break;
                case CallLog.Calls.OUTGOING_TYPE:
                    if (Duration==0) {
                        color = Color.RED;
                        resId = R.drawable.ic_call_missed_outgoing;
                    } else {
                        resId = R.drawable.ic_call_made;
                    }
                    break;
                default:
            }

            imageViewCalltype.setColorFilter(color);
            imageViewCalltype.setImageResource(resId);

        }

        return vi;
    }
}
