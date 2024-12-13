package com.example.movie.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.movie.fragment.FavoriteFragment;
import com.example.movie.fragment.HistoryFragment;
import com.example.movie.fragment.HomeFragment;

public class MyViewPagerAdapter extends FragmentStateAdapter {

    // Constructor để khởi tạo adapter với một FragmentActivity
    public MyViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity); // Gọi constructor của lớp cha (FragmentStateAdapter)
    }

    // Phương thức tạo ra một Fragment tương ứng với vị trí đã cho trong ViewPager
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Sử dụng switch-case để xác định Fragment nào sẽ được trả về dựa trên vị trí
        switch (position) {
            case 1: // Nếu vị trí là 1, trả về FavoriteFragment
                return new FavoriteFragment();

            case 2: // Nếu vị trí là 2, trả về HistoryFragment
                return new HistoryFragment();

            default: // Mặc định trả về HomeFragment cho vị trí khác
                return new HomeFragment();
        }
    }

    // Phương thức trả về tổng số lượng item (Fragment) có trong ViewPager
    @Override
    public int getItemCount() {
        return 3; // Trả về 3 Fragment: Home, Favorite, History
    }
}

