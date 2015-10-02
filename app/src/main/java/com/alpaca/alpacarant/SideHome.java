package com.alpaca.alpacarant;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by justinyeo on 17/9/15.
 */
public class SideHome extends Fragment implements OnTabChangeListener, OnPageChangeListener {

    private TabHost tabHost;
    private ViewPager viewPager;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    int i = 0;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.side_home_page, container, false);

        i++;

        initViewPager();

        initTabHost();

        return v;
    }

    public class FakeContent implements TabHost.TabContentFactory {

        Context context;

        public FakeContent(Context mcontext) {
            context = mcontext;
        }

        @Override
        public View createTabContent(String tag) {
            View fakeView = new View(context);
            fakeView.setMinimumHeight(0);
            fakeView.setMinimumWidth(0);
            return fakeView;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int selectedItem) {

        tabHost.setCurrentTab(selectedItem);
    }

    // viewPager Listener
    @Override
    public void onPageScrollStateChanged(int state) {

    }

    // tabHost Listener
    @Override
    public void onTabChanged(String tabId) {

        int selectedItem = tabHost.getCurrentTab();
        viewPager.setCurrentItem(selectedItem);

        HorizontalScrollView horizontalScrollView = (HorizontalScrollView) v.findViewById(R.id.h_scroll_view);
        View tabView = tabHost.getCurrentTabView();
        int scrollPos = tabView.getLeft() - (horizontalScrollView.getWidth() - tabView.getWidth()) / 2;
        horizontalScrollView.smoothScrollTo(scrollPos, 0);
    }

    private void initViewPager() {
        viewPager = (ViewPager) v.findViewById(R.id.view_pager);

        List<Fragment> listFragments = new ArrayList<Fragment>();
        listFragments.add(new TabLiveFeed());
        listFragments.add(new TabPostRant());
        listFragments.add(new TabInbox());

        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), listFragments);
        viewPager.setAdapter(myFragmentPagerAdapter);
        viewPager.setOnPageChangeListener(this);
    }

    private void initTabHost() {

        tabHost = (TabHost) v.findViewById(R.id.tabHost);
        tabHost.setup();

        String[] tabNames = {"Livefeed", "Post Rant", "Inbox"};

        for (int i = 0; i < tabNames.length; i++) {
            TabHost.TabSpec tabSpec;
            tabSpec = tabHost.newTabSpec(tabNames[i]);
            tabSpec.setIndicator(tabNames[i]);
            tabSpec.setContent(new FakeContent(getActivity()));
            tabHost.addTab(tabSpec);
        }
        tabHost.setOnTabChangedListener(this);
    }
}
