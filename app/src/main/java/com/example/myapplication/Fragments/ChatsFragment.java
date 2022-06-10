package com.example.myapplication.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.myapplication.Adapter.GroupAdapter;
import com.example.myapplication.Adapter.UserAdapter;
import com.example.myapplication.CodeVideoActivity;
import com.example.myapplication.CreateGroupActivity;
import com.example.myapplication.Model.Chat;
import com.example.myapplication.Model.Chatlist;
import com.example.myapplication.Model.Group;
import com.example.myapplication.Model.GroupList;
import com.example.myapplication.Model.User;
import com.example.myapplication.Notifications.Token;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.ArrayList;
import java.util.List;


public class ChatsFragment extends Fragment {

    private RecyclerView recyclerView, recyclerView2;

    private UserAdapter userAdapter;
    private GroupAdapter groupAdapter;
    private List<User> mUsers;
    private List<Group> mGroups;

    FirebaseUser fuser;
    DatabaseReference reference, reference2;

    //modify
    //private List<String> usersList;
    private List<Chatlist> usersList;
    private List<GroupList> groupLists;

    ImageButton btn_codevideo;

    String t;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("聊天");

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView2 = view.findViewById(R.id.recycler_view2);
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext()));


        fuser = FirebaseAuth.getInstance().getCurrentUser();

        usersList = new ArrayList<>();
        groupLists = new ArrayList<>();

        btn_codevideo = view.findViewById(R.id.btn_codevideo);
        btn_codevideo.setColorFilter(getResources().getColor(R.color.iconcolor));
        btn_codevideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CodeVideoActivity.class));
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
                    Chatlist chatlist = snapshot1.getValue(Chatlist.class);
                    usersList.add(chatlist);
                }

                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference2 = FirebaseDatabase.getInstance().getReference("GroupChatlist").child(fuser.getUid());
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupLists.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
                    GroupList groupList = snapshot1.getValue(GroupList.class);
                    groupLists.add(groupList);
                }

                groupchatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }


    private void chatList()
    {
        mUsers = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
                    User user = snapshot1.getValue(User.class);

                    //if(usersList.contains(user.getId())) mUsers.add(user);

                    for(Chatlist chatlist:usersList)
                    {
                        if(user.getId().equals(chatlist.getId()))
                        {
                            mUsers.add(user);
                        }
                    }
                }

                userAdapter = new UserAdapter(getContext(),mUsers,true, true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void groupchatList()
    {
        mGroups = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Groups");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mGroups.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
                    Group group = snapshot1.getValue(Group.class);

                    //if(usersList.contains(user.getId())) mUsers.add(user);
                    for(GroupList groupList:groupLists)
                    {
                        if(group.getId().equals(groupList.getId()))
                        {
                            mGroups.add(group);
                        }
                    }
                }

                groupAdapter = new GroupAdapter(getContext(),mGroups,true);
                recyclerView2.setAdapter(groupAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}