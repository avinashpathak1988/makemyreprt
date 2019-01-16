package com.luminousinfoways.makemyreportandroid.ReportList;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.luminousinfoways.makemyreportandroid.R;
import com.luminousinfoways.makemyreportandroid.activity.LoginActivity;
import com.luminousinfoways.makemyreportandroid.util.Constants;


public class MainActivity extends ActionBarActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private ListView mDrawerList;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.navdrawer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_ab_drawer);
        }
        toolbar.setTitle("Report List");
        drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.report_list, R.string.report_list);
        mDrawerLayout.setDrawerListener(drawerToggle);

        String userName = getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 1).getString(Constants.SP_FULL_NAME, null);
        String[] values = null;
        if(userName != null)
            values = new String[] { userName ,"Dashboard", "Report List", "Change Password","", "Logout" };
        else
            values = new String[] {"" ,"Dashboard", "Report List", "Change Password","", "Logout" };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position) {
                    case 0:
                        //Profile
                        toolbar.setTitle("Profile");
                        FragmentTransaction fProfile = getSupportFragmentManager().beginTransaction();
                        fProfile.replace(R.id.content_frame, FragmentProfile.newInstance());
                        fProfile.commit();
                        mDrawerLayout.closeDrawer(Gravity.START);
                        break;
                    case 1:
                        //Dashboard
                        toolbar.setTitle("Dashboard");
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.content_frame, FragmentDashboard.newInstance());
                        ft.commit();
                        mDrawerLayout.closeDrawer(Gravity.START);

                        break;
                    case 2:
                        //Report List
                        toolbar.setTitle("Report List");
                        FragmentTransaction fReportList = getSupportFragmentManager().beginTransaction();
                        fReportList.replace(R.id.content_frame, FragmentReportlist.newInstance());
                        fReportList.commit();
                        mDrawerLayout.closeDrawer(Gravity.START);

                        break;
                    case 3:
                        //Change Password
                        toolbar.setTitle("Change Password");
                        FragmentTransaction fChangePassword = getSupportFragmentManager().beginTransaction();
                        fChangePassword.replace(R.id.content_frame, FragmentChangePassword.newInstance());
                        fChangePassword.commit();
                        mDrawerLayout.closeDrawer(Gravity.START);

                        break;
                    case 5:
                        //Logout

                        getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putString(Constants.SP_FULL_NAME, null).commit();
                        getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putString(Constants.SP_API_KEY, null).commit();
                        getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putInt(Constants.SP_USER_LOGIN_STS, 0).commit();

                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();

                        break;
                }
            }
        });

        toolbar.setTitle("Report List");
        FragmentTransaction fReportList = getSupportFragmentManager().beginTransaction();
        fReportList.replace(R.id.content_frame, FragmentReportList1.newInstance());
        fReportList.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(Gravity.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

}
