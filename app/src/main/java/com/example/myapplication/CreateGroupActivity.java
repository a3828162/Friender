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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Adapter.AddGroupMemberAdapter;
import com.example.myapplication.Adapter.UserAdapter;
import com.example.myapplication.Model.Friend;
import com.example.myapplication.Model.User;
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

public class CreateGroupActivity extends AppCompatActivity {

    DatabaseReference friendreference;
    private List<User> mUsers;
    private List<Friend> friendList;
    private List<User> mChoose;
    private RecyclerView recyclerView;
    private Button btn_createg;
    private EditText edt_groupname;
    private AddGroupMemberAdapter addGroupMemberAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create Group");
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

        btn_createg = findViewById(R.id.btn_createg);
        edt_groupname = findViewById(R.id.edt_groupname);

        btn_createg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edt_groupname.getText().toString().equals(""))
                {
                    Toast.makeText(CreateGroupActivity.this,"Blank",Toast.LENGTH_SHORT).show();
                }
                else
                {

                    mChoose.clear();
                    mChoose = addGroupMemberAdapter.getChooseMember();

                    // create group
                    DatabaseReference t1 = FirebaseDatabase.getInstance().getReference("Groups");

                    String s = t1.push().getKey();

                    HashMap<String,Object> h = new HashMap<>();
                    h.put("id",s);
                    h.put("name",edt_groupname.getText().toString());
                    h.put("imageURl","default");
                    t1.child(s).setValue(h);

                    // create groupmember

                    t1  = FirebaseDatabase.getInstance().getReference("Groups").child(s).child("member").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    h.clear();
                    h.put("id",FirebaseAuth.getInstance().getCurrentUser().getUid());
                    t1.setValue(h);
                    for(User tmpuser:mChoose)
                    {
                        t1  = FirebaseDatabase.getInstance().getReference("Groups").child(s).child("member").child(tmpuser.getId());
                        h.clear();
                        h.put("id",tmpuser.getId());
                        t1.setValue(h);
                    }

                    //create grouplist

                    t1 = FirebaseDatabase.getInstance().getReference("GroupsList").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(s);
                    h.clear();
                    h.put("id",s);
                    t1.setValue(h);

                    for(User tmpuser:mChoose)
                    {
                        t1 = FirebaseDatabase.getInstance().getReference("GroupsList").child(tmpuser.getId()).child(s);
                        h.clear();
                        h.put("id",s);
                        t1.setValue(h);
                    }


                    Toast.makeText(CreateGroupActivity.this,"Success",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(CreateGroupActivity.this));

        mUsers = new ArrayList<>();
        mChoose = new ArrayList<>();
        friendList = new ArrayList<>();

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

                addGroupMemberAdapter = new AddGroupMemberAdapter(CreateGroupActivity.this,mUsers);
                recyclerView.setAdapter(addGroupMemberAdapter);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}