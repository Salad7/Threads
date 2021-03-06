package com.example.msalad.threads;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.github.ag.floatingactionmenu.OptionsFabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;
import static com.example.msalad.threads.R.attr.icon;

/**
 * Created by cci-loaner on 10/23/17.
 */

public class LocalFragment extends Fragment {

    ListView listView;
    LocalFragmentItemAdapter localFragmentItemAdapter;
    List<Topics> topics;

    private FirebaseDatabase database;
    private DatabaseReference threadRef;
    public String threadCode;
    private String threadTitle;
    private int openSpotInFirebase = 0;
    private ArrayList<String> notifyList;
    private TextView threadTitleTV;
    OptionsFabLayout fabWithOptions;
    private String MY_PREFS_NAME = "MY_PREFS_NAME";
    private String token = "";
    String tp;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_local, container, false);
        FirebaseApp.initializeApp(getContext());
        database = FirebaseDatabase.getInstance();
        threadRef = database.getReference();
        SharedPreferences prefs = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        threadCode = prefs.getString("threadCode", "8080");//"No name defined" is the default value.
        listView = v.findViewById(R.id.local_list);
        notifyList = new ArrayList<>();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Post p = new Post();
                Topics t = topics.get(i);
                p.setUpvoters(t.getUpvoters());
                p.setParent(t.getParent());
                p.setPosition(t.getPosition());
                p.setAnonCode(t.getAnonCode());
                p.setHostUID(t.getHostUID());
                p.setMessages(t.getMessages());
                p.setReplies(t.getReplies());
                p.setTimeStamp(t.getTimeStamp());
                p.setTopicTitle(t.getTopicTitle());
                p.setTopicInvite(t.getTopicInvite());
                //p.setInviteCode(t.getInviteCode());
                String tt = threadTitle;
                Intent intent = new Intent(getActivity(), PostActivity.class);
                intent.putExtra("post", p);
                intent.putExtra("tt", tt);
                startActivity(intent);
            }
        });
        threadTitleTV = v.findViewById(R.id.textView8);
        topics = new ArrayList<>();
        localFragmentItemAdapter = new LocalFragmentItemAdapter(getContext(), R.layout.custom_topic, topics);
        listView.setAdapter(localFragmentItemAdapter);

        addFirebaseLocalThreads();

        localFragmentItemAdapter.setNotifyOnChange(true);
        fabWithOptions = (OptionsFabLayout) v.findViewById(R.id.fab_l);

