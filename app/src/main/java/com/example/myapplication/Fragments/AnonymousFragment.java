package com.example.myapplication.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.AnonymousMessageActivity;
import com.example.myapplication.MessageActivity;
import com.example.myapplication.Model.AnonymousUser;
import com.example.myapplication.Model.User;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class AnonymousFragment extends Fragment {

    FirebaseUser fuser;
    DatabaseReference reference, userreference;

    TextView flower, txt_anonymous;
    Button btn_anonymous;

    String anonymousid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_anonymous, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("匿名");

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        flower = view.findViewById(R.id.txt_flower);
        txt_anonymous = view.findViewById(R.id.anonymous);
        btn_anonymous = view.findViewById(R.id.btn_talking);

        /*reference = FirebaseDatabase.getInstance().getReference("Anonymous").child("URBoWCM3KDYIKAJSdxBfkrEaZao1");
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("id","nTZBpAtuL5O2pKzg5Kc8z1FRZbb2");
        reference.setValue(hashMap);*/


        reference = FirebaseDatabase.getInstance().getReference("Anonymous").child(fuser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AnonymousUser anonymousUser = snapshot.getValue(AnonymousUser.class);
                anonymousid = anonymousUser.getId();
                if(anonymousid.equals("default"))
                {
                    txt_anonymous.setText("抱歉今天您沒有配對到");
                    flower.setText("");
                    btn_anonymous.setVisibility(View.GONE);

                }
                else
                {
                    userreference = FirebaseDatabase.getInstance().getReference("Users").child(anonymousid);
                    userreference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);

                            txt_anonymous.setText(user.getUsername());
                            flower.setText("鮮花數: "+ user.getFlower());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                //txt_anonymous.setText(anonymousid);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        btn_anonymous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), AnonymousMessageActivity.class);
                intent.putExtra("userid",anonymousid);
                startActivity(intent);

            }
        });

        /**/

        // Inflate the layout for this fragment
        return view;
    }
}