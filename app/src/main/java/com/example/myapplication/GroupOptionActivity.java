package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.Model.Group;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupOptionActivity extends AppCompatActivity {

    ImageButton btn_exit, btn_personNumber, btn_groupAdd, btn_note;
    FirebaseUser fuser;
    String groupid;

    CircleImageView group_profile;

    Intent intent;

    StorageReference storageReference;
    private Uri imageUri;
    private static final int IMAGE_REQUEST = 1;
    private StorageTask uploadTask;

    DatabaseReference imageR,reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_option);

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

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        intent = getIntent();
        //modify
        groupid = intent.getStringExtra("groupid");

        group_profile = findViewById(R.id.group_profile);
        group_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

        btn_groupAdd = findViewById(R.id.btn_personAdd);
        btn_groupAdd.setColorFilter(getResources().getColor(R.color.iconcolor));
        btn_groupAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tmpintent = new Intent(GroupOptionActivity.this,GroupAddActivity.class);
                tmpintent.putExtra("groupid",groupid);

                startActivity(tmpintent);
            }
        });

        btn_note = findViewById(R.id.btn_note);
        btn_note.setColorFilter(getResources().getColor(R.color.iconcolor));
        btn_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tmpintent = new Intent(GroupOptionActivity.this,GroupNoteActivity.class);
                tmpintent.putExtra("groupid",groupid);

                startActivity(tmpintent);
            }
        });

        btn_personNumber = findViewById(R.id.btn_personNumber);
        btn_personNumber.setColorFilter(getResources().getColor(R.color.iconcolor));
        btn_personNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tmpintent = new Intent(GroupOptionActivity.this,GroupPersonNumberActivity.class);
                tmpintent.putExtra("groupid",groupid);

                startActivity(tmpintent);
            }
        });

        btn_exit = findViewById(R.id.btn_exit);
        btn_exit.setColorFilter(getResources().getColor(R.color.iconcolor));
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference tmp = FirebaseDatabase.getInstance().getReference("GroupsList").child(fuser.getUid()).child(groupid);
                tmp.removeValue();
                tmp = FirebaseDatabase.getInstance().getReference("Groups").child(groupid).child("member").child(fuser.getUid());
                tmp.removeValue();
                tmp = FirebaseDatabase.getInstance().getReference("GroupChatlist").child(fuser.getUid()).child(groupid);
                tmp.removeValue();
                startActivity(new Intent(GroupOptionActivity.this, MainActivity.class));
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Groups").child(groupid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    Group group = snapshot.getValue(Group.class);

                    if(group.getImageURl().equals("default"))
                    {
                        group_profile.setImageResource(R.mipmap.ic_launcher);
                    }
                    else
                    {
                        Glide.with(getApplicationContext()).load(group.getImageURl()).into(group_profile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void openImage()
    {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }

    private String getFileExtension(Uri imageUri)
    {
        ContentResolver contentResolver = getApplicationContext().getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(this.imageUri));
    }

    private void uploadImage()
    {
        ProgressDialog pd = new ProgressDialog(GroupOptionActivity.this);
        pd.setMessage("Uploading");
        pd.show();

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

                        DatabaseReference tmp = FirebaseDatabase.getInstance().getReference("Groups").child(groupid).child("imageURl");
                        tmp.setValue(mUri);

                        pd.dismiss();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    pd.dismiss();
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
            }
        }
    }
}