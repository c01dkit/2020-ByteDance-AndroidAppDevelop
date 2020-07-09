package com.example.chapter_3;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

/**
 * 使用 ViewPager 和 Fragment 做一个简单版的好友列表界面
 * 1. 使用 ViewPager 和 Fragment 做个可滑动界面
 * 2. 使用 TabLayout 添加 Tab 支持
 * 3. 对于好友列表 Fragment，使用 Lottie 实现 Loading 效果，在 5s 后展示实际的列表，要求这里的动效是淡入淡出
 */
public class Ch3Ex3Activity extends AppCompatActivity {
    private List<Fragment> list = new ArrayList<>();
    private String[] title = {"对话", "通知", "好友列表"};

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_ch3ex3);
            TabLayout myTabLayout = findViewById(R.id.my_tab_layout);
            ViewPager2 myViewPager = findViewById(R.id.my_view_pager);

            list.add(MyFragment.newInstance(title[0], "暂时没有对话消息哦"));
            list.add(MyFragment.newInstance(title[1], "暂时没有通知消息哦"));
            list.add(MyFragment.newInstance(title[2], "好友列表空无一人~"));

            myViewPager.setAdapter(new MyPagerAdapter(this));

            new TabLayoutMediator(myTabLayout, myViewPager, new TabLayoutMediator.TabConfigurationStrategy() {
                @Override
                public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                    tab.setText(title[position]);
                    }
                }).attach();


            }
            public class MyPagerAdapter extends FragmentStateAdapter {
                public MyPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
                    super(fragmentActivity);
                }
                @Override
                    public Fragment createFragment(int position) {
                    return list.get(position);
                }
                @Override
                    public int getItemCount() {
                    return list.size();
                }
            }
}

