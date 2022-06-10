package com.example.myapplication.Fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.Adapter.UserAdapter;
import com.example.myapplication.LoginActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.Model.Block;
import com.example.myapplication.Model.User;
import com.example.myapplication.R;
import com.example.myapplication.StartActivity;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersFragment extends Fragment {

    private static final int RESULT_OK = -1;
    CircleImageView image_profile;
    TextView /*username,*/ email;
    EditText statesign, edit_email, edit_hobbit, edit_birthday, edit_name;

    ImageButton btn_editbirthday, btn_edithabbit, btn_editname, btn_editstatesign;

    DatabaseReference reference;
    FirebaseUser fuser;

    SwitchCompat switch_mode;
    SharedPreferences sharedPreferences = null;

    Button btn_logout;

    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;

    private User currentuser;

    boolean canedit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);



        image_profile = view.findViewById(R.id.profile_image);
        //username = view.findViewById(R.id.username);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("主頁");

        email = view.findViewById(R.id.txt_email);
        statesign = view.findViewById(R.id.edit_statesign);

        btn_editname = view.findViewById(R.id.btn_editName);
        btn_editbirthday = view.findViewById(R.id.btn_editBirthday);
        btn_edithabbit = view.findViewById(R.id.btn_editHabbit);
        btn_editstatesign = view.findViewById(R.id.btn_editStatesign);

        btn_editname.setColorFilter(getResources().getColor(R.color.iconcolor));
        btn_editbirthday.setColorFilter(getResources().getColor(R.color.iconcolor));
        btn_edithabbit.setColorFilter(getResources().getColor(R.color.iconcolor));
        btn_editstatesign.setColorFilter(getResources().getColor(R.color.iconcolor));

        canedit = false;

        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        edit_name = view.findViewById(R.id.username);
        edit_email = view.findViewById(R.id.edt_email);
        edit_hobbit = view.findViewById(R.id.edt_hobbit);
        edit_birthday = view.findViewById(R.id.edt_birthdaty);

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(isAdded())
                {
                    User user = snapshot.getValue(User.class);

                    currentuser = user;

                    edit_name.setText(user.getUsername());
                    //email.setText(user.);
                    //email.setText("Email: " + user.getEmail());

                    edit_email.setText(user.getEmail());

                    if(user.getStatesign().equals("default"))
                    {
                        statesign.setText("你可以在這邊寫點什麼");
                        statesign.setEnabled(false);
                    }
                    else
                    {
                        statesign.setText(user.getStatesign());
                        statesign.setEnabled(false);
                    }

                    if(user.getHabbit().equals("default"))
                    {
                        edit_hobbit.setText("輸入點興趣吧");
                    }
                    else
                    {
                        edit_hobbit.setText(user.getHabbit());
                    }

                    if(user.getBirthday().equals("default"))
                    {
                        edit_birthday.setText("讓大家知道你的生日");
                    }
                    else
                    {
                        edit_birthday.setText(user.getBirthday());
                    }

                    if(user.getImageURl().equals("default"))
                    {
                        image_profile.setImageResource(R.mipmap.ic_launcher);
                    }
                    else
                    {
                        if(isAdded()) Glide.with(getContext()).load(user.getImageURl()).into(image_profile);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        switch_mode = view.findViewById(R.id.switch_mode);

        sharedPreferences = getActivity().getSharedPreferences("night", 0);
        Boolean booleanValue = sharedPreferences.getBoolean("night_mode",false);
        if(booleanValue)
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            switch_mode.setChecked(true);
        }
        else
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            switch_mode.setChecked(false);
        }

        switch_mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    switch_mode.setChecked(true);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("night_mode",true);
                    editor.commit();
                }
                else
                {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    switch_mode.setChecked(false);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("night_mode",false);
                    editor.commit();
                }
            }
        });

        //add
        btn_logout = view.findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //FirebaseAuth.getInstance().signOut();


                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtra("logout","logout");
                //FirebaseAuth.getInstance().signOut();
                startActivity(intent);
                getActivity().finish();
            }
        });

        btn_edithabbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!canedit)
                {
                    //Toast.makeText(getContext(),"Edit Start",Toast.LENGTH_SHORT).show();
                    btn_edithabbit.setImageResource(R.drawable.editend);
                    canedit = true;

                    edit_hobbit.setEnabled(true);
                    edit_hobbit.setBackground(getResources().getDrawable(R.drawable.edittext_shape_after));
                }
                else
                {
                    //Toast.makeText(getContext(),"Edit End",Toast.LENGTH_SHORT).show();
                    btn_edithabbit.setImageResource(R.drawable.editstart);
                    canedit = false;

                    edit_hobbit.setEnabled(false);
                    edit_hobbit.setBackground(getResources().getDrawable(R.drawable.edittext_shape2));

                    if(!edit_hobbit.getText().toString().equals(""))
                    {
                        DatabaseReference tmpRefer = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("habbit");
                        tmpRefer.setValue(edit_hobbit.getText().toString());
                    }
                    else
                    {
                        edit_hobbit.setText(currentuser.getHabbit());
                    }
                }
            }
        });

        btn_editbirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!canedit)
                {
                    //Toast.makeText(getContext(),"Edit Start",Toast.LENGTH_SHORT).show();
                    btn_editbirthday.setImageResource(R.drawable.editend);
                    canedit = true;

                    edit_birthday.setEnabled(true);
                    edit_birthday.setBackground(getResources().getDrawable(R.drawable.edittext_shape_after));
                }
                else
                {
                    //Toast.makeText(getContext(),"Edit End",Toast.LENGTH_SHORT).show();
                    btn_editbirthday.setImageResource(R.drawable.editstart);
                    canedit = false;

                    edit_birthday.setEnabled(false);
                    edit_birthday.setBackground(getResources().getDrawable(R.drawable.edittext_shape2));

                    if(!edit_birthday.getText().toString().equals(""))
                    {
                        DatabaseReference tmpRefer = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("birthday");
                        tmpRefer.setValue(edit_birthday.getText().toString());
                    }
                    else
                    {
                        edit_birthday.setText(currentuser.getBirthday());
                    }
                }
            }
        });

        btn_editname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!canedit)
                {
                    //Toast.makeText(getContext(),"Edit Start",Toast.LENGTH_SHORT).show();
                    btn_editname.setImageResource(R.drawable.editend);
                    canedit = true;

                    edit_name.setEnabled(true);
                    edit_name.setBackground(getResources().getDrawable(R.drawable.edittext_shape_after));

                    edit_name.setTextColor(getResources().getColor(R.color.black));
                }
                else
                {
                    //Toast.makeText(getContext(),"Edit End",Toast.LENGTH_SHORT).show();
                    btn_editname.setImageResource(R.drawable.editstart);
                    canedit = false;

                    edit_name.setEnabled(false);
                    edit_name.setBackground(getResources().getDrawable(R.color.backgroundcolor));

                    edit_name.setTextColor(getResources().getColor(R.color.textblackcolor));

                    if(!edit_birthday.getText().toString().equals(""))
                    {
                        DatabaseReference tmpRefer = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("username");
                        tmpRefer.setValue(edit_name.getText().toString());

                        DatabaseReference tmpRefer2 = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("search");
                        tmpRefer2.setValue(edit_name.getText().toString().toLowerCase());
                    }
                    else
                    {
                        edit_name.setText(currentuser.getBirthday());
                    }
                }
            }
        });

        btn_editstatesign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!canedit)
                {
                    //Toast.makeText(getContext(),"Edit Start",Toast.LENGTH_SHORT).show();
                    btn_editstatesign.setImageResource(R.drawable.editend);
                    canedit = true;

                    statesign.setEnabled(true);
                    statesign.setBackground(getResources().getDrawable(R.drawable.edittext_shape_after));

                    //statesign.setTextColor(getResources().getColor(R.color.black));
                }
                else
                {
                    //Toast.makeText(getContext(),"Edit End",Toast.LENGTH_SHORT).show();
                    btn_editstatesign.setImageResource(R.drawable.editstart);
                    canedit = false;

                    statesign.setEnabled(false);
                    statesign.setBackground(getResources().getDrawable(R.color.backgroundcolor));

                    //statesign.setTextColor(getResources().getColor(R.color.textblackcolor));

                    if(!statesign.getText().toString().equals(""))
                    {
                        DatabaseReference tmpRefer = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("statesign");
                        tmpRefer.setValue(statesign.getText().toString());
                    }
                    else
                    {
                        statesign.setText(currentuser.getStatesign());
                    }
                }
            }
        });

        edit_hobbit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) edit_hobbit.setText("");
            }
        });

        edit_birthday.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) edit_birthday.setText("");
            }
        });

        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        //FirebaseAuth.getInstance().signOut();
        //FirebaseAuth.getInstance().signOut();
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
        ContentResolver contentResolver = getContext().getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(this.imageUri));
    }

    private void uploadImage()
    {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading");
        pd.show();

        if(imageUri != null)
        {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {
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

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURl",mUri);
                        reference.updateChildren(map);

                        pd.dismiss();
                    }
                    else
                    {
                        Toast.makeText(getContext(),"Failed",Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }
        else
        {
            Toast.makeText(getContext(),"No image selected",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(),"Upload in progress",Toast.LENGTH_SHORT).show();
            }
            else
            {
                uploadImage();
            }
        }
    }
}