package com.geoxhonapps.physio_app;

import android.view.MotionEvent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.geoxhonapps.physio_app.fragments.AppointmentFragment;
import com.geoxhonapps.physio_app.fragments.CalendarFragment;
import com.geoxhonapps.physio_app.fragments.DoctorsFragment;
import com.geoxhonapps.physio_app.fragments.HistoryFragment;
import com.geoxhonapps.physio_app.fragments.HomeFragment;
import com.geoxhonapps.physio_app.fragments.SearchFragment;

/**
 * Αυτή η κλάση διαχειρίζεται το Bottom Navigation Bar της εφαρμογής
 */
public class HomePagerAdapter extends FragmentStateAdapter {
    private static int NUM_PAGES = 3;

    public HomePagerAdapter(FragmentActivity fa) {
        super(fa);
        switch(StaticFunctionUtilities.getUser().getAccountType()){
            case Manager:
                NUM_PAGES = 2;
                break;
            case Doctor:
                NUM_PAGES = 4;
                break;
            case Patient:
                NUM_PAGES = 3;
                break;
        }
    }

    @Override
    public Fragment createFragment(int position) {
        // Return a new instance of the fragment for the given position
        switch(StaticFunctionUtilities.getUser().getAccountType()){
            case Manager:
                switch (position) {
                    case 0:
                        return new HomeFragment();
                    case 1:
                        return new DoctorsFragment();
                    default:
                        return null;
                }

            case Doctor:
                switch (position) {
                    case 0:
                        return new HomeFragment();
                    case 1:
                        return new CalendarFragment();
                    case 2:
                        return new SearchFragment();
                    case 3:
                        return new AppointmentFragment();
                   default:
                        return null;
                }

            case Patient:
                switch (position) {
                    case 0:
                        return new HomeFragment();
                    case 1:
                        return new AppointmentFragment();
                    case 2:
                        return new HistoryFragment();
                    default:
                        return null;
                }
            default:
                return null;
        }

    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}

