package com.salman.myproject.ui;


import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.salman.myproject.BuildConfig;
import com.salman.myproject.R;
import com.salman.myproject.adapter.StudentAdapter;
import com.salman.myproject.firebase_pojo.EnrollmentKey;
import com.salman.myproject.firebase_pojo.FirebaseAttendance;
import com.salman.myproject.firebase_pojo.FirebaseImage;
import com.salman.myproject.realm_pojo.Attendance;
import com.salman.myproject.realm_pojo.RealmStudent;
import com.salman.myproject.rest_api.Errors;
import com.salman.myproject.rest_api.KairosApi;
import com.salman.myproject.rest_api.Recognize;
import com.salman.myproject.rest_api.RecognizeUser;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import id.zelory.compressor.Compressor;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.CLIPBOARD_SERVICE;


public class StudentFragment extends Fragment implements StudentAdapter.clickAdapter {


    private String mKey;
    private View mView;
    private RecyclerView mRecyclerView;
    private Realm mRealm;
    private StudentAdapter mAdapter;
    private TextView mTakeAttendance, mViewAttendance,mAddStudent,mViewDate,mEnrollmentKey,mStudentRequest,mProperty;
    private boolean isHidden = false,reTake = false;
    private FirebaseUser mUser;
    private StorageReference mStorageReference;
    private DatabaseReference mReference;
    private File mFile = null;
    private ProgressDialog mProgressDialog;
    private ArrayList<String> mFaceId;
    private String mDate = "",mEnrollmentString,mName,mEmail,mLink;
    private ArrayList<RealmStudent> mStudentList;
    private static final String TAG = "MainActivity";
    private String mCurrentPhotoPath;

    public StudentFragment() {
        // Required empty public constructor
    }

