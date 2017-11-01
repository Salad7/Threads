package com.example.msalad.threads;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by cci-loaner on 10/23/17.
 */

public class CreateThread extends AppCompatActivity {

    private Button cThread;
    private TextView tThread;
    private DatabaseReference threadRef;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_thread);

        cThread = findViewById(R.id.createThreadBtn);
        tThread = findViewById(R.id.threadTitle);
        cThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
