package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.Adapter.GroupMessageAdapter;
import com.example.myapplication.Adapter.MessageAdapter;
import com.example.myapplication.Fragments.APIService;
import com.example.myapplication.Model.Chat;
import com.example.myapplication.Model.Group;
import com.example.myapplication.Model.GroupList;
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

public class GroupMessageActivity extends AppCompatActivity {

    private static final int RESULT_OK = -1;
    private static final int IMAGE_REQUEST = 1;

    CircleImageView profile_image;
    TextView username;

    FirebaseUser fuser;
    DatabaseReference reference;

    Intent intent;
    List<Chat> mchat;

    GroupMessageAdapter groupmessageAdapter;

    RecyclerView recyclerView;

    ImageButton btn_send, btn_sendphoto, btn_dehaze;
    EditText text_send;

    private Uri imageUri;
    private StorageTask uploadTask;
    StorageReference storageReference;

    APIService apiService;

    List<User> mUser;

    String groupid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_message);

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

        mchat = new ArrayList<>();
        mUser = new ArrayList<>();

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        btn_sendphoto = findViewById(R.id.btn_sendphoto);
        btn_dehaze = findViewById(R.id.btn_dehaze);
        text_send = findViewById(R.id.text_send);

        btn_send.setColorFilter(getResources().getColor(R.color.iconcolor));
        btn_sendphoto.setColorFilter(getResources().getColor(R.color.iconcolor));
        btn_dehaze.setColorFilter(getResources().getColor(R.color.iconcolor));


        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        intent = getIntent();
        //modify
        groupid = intent.getStringExtra("groupid");

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        btn_dehaze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupMessageActivity.this, GroupOptionActivity.class);
                intent.putExtra("groupid",groupid);
                startActivity(intent);
                //startActivity(new Intent(GroupMessageActivity.this, GroupOptionActivity.class));
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String msg = text_send.getText().toString();
                if(!msg.equals(""))
                {
                    sendMessage(fuser.getUid(),groupid,msg);
                }
                else
                {
                    Toast.makeText(GroupMessageActivity.this,"You can't send empty message!",Toast.LENGTH_SHORT).show();
                }

                text_send.setText("");
            }
        });

        btn_sendphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openImage();


                //Toast.makeText(MessageActivity.this,tmpimguri,Toast.LENGTH_SHORT).show();
                //Toast.makeText(MessageActivity.this,"Success",Toast.LENGTH_SHORT).show();


            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Groups").child(groupid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    Group group = snapshot.getValue(Group.class);
                    username.setText(group.getName());

                    if(group.getImageURl().equals("default"))
                    {
                        profile_image.setImageResource(R.mipmap.ic_launcher);
                    }
                    else
                    {
                        Glide.with(getApplicationContext()).load(group.getImageURl()).into(profile_image);
                    }

                    readMessage(fuser.getUid(),groupid, group.getImageURl());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference tmpR = FirebaseDatabase.getInstance().getReference("Groups").child(groupid).child("member");
        tmpR.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    mUser.clear();
                    for(DataSnapshot snapshot1:snapshot.getChildren())
                    {
                        User user = snapshot1.getValue(User.class);
                        mUser.add(user);
                    }

                }
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

                    Data data = new Data(fuser.getUid(),R.drawable.icon3,username+": "+message,"New Message",groupid, "1","normal","");

                    Sender sender = new Sender(data,token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code() == 200)
                                    {
                                        if(response.body().success != 1)
                                        {
                                            Toast.makeText(GroupMessageActivity.this,"Failed",Toast.LENGTH_SHORT).show();
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

    private void sendPhoto(String sender,String receiver,String imguri)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups").child(groupid);

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

        reference.child("Chats").push().setValue(hashMap);

    }

    private void sendMessage(String sender, String receiver, String message)
    {
        DatabaseReference chatreference = FirebaseDatabase.getInstance().getReference("Groups").child(groupid);

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

        chatreference.child("Chats").push().setValue(hashMap);

        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("GroupChatlist")
                .child(fuser.getUid())
                .child(groupid);
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists())
                {
                    chatRef.child("id").setValue(groupid);
                    DatabaseReference gRef = FirebaseDatabase.getInstance().getReference("Groups").child(groupid).child("member");
                    List<GroupList> groupLists = new ArrayList<>();

                    gRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot snapshot1:snapshot.getChildren())
                            {
                                GroupList groupList = snapshot1.getValue(GroupList.class);
                                groupLists.add(groupList);
                            }

                            for(GroupList groupList:groupLists)
                            {
                                DatabaseReference tmp = FirebaseDatabase.getInstance().getReference("GroupChatlist").child(groupList.getId()).child(groupid);
                                tmp.child("id").setValue(groupid);
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

    private void readMessage(String myid, String groupid, String imageurl)
    {
        mchat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Groups").child(groupid).child("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    mchat.clear();
                    for(DataSnapshot snapshot1:snapshot.getChildren())
                    {

                        Chat chat = snapshot1.getValue(Chat.class);

                        chat.setId(snapshot1.getKey());
                        //chat.setMessage(chat.getMessage()+snapshot1.getKey());

                        mchat.add(chat);


                        groupmessageAdapter = new GroupMessageAdapter(GroupMessageActivity.this,mchat,imageurl,groupid);
                        recyclerView.setAdapter(groupmessageAdapter);
                    }
                }
                else
                {
                    mchat.clear();
                    groupmessageAdapter = new GroupMessageAdapter(GroupMessageActivity.this,mchat,imageurl,groupid);
                    recyclerView.setAdapter(groupmessageAdapter);
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

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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

                        sendPhoto(fuser.getUid(),groupid,mUri);
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
}