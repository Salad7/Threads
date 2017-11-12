package com.example.msalad.threads;

import android.*;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

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
    ImageButton upvoteBtn;
    ImageButton backBtn;
    ImageButton shareBtn;
    Post incomingPost;
    String threadCode;
    EditText et_reply;
    ImageButton send;
    int topicPostion;
    ArrayList<Message> messages;
    ArrayList<String> notifyList;
    int messagePosition;
    MessageAdapter messageAdapter;
    String androidId;
    String token;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

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
        upvoteBtn = findViewById(R.id.upvoteBtnPost);
        upvoteCount = findViewById(R.id.post_upvotes);
        backBtn = findViewById(R.id.backBtn);
        shareBtn = findViewById(R.id.shareBtn);
        token = FirebaseInstanceId.getInstance().getToken();
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PostActivity.this, ThreadActivity.class);
                startActivity(i);
            }
        });
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               showContacts();
            }
        });
        send = findViewById(R.id.send);
        messages = new ArrayList<>();
        if (getIntent().getExtras() != null) {
            incomingPost = (Post) getIntent().getSerializableExtra("post");
            title.setText(getIntent().getStringExtra("tt"));
            setTitle(getIntent().getStringExtra("tt"));
            replies.setText(incomingPost.getReplies() + " Replies");
            timestamp.setText(ThreadFinder.getElapsedTime(incomingPost.getTimeStamp()));
            message.setText(incomingPost.getTopicTitle());
            topicPostion = incomingPost.getPosition();
            notifyList = getIntent().getStringArrayListExtra("notifyList");
            if (incomingPost.getUpvoters() != null) {
                upvoteCount.setText(incomingPost.getUpvoters().size() + "");
            } else {
                upvoteCount.setText(0 + "");
            }
            threadCode = getIntent().getStringExtra("threadCode");
        }

        et_reply = findViewById(R.id.reply_field);
        upvoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String androidId = Settings.Secure.getString(PostActivity.this.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                if (incomingPost.getUpvoters() == null) {
                    ArrayList<String> alist = new ArrayList<String>();
                    alist.add(androidId);
                    HashMap map = new HashMap();
                    map.put("upvoters", alist);
                    threadRef.child("Threads").child(threadCode).child("topics").child("topicPosition").updateChildren(map);
                    upvoteCount.setText(1 + "");
                } else if (!incomingPost.getUpvoters().contains(androidId)) {
                    ArrayList<String> alist = incomingPost.getUpvoters();
                    alist.add(androidId);
                    HashMap map = new HashMap();
                    map.put("upvoters", alist);
                    threadRef.child("Threads").child(threadCode).child("topics").child("topicPosition").updateChildren(map);
                    upvoteCount.setText(alist.size() + "");
                }
            }
        });


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_reply.getText().toString().length() > 0) {
                    DatabaseReference newMessagePath = threadRef.child("Threads").child(threadCode).child("topics").child(topicPostion + "").child("messages").child(messagePosition + "");
                    Log.d("PostActivity", "Topic position " + topicPostion);
                    Message message = new Message();
                    androidId = Settings.Secure.getString(PostActivity.this.getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                    message.setMsg(et_reply.getText().toString());
                    message.setHostUID(androidId);
                    message.setReplies(0);
                    message.setUpvoters(new ArrayList<String>());
                    message.setTimeStamp(ThreadFinder.getTimeStamp());
                    HashMap map = new HashMap();
                    HashMap messageMap = new HashMap();
                    map.put(androidId, "blue");
                    message.setAnonCode(map);
                    message.setPosition(messagePosition);
                    messageMap.put("UID", message.getAnonCode());
                    messageMap.put("timeStamp", message.getTimeStamp());
                    //messageMap.put("upvotes",message.getUpvotes())
                    messageMap.put("replies", message.getReplies());
                    messageMap.put("message", message.getMsg());
                    messageMap.put("position", message.getPosition());
                    messageMap.put("upvotes", message.getUpvotes());
                    HashMap mappy = new HashMap();
                    mappy.put(androidId, "blue");
                    newMessagePath.updateChildren(messageMap);
                    dismissKeyboard(PostActivity.this);
                    et_reply.setText("");
                    queueNotifications(message.getMsg());


                }
            }
        });
        messageAdapter = new MessageAdapter(this, R.layout.custom_message, messages);
        messagesList.setAdapter(messageAdapter);
        loadPosts();


        dismissKeyboard(PostActivity.this);

    }

    public void queueNotifications(final String msg){
        if(!notifyList.contains(token)) {
            notifyList.add(token);
            //Update firebase
            HashMap map = new HashMap();
            map.put("notifyList", notifyList);

            threadRef.child("Threads").child(threadCode).child("topics").child(topicPostion + "").updateChildren(map);
        }
            threadRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.child("Notify").exists()){
                        threadRef.child("Notify").child(0+"").child("message").setValue(msg);
                        threadRef.child("Notify").child(0+"").child("notifyList").setValue(notifyList);
                        threadRef.child("Notify").child(0+"").child("threadTitle").setValue(getIntent().getStringExtra("tt"));
                        threadRef.child("Notify").child(0+"").child("topicPosition").setValue(topicPostion);
                        threadRef.child("Notify").child(0+"").child("threadCode").setValue(threadCode);



                    }
                    else{
                        for(int i = 0; i < ThreadFinder.MAX_NOTIFICATIONS; i++){
                            if(!dataSnapshot.child("Notify").child(i+"").exists()){
                                threadRef.child("Notify").child(i+"").child("message").setValue(msg);
                                threadRef.child("Notify").child(i+"").child("notifyList").setValue(notifyList);
                                threadRef.child("Notify").child(i+"").child("threadTitle").setValue(getIntent().getStringExtra("tt"));
                                threadRef.child("Notify").child(i+"").child("topicPosition").setValue(topicPostion);
                                threadRef.child("Notify").child(i+"").child("threadCode").setValue(threadCode);
                                i = ThreadFinder.MAX_NOTIFICATIONS+1;
                                Log.d("PostActivity"," adding to Notify");
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


    }

    private void showContacts(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }
    }

    public void dismissKeyboard(AppCompatActivity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != activity.getCurrentFocus())
            imm.hideSoftInputFromWindow(activity.getCurrentFocus()
                    .getApplicationWindowToken(), 0);
    }

    public void loadPosts() {
        threadRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int currentLowest = -1;
                messages.clear();
                if (dataSnapshot.child("Threads").child(threadCode).exists()) {
                    DataSnapshot threadPath = dataSnapshot.child("Threads").child(threadCode);
                    if (threadPath.child("topics").exists()) {
                        DataSnapshot topicPath = threadPath.child("topics");
                        if (topicPath.child(topicPostion + "").child("messages").exists()) {
                            DataSnapshot messagesPath = topicPath.child(topicPostion + "").child("messages");
                            int totalMessages = (int) messagesPath.getChildrenCount();
                            int messagesFound = 0;
                            for (int i = 0; i < ThreadFinder.MAX_MESSAGES; i++) {
                                if (messagesPath.child(i + "").exists() && totalMessages >= messagesFound) {
                                    Log.d("PostActivity", " Found message!");
                                    DataSnapshot specificMessagesPath = messagesPath.child(i + "");
                                    //if(specificMessagesPath.child("anonCode").exists()){
                                    Message message = new Message();
                                    try {
                                    if (specificMessagesPath.child("upvoters").exists()) {
                                        message.setUpvoters((ArrayList) specificMessagesPath.child("upvoters").getValue());
                                    }
                                    message.setMsg(specificMessagesPath.child("message").getValue(String.class));
                                    message.setPosition(i);

                                        message.setTimeStamp(specificMessagesPath.child("timeStamp").getValue(Integer.class));

                                    message.setAnonCode((Map) specificMessagesPath.child("anonCode").getValue());
                                    message.setReplies(specificMessagesPath.child("replies").getValue(Integer.class));
                                    } catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    messages.add(message);
                                    messagesFound += 1;
                                } else {
                                    if (currentLowest == -1) {
                                        currentLowest = i;
                                        messagePosition = i;
                                    }
                                }

                            }
                        }
                    } else {
                        messagePosition = 0;
                    }

                }
                replies.setText(messages.size() + " Replies");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        messageAdapter.notifyDataSetChanged();
        messageAdapter.setNotifyOnChange(true);
        Log.d("PostActivity ", messages.size() + "");
        Log.d("PostActivity ", "Message adapter size " + messageAdapter.getCount());
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted
                    // continuePostRequest();

                    Intent i = new Intent(PostActivity.this,ShareContactsActivity.class);
                    i.putExtra("invite",incomingPost.getTopicInvite());
                    startActivity(i);

                } else {
                    Toast.makeText(this, "Until you grant the permission, we cannot display the names", Toast.LENGTH_SHORT).show();
                }
            }
        }

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
            final TextView count;
            ImageButton upvoteBtn;
            label = convertView.findViewById(R.id.posttitle);
            msg = convertView.findViewById(R.id.post_reply);
            count = convertView.findViewById(R.id.post_upvote_count);
            upvoteBtn = convertView.findViewById(R.id.upvote_img);
            if(incomingPost.getHostUID().equals(messages.get(position).getHostUID())) {
                label.setText("OP");
            }
            else{

            }
            msg.setText(messages.get(position).getMsg());
            if(messages.get(position).getUpvoters() == null){
                count.setText(0+"");

            }else {
                count.setText(messages.get(position).getUpvoters().size() + "");
            }
            upvoteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String androidId = Settings.Secure.getString(PostActivity.this.getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                    if(messages.get(position).getUpvoters() == null){
                        ArrayList<String> androids = new ArrayList<String>();
                        androids.add(androidId);
                        HashMap map = new HashMap();
                        map.put("upvoters",androids);
                        threadRef.child("Threads").child(threadCode).child("topics").child(topicPostion+"").child("messages").child(messages.get(position).getPosition()+"")
                                .updateChildren(map);
                        count.setText(1+"");

                    }
                    else if(!messages.get(position).getUpvoters().contains(androidId)){
                        ArrayList<String> androids = messages.get(position).getUpvoters();
                        androids.add(androidId);
                        HashMap map = new HashMap();
                        map.put("upvoters",androids);
                        threadRef.child("Threads").child(threadCode).child("topics").child(topicPostion+"").child("messages").child(messages.get(position).getPosition()+"")
                                .updateChildren(map);
                        count.setText(androids.size());
                        threadRef.child("Pushes").child(androidId).child("ha").setValue("push");
                        Log.d("PostActivty", threadRef.child("Pushes").child(androidId).child("ha").getKey());
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
