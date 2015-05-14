package pl.szalach.krzysztof.trellocardsmanager.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import pl.szalach.krzysztof.trellocardsmanager.api.model.TrelloList;
import pl.szalach.krzysztof.trellocardsmanager.fragments.TrelloListFragment;

/**
 * Created by kszalach on 2015-05-14.
 */
public class ListAdapter extends FragmentStatePagerAdapter {

    private ArrayList<TrelloList> mLists;

    public ListAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setItems(ArrayList<TrelloList> lists) {
        mLists = lists;
    }

    @Override
    public Fragment getItem(int position) {
        return mLists == null ? null : TrelloListFragment.newInstance(mLists, position);
    }

    @Override
    public int getCount() {
        return mLists == null ? 0 : mLists.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mLists == null ? null : mLists.get(position).getName();
    }
}
