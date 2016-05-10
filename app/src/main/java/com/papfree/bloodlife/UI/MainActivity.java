package com.papfree.bloodlife.UI;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LevelEndEvent;
import com.crashlytics.android.answers.LevelStartEvent;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.papfree.bloodlife.Model.userData;
import com.papfree.bloodlife.OneFragment;
import com.papfree.bloodlife.R;
import com.papfree.bloodlife.TwoFragment;
import com.papfree.bloodlife.UI.NavigPane.GalleryFragment;
import com.papfree.bloodlife.UI.NavigPane.MainFragment;
import com.papfree.bloodlife.Utils.Constants;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView = null;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Firebase mFirebaseRef;
    private SharedPreferences mSharedPref;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        Answers.getInstance().logLevelStart(new LevelStartEvent()
                .putLevelName("Level 2A"));


        //Set the fragment initially
        MainFragment fragment = new MainFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();

        mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mFirebaseRef = new Firebase(Constants.FIREBASE_URL);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.pager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        userData();

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);




        //Crashlytics for app crash monitoring
        Answers.getInstance().logLevelEnd(new LevelEndEvent()
                .putLevelName("Level 2A")
                .putScore(350)
                .putSuccess(true));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OneFragment(), "ONE");
        adapter.addFragment(new TwoFragment(), "TWO");
        //adapter.addFragment(new ThreeFragment(), "THREE");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            //Set the fragment initially
            MainFragment fragment = new MainFragment();
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            //Set the fragment initially
            GalleryFragment fragment = new GalleryFragment();
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } /*else if (id == R.id.nav_send) {
            showErrorToast("you clicked send");

        }*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
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

    /**
     * Show error toast to users
     */
    private void showErrorToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }



    public void userData() {
        final String mEmail = mSharedPref.getString(Constants.KEY_SIGNUP_EMAIL, mEncodedEmail);

        // Get a reference to our posts
        Firebase ref = new Firebase(Constants.FIREBASE_URL_USERS + "/"+ mEmail);

// Attach an listener to read the data at our posts reference

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                final userData user = new userData();
                if(snapshot.getValue()!=null) {
                    user.setName(snapshot.child(Constants.NAME).getValue().toString());
                    user.setEmail(snapshot.child(Constants.EMAIL).getValue().toString());
                    View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
                    TextView text = (TextView) headerView.findViewById(R.id.username);
                    TextView emailText = (TextView) headerView.findViewById(R.id.email);
                    text.setText(user.getName());
                    emailText.setText(user.getEmail());
                }

                if(snapshot.getValue()== null){

                    Firebase ref = new Firebase(Constants.FIREBASE_URL_ORGS + "/"+ mEmail);

// Attach an listener to read the data at our posts reference

                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            user.setName(snapshot.child("name").getValue().toString());
                            user.setEmail(snapshot.child("email").getValue().toString());
                            View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
                            TextView text = (TextView) headerView.findViewById(R.id.username);
                            TextView emailText = (TextView) headerView.findViewById(R.id.email);
                            text.setText(user.getName());
                            emailText.setText(user.getEmail());
                        }
                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            System.out.println("The read failed: " + firebaseError.getMessage());
                        }
                    });

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }
}