package com.salman.myproject.ui;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.salman.myproject.R;
import com.salman.myproject.adapter.StudentAttendanceSummaryAdapter;
import com.salman.myproject.firebase_pojo.FirebaseAttendance;

import java.util.ArrayList;
import java.util.Iterator;

public class StudentAttendanceActivity extends AppCompatActivity {

    private String courseName;
    private DatabaseReference mReference;
    private FirebaseUser mUser;
    private ArrayList<FirebaseAttendance> mAttendanceList;
    private StudentAttendanceSummaryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_attendance);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mReference = FirebaseDatabase.getInstance().getReference();
        mAttendanceList = new ArrayList<>();
        mAdapter = new StudentAttendanceSummaryAdapter(this,mAttendanceList);

        RecyclerView recyclerView = findViewById(R.id.attedence_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
        Toolbar toolbar = findViewById(R.id.attendance_summary_toolbar);
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();
        if(bar!=null)
        {
            bar.setDisplayShowHomeEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle bundle = getIntent().getExtras();

        if(bundle!=null)
        {
            courseName = bundle.getString("course");
        }


        getAttendance();
    }

    private void getAttendance()
    {
        mReference.child("StudentsAttendance").child(mUser.getUid())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        FirebaseAttendance attendance = dataSnapshot.getValue(FirebaseAttendance.class);
                        if(attendance!=null) {
                            if(attendance.getCourse().equals(courseName)) {
                                mAttendanceList.add(attendance);
                                mAdapter.notifyItemInserted(mAttendanceList.size() - 1);
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        FirebaseAttendance attendance = dataSnapshot.getValue(FirebaseAttendance.class);
                        int i = 0;
                        if(attendance!=null)
                        {
                            Iterator<FirebaseAttendance> iterator = mAttendanceList.iterator();
                            while (iterator.hasNext())
                            {
                                FirebaseAttendance a = iterator.next();
                                if((a.getCourse().equals(courseName))&&a.getDate().equals(attendance.getDate()))
                                {
                                    iterator.remove();
                                    mAdapter.notifyItemRemoved(i);
                                    break;
                                }
                                i+=1;
                            }
                            Log.d("MainActivity", "onChildChanged: "+String.valueOf(i));
                            mAttendanceList.add(i,attendance);
                            Log.d("MainActivity", "onChildChanged: "+mAttendanceList);
                            mAdapter.notifyItemInserted(i);
                        }

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                        FirebaseAttendance attendance = dataSnapshot.getValue(FirebaseAttendance.class);
                        int i = 0;
                        if(attendance!=null)
                        {
                            Iterator<FirebaseAttendance> iterator = mAttendanceList.iterator();
                            while (iterator.hasNext())
                            {
                                FirebaseAttendance a = iterator.next();
                                if(a.getCourse().equals(courseName))
                                {
                                    iterator.remove();
                                    break;
                                }
                                i+=1;
                            }
                            mAdapter.notifyItemRemoved(i);
                        }

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
            onBackPressed();

        return super.onOptionsItemSelected(item);
    }
}