//Set main fab clicklistener.
        fabWithOptions.setMainFabOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(), "Main fab clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        //Set mini fabs clicklisteners.
        fabWithOptions.setMiniFabSelectedListener(new OptionsFabLayout.OnMiniFabSelectedListener() {
            @Override
            public void onMiniFabSelected(MenuItem fabItem) {
                switch (fabItem.getItemId()) {
                    case R.id.fab_add:
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
                        break;
                    case R.id.fab_link:
                        Toast.makeText(getActivity(),
                                fabItem.getTitle() + "clicked!",
                                Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(getActivity());
                        alertDialog2.setTitle("Invited to a thread? join below");

                        final EditText input2 = new EditText(getActivity());
                        // final Button button = new Button(getActivity());
                        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT);
                        input2.setLayoutParams(lp2);
                        input2.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                tp = editable.toString();
                            }
                        });
                        //button.setLayoutParams(lp);
                        alertDialog2.setView(input2);
                        //alertDialog.setView(button)

                        alertDialog2.setPositiveButton("Join",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Log.d("LocalFragment", " Join button hit " + tp);
                                        if (input2.getText().toString().length() > 2) {
                                            threadRef.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    if (dataSnapshot.child("Invites").child(tp).exists()) {
                                                        //Start PostActivty
                                                        Toast.makeText(getActivity(), " Found code! ", Toast.LENGTH_SHORT).show();
                                                        Log.d("LocalFragment", "Found code! ready to start postActivity!");
                                                        String longVersion = dataSnapshot.child("Invites").child(tp).getValue(String.class);
                                                        String[] decoded = LocalFragment.decodeLongVersion(longVersion);
                                                        DataSnapshot topicPath = dataSnapshot.child("Threads").child(decoded[0]).child("topics").child(decoded[1]);
                                                        Post p = new Post();
                                                        p.setTopicInvite(topicPath.child("topicInvite").getValue(String.class));
                                                        p.setTopicTitle(topicPath.child("topicTitle").getValue(String.class));
                                                        p.setTimeStamp(topicPath.child("timeStamp").getValue(Integer.class));
                                                        p.setReplies(topicPath.child("replies").getValue(Integer.class));
                                                        p.setAnonCode((HashMap) topicPath.child("anonCode").getValue());
                                                        p.setHostUID(topicPath.child("UID").getValue(String.class));
                                                        p.setParent(topicPath.child("parent").getValue(String.class));
                                                        p.setPosition(topicPath.child("position").getValue(Integer.class));
                                                        Intent i = new Intent(getActivity(), PostActivity.class);
                                                        i.putExtra("post", p);
                                                        i.putExtra("tt", threadTitle);
                                                        i.putExtra("threadCode", threadCode);
                                                        ArrayList<String> tempNotifyList = new ArrayList<String>();
                                                        tempNotifyList =  (ArrayList<String>) topicPath.child("notifyList").getValue();
                                                        i.putExtra("notifyList",tempNotifyList);
                                                        startActivity(i);
                                                    } else {
                                                        //Toast.makeText(getActivity(), " Code invalid please, try again", Toast.LENGTH_SHORT).show();
                                                       // Log.d("LocalFragment", " Could not find code");

                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        } else {
                                            Toast.makeText(getActivity(), " Please enter more than 2 characters, try again ", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                        alertDialog2.setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                        alertDialog2.show();
                        break;
                    case R.id.fab_resync:
                        Intent i = new Intent(getActivity(),MainActivity.class);
                        startActivity(i);
                    default:
                        break;
                }
            }
        });
        return v;
    }



    public void addFirebaseLocalThreads() {
        threadRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Threads").child(threadCode).exists()) {
                    topics.clear();
                    DataSnapshot threadPath = dataSnapshot.child("Threads").child(threadCode);
                    threadTitle = threadPath.child("threadTitle").getValue(String.class);
                    threadTitleTV.setText(threadTitle);
                    try {
                        getActivity().setTitle(threadTitle);
                        if (threadPath.child("topics").exists()) {
                            DataSnapshot topicPath = threadPath.child("topics");
                            for (int i = 0; i < ThreadFinder.MAX_TOPICS; i++) {
                                int totalTopics = (int) topicPath.getChildrenCount();
                                int topicsFound = 0;
//                            if(topicPath.child(i+"").child("upvoters").exists()){
//                                locUpvoters = (ArrayList) topicPath.child(i+"").child("upvoters").getValue();
//                            }
                                //Log.d("LocalFragment",totalTopics+" TopicPath"+topicPath.toString());
                                if (topicPath.child(i + "").exists()) {
                                    DataSnapshot specificThreadPath = topicPath.child(i + "");
                                    Topics topic = new Topics();
                                    if (specificThreadPath.child("topicTitle").exists() && specificThreadPath.child("timeStamp").exists()) {
                                        topic.setTopicTitle(specificThreadPath.child("topicTitle").getValue(String.class));
                                        topic.setAnonCode((Map) specificThreadPath.child("anonCode").getValue());
                                        topic.setTimeStamp(specificThreadPath.child("timeStamp").getValue(Integer.class));
                                        topic.setHostUID(specificThreadPath.child("UID").getValue(String.class));
                                        topic.setPosition(i);
                                        topic.setUpvoters((ArrayList) specificThreadPath.child("upvoters").getValue());
                                        topic.setParent(specificThreadPath.child("parent").getValue(String.class));
                                        topic.setReplies(specificThreadPath.child("replies").getValue(Integer.class));
                                        topic.setTopicInvite(specificThreadPath.child("topicInvite").getValue(String.class));
                                        topic.setNotifyList((ArrayList) specificThreadPath.child("notifyList").getValue());
                                        if (specificThreadPath.child("messages").exists()) {
                                            topic.setMessages((ArrayList) specificThreadPath.child("messages").getValue());
                                        }
                                        //topic.setUpvoters(locUpvoters);
                                        //locUpvoters.clear();
                                        topics.add(topic);
                                        topicsFound += 1;
                                    }
                                } else if (!topicPath.child(i + "").exists()) {
                                    //if(i < openSpotInFirebase){
                                    openSpotInFirebase = i;
                                    //}
                                }

                            }
                        }
                    } catch (NullPointerException e) {

                    }
                }

                // Log.d("topics count ", topics.size()+"");
                Collections.sort(topics);
                localFragmentItemAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


    }

    public void createTopic(String title) {
        String androidId = Settings.Secure.getString(getActivity().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if (openSpotInFirebase > 10000) {
            openSpotInFirebase = 0;
        }
        //HashMap map = new HashMap();
        HashMap anonMap = new HashMap();
        HashMap topicsMap = new HashMap();
        HashMap threadUsers = new HashMap();
        //map.put("threadTitle",title);
        UUID uuid = UUID.randomUUID();
        String ud = "";
        for (int x = 0; x < 5; x++) {
            ud += uuid.toString().charAt(x);
        }
        anonMap.put(androidId, "red");
        topicsMap.put("topicInvite", ud);
        topicsMap.put("parent", threadCode);
        topicsMap.put("UID", androidId);
        topicsMap.put("position", openSpotInFirebase);
        topicsMap.put("replies", 0);
        topicsMap.put("upvotes", 0);
        topicsMap.put("topicTitle", title);
        ArrayList<String> notify = new ArrayList<>();
        token = FirebaseInstanceId.getInstance().getToken();
        notify.add(token);
        topicsMap.put("notifyList",notify);
        //Log.d("LocalFragment",ThreadFinder.getTimeStamp()+"");
        topicsMap.put("timeStamp", ThreadFinder.getTimeStamp());
        ArrayList<String> anonUsers = new ArrayList<>();
        anonUsers.add(androidId);
        threadUsers.put("UIDs", anonUsers);
        DatabaseReference threadPath = threadRef.child("Threads").child(threadCode);
        //threadPath.updateChildren(map);
        threadPath.child("topics").child(openSpotInFirebase + "").child("anonCode").updateChildren(anonMap);
        threadPath.child("topics").child(openSpotInFirebase + "").updateChildren(topicsMap);
        threadPath.updateChildren(threadUsers);
        threadRef.child("Invites").child(ud).setValue(threadCode + ">" + openSpotInFirebase);

    }


    public class LocalFragmentItemAdapter extends ArrayAdapter<Topics> {

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
            convertView = inflater.inflate(res, parent, false);
            final TextView upvote = (TextView) convertView.findViewById(R.id.upvote_txt);
            TextView reply = (TextView) convertView.findViewById(R.id.reply_txt);
            TextView message = (TextView) convertView.findViewById(R.id.message_txt);
            TextView elapsed = (TextView) convertView.findViewById(R.id.elapsedTimeTxt);
            Button upvoteBtn = (Button) convertView.findViewById(R.id.upvoteBtn);

            //ImageView cancel = (ImageView) convertView.findViewById(R.id.cancel_iv);
//            Log.d("topics size",topics.get(position).upvoters.size()+"");
            if (topics.get(position).getUpvoters() != null) {
                upvote.setText(topics.get(position).getUpvoters().size() + "");
            } else {
                upvote.setText(0 + "");

            }
            if (topics.get(position).getMessages() != null) {
                reply.setText(topics.get(position).getMessages().size() + " Replies");
            } else {
                reply.setText("0 Replies");

            }
            message.setText(topics.get(position).getTopicTitle());
            elapsed.setText(ThreadFinder.getElapsedTime(topics.get(position).getTimeStamp()));

            upvoteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String androidId = Settings.Secure.getString(getActivity().getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                    if (topics.get(position).getUpvoters() == null) {
                        HashMap map = new HashMap<String, ArrayList<String>>();
                        ArrayList<String> upvoters = new ArrayList<String>();
                        upvoters.add(androidId);
                        map.put("upvoters", upvoters);
                        threadRef.child("Threads").child(threadCode).child("topics").child(topics.get(position).getPosition() + "").updateChildren(map);
                    } else if (!topics.get(position).getUpvoters().contains(androidId)) {
                        topics.get(position).getUpvoters().add(androidId);
                        HashMap map = new HashMap<String, ArrayList<String>>();
                        map.put("upvoters", topics.get(position).getUpvoters());
                        threadRef.child("Threads").child(threadCode).child("topics").child(topics.get(position).getPosition() + "").updateChildren(map);
                        //Update firebase here
                    }
                }
            });

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Post p = new Post();
                    Topics t = topics.get(position);
                    p.setUpvoters(t.getUpvoters());
                    p.setParent(t.getParent());
                    p.setPosition(t.getPosition());
                    p.setAnonCode(t.getAnonCode());
                    p.setHostUID(t.getHostUID());
                    p.setMessages(t.getMessages());
                    p.setReplies(t.getReplies());
                    p.setTimeStamp(t.getTimeStamp());
                    p.setTopicTitle(t.getTopicTitle());
                    p.setTopicInvite(t.getTopicInvite());
                    String tt = threadTitle;
                    Intent intent = new Intent(getActivity(), PostActivity.class);
                    intent.putExtra("post", p);
                    intent.putExtra("tt", tt);
                    intent.putExtra("threadCode", threadCode);
                    intent.putExtra("notifyList", topics.get(position).getNotifyList());
                    //intent.putExtra("notifyList",)
                    Log.d("LocalFragment ", "Sending topicInvite " + p.getTopicInvite());
                    startActivity(intent);
                }
            });


            return convertView;
        }


    }
    public static String[] decodeLongVersion(String longVersion) {
        String[] parts = longVersion.split(">");
        Log.d("decodingLongVersion", "Part 1: " + parts[0]);
        Log.d("decodingLongVersion", "Part 2: " + parts[1]);
        return parts;

    }

    public class JoinThreadDialogFragment extends Dialog {
        EditText editText;
        Button joinBtn;
        String tp;
        String tCode;
        String tPosition;
        String longVersion;
        Boolean foundThread = false;

        public JoinThreadDialogFragment(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.custom_dialog_join_thread);
            editText = findViewById(R.id.et_join);
            joinBtn = findViewById(R.id.joinBtn);

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    tp = editable.toString();
                }
            });

            joinBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(getActivity()," Clicked button, edit text is: "+editText.getText().toString(),Toast.LENGTH_SHORT).show();
                    Log.d("LocalFragment", " Join button hit " + tp);
                    if (editText.getText().toString().length() > 2) {

                        initFirebase();
                    } else {
                        Toast.makeText(getActivity(), " Enter more than characters please " + tp, Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

        public void initFirebase() {
            threadRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child("Invites").child(tp).exists()) {
                        //Start PostActivty
                        Toast.makeText(getActivity(), " Found code! ", Toast.LENGTH_SHORT).show();
                        Log.d("LocalFragment", "Found code! ready to start postActivity!");
                        longVersion = dataSnapshot.child("Invites").child(tp).getValue(String.class);
                        String[] decoded = decodeLongVersion(longVersion);
                        DataSnapshot topicPath = dataSnapshot.child("Threads").child(decoded[0]).child("topics").child(decoded[1]);
                        Post p = new Post();
                        p.setTopicInvite(topicPath.child("topicInvite").getValue(String.class));
                        p.setTopicTitle(topicPath.child("topicTitle").getValue(String.class));
                        p.setTimeStamp(topicPath.child("timeStamp").getValue(Integer.class));
                        p.setReplies(topicPath.child("replies").getValue(Integer.class));
                        p.setAnonCode((HashMap) topicPath.child("anonCode").getValue());
                        p.setHostUID(topicPath.child("UID").getValue(String.class));
                        p.setParent(topicPath.child("parent").getValue(String.class));
                        p.setPosition(topicPath.child("position").getValue(Integer.class));
                        Intent i = new Intent(getActivity(), PostActivity.class);
                        i.putExtra("post", p);
                        i.putExtra("tt", threadTitle);
                        i.putExtra("threadCode", threadCode);
                        i.putExtra("notifyList",notifyList);
                        startActivity(i);
                    } else {
                        Toast.makeText(getActivity(), " Code is invalid, please, try again", Toast.LENGTH_SHORT).show();
                        Log.d("LocalFragment", " Could not find code");

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            //threadRef.removeEventListener(t);


        }

        @Override
        public void onBackPressed() {
            super.onBackPressed();
            Intent i = new Intent(getActivity(),MainActivity.class);
            startActivity(i);
        }

        public String[] decodeLongVersion(String longVersion) {
            String[] parts = longVersion.split(">");
            Log.d("decodingLongVersion", "Part 1: " + parts[0]);
            Log.d("decodingLongVersion", "Part 2: " + parts[1]);
            return parts;

        }


    }
}
