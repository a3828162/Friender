package com.example.myapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.FriendProfileActivity;
import com.example.myapplication.MessageActivity;
import com.example.myapplication.Model.Chat;
import com.example.myapplication.Model.Chatlist;
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

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private boolean ischat;
    private boolean canclick;

    String theLastMessage;

    public UserAdapter(Context mContext, List<User> mUsers,boolean ischat, boolean canclick)
    {
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.ischat = ischat;
        this.canclick = canclick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        User user = mUsers.get(position);
        holder.username.setText(user.getUsername());
        if(user.getImageURl().equals("default"))
        {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }
        else
        {
             Glide.with(mContext).load(user.getImageURl()).into(holder.profile_image);
        }

        if(ischat)
        {
            lastMessage(user.getId(), holder.last_msg, holder.txt_msgcount);
        }
        else
        {
            holder.last_msg.setVisibility(View.GONE);
        }

        /*if(ischat)
        {
            if(user.getStatus().equals("online"))
            {
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            }
            else
            {
                holder.img_off.setVisibility(View.VISIBLE);
                holder.img_on.setVisibility(View.GONE);
            }
        }
        else
        {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }*/
        if(canclick)
        {
            if (ischat)
            {
                holder.itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view)
                    {
                        //holder.search_users.setText("");

                        Intent intent = new Intent(mContext, MessageActivity.class);
                        intent.putExtra("userid",user.getId());
                        mContext.startActivity(intent);
                    }

                });
            }
            else
            {
                holder.itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view)
                    {
                        //holder.search_users.setText("");

                /*Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid",user.getId());
                mContext.startActivity(intent);*/

                        Intent intent = new Intent(mContext, FriendProfileActivity.class);
                        intent.putExtra("userid",user.getId());
                        mContext.startActivity(intent);
                    }

                });
            }
        }


    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profile_image;
        private ImageView img_on;
        private ImageView img_off;
        private TextView last_msg;
        private TextView txt_msgcount;

        public ViewHolder(View itemView)
        {
            super(itemView);

            //add
            //search_users = itemView.findViewById(R.id.search_users);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            last_msg = itemView.findViewById(R.id.last_msg);
            txt_msgcount = itemView.findViewById(R.id.txt_messagecount);
        }
    }

    // check for last message
    private void lastMessage(String userid, TextView last_msg, TextView txt_messagecount)
    {
        theLastMessage = "default";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean theLastMessageSeen = false;
                int count = 0;

                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
                    Chat chat = snapshot1.getValue(Chat.class);
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid()))
                    {
                        //modify
                        if(firebaseUser.getUid().equals(chat.getSender()))
                        {
                            if(chat.getMessagetype() == 1) theLastMessage = "你: " + chat.getMessage();
                            else if(chat.getMessagetype() == 2) theLastMessage = "你: 已傳送圖片";
                        }
                        else
                        {
                            if(chat.getMessagetype() == 1) theLastMessage = chat.getMessage();
                            else if(chat.getMessagetype() == 2) theLastMessage = "對方傳送圖片";
                        }

                        if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid))
                        {
                            if(chat.getIsseen().equals("true")) theLastMessageSeen = true;
                            else
                            {
                                theLastMessageSeen = false;
                                ++count;
                            }

                            //if(chat.getIsseen().equals("false")) ++count;
                        }
                        else
                        {
                            theLastMessageSeen = true;
                        }



                    }
                }

                switch (theLastMessage)
                {
                    case "default":
                        last_msg.setText("No Message");
                        break;

                    default:

                        last_msg.setText(theLastMessage);

                        if(theLastMessageSeen) last_msg.setTextColor(mContext.getResources().getColor(R.color.unseencolor));
                        else last_msg.setTextColor(mContext.getResources().getColor(R.color.seencolor));

                        if(count == 0) txt_messagecount.setText("");
                        else txt_messagecount.setText(""+count);

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
