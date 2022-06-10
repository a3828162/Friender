package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendProfileActivity extends AppCompatActivity {

    ImageButton btn_clear, btn_chat, btn_block;
    CircleImageView profile_image;
    TextView txt_name,txt_statesign,txt_birthday;

    String userid;

    Intent intent;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);

        btn_clear = findViewById(R.id.btn_clear);
        btn_chat = findViewById(R.id.btn_chat);
        btn_block = findViewById(R.id.btn_block);
        profile_image = findViewById(R.id.profile_image);
        txt_name = findViewById(R.id.name);
        txt_statesign = findViewById(R.id.edit_statesign);
        txt_birthday = findViewById(R.id.edt_birthdaty);

        intent = getIntent();
        userid = intent.getStringExtra("userid");

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                txt_name.setText(user.getUsername());

                if(user.getStatesign().equals("default"))
                {
                    txt_statesign.setText("他沒有心情可以分享=)");
                }
                else
                {
                    txt_statesign.setText(user.getStatesign());
                }

                if(user.getBirthday().equals("default"))
                {
                    txt_birthday.setText("這個人不想讓大家知道生日=(");
                }
                else
                {
                    txt_birthday.setText(user.getBirthday());
                }

                if(user.getImageURl().equals("default"))
                {
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                }
                else
                {
                    Glide.with(getApplicationContext()).load(user.getImageURl()).into(profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btn_clear.setColorFilter(getResources().getColor(R.color.iconcolor));
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_chat.setColorFilter(getResources().getColor(R.color.iconcolor));
        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(FriendProfileActivity.this, MessageActivity.class);
                intent.putExtra("userid",userid);
                startActivity(intent);
                finish();
            }
        });


        btn_block.setColorFilter(getResources().getColor(R.color.iconcolor));
        btn_block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference tmp = FirebaseDatabase.getInstance().getReference("Friends").child(fuser.getUid()).child(userid);
                if(tmp!=null) tmp.removeValue();


                DatabaseReference tmp2 = FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid()).child(userid);
                if(tmp2 != null) tmp2.removeValue();

                DatabaseReference tmp3 = FirebaseDatabase.getInstance().getReference("Blocks").child(fuser.getUid()).child(userid);
                HashMap<String,String> hashMap = new HashMap();
                hashMap.put("id",userid);
                tmp3.setValue(hashMap);

                Toast.makeText(FriendProfileActivity.this,"Block success",Toast.LENGTH_SHORT).show();

                finish();
            }
        });

    }
}