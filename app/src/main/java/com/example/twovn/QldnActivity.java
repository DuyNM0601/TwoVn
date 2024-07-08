package com.example.twovn;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.twovn.adapter.Adapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class QldnActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qldh);
        String[] tabTitles = { "Đang giao hàng", "Hoàn thành"};

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager2 = findViewById(R.id.viewPager);
        Adapter adapter = new Adapter(getSupportFragmentManager(), getLifecycle());
        viewPager2.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager2, (tab, i) -> {
            tab.setText(tabTitles[i]);
        }).attach();
    }


}
