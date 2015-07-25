package br.com.maykoone.app.bancohoras;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import br.com.maykoone.app.bancohoras.db.ControleDatabaseHelper;
import br.com.maykoone.app.bancohoras.db.RegistroPontoEntity;
import br.com.maykoone.app.bancohoras.fragments.ListTimeRecordsFragment;
import br.com.maykoone.app.bancohoras.fragments.TimePickerDialogFragment;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener, TimePickerDialogFragment.OnTimeSelectedListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                DialogFragment fragment = new TimePickerDialogFragment();
                fragment.show(getSupportFragmentManager(), "timerPicker");
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTimeSelected(String time) {
        Log.i("onTimeSelected", "onTimeSelected");
        RegistroPontoEntity registroPonto = new RegistroPontoEntity(time);
        ControleDatabaseHelper db = new ControleDatabaseHelper(getApplicationContext());
        db.addRegistroPonto(registroPonto);
        ListTimeRecordsFragment currentFragment = (ListTimeRecordsFragment) mSectionsPagerAdapter.getRegisteredFragment(mViewPager.getCurrentItem());
        currentFragment.notifyUpdate(registroPonto);
//        currentFragment.notifyUpdate();
    }

    @Override
    public void onTimeSelected(String time, Integer id) {
        Log.i("onTimeSelected", "onTimeSelected");
        RegistroPontoEntity registroPonto = new RegistroPontoEntity(id, time);
        ControleDatabaseHelper db = new ControleDatabaseHelper(getApplicationContext());
        db.updateRegistroPonto(registroPonto);
        ListTimeRecordsFragment currentFragment = (ListTimeRecordsFragment) mSectionsPagerAdapter.getRegisteredFragment(mViewPager.getCurrentItem());
        currentFragment.notifyUpdate(registroPonto);
//        currentFragment.notifyUpdate();
    }

}
