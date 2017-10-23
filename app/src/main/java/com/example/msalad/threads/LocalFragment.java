package com.example.msalad.threads;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by cci-loaner on 10/23/17.
 */

public class LocalFragment extends Fragment {



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_local,container,false);
        return super.onCreateView(inflater, container, savedInstanceState);
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
            reply.setText(topics.get(position).getReplies()+"");
            message.setText(topics.get(0).getMessages().get(0).getMsg());
            elapsed.setText(topics.get(0).getTimeStamp()+"");

            upvoteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String android_id = UUID.randomUUID().toString();
                    if(!topics.get(position).getUpvoters().contains(android_id)){
                        topics.get(position).getUpvoters().add(android_id);
                        //Update firebase here
                    }
                }
            });



            return convertView;
        }
    }
}
