package com.example.myapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Model.Group;
import com.example.myapplication.Model.User;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class AddGroupMemberAdapter extends RecyclerView.Adapter<AddGroupMemberAdapter.ViewHolder>{

    private Context mContext;
    private List<User> mUsers;
    private List<User> mChoose;

    public AddGroupMemberAdapter(Context mContext, List<User> mUsers)
    {
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.mChoose = new ArrayList<>();
    }

    @NonNull
    @Override
    public AddGroupMemberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.choosegroupmember_item,parent,false);
        return new AddGroupMemberAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddGroupMemberAdapter.ViewHolder holder, int position) {
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

        holder.btn_choosegroupmember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*if(holder.btn_choosegroupmember.getDrawable().getCurrent().getConstantState().equals(mContext.getResources().getDrawable(R.drawable.img_checkbox).getConstantState()))
                {
                    holder.btn_choosegroupmember.setImageResource(R.drawable.img_checkboxoutline);
                    for(int i=0;i<mChoose.size();++i)
                    {
                        if(mChoose.get(i).getUsername().equals(user.getUsername()))
                        {
                            mChoose.remove(i);
                            break;
                        }
                    }
                }
                else
                {
                    holder.btn_choosegroupmember.setImageResource(R.drawable.img_checkbox);
                    mChoose.add(user);
                }*/

                if(holder.selected)
                {
                    holder.btn_choosegroupmember.setImageResource(R.drawable.img_checkboxoutline);
                    for(int i=0;i<mChoose.size();++i)
                    {
                        if(mChoose.get(i).getUsername().equals(user.getUsername()))
                        {
                            mChoose.remove(i);
                            break;
                        }
                    }
                    holder.selected = false;
                }
                else
                {
                    holder.btn_choosegroupmember.setImageResource(R.drawable.img_checkbox);
                    mChoose.add(user);
                    holder.selected = true;
                }
            }
        });
    }

    public List<User> getChooseMember()
    {
        return mChoose;
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public ImageView profile_image;
        public ImageButton btn_choosegroupmember;

        boolean selected = false;

        public ViewHolder(View itemView)
        {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            btn_choosegroupmember = itemView.findViewById(R.id.btn_choosegroupmember);
        }
    }

}
