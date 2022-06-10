package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.Fragments.APIService;
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

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncomingInvitationActivity extends AppCompatActivity {

    /*
    type:
    1:normal
    2:anonymous
    3:call
    4:accept
    5:reject
    6:cancel
    7:group
     */

    Intent intent;
    ImageView img_meettype,img_accept,img_reject;
    CircleImageView profile_image;
    TextView txt_username;

    String userid, calltype;
    User calluser;
    DatabaseReference reference;
    FirebaseUser fuser;
    APIService apiService;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_invitation);

        img_meettype = findViewById(R.id.imageMeetintType);
        img_accept = findViewById(R.id.img_acceptinvitation);
        img_reject = findViewById(R.id.img_rejectinvitation);
        profile_image = findViewById(R.id.profile_image);
        txt_username = findViewById(R.id.txt_username);

        intent = getIntent();
        userid = intent.getStringExtra("userid");
        calltype = intent.getStringExtra("calltype");

        //txt_username.setText(userid);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        img_meettype.setColorFilter(getResources().getColor(R.color.iconcolor));
        if(calltype.equals("call"))
        {
            img_meettype.setImageResource(R.drawable.img_call);

        }
        else if(calltype.equals("video"))
        {
            img_meettype.setImageResource(R.drawable.img_videocam);
        }

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    User user = snapshot.getValue(User.class);
                    calluser = user;
                    txt_username.setText(calluser.getUsername());
                    if(calluser.getImageURl().equals("default"))
                    {
                        profile_image.setImageResource(R.mipmap.ic_launcher);
                    }
                    else
                    {
                        //modify
                        Glide.with(getApplicationContext()).load(calluser.getImageURl()).into(profile_image);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        img_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    URL serverURL = new URL("https://meet.jit.si");
                    /*JitsiMeetConferenceOptions conferenceOptions =
                            new JitsiMeetConferenceOptions.Builder()
                            .setServerURL(serverURL)
                            .setWelcomePageEnabled(false)
                            //.setRoom(getIntent().getStringExtra("meetroom"))
                                    .setRoom(getIntent().getStringExtra("meetroom"))
                            .build();

                    JitsiMeetActivity.launch(IncomingInvitationActivity.this,conferenceOptions);
                    Toast.makeText(IncomingInvitationActivity.this,getIntent().getStringExtra("meetroom"),Toast.LENGTH_SHORT).show();*/
                    JitsiMeetConferenceOptions.Builder builder = new JitsiMeetConferenceOptions.Builder();
                    builder.setServerURL(serverURL);
                    builder.setWelcomePageEnabled(false);
                    builder.setRoom(getIntent().getStringExtra("meetroom"));
                    if(calltype.equals("call"))
                    {
                        builder.setVideoMuted(true);
                    }

                    JitsiMeetActivity.launch(IncomingInvitationActivity.this,builder.build());
                    sendNotification(userid,"","","4");
                    finish();

                }catch (Exception exception)
                {
                    Toast.makeText(IncomingInvitationActivity.this,exception.getMessage(),Toast.LENGTH_SHORT).show();
                    finish();
                }



            }
        });

        img_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification(userid,"","","5");
                finish();
            }
        });

    }

    private void sendNotification(String receiver,String username, String message,String type)
    {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
                    Token token = snapshot1.getValue(Token.class);

                    Data data = new Data(fuser.getUid(),R.drawable.icon3,username+": "+message,"New Message",userid, type, calltype,getIntent().getStringExtra("meetroom"));

                    Sender sender = new Sender(data,token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code() == 200)
                                    {
                                        if(response.body().success != 1)
                                        {
                                            Toast.makeText(IncomingInvitationActivity.this,"Failed "+type+" "+receiver,Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            //Toast.makeText(IncomingInvitationActivity.this,"Success "+type+" "+receiver,Toast.LENGTH_SHORT).show();
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

    private BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra("type");
            if(type!=null)
            {
                if(type.equals("6"))
                {
                    //Toast.makeText(IncomingInvitationActivity.this,"Cancel accept",Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
            else
            {
                Toast.makeText(IncomingInvitationActivity.this,"Fail",Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                invitationResponseReceiver,
                new IntentFilter("type")
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                invitationResponseReceiver,
                new IntentFilter("type")
        );*/
    }

    @Override
    protected void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                invitationResponseReceiver
        );
    }
}