    public static StudentFragment newInstance(String key,String name,String email,String link) {
        StudentFragment fragment = new StudentFragment();
        Bundle args = new Bundle();
        args.putString("key", key);
        args.putString("name",name);
        args.putString("email",email);
        args.putString("link",link);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_student, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();
        FloatingActionButton fab = mView.findViewById(R.id.addStudentFab);
        mRecyclerView = mView.findViewById(R.id.viewStudentRecycler);
        mRealm = Realm.getDefaultInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mReference = FirebaseDatabase.getInstance().getReference();
        mTakeAttendance = mView.findViewById(R.id.take_attendance_frag);
        mViewAttendance = mView.findViewById(R.id.attendance_summary);
        mAddStudent = mView.findViewById(R.id.add_student_text);
        mViewDate = mView.findViewById(R.id.view_date_frag);
        mEnrollmentKey = mView.findViewById(R.id.enrollment_key);
        mStudentRequest = mView.findViewById(R.id.student_request);
        mProperty = mView.findViewById(R.id.view_property);
        mFaceId = new ArrayList<>();
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage("Please Wait . . . ");
        mProgressDialog.setCancelable(false);

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        mDate = String.valueOf(day)+"-"+String.valueOf(month+1)+"-"+String.valueOf(year);
        String d = "Date: "+mDate;
        mViewDate.setText(d);

        if(args!=null)
        {
            mKey = args.getString("key");
            mName = args.getString("name");
            mEmail = args.getString("email");
            mLink = args.getString("link");
            setRecyclerView();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isHidden)
                {
                    mTakeAttendance.setVisibility(View.INVISIBLE);
                    mAddStudent.setVisibility(View.INVISIBLE);
                    mViewAttendance.setVisibility(View.INVISIBLE);
                    mViewDate.setVisibility(View.INVISIBLE);
                    mStudentRequest.setVisibility(View.INVISIBLE);
                    mEnrollmentKey.setVisibility(View.INVISIBLE);
                    mProperty.setVisibility(View.INVISIBLE);
                    isHidden = false;
                }
                else
                {
                    mTakeAttendance.setVisibility(View.VISIBLE);
                    mAddStudent.setVisibility(View.VISIBLE);
                    mViewAttendance.setVisibility(View.VISIBLE);
                    mViewDate.setVisibility(View.VISIBLE);
                    mStudentRequest.setVisibility(View.VISIBLE);
                    mEnrollmentKey.setVisibility(View.VISIBLE);
                    mProperty.setVisibility(View.VISIBLE);
                    isHidden = true;
                }
            }
        });

        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Intent i = new Intent(getContext(),PropertyActivity.class);
                startActivity(i);
                return true;
            }
        });



        saveFrag();
        setTextViewClick();
        getEnrollmentKey();

    }

    private void checkIfAttendanceIsTaken(boolean b)
    {
        Attendance attendance = mRealm.where(Attendance.class).equalTo("course",mKey).equalTo("date",mDate).findFirst();

        if(attendance!=null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Attendance Information");
            builder.setMessage("Attendance is already taken");

            if(b)
            {
                builder.setPositiveButton("ReTake", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        getImageDialog();
                        reTake = true;
                        dialogInterface.dismiss();
                    }
                });
            }


            builder.setNegativeButton("Show Attendance", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    mAdapter.setCourse(mKey);
                    mAdapter.setDate(mDate);
                    mAdapter.notifyDataSetChanged();

                    dialogInterface.dismiss();
                }
            });

            builder.show();

        }
        else
        {
            if(b)
                getImageDialog();
            else
            {
                Toast.makeText(getContext(),"Previous attendance not found",Toast.LENGTH_SHORT).show();
                mAdapter.setCourse(mKey);
                mAdapter.setDate(mDate);
                mAdapter.notifyDataSetChanged();
            }
        }

    }

    private void getEnrollmentKey()
    {
        mReference.child("EnrollmentKey").child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                String s;

                if(dataSnapshot.exists())
                {
                    for(DataSnapshot d:dataSnapshot.getChildren())
                    {
                        EnrollmentKey key = d.getValue(EnrollmentKey.class);
                        if(key!=null)
                        {
                            if(key.getCourseName().equals(mKey))
                            {
                                s = "View Enrollment Class Key";
                                mEnrollmentKey.setText(s);
                                mEnrollmentString = d.getKey();
                                Log.d(TAG, "onDataChange: "+mEnrollmentString);
                            }
                        }
                    }
                }
                else
                {
                    s = "Get Enrollment Class Key";
                    mEnrollmentKey.setText(s);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getImageDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        String s = "    Take Picture,    Pick Picture";
        builder.setTitle("Picture");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),android.R.layout.simple_selectable_list_item,new ArrayList<>(Arrays.asList(s.split(",")))
        );
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(i==0)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            try {
                                cameraTakePictureIntent();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else
                            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
                    }
                }
                else
                {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent,"Select Picture"),123);
                }
            }
        });

        builder.show();

    }

    private void dateAlertDialog(final boolean b)
    {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayMonth) {

                mDate = String.valueOf(dayMonth)+"-"+String.valueOf(month+1)+"-"+String.valueOf(year);
                checkIfAttendanceIsTaken(b);

                String d = "Date: "+mDate;
                mViewDate.setText(d);


            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();

    }


    private void setTextViewClick()
    {
        mStudentRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(),StudentRequestActivity.class);
                i.putExtra("name",mName);
                startActivity(i);
            }
        });

        mProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(),PropertyActivity.class);

                Log.d("MainActivity", "Name: "+mName);
                Log.d("MainActivity", "Email: "+mEmail);
                Log.d("MainActivity", "mLink: "+mLink);

                i.putExtra("pName",mName);
                i.putExtra("pEmail",mEmail);
                i.putExtra("pLink",mLink);
                i.putExtra("isTeacher",true);
                startActivity(i);
            }
        });


        mTakeAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                dateAlertDialog(true);

            }
        });

        mAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(),AddUserActivity.class);
                intent.putExtra("className",mKey);
                startActivity(intent);
            }
        });

        mViewAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getAllDateAlertDialog();

            }
        });

        mViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateAlertDialog(false);
            }
        });

        mEnrollmentKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mEnrollmentKey.getText().toString().equals("View Enrollment Class Key"))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(mKey+" Key");
                    builder.setMessage("Class Enrollment key is : "+mEnrollmentString);
                    builder.setPositiveButton("Get Key", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("key", mEnrollmentString);
                            if (clipboard != null)
                            {
                                clipboard.setPrimaryClip(clip);
                                Toast.makeText(getContext(),"copied to clipboard",Toast.LENGTH_SHORT).show();
                            }
                            else
                                Toast.makeText(getContext(),"not copied to clipboard",Toast.LENGTH_SHORT).show();
                        }
                    });

                    builder.show();
                }
                else
                {
                    final EnrollmentKey key = new EnrollmentKey(mUser.getUid(),mKey);
                    mReference.child("EnrollmentKey").child(mUser.getUid()).push().setValue(key, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError == null)
                            {
                                String s = "View Enrollment Class Key";
                                mEnrollmentKey.setText(s);
                                mEnrollmentString = databaseReference.getKey();
                                key.setKey(mEnrollmentString);
                                mReference.child("PublicEnrollmentKey").push().setValue(key);
                                Toast.makeText(getContext(),"Enrollment Class Key Set",Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "onDataChange: "+mEnrollmentString);
                            }
                        }
                    });
                }
            }
        });

    }

    private void getAllDateAlertDialog() {

        RealmResults<Attendance> results = mRealm.where(Attendance.class).equalTo("course",mKey).findAll();
        final ArrayList<String> list = new ArrayList<>();
        for(Attendance attendance:results)
        {
            if(list.isEmpty())
                list.add(attendance.getDate());
            else
            {
                if(!list.contains(attendance.getDate()))
                    list.add("    "+attendance.getDate());
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_selectable_list_item,list);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext())
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent intent = new Intent(getContext(),AttendanceActivity.class);
                        intent.putExtra("date",list.get(i));
                        intent.putExtra("course",mKey);
                        startActivity(intent);

                        dialogInterface.dismiss();
                    }
                });

        alertDialog.setTitle(mKey);
        alertDialog.show();

    }

    private void setRecyclerView() {

        RealmResults<RealmStudent> results = mRealm.where(RealmStudent.class).equalTo("courseName", mKey).findAll();
        mStudentList = new ArrayList<>();
        mStudentList.addAll(results);

        mAdapter = new StudentAdapter(mStudentList,getContext());
        mAdapter.setDate(mDate);
        mAdapter.setCourse(mKey);
        mAdapter.setClickAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    public void dataAddedOrChanged(RealmStudent student,boolean ischanged)
    {
        if(student.getCourseName().equals(mKey)) {
            if (!ischanged) {
                mStudentList.add(student);
                mAdapter.notifyItemInserted(mStudentList.size() - 1);
            }
        }
    }

    private void saveFrag()
    {
        SharedPreferences.Editor editor = getContext().getSharedPreferences("frag", Context.MODE_PRIVATE).edit();
        editor.putString("lastFrag",mKey);
        editor.apply();
        editor.commit();
    }



    private void realmAttendance(final int i, final boolean p, final boolean a, final boolean l, final String faceId,final String uid)
    {

        final Attendance attendance = mRealm.where(Attendance.class).equalTo("date",mDate)
                .equalTo("course",mKey).equalTo("faceId",faceId).findFirst();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(final Realm realm) {

                mAdapter.setDate(mDate);
                FirebaseAttendance fa = new FirebaseAttendance(faceId, p, a, l, mDate, mKey);

                int k =0;
                Number number = mRealm.where(Attendance.class).max("id");
                if(number!=null)
                    k = number.intValue()+1;

                if(attendance!=null)
                {
                    attendance.setAbsent(a);
                    attendance.setPresent(p);
                    attendance.setLeave(l);
                    realm.insertOrUpdate(attendance);

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl(attendance.getRef());
                    reference.setValue(fa);
                    reference.child("ref").setValue(attendance.getStudentRef());

                    reference = FirebaseDatabase.getInstance().getReferenceFromUrl(attendance.getStudentRef());
                    reference.setValue(fa);

                }
                else
                {
                    final Attendance attendance1 = new Attendance(faceId,mDate,mKey,p,a,l);
                    attendance1.setId(k);

                    mReference.child("Attendance").child(mUser.getUid()).push().setValue(fa, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError == null)
                            {
                                attendance1.setRef(databaseReference.getRef().toString());
                                Log.d("MainActivity", "onComplete teacher: added");
                            }

                        }
                    });

                    mReference.child("StudentsAttendance").child(uid)
                            .push().setValue(fa, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError == null)
                            {
                                attendance1.setStudentRef(databaseReference.getRef().toString());
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl(attendance1.getRef());
                                reference.child("ref").setValue(databaseReference.getRef().toString());
                                Log.d("MainActivity", "onComplete teacher: added");
                                mRealm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        realm.insertOrUpdate(attendance1);

                                    }
                                });


                            }

                        }
                    });

                }

                mAdapter.notifyItemChanged(i);

            }
        });
    }

    @Override
    public void setRecClick(final int p) {

        final BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        dialog.setContentView(R.layout.bottom_attedence);

        View present,absent,leave;

        present = dialog.findViewById(R.id.present_permission);
        absent = dialog.findViewById(R.id.absent_permission);
        leave = dialog.findViewById(R.id.leave_permission);


        assert present != null;
        present.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!mDate.isEmpty()) {
                    realmAttendance(p, true, false, false, mStudentList.get(p).getFaceId(),mStudentList.get(p).getStudentUid());
                    dialog.dismiss();
                }else
                    Toast.makeText(getContext(),"Add Date",Toast.LENGTH_SHORT).show();
            }
        });

        assert absent!= null;
        absent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!mDate.isEmpty()) {
                    realmAttendance(p, false, true, false, mStudentList.get(p).getFaceId(),mStudentList.get(p).getStudentUid());
                    dialog.dismiss();
                }else
                    Toast.makeText(getContext(),"Add Date",Toast.LENGTH_SHORT).show();
            }
        });

        assert leave!= null;
        leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!mDate.isEmpty()) {
                    realmAttendance(p, false, false, true, mStudentList.get(p).getFaceId(),mStudentList.get(p).getStudentUid());
                    dialog.dismiss();
                }else
                    Toast.makeText(getContext(),"Add Date",Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();


    }

    @Override
    public void setRecLongClick(int p) {


    }

    private void uploadImage()
    {
        if(mFile!=null)
        {
            mProgressDialog.show();
            StorageReference ref = mStorageReference.child("images/"+mFile.getName());
            ref.putFile(Uri.fromFile(mFile))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            String link = taskSnapshot.getDownloadUrl().toString();

                            mReference.child("Images").child(mUser.getUid()).push().setValue(new FirebaseImage(
                                    mFile.getName(),link));

                            getDataFromServer(link);

                        }
                    });
        }
        else
            Toast.makeText(getContext(),"Image not found",Toast.LENGTH_LONG).show();
    }

    private void getDataFromServer(final String link) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.kairos.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        KairosApi api = retrofit.create(KairosApi.class);

        api.recognizeUser(new RecognizeUser(link,mKey+mUser.getUid()))
                .enqueue(new Callback<Recognize>() {
                    @Override
                    public void onResponse(Call<Recognize> call, Response<Recognize> response) {
                        mProgressDialog.dismiss();
                        if(response.isSuccessful())
                        {
                            Gson gson = new Gson();
                            String s = gson.toJson(response.body());
                            if(s.contains("Errors"))
                            {
                                Errors errors = gson.fromJson(s, Errors.class);
                                String error = errors.getErrors().get(0).getMessage();

                                final Snackbar snackbar = Snackbar.make(mView.findViewById(R.id.addStudentFab), error, Snackbar.LENGTH_INDEFINITE);

                                snackbar.setAction("Ok", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        snackbar.dismiss();
                                    }
                                });

                                snackbar.show();
                                mFile = null;

                                StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(link);
                                ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getContext(),"Image Deleted",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else
                            {

                                Recognize recognize = response.body();
                                mFaceId.clear();
                                List<Recognize.ImagesBean> images = recognize.getImages();
                                for(int i =0;i<images.size();i++)
                                {
                                    if(images.get(i).getCandidates()!=null)
                                        mFaceId.add(images.get(i).getCandidates().get(0).getFace_id());
                                }

                                takeAttendance();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Recognize> call, Throwable t) {

                        Log.d(TAG, "onFailure: "+t.getMessage());
                        Toast.makeText(getContext(),t.getMessage(),Toast.LENGTH_LONG).show();
                        mProgressDialog.dismiss();
                    }
                });

    }


    private void takeAttendance() {

        if(!reTake)
        {
            for(String s:mFaceId)
            {
                RealmStudent student = mRealm.where(RealmStudent.class).equalTo("faceId",s).findFirst();
                if(student!=null)
                {

                    final Attendance attendance = new Attendance(s,mDate,mKey,true,false,false);

                    mReference.child("Attendance").child(mUser.getUid()).push().setValue(new FirebaseAttendance(
                            s, true, false, false, mDate, mKey), new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError == null)
                            {
                                attendance.setRef(databaseReference.getRef().toString());
                                Log.d("MainActivity", "onComplete teacher: added");
                            }
                        }
                    });

                    mReference.child("StudentsAttendance").child(student.getStudentUid())
                            .push().setValue(new FirebaseAttendance(
                            s, true, false, false, mDate, mKey), new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError == null)
                            {
                                attendance.setStudentRef(databaseReference.getRef().toString());
                                mRealm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        int k=0;
                                        Number number = mRealm.where(Attendance.class).max("id");
                                        if(number!=null)
                                            k = number.intValue()+1;
                                        attendance.setId(k);
                                        realm.insertOrUpdate(attendance);
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl(attendance.getRef());
                                        reference.child("ref").setValue(attendance.getStudentRef());
                                    }
                                });

                                Log.d("MainActivity", "onComplete student: added");
                            }
                        }
                    });


                }
            }

        }
        else
        {
            for(String s:mFaceId)
            {
               final Attendance attendance = mRealm.where(Attendance.class).equalTo("date",mDate)
                       .equalTo("course",mKey).equalTo("faceId",s).findFirst();
               if(attendance!=null)
               {
                   mRealm.executeTransaction(new Realm.Transaction() {
                       @Override
                       public void execute(Realm realm) {

                           attendance.setPresent(true);
                           attendance.setAbsent(false);
                           attendance.setLeave(false);
                           realm.insertOrUpdate(attendance);

                           FirebaseAttendance a = new FirebaseAttendance(attendance.getFaceId(),
                                   true,false,false,mDate,mKey);

                           DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl(attendance.getStudentRef());
                           reference.setValue(a);

                           reference = FirebaseDatabase.getInstance().getReferenceFromUrl(attendance.getRef());
                           reference.setValue(a);


                       }
                   });
               }
            }
        }




        int i = 0;
        Iterator<RealmStudent> iterator = mStudentList.iterator();
        ArrayList<RealmStudent> dummyList = new ArrayList<>();
        while(iterator.hasNext())
        {
            RealmStudent s = iterator.next();
            if(!mFaceId.contains(s.getFaceId()))
            {
                iterator.remove();
                dummyList.add(s);
            }
        }

        for(RealmStudent s:dummyList)
        {
            mStudentList.add(i,s);
            i+=1;
        }

        mProgressDialog.dismiss();
        mFaceId.clear();
        if(i!=0)
        {

            final Snackbar bar = Snackbar.make(mView.findViewById(R.id.addStudentFab),String.valueOf(i)+" student are not found",Snackbar.LENGTH_INDEFINITE);
            bar.setActionTextColor(ContextCompat.getColor(getContext(),R.color.redColor));
            bar.setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bar.dismiss();
                }
            });


            bar.show();
        }

        mAdapter.setDate(mDate);
        mAdapter.setCourse(mKey);
        mAdapter.notifyDataSetChanged();
        reTake = false;

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: running "+String.valueOf(requestCode)+" "+String.valueOf(resultCode));
        if(requestCode == 123 && resultCode==RESULT_OK)
        {
            if(data.getData()!=null)
            {
                CropImage.activity(data.getData())
                        .start(getContext(),this);
            }
        }
        else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode==RESULT_OK)
            {
                Uri uri = result.getUri();

                try
                {
                    mFile = new Compressor(getContext()).compressToFile(new File(uri.getPath()));
                    Toast.makeText(getContext(),mFile.getName(),Toast.LENGTH_LONG).show();

                    uploadImage();

                } catch (IOException e)
                {
                    Toast.makeText(getContext(),"error",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }
        else if(requestCode == 456 && resultCode == Activity.RESULT_OK)
        {
            Uri uri = Uri.parse(mCurrentPhotoPath);
            CropImage.activity(uri)
                    .start(getContext(),this);

        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void cameraTakePictureIntent() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                return;
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        BuildConfig.APPLICATION_ID + ".provider",
                        createImageFile());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 456);
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        setRecyclerView();
    }
}
