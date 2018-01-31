package com.salman.myproject.ui;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.salman.myproject.R;
import com.salman.myproject.adapter.PropertyAdapter;
import com.salman.myproject.realm_pojo.PropertyDetail;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class PropertyUserActivity extends AppCompatActivity implements PropertyAdapter.propertyClick {


    private PropertyAdapter mAdapter;
    private ArrayList<PropertyDetail> mList;
    private FirebaseUser mUser;
    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_user);

        Toolbar toolbar = findViewById(R.id.toolbar_pob_user);
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();
        if(bar!=null)
        {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayShowHomeEnabled(true);
        }


        mRealm = Realm.getDefaultInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mList = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.pop_user_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RealmResults<PropertyDetail> results = mRealm.where(PropertyDetail.class).equalTo("userKey",mUser.getUid()).findAll();
        mList.addAll(results);
        mAdapter = new PropertyAdapter(this,mList);
        mAdapter.setPc(this);
        recyclerView.setAdapter(mAdapter);

    }

    @Override
    public void setPropertyClick(int p) {

        PropertyDetail pd = mList.get(p);

        Intent i = new Intent(PropertyUserActivity.this,PropertyDetailActivity.class);
        i.putExtra("location",pd.getLocation());
        i.putExtra("email",pd.getEmail());
        i.putExtra("details",pd.getDetails());
        i.putExtra("price",String.valueOf(pd.getPrice()));
        i.putExtra("property_type",pd.getPropertyType());
        i.putExtra("deal_type",pd.getDealType());
        i.putExtra("link",pd.getPhotoLink());
        i.putExtra("contact",pd.getContactNumber());
        i.putExtra("change",true);
        i.putExtra("userKey",pd.getPropertyId());
        i.putExtra("publicKey",pd.getImageKey());
        i.putExtra("realmKey",pd.getKey());
        i.putExtra("position",p);
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
            onBackPressed();


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mList.clear();
        RealmResults<PropertyDetail> results = mRealm.where(PropertyDetail.class).equalTo("userKey",mUser.getUid()).findAll();
        mList.addAll(results);
        mAdapter.notifyDataSetChanged();
    }
}
