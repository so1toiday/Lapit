package com.example.lapit.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.lapit.ui.fragment.ChatsFragment;
import com.example.lapit.ui.fragment.FriendsFragment;
import com.example.lapit.ui.fragment.RequestFragment;

public class AdapterViewpager extends FragmentStatePagerAdapter {


    public AdapterViewpager(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new RequestFragment();
                break;
            case 1:
                fragment=new ChatsFragment();
                break;
            default:
                fragment=new FriendsFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title="";
        switch (position) {
            case 0:
               title="REQUESTS";
                break;
            case 1:
                title="CHATS";
                break;
            default:
                title="FRIENDS";
                break;
        }
        return title;
    }
}
