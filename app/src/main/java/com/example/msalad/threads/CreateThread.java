package com.example.msalad.threads;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by cci-loaner on 10/23/17.
 */

public class CreateThread extends AppCompatActivity {

    private Button cThread;
    private EditText tThread;
    private DatabaseReference threadRef;
    private FirebaseDatabase database;
    private String threadCode;
    private String threadTitle;
    private String MY_PREFS_NAME = "MY_PREFS_NAME";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_thread);



        cThread = findViewById(R.id.createThreadBtn);
        tThread = findViewById(R.id.threadTitle);
        cThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(threadTitle.length() > 3){
                createThread();
                //Intent i = new Intent(CreateThread.this,ThreadActivity.class);
                }else{
                    Toast.makeText(CreateThread.this,"Please name your thread (more than 3 letters)",Toast.LENGTH_SHORT).show();
                }

            }
        });

        tThread.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                threadTitle = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }



    public void createThread(){
        Thread t = new Thread();
        ArrayList<String> u = new ArrayList<>();
        String androidId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        int time = (int) (System.currentTimeMillis())/1000;
        //Timestamp tsTemp = new Timestamp(time);
        //String ts =  tsTemp.toString();
        u.add(androidId);
        t.setAnons(u);
        t.setThreadTitle(threadTitle);
        t.setTimeStamp(time);
        final Topics topic = new Topics();
        DatabaseReference threadRoot = threadRef.child("Threads").child(threadCode);
        HashMap map = new HashMap();
        HashMap map2 = new HashMap();
        map.put("threadTitle",threadTitle);
        map.put("timeStamp",time);
        map.put("threadTitle",threadTitle);
        //map2.put("upvoters",u);
        //DatabaseReference threadTopicRoot = threadRef.child("Threads").child(threadCode).child("topics").child("0");
        //threadTopicRoot.updateChildren(map2);
       threadRoot.updateChildren(map);
        Intent i = new Intent(CreateThread.this,ThreadActivity.class);
        startActivity(i);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();
        threadRef = database.getReference();
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        //String restoredText = prefs.getString("text", null);
       // if (restoredText != null) {
            threadCode = prefs.getString("threadCode", "8080");//"No name defined" is the default value.
        //}
    }
}
