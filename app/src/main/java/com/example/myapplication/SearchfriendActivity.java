package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.myapplication.Adapter.AddUserAdapter;
import com.example.myapplication.Adapter.UserAdapter;
import com.example.myapplication.Model.AddUser;
import com.example.myapplication.Model.Friend;
import com.example.myapplication.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchfriendActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private AddUserAdapter adduserAdapter;
    private List<AddUser> mUsers;

    MaterialEditText edittxt_search;
    ImageButton btn_search;

    private List<Friend> mFriends;

    private DatabaseReference statusref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchfriend);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Friend");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable nav = toolbar.getNavigationIcon();
        nav.setTint(getResources().getColor(R.color.textblackcolor));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if crash change code
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

        mUsers = new ArrayList<>();
        mFriends = new ArrayList<>();

        edittxt_search = findViewById(R.id.edittxt_search);
        btn_search = findViewById(R.id.btn_search);
        btn_search.setColorFilter(getResources().getColor(R.color.iconcolor));

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference friendreference = FirebaseDatabase.getInstance().getReference("Friends").child(firebaseUser.getUid());
        friendreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mFriends.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
                    Friend friend = snapshot1.getValue(Friend.class);
                    mFriends.add(friend);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!edittxt_search.getText().toString().equals(""))
                {
                    searchUser(edittxt_search.getText().toString());
                }
                else
                {
                    Toast.makeText(SearchfriendActivity.this,"Please input something",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void searchUser(String s)
    {
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();

        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("email")
                .equalTo(s);


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
                    AddUser user = snapshot1.getValue(AddUser.class);

                    if(!user.getId().equals(fuser.getUid()))
                    {
                        mUsers.add(user);
                    }

                }

                adduserAdapter = new AddUserAdapter(SearchfriendActivity.this,mUsers,mFriends);
                recyclerView.setAdapter(adduserAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void status(String status)
    {
        statusref= FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status",status);

        statusref.updateChildren(hashMap);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

}