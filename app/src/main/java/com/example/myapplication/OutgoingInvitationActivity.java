package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
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
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OutgoingInvitationActivity extends AppCompatActivity {

    String userid,calltype, meetroom = null;
    Intent intent;

    FirebaseUser fuser;
    DatabaseReference reference;

    CircleImageView profile_image;
    TextView username;
    ImageView img_stop, img_type;

    User currentuser;

    APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoing_invitation);

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.txt_username);
        img_stop = findViewById(R.id.img_stopinvitation);
        img_type = findViewById(R.id.imageMeetintType);

        img_type.setColorFilter(getResources().getColor(R.color.iconcolor));
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        intent = getIntent();
        userid = intent.getStringExtra("userid");
        calltype = intent.getStringExtra("calltype");

        meetroom = fuser.getUid()+"_"+UUID.randomUUID().toString().substring(0,5);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        if(calltype.equals("call"))
        {
            img_type.setImageResource(R.drawable.img_call);

        }
        else if(calltype.equals("video"))
        {
            img_type.setImageResource(R.drawable.img_videocam);
        }

        img_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification(currentuser.getId(),"","","6");
                finish();
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    User user = snapshot.getValue(User.class);
                    currentuser = user;

                    username.setText(user.getUsername());

                    if(user.getImageURl().equals("default"))
                    {
                        profile_image.setImageResource(R.mipmap.ic_launcher);
                    }
                    else
                    {
                        //modify
                        Glide.with(getApplicationContext()).load(user.getImageURl()).into(profile_image);
                    }

                    sendNotification(currentuser.getId(),"","","3");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

                    //modify icon
                    //Data data = new Data(fuser.getUid(),R.mipmap.ic_launcher,username+": "+message,"New Message",userid);

                    Data data = new Data(fuser.getUid(),R.drawable.icon3,username+": "+message,"New Message",userid, type,calltype,meetroom);

                    Sender sender = new Sender(data,token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code() == 200)
                                    {
                                        if(response.body().success != 1)
                                        {
                                            Toast.makeText(OutgoingInvitationActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            //Toast.makeText(OutgoingInvitationActivity.this,"Success",Toast.LENGTH_SHORT).show();
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
                if(type.equals("4"))
                {
                    //Toast.makeText(OutgoingInvitationActivity.this,"Success accept",Toast.LENGTH_SHORT).show();
                    try
                    {
                        URL serverURL = new URL("https://meet.jit.si");

                        JitsiMeetConferenceOptions.Builder builder = new JitsiMeetConferenceOptions.Builder();
                        builder.setServerURL(serverURL);
                        builder.setWelcomePageEnabled(false);
                        builder.setRoom(meetroom);
                        if(calltype.equals("call"))
                        {
                            builder.setVideoMuted(true);
                        }

                        /*JitsiMeetConferenceOptions conferenceOptions =
                                new JitsiMeetConferenceOptions.Builder()
                                        .setServerURL(serverURL)
                                        .setWelcomePageEnabled(false)
                                        //.setRoom(meetroom)
                                        .setRoom(meetroom)
                                        .build();*/

                        //Toast.makeText(OutgoingInvitationActivity.this,meetroom,Toast.LENGTH_SHORT).show();

                        //JitsiMeetActivity.launch(OutgoingInvitationActivity.this,conferenceOptions);

                        JitsiMeetActivity.launch(OutgoingInvitationActivity.this,builder.build());

                        finish();

                    }catch (Exception exception)
                    {
                        Toast.makeText(OutgoingInvitationActivity.this,exception.getMessage(),Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                else if(type.equals("5"))
                {
                    //Toast.makeText(OutgoingInvitationActivity.this,"Success Reject",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            else
            {
                Toast.makeText(OutgoingInvitationActivity.this,"Fail",Toast.LENGTH_SHORT).show();
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