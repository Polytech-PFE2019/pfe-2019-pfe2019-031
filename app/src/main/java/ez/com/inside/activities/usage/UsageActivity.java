package ez.com.inside.activities.usage;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import ez.com.inside.R;

import static android.graphics.Color.rgb;

public class UsageActivity extends AppCompatActivity
{
    public static final int[][] COLOR_PANEL =
            {{rgb(255, 80, 80), rgb(255, 153, 153)},
                    {rgb(255, 168, 82),rgb(255, 204, 153)},
                    {rgb(255, 255, 82),rgb(255, 255, 153)},
                    {rgb(82, 255, 82),rgb(153, 255, 153)},
                    {rgb(82, 255, 255),rgb(153, 255, 255)},
                    {rgb(82, 82, 255),rgb(153, 153, 255)},
                    {rgb(168, 82, 255),rgb(204, 153, 255)},
                    {rgb(255, 82, 212),rgb(255, 153, 230)}
            };

    public static final int[] COLOR_OTHER = {rgb(121, 85, 72), rgb(161, 136, 127)};

    public static final String EXTRA_APPNAME = "ez.com.inside.extraapptime";
    public static final String EXTRA_APPPKGNAME = "ez.com.inside.extraapppkg";
    public static final String EXTRA_GRAPHMODE = "ez.com.inside.graphmode";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage);

        setTitle(getString(R.string.card_usage_title));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewPager viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new WeeklyFragment(), "Semaine");
        adapter.addFragment(new MonthlyFragment(), "Mois");
        viewPager.setAdapter(adapter);
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
}
