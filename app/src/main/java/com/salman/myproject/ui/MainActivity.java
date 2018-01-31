package com.salman.myproject.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.salman.myproject.R;
import com.salman.myproject.adapter.TeacherClassAdapter;
import com.salman.myproject.databinding.ActivityMainBinding;
import com.salman.myproject.firebase_pojo.AddStudent;
import com.salman.myproject.firebase_pojo.FirebaseAttendance;
import com.salman.myproject.realm_pojo.Attendance;
import com.salman.myproject.realm_pojo.RealmStudent;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements TeacherClassAdapter.Click {

    private ActivityMainBinding mBinding;
    private TeacherClassAdapter mAdapter;
    private ArrayList<String> mList;
    private FirebaseUser mUser;
    private DatabaseReference mDatabase;
    private Realm mRealm;
    private String name,email,link;
    private StudentFragment frag = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        setSupportActionBar(mBinding.toolbar);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRealm = Realm.getDefaultInstance();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,mBinding.drawerLayout,mBinding.toolbar,R.string.open,R.string.close
        );

        mBinding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        mBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setClassNameDialog();
            }
        });

        setNavigationView();
        setRecyclerView();
        getStudents();
        isConnectedToInternet();

        if(!getSharedAttendance())
            getAttendanceOnce();

    }

    private void setSharedAttendance()
    {
        SharedPreferences.Editor editor = getSharedPreferences("att",MODE_PRIVATE).edit();
        editor.putBoolean("bool",true);
        editor.apply();
        editor.commit();

    }

    private boolean getSharedAttendance() {
        SharedPreferences preferences = getSharedPreferences("att", MODE_PRIVATE);
        return preferences != null && preferences.getBoolean("bool", false);
    }


    private void getAttendanceOnce()
    {

        setSharedAttendance();

        mDatabase.child("Attendance").child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot d:dataSnapshot.getChildren())
                {
                    final FirebaseAttendance attendance = d.getValue(FirebaseAttendance.class);
                    if(attendance!=null)
                    {
                        final String r = d.getRef().toString();
                        mRealm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                int i =0;
                                Number number = mRealm.where(Attendance.class).max("id");
                                if(number!=null)
                                    i = number.intValue()+1;

                                Attendance a = new Attendance(attendance.getFaceId(),attendance.getDate(),
                                        attendance.getCourse(),attendance.isPresent(),attendance.isAbsent(),attendance.isLeave());
                                a.setStudentRef(attendance.getRef());
                                a.setRef(r);
                                a.setId(i);
                                realm.insertOrUpdate(a);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    private void setShowOnlyOnce()
    {
        SharedPreferences.Editor editor = getSharedPreferences("show",MODE_PRIVATE).edit();
        editor.putBoolean("once",true);
        editor.apply();
        editor.commit();
    }

    private boolean getShowOnlyOnce() {
        SharedPreferences preferences = getSharedPreferences("show", MODE_PRIVATE);
        return preferences != null && preferences.getBoolean("once", false);
    }

    private void isConnectedToInternet()
    {
        ReactiveNetwork.observeInternetConnectivity()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean b) throws Exception {

                        if(b)
                        {
                            if(Alerter.isShowing())
                            {
                                Alerter.hide();
                                Alerter.create(MainActivity.this)
                                        .setBackgroundColorInt(ContextCompat.getColor(MainActivity.this,R.color.alerter_default_success_background))
                                        .setIcon(R.drawable.ic_wifi_connected)
                                        .enableSwipeToDismiss()
                                        .setDuration(500)
                                        .setTitle("Internet Established")
                                        .show();

                            }

                        }
                        else
                        {

                            Alerter.create(MainActivity.this)
                                    .setBackgroundColorInt(ContextCompat.getColor(MainActivity.this,R.color.alert_default_error_background))
                                    .setIcon(R.drawable.ic_wifi_not_connected)
                                    .enableInfiniteDuration(true)
                                    .disableOutsideTouch()
                                    .enableVibration(true)
                                    .setText("Your device is not connected to internet connection.\n" +
                                            "\n" +
                                            "Try:\n" +
                                            "Reconnecting to Wi-Fi or mobile network")
                                    .setTitle("Internet Not Avaliable")
                                    .show();

                        }

                    }
                });
    }




    private void getStudents() {

        mDatabase.child("Students").child(mUser.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                final AddStudent student = dataSnapshot.getValue(AddStudent.class);
                if(student!=null)
                {
                    RealmStudent student1 = mRealm.where(RealmStudent.class).equalTo("studentUid",student.getStudentUid())
                            .findFirst();
                    if(student1 == null)
                    {
                        mRealm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                int i =0;
                                Number number = mRealm.where(RealmStudent.class).max("id");
                                if(number!=null)
                                    i = number.intValue()+1;

                                RealmStudent realmStudent = new RealmStudent(i,student.getName(),
                                        student.getRollNumber(),student.getPhotoLink(),student.getFaceId(),
                                        student.getCourseName(),student.getStudentUid());

                                mRealm.insertOrUpdate(realmStudent);

                                if(frag!=null)
                                    frag.dataAddedOrChanged(realmStudent,false);

                            }
                        });

                        if(mList.isEmpty()) {
                            mList.add(student.getCourseName());
                            mAdapter.notifyItemInserted(mList.size()-1);
                        }
                        else
                        {
                            if(!mList.contains(student.getCourseName())) {
                                mList.add(student.getCourseName());
                                mAdapter.notifyItemInserted(mList.size()-1);
                            }
                        }

                        if(!getShowOnlyOnce())
                        {
                            setShowOnlyOnce();
                            mBinding.toolbarLayout.setTitle(mList.get(0));

                            if(!mList.isEmpty()) {
                                showFragment(mList.get(0));
                                setSharedPrefs();
                            }
                        }
                    }
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

    private void setNavigationView() {

        View view = mBinding.nav.getHeaderView(0);
        final ImageView userPhoto = view.findViewById(R.id.navImage);
        final TextView tname = view.findViewById(R.id.navName);
        final TextView temail = view.findViewById(R.id.navEmail);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null)
        {
            name = bundle.getString("tName");
            email = bundle.getString("tEmail");
            link = bundle.getString("tLink");
            tname.setText(name);
            temail.setText(email);
            if(!link.equals("null")) {
                Glide.with(this)
                        .load(link)
                        .into(userPhoto);
            }
        }

    }

    private void setRecyclerView() {

        mList = new ArrayList<>();
        getSharedPrefs();
        mAdapter = new TeacherClassAdapter(this,mList);
        mAdapter.setClick(this);
        mBinding.teacherRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBinding.teacherRecyclerView.setAdapter(mAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(getCallBack());
        helper.attachToRecyclerView(mBinding.teacherRecyclerView);

        getLastFrag();
    }

    private ItemTouchHelper.Callback getCallBack() {

        return new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                setDeleteList(viewHolder.getAdapterPosition());

            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                View item = viewHolder.itemView;
                Paint p = new Paint();

                float height = (float)item.getBottom() - (float)item.getTop();
                float width = height / 3;

                if(dX>0)
                {

                    Drawable d = ContextCompat.getDrawable(MainActivity.this,R.drawable.ic_delete);
                    icon = Bitmap.createBitmap(d.getIntrinsicWidth(),d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(icon);
                    d.setBounds(0,0,canvas.getWidth(),canvas.getHeight());
                    d.draw(canvas);

                    p.setColor(Color.parseColor("#D32F2F"));
                    RectF f = new RectF((float)item.getLeft(),(float)item.getTop(),dX,(float)item.getBottom());
                    c.drawRect(f,p);

                    RectF icon_dest = new RectF((float) item.getLeft() + width ,
                            (float) item.getTop() + width,(float) item.getLeft()+ 2*width,(float)item.getBottom() - width);
                    c.drawBitmap(icon,null,icon_dest,p);

                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            }
        };

    }

    private void showFragment(String key)
    {
        frag = StudentFragment.newInstance(key,name,email,link);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container,frag)
                .commit();
    }

    private void setDeleteList(final int p) {

        final String value = mList.get(p);
        AlertDialog.Builder builder;
        AlertDialog alertDialog;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(MainActivity.this,android.R.style.Theme_Material_Dialog_Alert);
        }
        else
            builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("Delete "+value);
        builder.setMessage("Are you sure you want to delete\" "+value+"\" ?");
        alertDialog = builder.create();

        builder.setPositiveButton("Agree", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mList.remove(p);
                mAdapter.notifyItemRemoved(p);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mAdapter.notifyItemChanged(p);
            }
        });

        if(!alertDialog.isShowing())
            mAdapter.notifyItemChanged(p);


        builder.show();

    }

    private void setSharedPrefs()
    {
        SharedPreferences.Editor editor = getSharedPreferences("className",MODE_PRIVATE).edit();
        editor.putInt("size",mList.size());
        int i = 0;
        for(String s:mList)
            editor.putString("i"+String.valueOf(i),s);

        editor.apply();
        editor.commit();

    }

    private void getSharedPrefs()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("className",MODE_PRIVATE);
        if(sharedPreferences!=null)
        {
            int size = sharedPreferences.getInt("size",0);
            for(int i=0;i<size;i++)
                mList.add(sharedPreferences.getString("i"+String.valueOf(i),""));
        }
    }

    private void getLastFrag()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("frag",MODE_PRIVATE);
        if(sharedPreferences!=null)
        {
            String s = sharedPreferences.getString("lastFrag",null);
            if(s!=null)
            {
                mBinding.toolbarLayout.setTitle(s);
                showFragment(s);
            }
        }
    }

    private void setClassNameDialog()
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog);

        final EditText editText = dialog.findViewById(R.id.editText);
        final TextInputLayout textInputLayout = dialog.findViewById(R.id.textInput);
        Button save,cancel;
        save = dialog.findViewById(R.id.saveBtn);
        cancel = dialog.findViewById(R.id.cancelBtn);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = editText.getText().toString();

                if(mList.contains(s))
                {
                    textInputLayout.setErrorEnabled(true);
                    textInputLayout.setError("Class name is already avaliable");
                }
                else
                {
                    mList.add(s);
                    mAdapter.notifyItemInserted(mList.size() - 1);
                    showFragment(s);
                    dialog.dismiss();
                    mBinding.toolbarLayout.setTitle(s);

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

    @Override
    protected void onStop() {
        super.onStop();

        setSharedPrefs();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.main_sign_out)
        {
            signOutDialog();
        }


        return super.onOptionsItemSelected(item);
    }

    private void signOutDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sign Out");
        builder.setMessage("Are you sure you want to sign out ?");

        builder.setPositiveButton("Agree", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getSharedPreferences("att",MODE_PRIVATE).edit().clear().apply();
                getSharedPreferences("show",MODE_PRIVATE).edit().clear().apply();
                getSharedPreferences("className",MODE_PRIVATE).edit().clear().apply();
                getSharedPreferences("frag",MODE_PRIVATE).edit().clear().apply();
                getSharedPreferences("userInf",MODE_PRIVATE).edit().clear().apply();


                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        RealmResults<Attendance> attendance = mRealm.where(Attendance.class).findAll();
                        RealmResults<RealmStudent> student = mRealm.where(RealmStudent.class).findAll();

                        if(attendance.deleteAllFromRealm()&&student.deleteAllFromRealm())
                        {
                            FirebaseAuth.getInstance().signOut();
                            Intent i = new Intent(MainActivity.this,LoginActivity.class);
                            startActivity(i);
                        }


                    }
                });


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
        super.onBackPressed();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        }
        else
            finish();
    }

    @Override
    public void setOnTextClick(int p) {

        mBinding.drawerLayout.closeDrawer(GravityCompat.START);
        mBinding.toolbarLayout.setTitle(mList.get(p));
        showFragment(mList.get(p));

    }
}
