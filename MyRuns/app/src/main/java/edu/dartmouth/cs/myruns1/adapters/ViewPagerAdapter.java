package edu.dartmouth.cs.myruns1.adapters;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import edu.dartmouth.cs.myruns1.fragments.HistoryFragment;
import edu.dartmouth.cs.myruns1.fragments.StartFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private HistoryFragment frag;


    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        if (position == 0) {
            fragment = StartFragment.newInstance();
            return fragment;
        }
        else {
            frag = HistoryFragment.newInstance();
            return frag;
        }
    }

    public HistoryFragment getFrag() {
        return frag;
    }

    @Override
    public int getItemCount() {
        return 2; // because 2 fragments -- start and history
    }
}