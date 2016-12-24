package com.simawb.callloger;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.support.v7.widget.Toolbar;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private JobScheduler mJobScheduler;
    private static int JOB_PERIOD = 15 * 60 * 1000; //15 dakika

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mJobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo.Builder builder = new JobInfo.Builder(1, new ComponentName(getPackageName(), JobSchedulerService.class.getName()));
        builder.setPeriodic(JOB_PERIOD);
        if (mJobScheduler.schedule( builder.build() ) <= 0) {
            //
        }

        DBHelper dbHelper = DBHelper.getInstance(this);

        List<CallRecord> records = dbHelper.getAllRecords();
        MyListAdapter myListAdapter = new MyListAdapter(this, records);

        ListView LV = (ListView)findViewById(R.id.listViewRecords);
        LV.setAdapter(myListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), PrefsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
