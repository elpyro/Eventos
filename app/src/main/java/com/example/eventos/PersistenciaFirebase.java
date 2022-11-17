package com.example.eventos;

import com.google.firebase.database.FirebaseDatabase;

public class PersistenciaFirebase extends android.app.Application{
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
