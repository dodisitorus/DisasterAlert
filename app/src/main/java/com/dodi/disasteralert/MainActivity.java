package com.dodi.disasteralert;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dodi.disasteralert.fragment.NotificationFragment;
import com.dodi.disasteralert.fragment.SettingFragment;
import com.dodi.disasteralert.models.User;
import com.dodi.disasteralert.utils.UserPreferences;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        ViewPager viewPager = findViewById(R.id.viewpager);
        TabLayout tabLayout = findViewById(R.id.tabs);
        toolbar = findViewById(R.id.toolbar);

        FloatingActionButton floatingAction = findViewById(R.id.floatingActionButton);
        floatingAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, BotActivity.class));
            }
        });

        // set view pager
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        // set toolbar view
        onSetToolbar();
        // setup firebase notification
        registerFirebaseNotification();
    }

    private void registerFirebaseNotification() {
        String token = FirebaseInstanceId.getInstance().getToken();
        UserPreferences.setData(UserPreferences.tokenKey, token, getApplicationContext());
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        String uid = UserPreferences.getData(UserPreferences.userIdKey, getApplicationContext());
        String name = UserPreferences.getData(UserPreferences.nameKey, getApplicationContext());
        String email = UserPreferences.getData(UserPreferences.emailKey, getApplicationContext());

        User user = new User(email, name, token);

        DatabaseReference myRef = database.getReference("users").child(uid);
        myRef.setValue(user);
    }

    //TODO: Call this
    // If the device has been registered previously, hold push notifications when the app is paused
    @Override
    protected void onPause() {
        super.onPause();
    }

    // If the device has been registered previously, ensure the client sdk is still using the notification listener from onCreate when app is resumed
    @Override
    protected void onResume() {
        super.onResume();
    }

    private void onSetToolbar() {
        toolbar.setTitle(R.string.app_name);

        // set option on room chat
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.logout:
                        mAuth.signOut();
                        UserPreferences.setDataBool(UserPreferences.loginKey, false, getApplicationContext());
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                        return true;
                    default:
                        return false;
                }
            }
        });

        Menu menu = toolbar.getMenu();
        MenuItem mSearch = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint("Search");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new NotificationFragment(), "NOTIFICATION");
        adapter.addFragment(new SettingFragment(), "SETTING");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
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

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
