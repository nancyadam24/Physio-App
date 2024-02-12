package com.geoxhonapps.physio_app.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.cardview.widget.CardView;
import androidx.viewpager2.widget.ViewPager2;

import com.geoxhonapps.physio_app.ContextFlowUtilities;
import com.geoxhonapps.physio_app.HomePagerAdapter;
import com.geoxhonapps.physio_app.R;
import com.geoxhonapps.physio_app.RestUtilities.AAppointment;
import com.geoxhonapps.physio_app.RestUtilities.ADoctorUser;
import com.geoxhonapps.physio_app.StaticFunctionUtilities;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


public class HomeActivity extends ParentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new HomePagerAdapter(this));
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(
                new NavigationBarView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch(StaticFunctionUtilities.getUser().getAccountType()){
                            case Manager:
                                switch(item.getItemId()){
                                    case R.id.home:
                                        viewPager.setCurrentItem(0);
                                        return true;
                                    case R.id.doctors:
                                        viewPager.setCurrentItem(1);
                                        return true;
                                }
                                break;
                            case Doctor:
                                switch(item.getItemId()){
                                    case R.id.home:
                                        viewPager.setCurrentItem(0);
                                        return true;
                                    case R.id.calendar:
                                        viewPager.setCurrentItem(1);
                                        return true;
                                    case R.id.search:
                                        viewPager.setCurrentItem(2);
                                        return true;
                                    case R.id.appointments:
                                        viewPager.setCurrentItem(3);
                                        return true;
                                }
                                break;
                            case Patient:
                                switch(item.getItemId()){
                                    case R.id.home:
                                        viewPager.setCurrentItem(0);
                                        return true;
                                    case R.id.appointments:
                                        viewPager.setCurrentItem(1);
                                        return true;
                                    case R.id.history:
                                        viewPager.setCurrentItem(2);
                                        return true;
                                }
                                break;
                            default:
                                return false;

                        }
                        return false;
                    }
                }
        );


        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.action_bar_layout);

        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#FFFFFFFF"));

        // Set BackgroundDrawable
        actionBar.setBackgroundDrawable(colorDrawable);
        switch(StaticFunctionUtilities.getUser().getAccountType()){
            case Manager:
                bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_manager);
                break;
            case Doctor:

                bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_doctor);
                break;
            case Patient:
                bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_patient);
        }
        ContextFlowUtilities.dismissLoadingAlert();

    }
    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                ContextFlowUtilities.moveTo(SettingsActivity.class, true);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

}
