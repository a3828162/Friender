package com.example.myapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.MessageActivity;
import com.example.myapplication.Model.AddUser;
import com.example.myapplication.Model.Chat;
import com.example.myapplication.Model.Chatlist;
import com.example.myapplication.Model.Friend;
import com.example.myapplication.Model.User;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.List;

public class AddUserAdapter extends RecyclerView.Adapter<AddUserAdapter.ViewHolder> {

    private Context mContext;
    private List<AddUser> maddUsers;
    private List<Friend> mfriends;

    private boolean isffriend;

    public AddUserAdapter(Context mContext, List<AddUser> maddUsers, List<Friend> mfriends)
    {
        this.maddUsers = maddUsers;
        this.mContext = mContext;
        this.mfriends = mfriends;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adduser_item,parent,false);
        return new AddUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        AddUser user = maddUsers.get(position);
        holder.username.setText(user.getUsername());

        if(user.getImageURl().equals("default"))
        {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }
        else
        {
            Glide.with(mContext).load(user.getImageURl()).into(holder.profile_image);
        }
        isffriend = false;
        for(Friend f:mfriends)
        {
            if(f.getId().equals(user.getId())) isffriend = true;
        }
        if(isffriend)
        {
            holder.btn_adduser.setImageResource(R.drawable.cancel);
            holder.btn_adduser.setEnabled(false);
        }
        else
        {
            holder.btn_adduser.setImageResource(R.drawable.add);
            holder.btn_adduser.setEnabled(true);
        }

        holder.btn_adduser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isffriend = false;
                for(Friend f:mfriends)
                {
                    if(f.getId().equals(user.getId())) isffriend = true;
                }

                if(isffriend)
                {
                    Toast.makeText(mContext,"is friend",Toast.LENGTH_SHORT).show();

                }
                else
                {
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Friends")
                            .child(firebaseUser.getUid()).child(user.getId());

                    reference.child("id").setValue(user.getId());

                    DatabaseReference re2 = FirebaseDatabase.getInstance().getReference("Friends")
                            .child(user.getId()).child(firebaseUser.getUid());

                    re2.child("id").setValue(firebaseUser.getUid());

                    //modify
                    //mfriends.add(new Friend(user.getId()));
                    holder.btn_adduser.setImageResource(R.drawable.cancel);
                    holder.btn_adduser.setEnabled(false);
                    Toast.makeText(mContext,"add friend",Toast.LENGTH_SHORT).show();

                    DatabaseReference t0 = FirebaseDatabase.getInstance().getReference("Blocks").child(firebaseUser.getUid()).child(user.getId());
                    if(t0 != null) t0.removeValue();


                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return maddUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profile_image;
        public ImageButton btn_adduser;

        public ViewHolder(View itemView)
        {
            super(itemView);

            btn_adduser = itemView.findViewById(R.id.btn_adduser);

            btn_adduser.setColorFilter(itemView.getResources().getColor(R.color.iconcolor));

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
        }
    }
}
