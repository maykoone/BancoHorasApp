package br.com.maykoone.app.bancohoras;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.Locale;

import br.com.maykoone.app.bancohoras.fragments.ListTimeRecordsFragment;

/**
 * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    public static final int TAB_TODAY = 0;
    public static final int TAB_MONTH = 1;
    private MainActivity mainActivity;
    private SparseArray<Fragment> registeredFragments = new SparseArray<>();

    public SectionsPagerAdapter(MainActivity mainActivity, FragmentManager fm) {
        super(fm);
        this.mainActivity = mainActivity;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a ListTimeRecordsFragment (defined as a static inner class below).
        return ListTimeRecordsFragment.newInstance(position + 1);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        registeredFragments.remove(position);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case TAB_TODAY:
                return mainActivity.getString(R.string.title_section1).toUpperCase(l);
            case TAB_MONTH:
                return mainActivity.getString(R.string.title_section2).toUpperCase(l);

        }
        return null;
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}
