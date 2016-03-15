package com.antizikagame.control;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Thiago on 15/03/2016.
 */
public class TutoAdpater extends FragmentPagerAdapter {

    private List<Fragment> fragments;

    public TutoAdpater(FragmentManager fm,  List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
