package ez.com.inside.activities.network;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;


import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.viewpager.widget.ViewPager;
import ez.com.inside.R;
import ez.com.inside.dialogs.PhonePermissionDialog;

/**
 * This activity is a draft. v0 written to see how far we can go with network stats. 
 */

public class NetworkActivity extends AppCompatActivity {


    private static final int REQUEST_PERMISSION_RESPONSE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);

        setTitle("Données internet");

        checkPhonePermission();


        ViewPager viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager)
    {
        NetworkActivity.ViewPagerAdapter adapter = new NetworkActivity.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new WifiFragment(), "WIFI");
        adapter.addFragment(new NetworkFragment(), "DATA.");
        viewPager.setAdapter(adapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch(requestCode)
        {
            case REQUEST_PERMISSION_RESPONSE :
            {
                /* Go to ErrorAuthorizationActivity if the permission has not been granted.
                 * And kill this activity so the user cannot just press Back and continue.
                 */
                if(!hasPhonePermission())
                {
                    finish();
                }
            }
        }
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter
    {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }



    /************************************************
     *************** Permissions methods ************
     ***********************************************/
    private void checkPhonePermission(){
        if(!hasPhonePermission()){
            DialogFragment fragment = new PhonePermissionDialog();
            fragment.show(getSupportFragmentManager(), "autorisation");
            return;
        }
    }

    public void requestUsageStatsPermission(){
        String packagename = getApplicationContext().getPackageName();
        startActivityForResult(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packagename, null)),REQUEST_PERMISSION_RESPONSE);
    }

    private boolean hasPhonePermission(){
        String permission = "android.permission.READ_PHONE_STATE";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

}
