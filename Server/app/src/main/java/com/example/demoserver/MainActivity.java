package com.example.demoserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.demoserver.Module.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    List<String> userlist;
    Button btn_resetFlower, btn_resetAnonymous;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_resetFlower = findViewById(R.id.btn_resetflower);
        btn_resetAnonymous = findViewById(R.id.btn_resetanonymous);
        userlist = new ArrayList<>();

        btn_resetFlower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref;
                ref = FirebaseDatabase.getInstance().getReference("Users");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot snapshot1:snapshot.getChildren())
                        {
                            User user = snapshot1.getValue(User.class);
                            DatabaseReference tmp = FirebaseDatabase.getInstance().getReference("Users").child(user.getId()).child("flowersend");
                            tmp.setValue("true");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        btn_resetAnonymous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userlist.clear();
                DatabaseReference t;
                t = FirebaseDatabase.getInstance().getReference("Anonymous");
                t.removeValue();

                DatabaseReference ref;
                ref = FirebaseDatabase.getInstance().getReference("Users");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot snapshot1:snapshot.getChildren())
                        {
                            User user = snapshot1.getValue(User.class);
                            userlist.add(user.getId());
                        }
                        HashMap<String, String> hashMap = new HashMap<>();

                        Collections.shuffle(userlist);

                        int count = 0;
                        for(int i = 0;i<userlist.size();++i)
                        {
                            count++;
                            if(i+1==userlist.size()) hashMap.put(userlist.get(i),"default");
                            else if(count==1) hashMap.put(userlist.get(i),userlist.get(i+1));
                            else if(count == 2)
                            {
                                hashMap.put(userlist.get(i),userlist.get(i-1));
                                count = 0;
                            }
                        }

                        for(Map.Entry<String, String> entry: hashMap.entrySet())
                        {
                            DatabaseReference tmp = FirebaseDatabase.getInstance().getReference("Anonymous").child(entry.getKey());
                            HashMap<String, String> hashMap2 = new HashMap<>();
                            hashMap2.put("id",entry.getValue());
                            tmp.setValue(hashMap2);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


    }
}