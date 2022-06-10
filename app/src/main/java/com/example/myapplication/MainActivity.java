package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.Fragments.AnonymousFragment;
import com.example.myapplication.Fragments.ChatsFragment;
import com.example.myapplication.Fragments.FriendFragment;
import com.example.myapplication.Fragments.GroupFragment;
import com.example.myapplication.Fragments.UsersFragment;
import com.example.myapplication.Model.Chat;
import com.example.myapplication.Model.User;
import com.example.myapplication.Notifications.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Important";

    FirebaseUser firebaseuser;
    DatabaseReference reference;

    TabLayout tabLayout;
    ViewPager viewPager;
    //ViewPagerAdapter viewPagerAdapter;

    ImageButton searchfriend, btn_blockfriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DateFormat df = new SimpleDateFormat("a h:mm");
        String date = df.format(Calendar.getInstance().getTime());

        /*Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");*/

        //username = findViewById(R.id.username);



        firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseuser.getUid());

        /*FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            //Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast

                        //Log.d(TAG, token);
                        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
                        FirebaseUser f = FirebaseAuth.getInstance().getCurrentUser();
                        Token token1 = new Token(token);

                        reference.child(f.getUid()).setValue(token1);

                    }
                });*/


        //searchfriend = findViewById(R.id.btn_searchfriend);
       // btn_blockfriend = findViewById(R.id.btn_blockfriend);

        /*searchfriend.setColorFilter(getResources().getColor(R.color.iconcolor));
        searchfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (!task.isSuccessful()) {
                                    //Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                    return;
                                }

                                // Get new FCM registration token
                                String token = task.getResult();

                                // Log and toast

                                Log.d(TAG, token);
                                //Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();

                                DatabaseReference tokenreference = FirebaseDatabase.getInstance().getReference("Tokens");
                                FirebaseUser f = FirebaseAuth.getInstance().getCurrentUser();
                                Token token1 = new Token(token);

                                tokenreference.child(f.getUid()).setValue(token1);
                                //tokenreference.child(f.getUid()).removeValue();
                            }
                        });

                //Toast.makeText(MainActivity.this, FirebaseMessaging.getInstance().getToken().toString(), Toast.LENGTH_SHORT).show();

                startActivity(new Intent(MainActivity.this,SearchfriendActivity.class));
            }
        });

        btn_blockfriend.setColorFilter(getResources().getColor(R.color.iconcolor));
        btn_blockfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,BlockFriendActivity.class));
            }
        });*/

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(new UsersFragment(), "主頁");
        viewPagerAdapter.addFragment(new FriendFragment(), "朋友");
        //viewPagerAdapter.addFragment(new GroupFragment(),"群組");
        viewPagerAdapter.addFragment(new ChatsFragment(), "聊天");
        viewPagerAdapter.addFragment(new AnonymousFragment(), "匿名");
        //viewPagerAdapter.addFragment(new GroupFragment(), "ZOOM");
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        // modify
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        //tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(2).setIcon(tabIcons[3]);
        tabLayout.getTabAt(3).setIcon(tabIcons[4]);
        //tabLayout.getTabAt(5).setIcon(tabIcons[5]);

        tabLayout.getTabAt(0).getIcon().setColorFilter(Color.parseColor("#a4c49b"),PorterDuff.Mode.SRC_IN);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(getResources().getColor(R.color.tabselec), PorterDuff.Mode.SRC_IN);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                int uiMode = getApplicationContext().getResources().getConfiguration().uiMode;
                tab.getIcon().setColorFilter(getResources().getColor(R.color.tebunselec), PorterDuff.Mode.SRC_IN);

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private int[] tabIcons = {
            R.drawable.person,
            R.drawable.friend,
            R.drawable.img_groups,
            R.drawable.chatimage,
            R.drawable.cruss
            //R.drawable.img_flower
    };

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm)
        {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();

        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title)
        {
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position)
        {
            return titles.get(position);
        }
    }

    private void status(String status)
    {
        //firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseuser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status",status);

        reference.updateChildren(hashMap);
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        //status("online");
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        //status("offline");
    }

    /*@Override
    protected void onPause()
    {
        super.onPause();
        status("offline");
    }*/

}