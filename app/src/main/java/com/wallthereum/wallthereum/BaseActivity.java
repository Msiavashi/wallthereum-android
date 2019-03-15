package com.wallthereum.wallthereum;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";
    private static Context mContext;
    private NavigationView mNavigationView;
    private DrawerLayout mNavigationLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        this.mNavigationView = findViewById(R.id.navigationView);
        this.mNavigationLayout = findViewById(R.id.activity_container);
        this.initDrawerMenuItems();
    }

    @Override
    public void setContentView(int layoutResID) {
//        super.setContentView(layoutResID);
        DrawerLayout fullView = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout activityContainer = fullView.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        super.setContentView(fullView);
        this.initToolbar();
    }

    private void initDrawerMenuItems() {
        this.mNavigationView.getMenu().getItem(2).setActionView(R.layout.drawer_menu_item_image);
        this.mNavigationView.getMenu().getItem(1).setActionView(R.layout.drawer_menu_item_image);
        this.mNavigationView.getMenu().getItem(0).setActionView(R.layout.drawer_menu_item_image);
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

//    public void onInviteClicked(MenuItem item) {
//    }

    public void onExitClicked(MenuItem item) {
        finish();
        moveTaskToBack(true);
    }

    public void onAboutClicked(MenuItem item) {
        Intent intent = new Intent(BaseActivity.this, AboutAppActivity.class);
        startActivity(intent);
        mNavigationLayout.closeDrawer(GravityCompat.START);
    }

//    public void onBackupClicked(MenuItem item) {
//    }
}
