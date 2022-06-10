package com.example.myapplication.Adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Model.Chat;
import com.example.myapplication.Model.User;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.Reference;
import java.util.List;

public class GroupMessageAdapter extends RecyclerView.Adapter<GroupMessageAdapter.ViewHolder>{

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Chat> mChat;
    private String imageurl;
    private String groupid;

    FirebaseUser fuser;

    public GroupMessageAdapter(Context mContext, List<Chat> mChat, String imageurl, String groupid)
    {
        this.mChat = mChat;
        this.mContext = mContext;
        this.imageurl = imageurl;
        this.groupid = groupid;
    }

    @NonNull
    @Override
    public GroupMessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT)
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_group_item_right,parent,false);
            return new GroupMessageAdapter.ViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_group_item_left,parent,false);
            return new GroupMessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull GroupMessageAdapter.ViewHolder holder, int position) {

        Chat chat = mChat.get(position);

        if(chat.getMessagetype() == 1)
        {
            holder.show_message.setVisibility(View.VISIBLE);
            holder.show_message.setText(chat.getMessage());
            holder.txt_seen.setVisibility(View.VISIBLE);
            holder.txt_time.setVisibility(View.VISIBLE);

            holder.show_message.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    //holder.show_message.setText("hhhh");

                    //DatabaseReference t = FirebaseDatabase.getInstance().getReference("Chats").child(chat.getId());
                    //t.removeValue();
                    if(chat.getSender().equals(fuser.getUid()))
                    {

                        PopupMenu popup = new PopupMenu(mContext, holder.show_message);
                        popup.inflate(R.menu.menu);
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.copy:
                                        //handle menu1 click

                                        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                                        ClipData clip = ClipData.newPlainText("label", chat.getMessage());
                                        clipboard.setPrimaryClip(clip);

                                        return true;
                                    case R.id.TackBack:
                                        //handle menu1 click
                                        DatabaseReference t = FirebaseDatabase.getInstance().getReference("Groups").child(groupid).child("Chats").child(chat.getId());
                                        t.removeValue();

                                        return true;
                                    default:
                                        return false;
                                }
                            }
                        });
                        //displaying the popup
                        popup.show();
                    }
                    else
                    {

                        PopupMenu popup = new PopupMenu(mContext, holder.show_message);
                        popup.inflate(R.menu.menu2);
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.copy:
                                        //handle menu1 click

                                        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                                        ClipData clip = ClipData.newPlainText("label", chat.getMessage());
                                        clipboard.setPrimaryClip(clip);

                                        return true;
                                    default:
                                        return false;
                                }
                            }
                        });
                        //displaying the popup
                        popup.show();

                    }


                    return true;
                }
            });
        }
        else if(chat.getMessagetype() == 2 )
        {
            holder.show_photo.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(chat.getMessage()).into(holder.show_photo);
            holder.txt_seen2.setVisibility(View.VISIBLE);
            holder.txt_time2.setVisibility(View.VISIBLE);

            if(chat.getSender().equals(fuser.getUid())) {
                holder.show_photo.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {


                        PopupMenu popup = new PopupMenu(mContext, holder.show_photo);
                        popup.inflate(R.menu.menu3);
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.TackBack:
                                        //handle menu1 click
                                        DatabaseReference t = FirebaseDatabase.getInstance().getReference("Chats").child(chat.getId());
                                        t.removeValue();

                                        return true;
                                    default:
                                        return false;
                                }
                            }
                        });

                        popup.show();

                        return true;
                    }
                });
            }
        }

        /*if(imageurl.equals("default"))
        {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }
        else
        {
            Glide.with(mContext).load(imageurl).into(holder.profile_image);
        }*/

        DatabaseReference tmp = FirebaseDatabase.getInstance().getReference("Users").child(chat.getSender());
        tmp.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    User user = snapshot.getValue(User.class);
                    holder.txt_name.setText(user.getUsername());
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

        if(chat.getMessagetype() == 1)
        {
            holder.txt_time.setText(chat.getTime());
        }
        else if(chat.getMessagetype() == 2)
        {
            holder.txt_time2.setText(chat.getTime());
        }

        if(position == mChat.size()-1 && getItemViewType(position)==MSG_TYPE_RIGHT)
        {

            if(chat.getIsseen().equals("true"))
            {
                if(chat.getMessagetype() == 1) holder.txt_seen.setText("已讀");
                else if(chat.getMessagetype() == 2) holder.txt_seen2.setText("已讀");


            }
            else
            {
                if(chat.getMessagetype() == 1) holder.txt_seen.setText("已傳送");
                else if(chat.getMessagetype() == 2) holder.txt_seen2.setText("已傳送");

                //holder.txt_seen.setText("已傳送");
            }
        }
        else
        {
            if(chat.getMessagetype() == 1) holder.txt_seen.setVisibility(View.GONE);
            else if(chat.getMessagetype() == 2) holder.txt_seen2.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_message;
        public ImageView profile_image;
        public TextView txt_seen;
        public TextView txt_seen2;
        public ImageView show_photo;
        public TextView txt_time;
        public TextView txt_time2;
        public TextView txt_name;


        public ViewHolder(View itemView)
        {
            super(itemView);

            txt_name = itemView.findViewById(R.id.txt_name);

            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            show_photo = itemView.findViewById(R.id.show_photo);
            txt_seen2 = itemView.findViewById(R.id.txt_seen2);

            txt_time = itemView.findViewById(R.id.txt_time);
            txt_time2 = itemView.findViewById(R.id.txt_time2);
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(fuser.getUid()))
        {
            return MSG_TYPE_RIGHT;
        }
        else
        {
            return MSG_TYPE_LEFT;
        }
    }

}
