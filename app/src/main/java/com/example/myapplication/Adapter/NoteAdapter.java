package com.example.myapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.FriendProfileActivity;
import com.example.myapplication.GroupMessageActivity;
import com.example.myapplication.MessageActivity;
import com.example.myapplication.Model.Note;
import com.example.myapplication.Model.User;
import com.example.myapplication.NoteFullActivity;
import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder>{

    private Context mContext;
    private List<Note> mNotes;
    private String groupid;


    String theLastMessage;

    public NoteAdapter(Context mContext, List<Note> mNotes,String groupid)
    {
        this.mNotes = mNotes;
        this.mContext = mContext;
        this.groupid = groupid;
    }

    @NonNull
    @Override
    public NoteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.note_item,parent,false);
        return new NoteAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.ViewHolder holder, int position) {

        Note note = mNotes.get(position);

        holder.date.setText(note.getDate());
        holder.content.setText(note.getContent());

        DatabaseReference tmpR = FirebaseDatabase.getInstance().getReference("Users").child(note.getUser());
        tmpR.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    User user = snapshot.getValue(User.class);
                    holder.username.setText(user.getUsername());
                    if(user.getImageURl().equals("default"))
                    {
                        holder.profile_image.setImageResource(R.mipmap.ic_launcher);
                    }
                    else
                    {
                        Glide.with(mContext).load(user.getImageURl()).into(holder.profile_image);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, NoteFullActivity.class);
                intent.putExtra("noteid",note.getId());
                intent.putExtra("groupid",groupid);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public TextView date;
        public CircleImageView profile_image;
        public TextView content;

        public ViewHolder(View itemView)
        {
            super(itemView);

            username = itemView.findViewById(R.id.txt_username);
            date = itemView.findViewById(R.id.txt_date);
            profile_image = itemView.findViewById(R.id.profile_image);
            content = itemView.findViewById(R.id.txt_content);

        }
    }

}
