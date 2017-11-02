package com.example.msalad.threads;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by cci-loaner on 11/1/17.
 */

public class PostActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference threadRef;
    ListView messagesList;
    TextView title;
    TextView replies;
    TextView timestamp;
    TextView message;
    TextView upvoteCount;
    Button upvoteBtn;
    Post incomingPost;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        messagesList = findViewById(R.id.messages_list);
        title = findViewById(R.id.post_title);
        replies = findViewById(R.id.post_replies);
        timestamp = findViewById(R.id.post_timestamp);
        message = findViewById(R.id.post_msg);
        upvoteBtn = findViewById(R.id.upvoteBtn);
        upvoteCount = findViewById(R.id.post_upvotes);
        if(getIntent().getExtras() != null){
            incomingPost = (Post) getIntent().getSerializableExtra("post");
            title.setText(getIntent().getStringExtra("tt"));
            setTitle(getIntent().getStringExtra("tt"));
            replies.setText(incomingPost.getReplies()+" Replies");
            timestamp.setText(incomingPost.getTimeStamp()+"");
            message.setText(incomingPost.getTopicTitle());
            upvoteCount.setText(incomingPost.getUpvoters().size()+"");
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();
        threadRef = database.getReference();
    }
}
