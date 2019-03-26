package grpproject.projetgps;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

public class MainActivity extends FragmentActivity {

    private TabLayout tl;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;


       protected void onCreate(Bundle saveInstance){
        super.onCreate(saveInstance);
        setContentView(R.layout.layout);


        //TabLayout
        tl=findViewById(R.id.tabs);
        tl.setTabMode(TabLayout.MODE_FIXED);

        //ViewPager
        viewPager=findViewById(R.id.viewp);
        tl.setupWithViewPager(viewPager);

        //Recuperation FragmentManager
        adapter=new ViewPagerAdapter( getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        //Icons tabs
        tl.getTabAt(0).setIcon(R.drawable.ic_action_home);
        tl.getTabAt(1).setIcon(R.drawable.ic_action_drawmap);
        tl.getTabAt(2).setIcon(R.drawable.ic_action_editmap);
        tl.getTabAt(3).setIcon(R.drawable.ic_action_control);


    }



}
