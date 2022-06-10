package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.example.myapplication.Adapter.UserAdapter;
import com.example.myapplication.Model.GroupList;
import com.example.myapplication.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroupPersonNumberActivity extends AppCompatActivity {

    private List<User> mUsers;
    private List<GroupList> mPersonNumber;

    FirebaseUser fuser;
    DatabaseReference reference;
    String groupid;

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_person_number);

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

        recyclerView = findViewById(R.id.userlist);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        groupid = getIntent().getStringExtra("groupid");

        mUsers = new ArrayList<>();
        mPersonNumber = new ArrayList<>();

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference("Groups").child(groupid).child("member");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    mPersonNumber.clear();
                    for(DataSnapshot snapshot1:snapshot.getChildren())
                    {
                        GroupList groupList = snapshot1.getValue(GroupList.class);
                        mPersonNumber.add(groupList);
                    }

                    DatabaseReference tmpR = FirebaseDatabase.getInstance().getReference("Users");
                    tmpR.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists())
                            {
                                for(DataSnapshot snapshot1:snapshot.getChildren())
                                {
                                    User user = snapshot1.getValue(User.class);
                                    for(GroupList groupList:mPersonNumber)
                                    {
                                        if(groupList.getId().equals(user.getId()))
                                        {
                                            mUsers.add(user);
                                            break;
                                        }
                                    }
                                }

                                userAdapter = new UserAdapter(GroupPersonNumberActivity.this,mUsers,false,false);
                                recyclerView.setAdapter(userAdapter);
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
}