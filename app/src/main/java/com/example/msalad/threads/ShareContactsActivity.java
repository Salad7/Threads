package com.example.msalad.threads;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;

import static android.Manifest.permission.READ_CONTACTS;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cci-loaner on 11/4/17.
 */

public class ShareContactsActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private Button shareBtn;
    private Button cancelBtn;
    private String inviteCode;
    ListView mContactsList;
    ArrayList<Contact> contacts;
    ArrayList<String> recipients;
    private EditText msgET;
    String msg;
    CustomContactsAdapter customContactsAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        if(!getIntent().hasExtra("invite")){
            //Intent i = new Intent(ShareContactsActivity.this,ThreadActivity.class);
            Toast.makeText(ShareContactsActivity.this,"Invalide invite code",Toast.LENGTH_SHORT).show();
            finish();
            //startActivity(i);
        }
        inviteCode = getIntent().getStringExtra("invite");
        cancelBtn = findViewById(R.id.cancel);
        mContactsList =
                findViewById(R.id.contacts_list);
        contacts = new ArrayList<>();
        shareBtn = findViewById(R.id.shareWithFriends);
        customContactsAdapter = new CustomContactsAdapter(this,R.layout.custom_share_contacts,contacts);
        showContacts();
        customContactsAdapter.notifyDataSetChanged();
        customContactsAdapter.setNotifyOnChange(true);
        mContactsList.setAdapter(customContactsAdapter);
        recipients = new ArrayList<>();
        msgET = findViewById(R.id.msgET);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i =0; i < contacts.size(); i++){
                    if(contacts.get(i).getSelected()){
                        recipients.add(contacts.get(i).getPhone());
                    }
                }
                sendSMSMessage();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        msgET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                msg = editable.toString();
            }
        });
    }

    private void showContacts(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {

            Cursor managedCursor = getContentResolver()
                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            new String[]{ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

            try {
                ArrayList<String> foundNames = new ArrayList<>();
                while (managedCursor.moveToNext()) {
                    Contact c = new Contact();
                    c.setName(managedCursor.getString(1));
                    c.setPhone(managedCursor.getString(2));
                    //Log.d("ShareContactsActivity", "Cursor " + managedCursor.getString(1));
                    //Log.d("ShareContactsActivity", "Cursor " + managedCursor.getString(2));
                    if(!foundNames.contains(c.getName())) {
                        contacts.add(c);
                        foundNames.add(c.getName());

                    }

                }
                //}
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }



    }




    protected void sendSMSMessage() {
//        phoneNo = txtphoneNo.getText().toString();
//        message = txtMessage.getText().toString();
//
        Log.d("sendSMSMessage","hit function");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {


            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);

            }
        }
        else{
            SmsManager smsManager = SmsManager.getDefault();
            Log.d("ShareContactsActivity","sendSMSMessage size "+recipients.size());
            if(msg == null || msg.equals("")){
                Toast.makeText(ShareContactsActivity.this,"Enter a message",Toast.LENGTH_SHORT).show();
            }
            else if(recipients.size() == 0){
                Toast.makeText(this,"Please enter atleast one recipent",Toast.LENGTH_SHORT).show();
            }
            else {
                for (int i = 0; i < recipients.size(); i++) {
                    smsManager.sendTextMessage(recipients.get(i), null, msg + ". Invite token:  " + inviteCode, null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    Log.d("ShareContactsActivity","rpermissions request size "+recipients.size());
                    for(int i = 0; i < recipients.size(); i++) {
                        smsManager.sendTextMessage(recipients.get(i), null, "Check out this post on Threads code : "+inviteCode, null, null);
                        Toast.makeText(getApplicationContext(), "SMS sent.",
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }


    public class CustomContactsAdapter extends ArrayAdapter<Contact>{

        List<Contact> contacts;
        Context c;
        int r;

        public CustomContactsAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Contact> objects) {
            super(context, resource, objects);
            r= resource;
            c=context;
            contacts = objects;
        }

        @Override
        public int getCount() {
            return contacts.size();
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) c
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(r,parent,false);
            TextView name = convertView.findViewById(R.id.name);
            TextView phone = convertView.findViewById(R.id.phone);
            ImageView icon = convertView.findViewById(R.id.icon);
            final CheckBox checkBox = convertView.findViewById(R.id.isSelected);

            name.setText(contacts.get(position).getName());
            phone.setText(contacts.get(position).getPhone());
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(contacts.get(position).getIcon(),Color.parseColor("#3f76d2"));
            icon.setImageDrawable(drawable);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    contacts.get(position).setSelected(b);
                    if(b) {
                        Log.d("Contacts ", "Contact now set to recieve sms");
                    }else{
                        Log.d("Contacts ", "Contact now NOT set to recieve sms");

                    }
                }
            });





            return convertView;
        }
    }
}
