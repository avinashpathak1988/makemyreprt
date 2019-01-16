package com.luminousinfoways.makemyreportandroid.ReportList;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.luminousinfoways.makemyreportandroid.R;


public class FragmentReportList1 extends Fragment {

    ViewPager pager;
    private String titles[] = new String[]{"Pending", "Submitted", "Expired"};

    SlidingTabLayout slidingTabLayout;

    public static FragmentReportList1 newInstance() {
        FragmentReportList1 f = new FragmentReportList1();
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reportlist_tab_layout, container, false);

        pager = (ViewPager) rootView.findViewById(R.id.viewpager);
        slidingTabLayout = (SlidingTabLayout) rootView.findViewById(R.id.sliding_tabs);
        pager.setAdapter(new ViewPagerAdapter(getActivity().getSupportFragmentManager(), titles));

        slidingTabLayout.setViewPager(pager);
        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.WHITE;
            }
        });

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}
