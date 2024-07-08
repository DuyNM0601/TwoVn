package com.example.twovn.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.twovn.ChoThanhToanFragment;
import com.example.twovn.DangGiaoHangFragment;
import com.example.twovn.HoanThanhFragment;
import com.example.twovn.OrderSuccessful;

public class Adapter extends FragmentStateAdapter {

    public Adapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                // Return a new instance of the fragment for the "Chờ thanh toán" tab
                return new DangGiaoHangFragment();
            case 1:
                // Return a new instance of the fragment for the "Đang giao hàng" tab
                return new HoanThanhFragment();

            default:
                return new Fragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
