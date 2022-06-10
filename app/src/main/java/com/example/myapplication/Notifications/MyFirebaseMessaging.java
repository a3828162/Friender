package com.example.myapplication.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.myapplication.AnonymousMessageActivity;
import com.example.myapplication.IncomingInvitationActivity;
import com.example.myapplication.MessageActivity;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        String newtoken = token;

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(newtoken != null)
        {
            updateToken(newtoken);
        }
    }

    private void updateToken(String newtoken)
    {
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
            Token token = new Token(newtoken);
            reference.child(firebaseUser.getUid()).setValue(token);
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);



        String sented = message.getData().get("sented");
        String user = message.getData().get("user");

        String type = message.getData().get("type");
        String calltype = message.getData().get("calltype");

        if(type.equals("3"))
        {
            Intent intent = new Intent(getApplicationContext(), IncomingInvitationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("userid",user);
            intent.putExtra("calltype",calltype);
            String meetroom = message.getData().get("meetroom");
            intent.putExtra("meetroom",meetroom);
            startActivity(intent);
            return;
        }
        else if(type.equals("4")||type.equals("5")||type.equals("6"))
        {
            Intent intent = new Intent("type");
            intent.putExtra("type",type);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            return;
        }

        //String sented = message.getData().get("sent"); //after

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences preferences = getSharedPreferences("PREFS",MODE_PRIVATE);
        String currentuser = preferences.getString("currentuser","none");

        if(firebaseUser != null && sented.equals(firebaseUser.getUid()))
        {
            //DatabaseReference tmp = FirebaseDatabase.getInstance().getReference("Blocks").child(firebaseUser.getUid()).child(sented);
            //if(tmp==null)
            //{
            if(!currentuser.equals(user))
            {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    sendOreoNotification(message);
                }
                else {
                    sendNoification(message);
                }
            }

            //}

        }
    }

    private void sendOreoNotification(RemoteMessage message)
    {
        String user = message.getData().get("user");
        String icon = message.getData().get("icon");
        String title = message.getData().get("title");
        String body = message.getData().get("body");
        String type = message.getData().get("type");

        RemoteMessage.Notification notification = message.getNotification();
        int j = Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent;
        if(type.equals("1")) intent = new Intent(this, MessageActivity.class);
        else intent = new Intent(this, AnonymousMessageActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("userid",user);
        intent.putExtras(bundle);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,j,intent,PendingIntent.FLAG_IMMUTABLE);
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoNotification oreoNotification = new OreoNotification(this);
        // modify
        Notification.Builder builder = oreoNotification.getOreoNotification(title,body,pendingIntent,defaultSound,icon);
        //Notification.Builder builder = oreoNotification.getOreoNotification(title,body,pendingIntent,defaultSound,icon);

        int i=0;

        if(j>0)
        {
            i = j;
        }


        oreoNotification.getManager().notify(i,builder.build());
    }

    private void sendNoification(RemoteMessage message)
    {
        String user = message.getData().get("user");
        String icon = message.getData().get("icon");
        String title = message.getData().get("title");
        String body = message.getData().get("body");
        String type = message.getData().get("type");

        RemoteMessage.Notification notification = message.getNotification();
        int j = Integer.parseInt(user.replaceAll("[\\D]",""));

        //Intent intent = new Intent(this, MessageActivity.class);

        Intent intent;
        if(type.equals("1")) intent = new Intent(this, MessageActivity.class);
        else intent = new Intent(this, AnonymousMessageActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("userid",user);
        intent.putExtras(bundle);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,j,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                //modify
                //.setSmallIcon(Integer.parseInt(icon))
                .setSmallIcon(R.drawable.icon6)
                //
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);

        NotificationManager noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int i=0;

        if(j>0)
        {
            i = j;
        }

        noti.notify(i,builder.build());
    }
}
