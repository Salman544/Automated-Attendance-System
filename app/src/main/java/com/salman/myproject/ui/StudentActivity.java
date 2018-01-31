package com.salman.myproject.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.salman.myproject.R;
import com.salman.myproject.adapter.StudentClassAdapter;
import com.salman.myproject.firebase_pojo.AddStudent;
import com.salman.myproject.firebase_pojo.EnrollmentKey;
import com.salman.myproject.firebase_pojo.TeacherAndClassInfo;
import com.salman.myproject.pojo.TeacherInfo;
import com.salman.myproject.rest_api.EnrollUser;
import com.salman.myproject.rest_api.EnrollUserPost;
import com.salman.myproject.rest_api.Errors;
import com.salman.myproject.rest_api.KairosApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StudentActivity extends AppCompatActivity implements StudentClassAdapter.clickCardView {

    private FirebaseUser mUser;
    private DatabaseReference mReference;
    private DrawerLayout mDrawerLayout;
    private String name,email,photoLink;
    private KairosApi mKairosApi;
    private ProgressDialog mProgressDialog;
    private ArrayList<TeacherInfo> mList;
    private StudentClassAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        Toolbar toolbar = findViewById(R.id.student_toolbar);
        setSupportActionBar(toolbar);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.kairos.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mKairosApi = retrofit.create(KairosApi.class);


        RecyclerView recyclerView = findViewById(R.id.student_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mList = new ArrayList<>();
        mAdapter = new StudentClassAdapter(this,mList);
        mAdapter.setCardView(this);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mReference = FirebaseDatabase.getInstance().getReference();
        mDrawerLayout = findViewById(R.id.student_drawer_layout);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Please Wait . . . . ");


        recyclerView.setAdapter(mAdapter);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,mDrawerLayout,toolbar,R.string.open,R.string.close
        );

        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        setNavigationView();
        enrolledClasses();

    }


    private void enrolledClasses()
    {
        mReference.child("StudentEnrolledClasses").child(mUser.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                TeacherAndClassInfo info = dataSnapshot.getValue(TeacherAndClassInfo.class);
                if(info!=null)
                {
                    TeacherInfo teacherInfo = new TeacherInfo(info.getTeacherName(),info.getTeacherUid(),info.getClassName());
                    mList.add(teacherInfo);
                    mAdapter.notifyItemInserted(mList.size()-1);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getFaceKey(final String className, final String id, final String rollNo)
    {
        EnrollUser user = new EnrollUser(photoLink,name,className+id);
        mKairosApi.enrollUser(user).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                mProgressDialog.dismiss();
                if (response.isSuccessful())
                {
                    Gson gson = new Gson();
                    String s = gson.toJson(response.body());
                    if (s.contains("Errors")) {
                        Errors errors = gson.fromJson(s, Errors.class);
                        String error = errors.getErrors().get(0).getMessage();

                        final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_INDEFINITE);

                        snackbar.setAction("Ok", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snackbar.dismiss();
                            }
                        });

                        snackbar.show();

                    }
                    else
                    {
                        String face_id="";
                        try
                        {
                            JSONObject object = new JSONObject(s);
                            face_id = object.getString("face_id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("MainActivity", face_id);
                        AddStudent student = new AddStudent(name,rollNo,photoLink,face_id,className);
                        student.setStudentUid(mUser.getUid());
                        mReference.child("PendingEnrollment").child(id).push().setValue(student);
                        Toast.makeText(getApplicationContext(),"Class Enrollment request send",Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setNavigationView()
    {
        NavigationView navView = findViewById(R.id.student_nav);
        View view = navView.getHeaderView(0);
        final TextView namet = view.findViewById(R.id.navName);
        final TextView emailt = view.findViewById(R.id.navEmail);
        final ImageView imageViewt = view.findViewById(R.id.navImage);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null)
        {
            name = bundle.getString("studentName");
            email = bundle.getString("studentEmail");
            photoLink = bundle.getString("studentLink");
            namet.setText(name);
            emailt.setText(email);

            Glide.with(this)
                    .load(photoLink)
                    .into(imageViewt);
        }

        Menu menu = navView.getMenu();
        MenuItem enroll = menu.findItem(R.id.enroll_class);
        MenuItem signout = menu.findItem(R.id.sign_out_student);

        enroll.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
                setAlertDialog();
                return true;
            }
        });

        signout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                signOutDialog();
                return true;
            }
        });

    }

    private void setAlertDialog()
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.student_custom_dialog);

        final EditText editText = dialog.findViewById(R.id.editText);
        final EditText editText1 = dialog.findViewById(R.id.editText1);
        final TextInputLayout layout = dialog.findViewById(R.id.textInput1);

        Button save,cancel;
        save = dialog.findViewById(R.id.saveBtn);
        cancel = dialog.findViewById(R.id.cancelBtn);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = editText.getText().toString();
                final String s1 = editText1.getText().toString();
                if(s.isEmpty() || s.trim().length() == 0)
                    Toast.makeText(getApplicationContext(),"Need Enrollment Key",Toast.LENGTH_SHORT).show();
                else if(s1.isEmpty() || s1.trim().length() == 0)
                    Toast.makeText(getApplicationContext(),"Need Student Id",Toast.LENGTH_SHORT).show();
                else
                {
                    mReference.child("PublicEnrollmentKey").orderByChild("key").equalTo(s).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists())
                            {
                                EnrollmentKey key = null;
                                for(DataSnapshot d:dataSnapshot.getChildren())
                                    key = d.getValue(EnrollmentKey.class);

                                showEnrollmentDialog(key,s1,dialog,layout);
                                Toast.makeText(getApplicationContext(),"class found",Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"class not found",Toast.LENGTH_SHORT).show();
                                Log.d("MainActivity", "Not Enrolled: "+dataSnapshot.getKey());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showEnrollmentDialog(final EnrollmentKey key, final String s1, final Dialog dialog, final TextInputLayout layout)
    {

        mReference.child("Students").child(key.getTeacherUid()).orderByChild("studentUid")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            boolean check = false;
                            String className="";
                            for(DataSnapshot d:dataSnapshot.getChildren())
                            {
                                AddStudent student = d.getValue(AddStudent.class);
                                for(TeacherInfo a:mList)
                                {
                                    if((a.getClassName().equals(student.getCourseName()))&& (student.getStudentUid().equals(mUser.getUid())))
                                    {
                                        check = true;
                                        className = a.getClassName();
                                        break;
                                    }
                                }
                            }


                            if(check)
                            {
                                String s = "You are already registered in "+className;
                                layout.setError(s);
                                layout.setErrorEnabled(true);
                            }
                            else
                            {
                                layout.setErrorEnabled(false);
                                showEnrollmentDialog1(key, s1, dialog,layout);
                            }

                        }
                        else
                            showEnrollmentDialog1(key, s1, dialog,layout);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    public void showEnrollmentDialog1(final EnrollmentKey key,final String s1,final Dialog dialog,final TextInputLayout layout)
    {
        mReference.child("Students").child(key.getTeacherUid()).orderByChild("rollNumber").equalTo(s1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            layout.setErrorEnabled(true);
                            layout.setError("Roll number is already registered");
                        }
                        else
                        {
                            dialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(StudentActivity.this);
                            builder.setTitle("Class Information");
                            builder.setMessage("Class name: "+key.getCourseName());

                            builder.setPositiveButton("Enroll", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    mProgressDialog.show();
                                    getFaceKey(key.getCourseName(),key.getTeacherUid(),s1);

                                }
                            });

                            builder.show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }



    private void signOutDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sign Out");
        builder.setMessage("Are you sure you want to sign out ?");

        builder.setPositiveButton("Agree", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                FirebaseAuth.getInstance().signOut();
                getSharedPreferences("userInf",MODE_PRIVATE).edit().clear().apply();

                Intent intent = new Intent(StudentActivity.this,LoginActivity.class);
                startActivity(intent);

            }
        });

        builder.setNegativeButton("disagree", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.show();
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START))
            mDrawerLayout.closeDrawer(GravityCompat.START);
        else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();
            }else
                finish();
        }

    }

    @Override
    public void cardViewClicked(int p) {

        TeacherInfo info = mList.get(p);

        Intent i = new Intent(StudentActivity.this,StudentAttendanceActivity.class);
        i.putExtra("course",info.getClassName());
        startActivity(i);

    }
}
