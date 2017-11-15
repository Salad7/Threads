package com.example.msalad.threads.Register;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.example.msalad.threads.LocalFragment;
import com.example.msalad.threads.MainActivity;
import com.example.msalad.threads.R;
import com.example.msalad.threads.SettingsFragment;

import java.io.Serializable;

/**
 * Created by msalad on 11/14/2017.
 */

public class RegisterActivity extends AppCompatActivity implements Serializable {
    ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mViewPager = (ViewPager) findViewById(R.id.viewpager2);
        setUpTablayout();



    }
    public void setUpTablayout() {
        mViewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                final InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mViewPager.getWindowToken(), 0);
            }

            @Override
            public void onPageScrolled(int position, float offset, int offsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
            mViewPager.setCurrentItem(0);

    }


    public class PagerAdapter extends FragmentStatePagerAdapter {


        PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {

                //fragment = new YurperFragment();
                Fragment fragment = new RegisterFragment();
                Bundle args = new Bundle();
                // Our object is just an integer :-P
                args.putInt("frag",i);
                args.putSerializable("activity",RegisterActivity.this);
                fragment.setArguments(args);
                return fragment;
        }


        @Override
        public int getCount() {
            return 4;
        }


    }


}


