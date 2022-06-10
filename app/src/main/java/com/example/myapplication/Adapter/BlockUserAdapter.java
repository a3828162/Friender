package com.example.myapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Model.AddUser;
import com.example.myapplication.Model.Block;
import com.example.myapplication.Model.BlockUser;
import com.example.myapplication.Model.Friend;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

public class BlockUserAdapter extends RecyclerView.Adapter<BlockUserAdapter.ViewHolder>{

    private Context mContext;
    private List<BlockUser> mblockUsers;
    private List<Block> mblocks;

    public BlockUserAdapter(Context mContext, List<BlockUser> mblockUsers, List<Block> mblocks)
    {
        this.mblockUsers = mblockUsers;
        this.mContext = mContext;
        this.mblocks = mblocks;
    }

    @NonNull
    @Override
    public BlockUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.blockuser_item,parent,false);
        return new BlockUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlockUserAdapter.ViewHolder holder, int position) {

        BlockUser user = mblockUsers.get(position);
        holder.username.setText(user.getUsername());

        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();

        if(user.getImageURl().equals("default"))
        {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }
        else
        {
            Glide.with(mContext).load(user.getImageURl()).into(holder.profile_image);
        }

        holder.btn_blockuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(holder.btn_blockuser.getText().toString().equals("Friends")) return;
                DatabaseReference t1 = FirebaseDatabase.getInstance().getReference("Blocks").child(fuser.getUid()).child(user.getId());
                if(t1!=null) t1.removeValue();

                DatabaseReference t2 = FirebaseDatabase.getInstance().getReference("Friends").child(fuser.getUid()).child(user.getId());
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("id", user.getId());
                t2.setValue(hashMap);

                holder.btn_blockuser.setText("Friend");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mblockUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profile_image;
        public Button btn_blockuser;

        public ViewHolder(View itemView)
        {
            super(itemView);

            btn_blockuser = itemView.findViewById(R.id.btn_blockuser);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);

        }
    }

}
