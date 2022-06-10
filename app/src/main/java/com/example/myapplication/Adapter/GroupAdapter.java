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
import com.example.myapplication.GroupProfileActivity;
import com.example.myapplication.MessageActivity;
import com.example.myapplication.Model.Chat;
import com.example.myapplication.Model.Group;
import com.example.myapplication.Model.GroupList;
import com.example.myapplication.Model.User;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private Context mContext;
    private List<Group> mGroups;
    private boolean ischat;

    String theLastMessage;

    public GroupAdapter(Context mContext, List<Group> mGroups,boolean ischat)
    {
        this.mGroups = mGroups;
        this.mContext = mContext;
        this.ischat = ischat;
    }

    @NonNull
    @Override
    public GroupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.group_item,parent,false);
        return new GroupAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupAdapter.ViewHolder holder, int position) {
        Group group = mGroups.get(position);
        holder.username.setText(group.getName());
        if(group.getImageURl().equals("default"))
        {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }
        else
        {
            Glide.with(mContext).load(group.getImageURl()).into(holder.profile_image);
        }

        if(ischat)
        {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            lastMessage(user.getUid(), holder.last_msg,group.getId());
        }
        else
        {
            holder.last_msg.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {
                //holder.search_users.setText("");

                if(ischat)
                {
                    Intent intent = new Intent(mContext, GroupMessageActivity.class);
                    intent.putExtra("groupid",group.getId());
                    mContext.startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(mContext, GroupProfileActivity.class);
                    intent.putExtra("groupid",group.getId());
                    mContext.startActivity(intent);
                }
            }

        });
    }

    @Override
    public int getItemCount() {
        return mGroups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView username;
        private ImageView profile_image;
        private TextView last_msg;

        public ViewHolder(View itemView)
        {
            super(itemView);

            username = itemView.findViewById(R.id.groupname);
            profile_image = itemView.findViewById(R.id.profile_image);
            last_msg = itemView.findViewById(R.id.last_msg);
        }
    }

    // check for last message
    private void lastMessage(String userid, TextView last_msg,String groupid)
    {
        theLastMessage = "default";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups").child(groupid).child("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean theLastMessageSeen = false;


                for(DataSnapshot snapshot1:snapshot.getChildren())
                {

                    Chat chat = snapshot1.getValue(Chat.class);

                    if(firebaseUser.getUid().equals(chat.getSender()))
                    {
                        if(chat.getMessagetype() == 1) theLastMessage = "你: " + chat.getMessage();
                        else if(chat.getMessagetype() == 2) theLastMessage = "你: 已傳送圖片";
                    }
                    else
                    {
                        if(chat.getMessagetype() == 1) theLastMessage = chat.getMessage();
                        else if(chat.getMessagetype() == 2) theLastMessage = "傳送圖片";
                    }

                }

                switch (theLastMessage)
                {
                    case "default":
                        last_msg.setText("No Message");
                        break;

                    default:

                        last_msg.setText(theLastMessage);
                        last_msg.setTextColor(mContext.getResources().getColor(R.color.unseencolor));

                        break;
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
