package com.mitravisual.absensisiswasdnegeri105270.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.mitravisual.absensisiswasdnegeri105270.Admin.Fragment.FragmentDataGuru;
import com.mitravisual.absensisiswasdnegeri105270.Admin.Fragment.FragmentDataSiswa;

public class MyPagerAdapter extends FragmentPagerAdapter {

    public MyPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        // Kembalikan Fragment yang sesuai berdasarkan posisi
        switch (position) {
            case 0:
                return new FragmentDataGuru();
            case 1:
                return new FragmentDataSiswa();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if(position == 0){
            title = "DATA GURU";
        }else {
            title = "DATA SISWA";
        }
        return title;
    }
}
