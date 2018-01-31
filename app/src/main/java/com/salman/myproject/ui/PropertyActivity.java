package com.salman.myproject.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.salman.myproject.R;
import com.salman.myproject.adapter.PropertyAdapter;
import com.salman.myproject.databinding.ActivityPropertyBinding;
import com.salman.myproject.firebase_pojo.FirebasePropertyDetail;
import com.salman.myproject.realm_pojo.Attendance;
import com.salman.myproject.realm_pojo.PropertyDetail;
import com.salman.myproject.realm_pojo.RealmStudent;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class PropertyActivity extends AppCompatActivity implements PropertyAdapter.propertyClick {


    private ActivityPropertyBinding mBinding;
    private Realm mRealm;
    private DatabaseReference mDatabase;
    private ArrayList<PropertyDetail> mList;
    private PropertyAdapter mAdapter;
    private FirebaseUser mUser;
    private boolean isTeacher = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_property);

        setSupportActionBar(mBinding.toolbarProperty);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRealm = Realm.getDefaultInstance();
        mList = new ArrayList<>();

        mAdapter = new PropertyAdapter(this,mList);
        mAdapter.setPc(this);
        mBinding.propertyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBinding.propertyRecyclerView.setAdapter(mAdapter);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,mBinding.propertyDrawerLayout,mBinding.toolbarProperty,R.string.open,R.string.close);

        mBinding.propertyDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mBinding.addPropertyFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PropertyActivity.this,PropertyDetailActivity.class);
                i.putExtra("addChange",true);
                startActivity(i);
            }
        });


        getMyProperty();
        setNav();
    }

    private void setNav()
    {

        View header = mBinding.propertyNav.getHeaderView(0);

        final TextView pname = header.findViewById(R.id.navName);
        final TextView pemail = header.findViewById(R.id.navEmail);
        final ImageView pimageView = header.findViewById(R.id.navImage);

//        i.putExtra("pName",mName);
//        i.putExtra("pEmail",mEmail);
//        i.putExtra("pLink",mLink);
//        i.putExtra("isTeacher",true);

        Bundle bundle = getIntent().getExtras();

        if(bundle!=null)
        {
            pname.setText(bundle.getString("pName"));
            pemail.setText(bundle.getString("pEmail"));
            String link = bundle.getString("pLink");
            isTeacher = bundle.getBoolean("isTeacher");

            Toast.makeText(getApplicationContext(),String.valueOf(isTeacher),Toast.LENGTH_SHORT).show();

            if(link!=null)
            {
                if(!link.equals("null"))
                {
                    Glide.with(this)
                            .load(link)
                            .into(pimageView);
                }
            }

        }

        Menu navMenu = mBinding.propertyNav.getMenu();
        MenuItem user,name,range,location,signout;
        user = navMenu.findItem(R.id.show_user_property);
        name = navMenu.findItem(R.id.property_name);
        range = navMenu.findItem(R.id.property_range);
        location = navMenu.findItem(R.id.property_location);
        signout = navMenu.findItem(R.id.sign_out);

        user.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent i = new Intent(PropertyActivity.this,PropertyUserActivity.class);
                startActivity(i);
                return true;
            }
        });

        signout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                mBinding.propertyDrawerLayout.closeDrawer(GravityCompat.START);
                signOutDialog();

                return true;
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

                if(isTeacher)
                {
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
                                Intent i = new Intent(PropertyActivity.this,LoginActivity.class);
                                startActivity(i);
                            }
                            
                        }
                    });
                }
                else
                {
                    mRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            RealmResults<PropertyDetail> details = mRealm.where(PropertyDetail.class).findAll();
                            if(details.deleteAllFromRealm())
                            {
                                FirebaseAuth.getInstance().signOut();
                                Intent i = new Intent(PropertyActivity.this,LoginActivity.class);
                                startActivity(i);
                            }
                        }
                    });
                }



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




    private void getMyProperty() {

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        mDatabase.child("Properties").child("user").child(mUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for(final DataSnapshot d:dataSnapshot.getChildren())
                        {
                            final FirebasePropertyDetail detail = d.getValue(FirebasePropertyDetail.class);
                            if(detail!=null)
                            {
                                final PropertyDetail detail1 = mRealm.where(PropertyDetail.class).equalTo("imageKey",detail.getKey()).findFirst();
                                if(detail1 == null)
                                {
                                    addRealmData(detail,d);
                                }
                            }
                        }

                        getAllProperty();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    private void addRealmData(final FirebasePropertyDetail detail, final DataSnapshot d) {

        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                int i =0;
                Number number = mRealm.where(PropertyDetail.class).max("key");
                if(number!=null)
                    i = number.intValue()+1;

                PropertyDetail pd= new PropertyDetail(detail.getDealType(),detail.getPropertyType(),detail.getPrice(),
                        detail.getLocation(),detail.getDetails(),d.getKey(),detail.getContactNumber(),
                        detail.getEmail());
                pd.setUserKey(mUser.getUid());
                pd.setImageKey(detail.getKey());
                pd.setShortDesc(detail.getShortDesc());
                pd.setPhotoLink(detail.getPhotoLink());
                pd.setKey(i);

                realm.insertOrUpdate(pd);

            }
        });


    }

    private void getAllProperty() {

        mDatabase.child("Properties").child("allProprieties").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {

                final FirebasePropertyDetail detail = dataSnapshot.getValue(FirebasePropertyDetail.class);
                if(detail!=null)
                {
                    PropertyDetail detail1 = mRealm.where(PropertyDetail.class).equalTo("imageKey",dataSnapshot.getKey()).findFirst();
                    if(detail1 == null)
                    {


                        detail1 = new PropertyDetail(detail.getDealType(),detail.getPropertyType(),detail.getPrice(),
                                detail.getLocation(),detail.getDetails(),dataSnapshot.getKey(),detail.getContactNumber(),
                                detail.getEmail());
                        detail1.setShortDesc(detail.getShortDesc());
                        detail1.setPhotoLink(detail.getPhotoLink());

                        mList.add(detail1);
                        mAdapter.notifyItemInserted(mList.size() - 1);

                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                int i = 0;
                FirebasePropertyDetail detail = dataSnapshot.getValue(FirebasePropertyDetail.class);
                if(detail!=null)
                {
                    PropertyDetail detail1 = mRealm.where(PropertyDetail.class).equalTo("propertyId",dataSnapshot.getKey()).findFirst();
                    if(detail1 == null)
                    {
                        for(PropertyDetail pd:mList)
                        {
                            if(pd.getPropertyId().equals(dataSnapshot.getKey()))
                            {
                                detail1 = new PropertyDetail(detail.getDealType(),detail.getPropertyType(),detail.getPrice(),
                                        detail.getLocation(),detail.getDetails(),dataSnapshot.getKey(),detail.getContactNumber(),
                                        detail.getEmail());

                                detail1.setPhotoLink(detail.getPhotoLink());
                                detail1.setShortDesc(detail.getShortDesc());

                                mList.remove(i);
                                mAdapter.notifyItemRemoved(i);
                                mList.add(i,detail1);
                                mAdapter.notifyItemChanged(i);
                                break;
                            }
                            i+=1;
                        }
                    }
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                int i = 0;
                FirebasePropertyDetail detail = dataSnapshot.getValue(FirebasePropertyDetail.class);
                if(detail!=null)
                {
                    PropertyDetail detail1 = mRealm.where(PropertyDetail.class).equalTo("propertyId",dataSnapshot.getKey()).findFirst();
                    if(detail1 == null)
                    {
                        for(PropertyDetail pd:mList)
                        {
                            if(pd.getPropertyId().equals(dataSnapshot.getKey()))
                            {
                                mList.remove(i);
                                mAdapter.notifyItemChanged(i);
                                break;
                            }
                            i+=1;
                        }
                    }
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
    public void onBackPressed() {

        if(mBinding.propertyDrawerLayout.isDrawerOpen(GravityCompat.START))
            mBinding.propertyDrawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();

    }

    @Override
    public void setPropertyClick(int p) {

        PropertyDetail pd = mList.get(p);

        Intent i = new Intent(PropertyActivity.this,PropertyDetailActivity.class);
        i.putExtra("location",pd.getLocation());
        i.putExtra("email",pd.getEmail());
        i.putExtra("details",pd.getDetails());
        i.putExtra("price",String.valueOf(pd.getPrice()));
        i.putExtra("property_type",pd.getPropertyType());
        i.putExtra("deal_type",pd.getDealType());
        i.putExtra("link",pd.getPhotoLink());
        i.putExtra("contact",pd.getContactNumber());
        startActivity(i);


    }
}
