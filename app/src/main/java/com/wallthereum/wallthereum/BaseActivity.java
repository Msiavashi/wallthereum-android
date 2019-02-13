package com.wallthereum.wallthereum;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.FrameLayout;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";
    private static Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mContext = this;
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
//        super.setContentView(layoutResID);
        DrawerLayout fullView = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout activityContainer = (FrameLayout) fullView.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        super.setContentView(fullView);
        this.initToolbar();
    }

    private void initToolbar(){
        Toolbar mToolbar = findViewById(R.id.toolbar);
        DrawerLayout drawerLayout = findViewById(R.id.activity_container);
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, mToolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        toggle.syncState();

    }

//    private void initNetworkSpinner(){
//        MaterialSpinner networkDropDown = (MaterialSpinner) findViewById(R.id.network_spinner);
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item,
//                Network.getNetwork().getNames());
//        networkDropDown.setAdapter(adapter);
//    }

    public static Context getContext(){
        return mContext;
    }
}
