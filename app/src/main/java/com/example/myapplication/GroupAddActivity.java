package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.myapplication.Adapter.AddGroupMemberAdapter;
import com.example.myapplication.Model.Friend;
import com.example.myapplication.Model.GroupList;
import com.example.myapplication.Model.User;
import com.example.myapplication.Notifications.Data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GroupAddActivity extends AppCompatActivity {

    private List<User> mUsers;
    private List<Friend> friendList;
    private List<User> mChoose;
    private RecyclerView recyclerView;
    private AddGroupMemberAdapter addGroupMemberAdapter;

    Button btn_addperson;

    String groupid;

    DatabaseReference friendreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_add);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable nav = toolbar.getNavigationIcon();
        nav.setTint(getResources().getColor(R.color.textblackcolor));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 影片說會crash 但我不會
                finish();
                // modify
                //startActivity(new Intent(MessageActivity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        groupid = getIntent().getStringExtra("groupid");

        recyclerView = findViewById(R.id.userlist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        mUsers = new ArrayList<>();
        mChoose = new ArrayList<>();
        friendList = new ArrayList<>();

        btn_addperson = findViewById(R.id.btn_addperson);

        btn_addperson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChoose.clear();
                mChoose = addGroupMemberAdapter.getChooseMember();

                // create group
                DatabaseReference t1;


                HashMap<String,Object> h = new HashMap<>();


                // create groupmember

                t1  = FirebaseDatabase.getInstance().getReference("Groups").child(groupid).child("member").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                h.clear();
                h.put("id",FirebaseAuth.getInstance().getCurrentUser().getUid());
                t1.setValue(h);
                for(User tmpuser:mChoose)
                {
                    t1  = FirebaseDatabase.getInstance().getReference("Groups").child(groupid).child("member").child(tmpuser.getId());
                    h.clear();
                    h.put("id",tmpuser.getId());
                    t1.setValue(h);
                }

                //create grouplist

                t1 = FirebaseDatabase.getInstance().getReference("GroupsList").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(groupid);
                h.clear();
                h.put("id",groupid);
                t1.setValue(h);

                for(User tmpuser:mChoose)
                {
                    t1 = FirebaseDatabase.getInstance().getReference("GroupsList").child(tmpuser.getId()).child(groupid);
                    h.clear();
                    h.put("id",groupid);
                    t1.setValue(h);
                }


                Toast.makeText(GroupAddActivity.this,"Success",Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        friendreference = FirebaseDatabase.getInstance().getReference("Friends").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        friendreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //fix bug
                if(snapshot.exists())
                {
                    friendList.clear();
                    for(DataSnapshot snapshot1:snapshot.getChildren())
                    {
                        Friend friend = snapshot1.getValue(Friend.class);
                        friendList.add(friend);
                    }

                    DatabaseReference tmpR = FirebaseDatabase.getInstance().getReference("Groups").child(groupid).child("member");
                    tmpR.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists())
                            {
                                List<Friend> tmpF = new ArrayList<>();

                                for(DataSnapshot snapshot1:snapshot.getChildren())
                                {
                                    GroupList groupList = snapshot1.getValue(GroupList.class);

                                    for(int i = 0;i<friendList.size();++i)
                                    {
                                        if(friendList.get(i).getId().equals(groupList.getId()))
                                        {
                                            friendList.remove(i);
                                            break;
                                        }
                                    }
                                }

                                readUsers();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readUsers()
    {



        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


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

                addGroupMemberAdapter = new AddGroupMemberAdapter(GroupAddActivity.this,mUsers);
                recyclerView.setAdapter(addGroupMemberAdapter);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}