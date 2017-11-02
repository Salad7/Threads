package com.example.msalad.threads;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.widget.TabHost;

import static android.R.id.tabcontent;

/**
 * Created by cci-loaner on 10/23/17.
 */

public class ThreadActivity extends AppCompatActivity {
    //TabHost tabHost;
    private FragmentTabHost tabHost;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbar);
        FragmentManager fragmentManager = getSupportFragmentManager();
        tabHost = (FragmentTabHost) findViewById(R.id.tabs_host);
        tabHost.setup(this,fragmentManager,android.R.id.tabcontent);
        LocalFragment localFragment = new LocalFragment();
        tabHost.addTab(tabHost.newTabSpec("Same Room").setIndicator("Same Room"),LocalFragment.class,null);
        tabHost.addTab(tabHost.newTabSpec("Settings").setIndicator("Settings"),SettingsFragment.class,null);

//        TabHost.TabSpec spec = tabHost.newTabSpec("Same Room");
//        spec.setContent(new LocalFragment());
//        spec.setIndicator("Same Room");
//        TabHost.TabSpec spec2 = tabHost.newTabSpec("Settings");
//        spec.setContent(R.layout.fragment_settings);
//        spec.setIndicator("Settings");
//        tabHost.addTab(spec2);

    }
}
