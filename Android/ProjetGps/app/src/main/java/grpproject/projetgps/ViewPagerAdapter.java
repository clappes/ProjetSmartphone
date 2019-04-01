package grpproject.projetgps;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> tab;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        tab=new ArrayList<Fragment>();
        tab.add(new FragmentHome());
        tab.add(new FragmentOne());
        tab.add(new FragmentTwo());
        tab.add(new FragmentThree());


    }
    @Override
    public Fragment getItem(int i) {
       return  tab.get(i);
    }

    @Override
    public int getCount() {
        return tab.size();
    }

}
