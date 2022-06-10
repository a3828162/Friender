package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.myapplication.Adapter.AddGroupMemberAdapter;
import com.example.myapplication.Adapter.NoteAdapter;
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

public class GroupNoteActivity extends AppCompatActivity {

    ImageButton btn_addnote;
    String groupid;

    FirebaseUser fuser;
    DatabaseReference noteReference;
    User currentuser;

    List<Note> mNotes;

    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_note);

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

        recyclerView = findViewById(R.id.notelist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mNotes = new ArrayList<>();

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference tmpR1 = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        tmpR1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    User user = snapshot.getValue(User.class);
                    currentuser = user;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        groupid = getIntent().getStringExtra("groupid");

        noteReference = FirebaseDatabase.getInstance().getReference("Groups").child(groupid).child("notes");
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
                    noteAdapter = new NoteAdapter(GroupNoteActivity.this,mNotes,groupid);
                    recyclerView.setAdapter(noteAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        btn_addnote = findViewById(R.id.btn_addnote);
        btn_addnote.setColorFilter(getResources().getColor(R.color.iconcolor));
        btn_addnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(GroupNoteActivity.this);
                View view = getLayoutInflater().inflate(R.layout.dialog_note,null);
                alertDialog.setView(view);
                Button btOK = view.findViewById(R.id.button_ok);
                Button btC  = view.findViewById(R.id.buttonCancel);
                EditText editText = view.findViewById(R.id.ededed);
                AlertDialog dialog = alertDialog.create();
                dialog.show();

                btOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!editText.getText().toString().isEmpty())
                        {
                            DatabaseReference tmpR = FirebaseDatabase.getInstance().getReference("Groups").child(groupid).child("notes");
                            String s = tmpR.push().getKey();
                            DateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm");
                            String date = df.format(Calendar.getInstance().getTime());
                            //Note note = new Note(s,currentuser.getUsername(),date,editText.getText().toString());

                            HashMap<String,String> hashMap = new HashMap<>();
                            hashMap.put("id",s);
                            hashMap.put("user", currentuser.getId());
                            hashMap.put("date",date);
                            hashMap.put("content",editText.getText().toString());

                            tmpR.child(s).setValue(hashMap);

                            dialog.dismiss();
                        }
                    }
                });

                btC.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });

    }
}