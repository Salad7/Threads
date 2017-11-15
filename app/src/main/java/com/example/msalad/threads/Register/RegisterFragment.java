package com.example.msalad.threads.Register;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.msalad.threads.MainActivity;
import com.example.msalad.threads.R;

/**
 * Created by msalad on 11/14/2017.
 */

public  class RegisterFragment extends Fragment {
    Button nextBtn;
    Button submit;
    RegisterActivity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final int fragIndex = getArguments().getInt("frag");
        activity = (RegisterActivity) getArguments().getSerializable("activity");
        View v = null;
        if (fragIndex == 0) {
            v = inflater.inflate(R.layout.fragment_register_0, container, false);
            nextBtn = v.findViewById(R.id.beginBtn);
            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.mViewPager.setCurrentItem(activity.mViewPager.getCurrentItem() + 1);
                }
            });
        } else if (fragIndex == 1) {
            v = inflater.inflate(R.layout.fragment_register_1, container, false);
            nextBtn = v.findViewById(R.id.nextBtn);
            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.mViewPager.setCurrentItem(activity.mViewPager.getCurrentItem() + 1);
                }
            });
        } else if (fragIndex == 2) {
            v = inflater.inflate(R.layout.fragment_register_2, container, false);
            nextBtn = v.findViewById(R.id.contBtn);
            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.mViewPager.setCurrentItem(activity.mViewPager.getCurrentItem() + 1);
                }
            });
        } else if (fragIndex == 3) {
            v = inflater.inflate(R.layout.fragment_register_3, container, false);
            submit = v.findViewById(R.id.submit);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(), MainActivity.class);
                    startActivity(i);
                }
            });

        }

        return v;
    }

}