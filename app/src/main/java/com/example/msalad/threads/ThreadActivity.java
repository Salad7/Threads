package com.example.msalad.threads;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;
import android.widget.TabHost;
import android.widget.Toast;

import static android.R.attr.fragment;
import static android.R.id.tabcontent;

/**
 * Created by cci-loaner on 10/23/17.
 */

public class ThreadActivity extends AppCompatActivity {
    //TabHost tabHost;
    private FragmentTabHost tabHost;
    public PagerAdapter pagerAdapter;
    public FragmentManager fm;
    public TabLayout tabLayout;
    ViewPager mViewPager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbar);
        FragmentManager fragmentManager = getSupportFragmentManager();
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        pagerAdapter = new PagerAdapter(
                fragmentManager);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        //navigationView = (NavigationView) findViewById(R.id.nav_view);
        //drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        tabHost = (FragmentTabHost) findViewById(R.id.tabs_host);
//        tabHost.setup(this,fragmentManager,android.R.id.tabcontent);
//        LocalFragment localFragment = new LocalFragment();
//        tabHost.addTab(tabHost.newTabSpec("Same Room").setIndicator("Same Room"),LocalFragment.class,null);
//        tabHost.addTab(tabHost.newTabSpec("Settings").setIndicator("Settings"),SettingsFragment.class,null);
        setUpTablayout();
  //      ActionBar actionBar = getActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    public void setUpTablayout(){
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                final InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mViewPager.getWindowToken(), 0);


//                if(mAuth.getCurrentUser() == null && position != 1){
//                    Toast.makeText(getApplicationContext(),"Please login or register to use other features",Toast.LENGTH_LONG).show();
//                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
//                    startActivity(i);
//                }if(position == 2){
//
//                    //settingsActivity.
//                }
//                else {
//                    //cameraBtn.setVisibility(View.GONE);
//                }

            }

            @Override
            public void onPageScrolled(int position, float offset, int offsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                if(tab.getPosition() == 0 || tab.getPosition() == 1){
                    tabLayout.setSelectedTabIndicatorColor(Color.BLUE);
                }
                else{
                    tabLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT);

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mViewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++){
            if(i == 0){
                tabLayout.getTabAt(0).setIcon(R.drawable.pin);
                tabLayout.getTabAt(0).setText("Same Building");
            }
            if(i == 1){
                tabLayout.getTabAt(1).setIcon(R.drawable.settings);
                tabLayout.getTabAt(1).setText("Settings");
            }
            //if(i == 2){
                //tabLayout.getTabAt(2).setIcon(R.drawable.accountios2);
              //  tabLayout.getTabAt(2).setText("Account");

//            }
            mViewPager.setCurrentItem(0);
        }
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {


        PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            if(i == 0){
                //fragment = new YurperFragment();
                Fragment fragment = new LocalFragment();
                Bundle args = new Bundle();
                // Our object is just an integer :-P
                fragment.setArguments(args);
                return fragment;

            }
            if(i == 1){
                Fragment fragment = new SettingsFragment();
                Bundle args = new Bundle();
                // Our object is just an integeRelativeLayoutr :-P
                fragment.setArguments(args);
                return fragment;
            }

            return null;
        }


        @Override
        public int getCount() {
            return 2;
        }


    }
}
