package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.Adapter.MessageAdapter;
import com.example.myapplication.Fragments.APIService;
import com.example.myapplication.Model.Chat;
import com.example.myapplication.Model.User;
import com.example.myapplication.Notifications.Client;
import com.example.myapplication.Notifications.Data;
import com.example.myapplication.Notifications.MyResponse;
import com.example.myapplication.Notifications.Sender;
import com.example.myapplication.Notifications.Token;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.lang.reflect.Array;
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

public class MessageActivity extends AppCompatActivity {

    private static final int RESULT_OK = -1;
    private static final int IMAGE_REQUEST = 1;

    CircleImageView profile_image;
    TextView username;

    FirebaseUser fuser;
    DatabaseReference reference, tokenreference;

    Intent intent;

    MessageAdapter messageAdapter;
    List<Chat> mchat;

    RecyclerView recyclerView;

    ImageButton btn_send, btn_sendphoto, btn_call, btn_videocam;
    EditText text_send;

    ValueEventListener seenListener;

    private Uri imageUri;
    private StorageTask uploadTask;
    StorageReference storageReference;

    //modify
    String userid, tmpimguri;
    Token token;

    APIService apiService;

    boolean notify = false;

    User currentuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

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

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        btn_sendphoto = findViewById(R.id.btn_sendphoto);
        btn_call = findViewById(R.id.btn_call);
        btn_videocam = findViewById(R.id.btn_videocam);

        btn_send.setColorFilter(getResources().getColor(R.color.iconcolor));
        btn_sendphoto.setColorFilter(getResources().getColor(R.color.iconcolor));
        btn_call.setColorFilter(getResources().getColor(R.color.iconcolor));
        btn_videocam.setColorFilter(getResources().getColor(R.color.iconcolor));

