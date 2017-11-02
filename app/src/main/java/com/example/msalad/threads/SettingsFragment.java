package com.example.msalad.threads;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by cci-loaner on 10/23/17.
 */

public class SettingsFragment extends Fragment {
    ListView list;
    private FirebaseDatabase database;
    private DatabaseReference threadRef;
    private String threadCode;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings,container,false);
        list = v.findViewById(R.id.settings_list);
        FirebaseApp.initializeApp(getActivity());
        database = FirebaseDatabase.getInstance();
        threadRef = database.getReference();
        loadFirebase();

        return v;
    }

    public void loadFirebase(){
        threadRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
