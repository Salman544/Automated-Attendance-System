package com.salman.myproject.application;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

import io.realm.Realm;


public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Realm.init(this);

    }
}
