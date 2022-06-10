package com.example.myapplication.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.myapplication.Adapter.GroupAdapter;
import com.example.myapplication.Adapter.UserAdapter;
import com.example.myapplication.BlockFriendActivity;
import com.example.myapplication.CreateGroupActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.MessageActivity;
import com.example.myapplication.Model.Friend;
import com.example.myapplication.Model.Group;
import com.example.myapplication.Model.GroupList;
import com.example.myapplication.Model.User;
import com.example.myapplication.Notifications.Token;
import com.example.myapplication.R;
import com.example.myapplication.SearchfriendActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class FriendFragment extends Fragment {
    private RecyclerView recyclerView,recyclerView2;

    private UserAdapter userAdapter;
    private GroupAdapter groupAdapter;
    private List<User> mUsers;
    private List<Group> mGroups;

    MaterialEditText search_users;
    DatabaseReference friendreference, grouplistreference;
    private List<Friend> friendList;

    private List<GroupList> grouplistList;

    ImageButton searchfriend, btn_blockfriend,btn_expandfriend,btn_iconperson, btn_expandgroup, btn_icongroup, btn_creategroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend, container, false);

        /*DatabaseReference t1 = FirebaseDatabase.getInstance().getReference("Groups");

        String s = t1.push().getKey();

        HashMap<String,Object> h = new HashMap<>();
        h.put("id",s);
        h.put("name","testgroup1");
        h.put("imageURl","default");
        t1.child(s).setValue(h);*/

        /*DatabaseReference t1  = FirebaseDatabase.getInstance().getReference("Groups").child("-N1dEee_yLQC5a7Z03hg").child("member").child("mUDgHPZVjXSobEr9haVdfE7kRVq1");
        HashMap<String,Object> h = new HashMap<>();
        h.put("id","mUDgHPZVjXSobEr9haVdfE7kRVq1");
        t1.setValue(h);*/

        /*DatabaseReference t1 = FirebaseDatabase.getInstance().getReference("GroupsList").child("sZcc2UFzNVThhxgMZjk2y9rRbSt1").child("-N1dEee_yLQC5a7Z03hg");
        HashMap<String,Object> h = new HashMap<>();
        h.put("id","-N1dEee_yLQC5a7Z03hg");
        t1.setValue(h);

        t1 = FirebaseDatabase.getInstance().getReference("GroupsList").child("mUDgHPZVjXSobEr9haVdfE7kRVq1").child("-N1dEee_yLQC5a7Z03hg");
        HashMap<String,Object> h2 = new HashMap<>();
        h2.put("id","N1dEee_yLQC5a7Z03hg");
        t1.setValue(h2);

        t1 = FirebaseDatabase.getInstance().getReference("GroupsList").child("DBjYgnnW59QI4ILV1OQAEQ1hKZl2").child("-N1dEee_yLQC5a7Z03hg");
        HashMap<String,Object> h3 = new HashMap<>();
        h3.put("id","-N1dEee_yLQC5a7Z03hg");
        t1.setValue(h3);*/


        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("朋友");

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView2 = view.findViewById(R.id.recycler_view2);
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsers = new ArrayList<>();
        mGroups = new ArrayList<>();
        friendList = new ArrayList<>();
        grouplistList = new ArrayList<>();

        btn_expandfriend = view.findViewById(R.id.btn_expandfriend);
        btn_expandfriend.setColorFilter(getResources().getColor(R.color.iconcolor));
        btn_expandfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recyclerView.getVisibility() == View.VISIBLE)
                {
                    recyclerView.setVisibility(View.GONE);
                    btn_expandfriend.setImageResource(R.drawable.img_arrowdropup);
                }
                else if(recyclerView.getVisibility() == View.GONE)
                {
                    recyclerView.setVisibility(View.VISIBLE);
                    btn_expandfriend.setImageResource(R.drawable.img_arrowdropdown);
                }
            }
        });

        btn_iconperson = view.findViewById(R.id.icon_person);
        btn_iconperson.setColorFilter(getResources().getColor(R.color.iconcolor));

        btn_expandgroup = view.findViewById(R.id.btn_expandgroup);
        btn_expandgroup.setColorFilter(getResources().getColor(R.color.iconcolor));
        btn_expandgroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recyclerView2.getVisibility() == View.VISIBLE)
                {
                    recyclerView2.setVisibility(View.GONE);
                    btn_expandgroup.setImageResource(R.drawable.img_arrowdropup);
                }
                else if(recyclerView2.getVisibility() == View.GONE)
                {
                    recyclerView2.setVisibility(View.VISIBLE);
                    btn_expandgroup.setImageResource(R.drawable.img_arrowdropdown);
                }
            }
        });

        btn_creategroup = view.findViewById(R.id.btn_creategroup);
        btn_creategroup.setColorFilter(getResources().getColor(R.color.iconcolor));
        btn_creategroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CreateGroupActivity.class));
            }
        });

        btn_icongroup = view.findViewById(R.id.icon_group);
        btn_icongroup.setColorFilter(getResources().getColor(R.color.iconcolor));

        friendreference = FirebaseDatabase.getInstance().getReference("Friends").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        friendreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //fix bug
                friendList.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
                    Friend friend = snapshot1.getValue(Friend.class);
                    friendList.add(friend);
                }

                readUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        grouplistreference = FirebaseDatabase.getInstance().getReference("GroupsList").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        grouplistreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    grouplistList.clear();
                    for(DataSnapshot snapshot1:snapshot.getChildren())
                    {
                        //important
                        GroupList groupList = new GroupList(snapshot1.getKey());
                        grouplistList.add(groupList);
                    }

                    readGroups();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        search_users = view.findViewById(R.id.search_users);
        search_users.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // modify toLowercase
                searchUsers(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchfriend = view.findViewById(R.id.btn_searchfriend);
         btn_blockfriend = view.findViewById(R.id.btn_blockfriend);

        searchfriend.setColorFilter(getResources().getColor(R.color.iconcolor));
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


                                //Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();

                                DatabaseReference tokenreference = FirebaseDatabase.getInstance().getReference("Tokens");
                                FirebaseUser f = FirebaseAuth.getInstance().getCurrentUser();
                                Token token1 = new Token(token);

                                tokenreference.child(f.getUid()).setValue(token1);
                                //tokenreference.child(f.getUid()).removeValue();
                            }
                        });

                //Toast.makeText(MainActivity.this, FirebaseMessaging.getInstance().getToken().toString(), Toast.LENGTH_SHORT).show();

                startActivity(new Intent(getActivity(), SearchfriendActivity.class));
            }
        });

        btn_blockfriend.setColorFilter(getResources().getColor(R.color.iconcolor));
        btn_blockfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), BlockFriendActivity.class));
            }
        });

        return view;
    }

    private void searchUsers(String s)
    {
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();

        if(search_users.getText().toString().equals(""))
        {
            userAdapter = new UserAdapter(getContext(),mUsers,false, true);
            recyclerView.setAdapter(userAdapter);
            return;
        }



        // modify toLowerCase
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("search")
                .startAt(s)
                .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //mUsers.clear();

                List<User> usersearch = new ArrayList<>();

                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
                    User user = snapshot1.getValue(User.class);

                    for(Friend f: friendList)
                    {
                        if(!user.getId().equals(fuser.getUid())&&f.getId().equals(user.getId()))
                        {
                            //mUsers.add(user);
                            usersearch.add(user);
                        }
                    }
                }

                userAdapter = new UserAdapter(getContext(),usersearch,false, true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readGroups()
    {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(search_users.getText().toString().equals(""))
                {
                    mGroups.clear();
                    for(DataSnapshot snapshot1:snapshot.getChildren())
                    {
                        Group group = snapshot1.getValue(Group.class);

                        assert group!=null;
                        assert firebaseUser != null;

                        for(GroupList groupList:grouplistList)
                        {
                            if(groupList.getId().equals(group.getId()))
                            {
                                mGroups.add(group);
                            }
                        }

                    }

                    groupAdapter = new GroupAdapter(getContext(),mGroups,false);
                    recyclerView2.setAdapter(groupAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readUsers()
    {

        /*FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Friends")
                .child(firebaseUser.getUid()).child("ZjiDMG6lLAguHDsJ0QnepBCTwU02");

        reference.child("id").setValue("ZjiDMG6lLAguHDsJ0QnepBCTwU02");*/


        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(search_users.getText().toString().equals(""))
                {
                    mUsers.clear();
                    for(DataSnapshot snapshot1 : snapshot.getChildren())
                    {
                        User user = snapshot1.getValue(User.class);

                        Friend finto = new Friend(user.getId());

                        assert user != null;
                        assert firebaseUser != null;


                        for(Friend f:friendList)
                        {
                            if(!user.getId().equals(firebaseUser.getUid())&&f.getId().equals(user.getId()))
                            {
                                mUsers.add(user);
                            }
                        }
                    }

                    //之後再寫
                    //Collections.sort(list, new SortById());

                    userAdapter = new UserAdapter(getContext(),mUsers,false, true);
                    recyclerView.setAdapter(userAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        search_users.setText("");
    }

    @Override
    public void onStart() {
        super.onStart();

        // put in onStart or onPause are fine
        //search_users.setText("");
    }
}