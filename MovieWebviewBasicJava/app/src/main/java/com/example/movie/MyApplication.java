package com.example.movie;

import android.app.Application;
import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyApplication extends Application {

    private static final String FIREBASE_URL = "https://moviewebviewandroidjava-default-rtdb.firebaseio.com";
    private DatabaseReference mDatabaseReference;

    public static MyApplication get(Context context) {
        return (MyApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        initFirebase();
    }

    public void initFirebase() {
        // Phương thức để khởi tạo kết nối với Firebase Realtime Database.
        String mReference = "movie";
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_URL);
        mDatabaseReference = mFirebaseDatabase.getReference(mReference);
    }

    public DatabaseReference getDatabaseReference() {
        return mDatabaseReference;
    }
}
