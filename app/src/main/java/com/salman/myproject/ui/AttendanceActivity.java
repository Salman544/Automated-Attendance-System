package com.salman.myproject.ui;

import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.salman.myproject.R;
import com.salman.myproject.adapter.AttendanceSummaryAdapter;
import com.salman.myproject.realm_pojo.Attendance;
import com.salman.myproject.realm_pojo.RealmStudent;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class AttendanceActivity extends AppCompatActivity implements AttendanceSummaryAdapter.checkBoxClick {

    private Realm mRealm;
    private RecyclerView mRecyclerView;
    private AttendanceSummaryAdapter mAdapter;
    private ArrayList<RealmStudent> mList;
    private String date,course;
    private boolean mSafe = false;
    private MenuItem mSafeModeItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        Toolbar toolbar = findViewById(R.id.attendance_summary_toolbar);
        setSupportActionBar(toolbar);
        mRecyclerView = findViewById(R.id.summary_recycler_view);
        mRealm = Realm.getDefaultInstance();
        mList = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null) {
            date = bundle.getString("date");
            course = bundle.getString("course");
        }


        ActionBar bar = getSupportActionBar();
        if(bar!=null)
        {
            bar.setDisplayShowHomeEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setTitle(course);
        }

        setRecyclerView();

    }

    private void setRecyclerView() {

        RealmResults<RealmStudent> results = mRealm.where(RealmStudent.class).equalTo("courseName",course).findAll();
        mList.addAll(results);

        mAdapter = new AttendanceSummaryAdapter(this,mList);
        mAdapter.setClick(this);
        mAdapter.setDate(date);
        mAdapter.setEnabled(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.attendance_menu,menu);

        mSafeModeItem = menu.findItem(R.id.safe_mode);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home)
            onBackPressed();
        else if(id == R.id.safe_mode)
        {
            if(mSafe)
            {
                Toast.makeText(getApplicationContext(),"Safe Mode is on",Toast.LENGTH_LONG).show();
                mSafe = false;
                mAdapter.setEnabled(false);
                mAdapter.notifyDataSetChanged();
                mSafeModeItem.setTitle("Safe Mode is on");
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(AttendanceActivity.this);
                builder.setTitle("Message");
                builder.setMessage("Are you sure you want to turn off the safe mode");
                builder.setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mSafeModeItem.setTitle("Safe Mode is off");
                        mAdapter.setEnabled(true);
                        mAdapter.notifyDataSetChanged();
                    }
                });

                builder.show();
                mSafe = true;
            }

        }




        return super.onOptionsItemSelected(item);

    }

    private void setRealm(final int i, final boolean p, final boolean a, final boolean l, String faceId)
    {

        final Attendance attendance = mRealm.where(Attendance.class).equalTo("course",course)
                .equalTo("date",date).equalTo("faceId",faceId).findFirst();

        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                if(attendance!=null)
                {
                    attendance.setPresent(p);
                    attendance.setLeave(l);
                    attendance.setAbsent(a);
                    mAdapter.notifyItemChanged(i);
                }

            }
        });

    }

    @Override
    public void presentClick(int p) {

        RealmStudent student = mList.get(p);
        setRealm(p,true,false,false,student.getFaceId());



    }

    @Override
    public void absentClick(int p) {

        RealmStudent student = mList.get(p);
        setRealm(p,false,true,false,student.getFaceId());

    }

    @Override
    public void leaveClick(int p) {

        RealmStudent student = mList.get(p);
        setRealm(p,false,false,true,student.getFaceId());

    }
}
