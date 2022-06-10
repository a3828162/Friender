package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.Adapter.NoteAdapter;
import com.example.myapplication.Adapter.NoteMessageAdapter;
import com.example.myapplication.Model.Note;
import com.example.myapplication.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NoteFullActivity extends AppCompatActivity {

    public TextView username;
    public TextView date;
    public CircleImageView profile_image;
    public TextView content;

    EditText edt_send;
    ImageButton btn_send;

    List<Note> mNotes;
    private RecyclerView recyclerView;
    private NoteMessageAdapter notemessageAdapter;

    DatabaseReference noteReference;

    FirebaseUser currentuser;

    String noteid, groupid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_full);

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

        noteid = getIntent().getStringExtra("noteid");
        groupid = getIntent().getStringExtra("groupid");

        username = findViewById(R.id.txt_username);
        date = findViewById(R.id.txt_date);
        profile_image = findViewById(R.id.profile_image);
        content = findViewById(R.id.txt_content);
        btn_send = findViewById(R.id.btn_send);
        edt_send = findViewById(R.id.text_send);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mNotes = new ArrayList<>();

        currentuser = FirebaseAuth.getInstance().getCurrentUser();

        noteReference = FirebaseDatabase.getInstance().getReference("Groups").child(groupid).child("notes").child(noteid).child("message");
        noteReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    mNotes.clear();

                    for(DataSnapshot snapshot1:snapshot.getChildren())
                    {
                        Note note = snapshot1.getValue(Note.class);
                        mNotes.add(note);
                    }
                    notemessageAdapter = new NoteMessageAdapter(NoteFullActivity.this,mNotes);
                    recyclerView.setAdapter(notemessageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edt_send.getText().toString().equals(""))
                {
                    Toast.makeText(NoteFullActivity.this,"No Blank!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    DatabaseReference tmpR = FirebaseDatabase.getInstance().getReference("Groups").child(groupid).child("notes").child(noteid).child("message");
                    String s = tmpR.push().getKey();
                    DateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm");
                    String date = df.format(Calendar.getInstance().getTime());
                    //Note note = new Note(s,currentuser.getUsername(),date,editText.getText().toString());

                    HashMap<String,String> hashMap = new HashMap<>();
                    hashMap.put("id",s);
                    hashMap.put("user", currentuser.getUid());
                    hashMap.put("date",date);
                    hashMap.put("content",edt_send.getText().toString());

                    tmpR.child(s).setValue(hashMap);
                    edt_send.setText("");
                }
            }
        });

        DatabaseReference tmpR = FirebaseDatabase.getInstance().getReference("Groups").child(groupid).child("notes").child(noteid);
        tmpR.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    Note note = snapshot.getValue(Note.class);
                    date.setText(note.getDate());
                    content.setText(note.getContent());

                    DatabaseReference tmpR2 = FirebaseDatabase.getInstance().getReference("Users").child(note.getUser());
                    tmpR2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists())
                            {
                                User user = snapshot.getValue(User.class);
                                username.setText(user.getUsername());
                                if(user.getImageURl().equals("default"))
                                {
                                    profile_image.setImageResource(R.mipmap.ic_launcher);
                                }
                                else
                                {
                                    Glide.with(getApplicationContext()).load(user.getImageURl()).into(profile_image);
                                }
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
}