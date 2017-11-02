package com.example.msalad.threads;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by cci-loaner on 10/23/17.
 */

public class LocalFragment extends Fragment {

    ListView listView;
    LocalFragmentItemAdapter localFragmentItemAdapter;
    List<Topics> topics;
    FloatingActionButton post;
    private FirebaseDatabase database;
    private DatabaseReference threadRef;
    public String threadCode;
    private String threadTitle;
    private int openSpotInFirebase = 0;
    private TextView threadTitleTV;
    private String MY_PREFS_NAME = "MY_PREFS_NAME";



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_local,container,false);
        FirebaseApp.initializeApp(getContext());
        database = FirebaseDatabase.getInstance();
        threadRef = database.getReference();
        SharedPreferences prefs = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        threadCode = prefs.getString("threadCode", "8080");//"No name defined" is the default value.
        post = v.findViewById(R.id.postBtn);
        listView = v.findViewById(R.id.local_list);
        threadTitleTV = v.findViewById(R.id.textView8);
        topics = new ArrayList<>();
        localFragmentItemAdapter = new LocalFragmentItemAdapter(getContext(),R.layout.custom_topic,topics);
        listView.setAdapter(localFragmentItemAdapter);

        addFirebaseLocalThreads();
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("Create a topic");

                final EditText input = new EditText(getActivity());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);

                alertDialog.setPositiveButton("Create",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String topic = input.getText().toString();
                                if (topic.length() > 0) {
                                        Toast.makeText(getActivity(),
                                                "Topic created!", Toast.LENGTH_SHORT).show();
                                    createTopic(topic);
                                }
                            }
                        });

                alertDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();

            }
        });
        return v;
    }

    public void addFirebaseLocalThreads(){
        threadRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Threads").child(threadCode).exists()){
                    topics.clear();
                    DataSnapshot threadPath =  dataSnapshot.child("Threads").child(threadCode);
                    threadTitle = threadPath.child("threadTitle").getValue(String.class);
                    threadTitleTV.setText(threadTitle);
                    ArrayList<String> locUpvoters = new ArrayList<String>();
                    if(threadPath.child("topics").exists()){
                        DataSnapshot topicPath = threadPath.child("topics");
                        for(int i = 0; i < 10; i++){
                            int totalTopics = (int) topicPath.getChildrenCount();
                            int topicsFound = 0;
                            if(topicPath.child("upvoters").exists()){
                                locUpvoters = topicPath.child("upvoters").getValue(ArrayList.class);
                            }
                            Log.d("LocalFragment",totalTopics+" TopicPath"+topicPath.toString());
                            if(topicPath.child(i+"").exists()){
                                DataSnapshot specificThreadPath = topicPath.child(i+"");
                                Topics topic = new Topics();
                                if(specificThreadPath.child("topicTitle").exists() && specificThreadPath.child("timeStamp").exists()){
                                topic.setAnonCode((Map) specificThreadPath.child("anonCode").getValue());
                                topic.setTimeStamp(specificThreadPath.child("timeStamp").getValue(Integer.class));
                                topic.setTopicTitle(specificThreadPath.child("threadTitle").getValue(String.class));
                                topic.setHostUID(specificThreadPath.child("UID").getValue(String.class));
                                topic.setPosition(i);
                                topic.setParent(specificThreadPath.child("parent").getValue(String.class));
                                topic.setReplies(specificThreadPath.child("replies").getValue(Integer.class));
                                    if(specificThreadPath.child("messages").exists()){
                                        topic.setMessages(specificThreadPath.child("messages").getValue(ArrayList.class));
                                    }
                                    topic.setUpvoters(locUpvoters);
                                    locUpvoters.clear();
                                    topics.add(topic);
                                    topicsFound+=1;
                                }
                            }
                            else if(!topicPath.child(i+"").exists()){
                                //if(i < openSpotInFirebase){
                                    openSpotInFirebase = i;
                                //}
                            }

                        }
                    }

                }
                localFragmentItemAdapter.setNotifyOnChange(true);
                localFragmentItemAdapter.notifyDataSetChanged();
                Log.d("topics count ", topics.size()+"");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });






    }

    public void createTopic(String title){
        String androidId = Settings.Secure.getString(getActivity().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if(openSpotInFirebase > 10000){
            openSpotInFirebase = 0;
        }
        HashMap map = new HashMap();
        HashMap anonMap = new HashMap();
        HashMap topicsMap = new HashMap();
        HashMap threadUsers = new HashMap();
        //map.put("threadTitle",title);
        anonMap.put(androidId,"red");
        topicsMap.put("parent",threadCode);
        topicsMap.put("UID",androidId);

            topicsMap.put("position",0);



            topicsMap.put("position",openSpotInFirebase);

        topicsMap.put("replies",0);
        topicsMap.put("upvotes",0);
        topicsMap.put("topicTitle",title);
        int time = (int) (System.currentTimeMillis());
        topicsMap.put("timeStamp",time);
        ArrayList<String> anonUsers = new ArrayList<>();
        anonUsers.add(androidId);
        threadUsers.put("UIDs",anonUsers);
        DatabaseReference threadPath =  threadRef.child("Threads").child(threadCode);

        threadPath.updateChildren(map);
        threadPath.child("topics").child(openSpotInFirebase+"").child("anonCode").updateChildren(anonMap);
        threadPath.child("topics").child(openSpotInFirebase+"").updateChildren(topicsMap);
        threadPath.updateChildren(threadUsers);

    }



    public class LocalFragmentItemAdapter extends ArrayAdapter<Topics>{

        List<Topics> topics;
        Context c;
        int res;
        public LocalFragmentItemAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Topics> objects) {
            super(context, resource, objects);
            topics = objects;
            c = context;
            res = resource;

        }

        @Override
        public int getCount() {
            return topics.size();
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) c
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(res,parent,false);
            TextView upvote = (TextView) convertView.findViewById(R.id.upvote_txt);
            TextView reply = (TextView) convertView.findViewById(R.id.reply_txt);
            TextView message = (TextView) convertView.findViewById(R.id.message_txt);
            TextView elapsed = (TextView) convertView.findViewById(R.id.elapsedTimeTxt);
            Button upvoteBtn = (Button) convertView.findViewById(R.id.upvoteBtn);

            //ImageView cancel = (ImageView) convertView.findViewById(R.id.cancel_iv);
            upvote.setText(topics.get(position).getUpvoters().size()+"");
            reply.setText(topics.get(position).getReplies()+" Replies");
            if(topics.get(0).getMessages() != null) {
                if(topics.get(0).getMessages().get(0) != null) {
                    message.setText(topics.get(0).getMessages().get(0).getMsg());
                }
            }
            elapsed.setText(topics.get(0).getTimeStamp()+"");

            upvoteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String androidId = Settings.Secure.getString(getActivity().getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                    if(!topics.get(position).getUpvoters().contains(androidId)){
                        topics.get(position).getUpvoters().add(androidId);
                        HashMap map = new HashMap<String,ArrayList<String>>();
                        map.put("upvoters",topics);
                        threadRef.updateChildren(map);
                        //Update firebase here
                    }
                }
            });



            return convertView;
        }
    }
}
