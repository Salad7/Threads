package com.example.msalad.threads;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    String threadCode;
    EditText et_reply;
    ImageButton send;
    int topicPostion;
    ArrayList<Message> messages;
    int messagePosition;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();
        threadRef = database.getReference();
        messagesList = findViewById(R.id.messages_list);
        title = findViewById(R.id.post_title);
        replies = findViewById(R.id.post_replies);
        timestamp = findViewById(R.id.post_timestamp);
        message = findViewById(R.id.post_msg);
        upvoteBtn = findViewById(R.id.upvoteBtn);
        upvoteCount = findViewById(R.id.post_upvotes);
        send = findViewById(R.id.send);
        messages = new ArrayList<>();
        if(getIntent().getExtras() != null){
            incomingPost = (Post) getIntent().getSerializableExtra("post");
            title.setText(getIntent().getStringExtra("tt"));
            setTitle(getIntent().getStringExtra("tt"));
            replies.setText(incomingPost.getReplies()+" Replies");
            timestamp.setText(incomingPost.getTimeStamp()+"");
            message.setText(incomingPost.getTopicTitle());
            if(incomingPost.getUpvoters() != null) {
                upvoteCount.setText(incomingPost.getUpvoters().size() + "");
            }else{
                upvoteCount.setText(0+"");
            }
            threadCode = getIntent().getStringExtra("threadCode");
        }

        et_reply = findViewById(R.id.reply_field);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et_reply.getText().toString().length() > 0){
                    DatabaseReference newMessagePath = threadRef.child("Threads").child(threadCode).child("topics").child(topicPostion+"").child("messages").child(messagePosition+"");
                Message message = new Message();
                    String androidId = Settings.Secure.getString(PostActivity.this.getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                    message.setMsg(et_reply.getText().toString());
                    message.setHostUID(androidId);
                    message.setReplies(0);
                    Date d = new Date();
                    int time = (int) (d.getTime());
                    message.setTimeStamp(time/1000);
                    HashMap map = new HashMap();
                    HashMap messageMap = new HashMap();
                    map.put(androidId,"blue");
                    message.setAnonCode(map);
                    message.setPosition(messagePosition);
                    messageMap.put("UID",message.getAnonCode());
                    messageMap.put("timeStamp",message.getTimeStamp());
                    //messageMap.put("upvotes",message.getUpvotes())
                    messageMap.put("replies",message.getReplies());
                    messageMap.put("message",message.getMsg());
                    messageMap.put("position",message.getPosition());
                    HashMap mappy = new HashMap();
                    mappy.put(androidId,"blue");
                    newMessagePath.updateChildren(messageMap);
                }
            }
        });

        loadPosts();


    }

    public void loadPosts(){
        threadRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int currentLowest = -1;
                messages.clear();
                if(dataSnapshot.child("Threads").child(threadCode).exists()){
                    DataSnapshot threadPath = dataSnapshot.child("Threads").child(threadCode);
                    if(threadPath.child("topics").exists()){
                        DataSnapshot topicPath = threadPath.child("topics");
                        if(topicPath.child(topicPostion+"").child("messages").exists()){
                            DataSnapshot messagesPath = topicPath.child(topicPostion+"").child("messages");
                            int totalMessages = (int) messagesPath.getChildrenCount();
                            int messagesFound = 0;
                            for(int i = 0; i < 10000; i++){
                                if(messagesPath.child(i+"").exists() && totalMessages >= messagesFound){
                                DataSnapshot specificMessagesPath = messagesPath.child(i+"");
                                if(specificMessagesPath.child("anonCode").exists()){
                                Message message = new Message();
                                    if(specificMessagesPath.child("upvoters").exists()){
                                    message.setUpvoters((ArrayList)specificMessagesPath.child("upvoters").getValue());
                                    }
                                message.setMsg(specificMessagesPath.child("message").getValue(String.class));
                                message.setPosition(i);
                                message.setTimeStamp(specificMessagesPath.child("timeStamp").getValue(Integer.class));
                                message.setAnonCode((Map) specificMessagesPath.child("anonCode").getValue());
                                message.setReplies(specificMessagesPath.child("replies").getValue(Integer.class));
                                message.setUpvotes(specificMessagesPath.child("upvotes").getValue(Integer.class));
                                messages.add(message);
                                    messagesFound += 1;

                                }
                                }
                                else{
                                    if(currentLowest == -1){
                                    currentLowest = i;
                                        messagePosition = i;
                                    }
                                }

                            }
                        }
                    }else{
                        messagePosition = 0;
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        MessageAdapter messageAdapter = new MessageAdapter(this,R.layout.custom_message,messages);
        messageAdapter.notifyDataSetChanged();
        messagesList.setAdapter(messageAdapter);
        Log.d("PostActivity ",messages.size()+"");
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    public class MessageAdapter extends ArrayAdapter<Message> {
        Context c;
        int res;
        ArrayList<Message> messages;
        public MessageAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<Message> objects) {
            super(context, resource, objects);
            c = context;
            res = resource;
            messages = objects;
        }

        @Override
        public int getCount() {
            return messages.size();
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) c
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(res,parent,false);
            TextView label;
            TextView msg;
            TextView count;
            Button upvoteBtn;
            label = convertView.findViewById(R.id.posttitle);
            msg = convertView.findViewById(R.id.post_reply);
            count = convertView.findViewById(R.id.post_upvote_count);
            upvoteBtn = convertView.findViewById(R.id.upvote_img);
            label.setText(position+"");
            msg.setText(messages.get(position).getMsg());
            count.setText(messages.get(position).getUpvoters().size()+"");
            upvoteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String androidId = Settings.Secure.getString(PostActivity.this.getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                    if(messages.get(position).getUpvoters() == null || !messages.get(position).getUpvoters().contains(androidId)){
                        ArrayList<String> androids = new ArrayList<String>();
                        androids.add(androidId);
                        HashMap map = new HashMap();
                        map.put("upvoters",androids);
                        threadRef.child("Threads").child(threadCode).child("topics").child(topicPostion+"").child("messages").child(messages.get(position).getPosition()+"")
                                .updateChildren(map);
                    }
//                    else if(!messages.get(position).getUpvoters().contains(androidId)){
//
//                    }
                }
            });


            return convertView;
        }
    }
}