        text_send = findViewById(R.id.text_send);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");

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
                    Toast.makeText(MessageActivity.this,"You can't send empty message!",Toast.LENGTH_SHORT).show();
                }

                text_send.setText("");
            }
        });

        btn_sendphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;

                openImage();


                //Toast.makeText(MessageActivity.this,tmpimguri,Toast.LENGTH_SHORT).show();
                //Toast.makeText(MessageActivity.this,"Success",Toast.LENGTH_SHORT).show();


            }
        });

        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(token.getToken()==null&&token.getToken().trim().isEmpty())
                {
                    Toast.makeText(MessageActivity.this,currentuser.getUsername() + "is not available for meeting",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //Toast.makeText(MessageActivity.this,"Video meeting with " + currentuser.getUsername(),Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),OutgoingInvitationActivity.class);
                    intent.putExtra("userid",currentuser.getId());
                    intent.putExtra("calltype","call");
                    startActivity(intent);
                }

            }
        });

        btn_videocam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(token.getToken()==null&&token.getToken().trim().isEmpty())
                {
                    Toast.makeText(MessageActivity.this,currentuser.getUsername() + "is not available for video meeting",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //Toast.makeText(MessageActivity.this,"Video meeting with " + currentuser.getUsername(),Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),OutgoingInvitationActivity.class);
                    intent.putExtra("userid",currentuser.getId());
                    intent.putExtra("calltype","video");
                    startActivity(intent);
                }

            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

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

                readMessage(fuser.getUid(),userid, user.getImageURl());

                tokenreference = FirebaseDatabase.getInstance().getReference("Tokens").child(currentuser.getId());
                tokenreference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            token = snapshot.getValue(Token.class);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        seenMessage(userid);
    }

    private void seenMessage(String userid)
    {
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
                    Chat chat = snapshot1.getValue(Chat.class);

                    if(chat.getReceiver().equals(fuser.getUid())&&chat.getSender().equals(userid))
                    {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", "true");
                        snapshot1.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void sendPhoto(String sender,String receiver,String imguri)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message",imguri);
        hashMap.put("isseen","false");
        hashMap.put("messagetype",2);

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

        reference.child("Chats").push().setValue(hashMap);

    }

    private void sendMessage(String sender, String receiver, String message)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message",message);
        hashMap.put("isseen","false");
        hashMap.put("messagetype",1);

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

        reference.child("Chats").push().setValue(hashMap);

        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
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

        DatabaseReference t = FirebaseDatabase.getInstance().getReference("Blocks").child(userid).child(fuser.getUid());
        t.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                }
                else
                {
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
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final String msg = message;

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if(notify)
                {

                    DatabaseReference t = FirebaseDatabase.getInstance().getReference("Blocks").child(userid).child(fuser.getUid());

                    t.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists())
                            {
                                //Toast.makeText(MessageActivity.this,"1",Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                //Toast.makeText(MessageActivity.this,"2",Toast.LENGTH_SHORT).show();
                                sendNotification(receiver,user.getUsername(),msg);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    /*if()
                    {
                        Toast.makeText(MessageActivity.this,"1",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(MessageActivity.this,"2",Toast.LENGTH_SHORT).show();
                        sendNotification(receiver,user.getUsername(),msg);
                    }*/
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

                    Data data = new Data(fuser.getUid(),R.drawable.icon3,username+": "+message,"New Message",userid, "1","normal","");

                    Sender sender = new Sender(data,token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code() == 200)
                                    {
                                        if(response.body().success != 1)
                                        {
                                            Toast.makeText(MessageActivity.this,"Failed",Toast.LENGTH_SHORT).show();
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

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mchat.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren())
                {

                    Chat chat = snapshot1.getValue(Chat.class);

                    chat.setId(snapshot1.getKey());
                    //chat.setMessage(chat.getMessage()+snapshot1.getKey());
                    if((chat.getReceiver().equals(myid) && chat.getSender().equals(userid)) ||
                            (chat.getReceiver().equals(userid) && chat.getSender().equals(myid)))
                    {
                        mchat.add(chat);
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this,mchat,imageurl);
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


    private void openImage()
    {

        /*Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);*/
        Intent action = new Intent();
        action.setType("image/*");
        action.setAction(Intent.ACTION_GET_CONTENT);


        startActivityForResult(action,IMAGE_REQUEST);
    }

    private String getFileExtension(Uri imageUri)
    {
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(this.imageUri));
    }

    private void uploadImage()
    {
        /*final ProgressDialog pd = new ProgressDialog(getApplicationContext());
        pd.setMessage("Uploading");
        pd.show();*/

        if(imageUri != null)
        {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful())
                    {
                        Uri downloadUri = (Uri) task.getResult();
                        String mUri = downloadUri.toString();
                        //tmpimguri = mUri;

                        sendPhoto(fuser.getUid(),userid,mUri);
                        //Toast.makeText(getApplicationContext(),mUri,Toast.LENGTH_SHORT).show();

                        /*reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURl",mUri);
                        reference.updateChildren(map);*/

                        //pd.dismiss();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
                        //pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    //pd.dismiss();
                }
            });
        }
        else
        {
            Toast.makeText(getApplicationContext(),"No image selected",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //Toast.makeText(getApplicationContext(),"No image selected",Toast.LENGTH_SHORT).show();
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData()!=null)
        {
            imageUri = data.getData();

            if(uploadTask!= null &&uploadTask.isInProgress())
            {
                Toast.makeText(getApplicationContext(),"Upload in progress",Toast.LENGTH_SHORT).show();
            }
            else
            {
                uploadImage();
                Toast.makeText(getApplicationContext(),"Next",Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void currentUser(String userid)
    {
        SharedPreferences.Editor editor = getSharedPreferences("PREFS",MODE_PRIVATE).edit();
        editor.putString("currentuser",userid);
        editor.apply();
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
        currentUser(userid);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        reference.removeEventListener(seenListener);
        //status("offline");
        currentUser("none");
    }
}