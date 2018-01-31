package com.salman.myproject.ui;

import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.salman.myproject.R;
import com.salman.myproject.adapter.StudentRequestAdapter;
import com.salman.myproject.firebase_pojo.AddStudent;
import com.salman.myproject.firebase_pojo.TeacherAndClassInfo;

import java.util.ArrayList;

public class StudentRequestActivity extends AppCompatActivity implements StudentRequestAdapter.clickAdapter {

    private FirebaseUser mUser;
    private DatabaseReference mReference;
    private ArrayList<AddStudent> mList;
    private ArrayList<String> mKeyList;
    private StudentRequestAdapter mAdapter;
    private String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_request);

        Toolbar toolbar = findViewById(R.id.student_request_toolbar);
        RecyclerView recyclerView = findViewById(R.id.student_request_rec);
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        if(bar!=null)
        {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayShowHomeEnabled(true);
        }

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mReference = FirebaseDatabase.getInstance().getReference();
        mList = new ArrayList<>();
        mKeyList = new ArrayList<>();

        mAdapter = new StudentRequestAdapter(mList,this);
        mAdapter.setClickAdapter(this);

        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null)
        {
            name = bundle.getString("name");
        }




        getStudents();
    }

    private void getStudents()
    {
        mReference.child("PendingEnrollment").child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot d:dataSnapshot.getChildren())
                    {
                        AddStudent student = d.getValue(AddStudent.class);
                        if(student!=null)
                        {
                            mList.add(student);
                            mKeyList.add(d.getKey());
                            mAdapter.notifyItemInserted(mList.size() - 1);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void setBottomUserLayout(final AddStudent student, final String key, final int p)
    {
        String s = "Class Name: "+student.getCourseName();
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.bottom_approve);


        View approve = dialog.findViewById(R.id.approve_view);
        View reject = dialog.findViewById(R.id.reject_view);
        TextView textView = dialog.findViewById(R.id.className);
        assert textView != null;
        textView.setText(s);

        assert approve != null;
        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mReference.child("Students").child(mUser.getUid()).push().setValue(student);
                mReference.child("PendingEnrollment").child(mUser.getUid()).child(key).removeValue();
                TeacherAndClassInfo info = new TeacherAndClassInfo(name,mUser.getUid(),student.getCourseName());
                mReference.child("StudentEnrolledClasses").child(student.getStudentUid())
                        .push().setValue(info);
                mList.remove(p);
                mAdapter.notifyItemRemoved(p);
            }
        });

        assert reject != null;
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mReference.child("PendingEnrollment").child(mUser.getUid()).child(key).removeValue();
                mAdapter.notifyItemRemoved(p);
            }
        });

        dialog.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
            onBackPressed();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setRecClick(int p) {

        setBottomUserLayout(mList.get(p),mKeyList.get(p),p);

    }

    @Override
    public void setRecLongClick(int p) {

    }
}
