package com.example.msalad.threads;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.TabHost;

/**
 * Created by cci-loaner on 10/23/17.
 */

public class ThreadActivity extends AppCompatActivity {
    TabHost tabHost;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbar);
        tabHost = (TabHost) findViewById(R.id.tab_host);
        tabHost.setup();
        TabHost.TabSpec spec = tabHost.newTabSpec("Same Room");
        spec.setContent(R.layout.fragment_local);
        spec.setIndicator("Same Room");
        TabHost.TabSpec spec2 = tabHost.newTabSpec("Settings");
        spec.setContent(R.layout.fragment_settings);
        spec.setIndicator("Settings");
        tabHost.addTab(spec2);

    }
}
