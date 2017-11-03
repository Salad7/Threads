package com.example.msalad.threads;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by cci-loaner on 10/23/17.
 */

public class SettingsFragment extends Fragment {
    ListView list;
    private FirebaseDatabase database;
    private DatabaseReference threadRef;
    private ArrayList<Setting> settingArrayList;
    private SettingsAdapter settingsAdapter;
    String androidId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings,container,false);
        list = v.findViewById(R.id.settings_list);
        FirebaseApp.initializeApp(getActivity());
        database = FirebaseDatabase.getInstance();
        threadRef = database.getReference();
        settingArrayList = new ArrayList<>();
        settingsAdapter = new SettingsAdapter(getContext(),R.layout.custom_settings,settingArrayList);
        list.setAdapter(settingsAdapter);
        androidId = Settings.Secure.getString(getActivity().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        loadFirebase();
        settingsAdapter.setNotifyOnChange(true);

        return v;
    }

    public void loadFirebase(){
        threadRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                settingArrayList.clear();
                for (int i = 0; i < ThreadFinder.MAX_SETTINGS_THREAD; i++) {
                    if (dataSnapshot.child("Anons").child(androidId).child(i + "").exists()) {
                        Log.d("SettingsFragment ","Found setting! position "+i);
                        DataSnapshot settingPath = dataSnapshot.child("Anons").child(androidId).child(i + "");
                        Setting setting = new Setting();
                        setting.setName(settingPath.child("threadName").getValue(String.class));
                        setting.setTimeStamp(settingPath.child("timeStamp").getValue(Integer.class));
                        setting.setThreadCode(settingPath.child("threadCode").getValue(String.class));
                        //Log.d("SettingsFragment ","Name is "+setting.getName());
                        settingArrayList.add(setting);
                    }
                }
                Log.d("SettingsFragment ","Size of SettingsAdapter "+settingsAdapter.getCount());
                settingsAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public class SettingsAdapter extends ArrayAdapter<Setting>{
        Context context;
        int res;
        List<Setting> settings;

        public SettingsAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Setting> objects) {
            super(context, resource, objects);
            this.context = context;
            res = resource;
            settings = objects;
        }

        @Override
        public int getCount() {
            return settings.size();
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(res,parent,false);
            TextView title = convertView.findViewById(R.id.topicTitleTV);
            TextView elapsed = convertView.findViewById(R.id.creationDate);
            TextView leave = convertView.findViewById(R.id.leaveBtn);
            title.setText(settings.get(position).getName());
            elapsed.setText(ThreadFinder.getElapsedTime(settings.get(position).getTimeStamp()));
            leave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setMessage("Are you sure you want to report this thread?");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Toast.makeText(getContext(),"Thank you, this thread has been reported.",Toast.LENGTH_SHORT).show();
                                    dialog.cancel();
                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
            });
            return convertView;
        }
    }
}
