package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.Adapter.AnonymousMessageAdapter;
import com.example.myapplication.Adapter.MessageAdapter;
import com.example.myapplication.Fragments.APIService;
import com.example.myapplication.Model.AnonymousChat;
import com.example.myapplication.Model.Chat;
import com.example.myapplication.Model.User;
import com.example.myapplication.Notifications.Client;
import com.example.myapplication.Notifications.Data;
import com.example.myapplication.Notifications.MyResponse;
import com.example.myapplication.Notifications.Sender;
import com.example.myapplication.Notifications.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnonymousMessageActivity extends AppCompatActivity {

    TextView username;

    FirebaseUser fuser;
    DatabaseReference reference, flowerReference, flowercountReference;

    Intent intent;

    AnonymousMessageAdapter messageAdapter;
    List<AnonymousChat> mchat;

    RecyclerView recyclerView;

    ImageButton btn_send, btn_flower;
    EditText text_send;

    String flowersend;

    //modify
    String userid;

    APIService apiService;

    int userFlower;

    boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anonymous_message);

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


        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        btn_flower = findViewById(R.id.btn_flower);

        btn_send.setColorFilter(getResources().getColor(R.color.iconcolor));
        btn_flower.setColorFilter(getResources().getColor(R.color.iconcolor));

        text_send = findViewById(R.id.text_send);

        intent = getIntent();
        //modify
        userid = intent.getStringExtra("userid");

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                String msg = text_send.getText().toString();
                if(!msg.equals(""))
                {
                    sendMessage(fuser.getUid(),userid,msg);
                }
                else
                {
                    Toast.makeText(AnonymousMessageActivity.this,"You can't send empty message!",Toast.LENGTH_SHORT).show();
                }

                text_send.setText("");
            }
        });


        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);
                username.setText(user.getUsername());

                readMessage(fuser.getUid(),userid, user.getImageURl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        flowercountReference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("flowersend");
        flowercountReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String tmp = snapshot.getValue(String.class);
                flowersend = tmp;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btn_flower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                new AlertDialog.Builder(AnonymousMessageActivity.this).setTitle("確認提醒")//设置对话框标题

                        .setMessage("是否要送花花？")

                        .setPositiveButton("是", new DialogInterface.OnClickListener() {//添加确定按钮

                            @Override

                            public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件，点击事件没写，自己添加
                                if( !flowersend.isEmpty()&&flowersend.equals("true"))
                                {
                                    DatabaseReference tmp = FirebaseDatabase.getInstance().getReference("Users").child(userid).child("flower");
                                    tmp.setValue(userFlower+1);

                                    DatabaseReference tmp2 = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("flowersend");
                                    tmp2.setValue("false");

                                    Toast.makeText(AnonymousMessageActivity.this,"Send flower success!",Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(AnonymousMessageActivity.this,"You already send the flower!",Toast.LENGTH_SHORT).show();
                                }
                            }

                        }).setNegativeButton("否", new DialogInterface.OnClickListener() {//添加返回按钮

                    @Override

                    public void onClick(DialogInterface dialog, int which) {//响应事件，点击事件没写，自己添加

                    }

                }).show();//在按键响应事件中显示此对话框

            }




                /*if( !flowersend.isEmpty()&&flowersend.equals("true"))
                {
                    DatabaseReference tmp = FirebaseDatabase.getInstance().getReference("Users").child(userid).child("flower");
                    tmp.setValue(userFlower+1);

                    DatabaseReference tmp2 = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("flowersend");
                    tmp2.setValue("false");

                    Toast.makeText(AnonymousMessageActivity.this,"Send flower success!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(AnonymousMessageActivity.this,"You already send the flower!",Toast.LENGTH_SHORT).show();
                }*/
        });

        flowerReference = FirebaseDatabase.getInstance().getReference("Users").child(userid).child("flower");
        flowerReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userFlower = snapshot.getValue(int.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendMessage(String sender, String receiver, String message)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message",message);

        DateFormat df = new SimpleDateFormat("a h:mm");
        String date = df.format(Calendar.getInstance().getTime());

        String tt = "";
        if(date.contains("A"))
        {
            tt = date.replace("AM","上午");
        }
        else if(date.contains("P"))
        {
            tt = date.replaceAll("PM","下午");
        }
        else
        {
            tt = date;
        }

        hashMap.put("time",tt);

        intent = getIntent();

        //modify
        userid = intent.getStringExtra("userid");

        reference.child("AnonymousChats").push().setValue(hashMap);

        /*DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(fuser.getUid())
                .child(userid);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists())
                {
                    chatRef.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(userid)
                .child(fuser.getUid());

        chatRefReceiver.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    chatRefReceiver.child("id").setValue(fuser.getUid());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        final String msg = message;

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if(notify)
                {
                    sendNotification(receiver,user.getUsername(),msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendNotification(String receiver,String username, String message)
    {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
                    Token token = snapshot1.getValue(Token.class);

                    //modify icon
                    //Data data = new Data(fuser.getUid(),R.mipmap.ic_launcher,username+": "+message,"New Message",userid);

                    Data data = new Data(fuser.getUid(),R.drawable.icon3,"匿名: "+message,"New Message",userid, "2","anonymous","");

                    Sender sender = new Sender(data,token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code() == 200)
                                    {
                                        if(response.body().success != 1)
                                        {
                                            Toast.makeText(AnonymousMessageActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable throwable) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readMessage(String myid, String userid, String imageurl)
    {
        mchat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("AnonymousChats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mchat.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
                    AnonymousChat chat = snapshot1.getValue(AnonymousChat.class);
                    if((chat.getReceiver().equals(myid) && chat.getSender().equals(userid)) ||
                            (chat.getReceiver().equals(userid) && chat.getSender().equals(myid)))
                    {
                        mchat.add(chat);
                    }

                    messageAdapter = new AnonymousMessageAdapter(AnonymousMessageActivity.this,mchat,imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }

                //Toast.makeText(MessageActivity.this,"You can't send empty message!",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Toast.makeText(MessageActivity.this,"You can't send empty message!",Toast.LENGTH_SHORT).show();
    }

    private void status(String status)
    {
        reference= FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

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
    protected void onPause()
    {
        super.onPause();
        //status("offline");
    }

}