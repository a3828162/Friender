package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.*;

import com.example.myapplication.Notifications.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;


import com.rengwuxian.materialedittext.MaterialEditText;

public class LoginActivity extends AppCompatActivity {

    MaterialEditText email, password;
    Button btn_login;

    FirebaseAuth auth;
    TextView forgot_password;

    @Override
    protected void onStart() {
        super.onStart();
        //Intent intent = getIntent();
        //String tmp = intent.getStringExtra("str");
        //if(auth!=null) FirebaseAuth.getInstance().signOut();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent backintent = getIntent();
        String tmp = backintent.getStringExtra("logout");

        if(tmp!=null&&tmp.equals("logout"))
        {
            FirebaseAuth.getInstance().signOut();
        }


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable nav = toolbar.getNavigationIcon();
        nav.setTint(getResources().getColor(R.color.textblackcolor));



        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 影片說會crash 但我不會
                //finish();
                Intent intent = new Intent(LoginActivity.this,StartActivity.class);
                //modify
                //intent.putExtra("logout","T");
                startActivity(intent);
                finish();
                // modify
                //startActivity(new Intent(MessageActivity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });



        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.Email);
        password = findViewById(R.id.Password);
        btn_login = findViewById(R.id.btn_login);
        forgot_password = findViewById(R.id.forgot_password);

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ResetPasswordActivity.class));
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();

                if( TextUtils.isEmpty(txt_email) ||TextUtils.isEmpty(txt_password))
                {
                    Toast.makeText(LoginActivity.this,"All field are required",Toast.LENGTH_LONG).show();
                }
                else
                {
                    auth.signInWithEmailAndPassword(txt_email,txt_password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful())
                                    {
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                        if(user.isEmailVerified())
                                        {
                                            //add

                                            FirebaseMessaging.getInstance().getToken()
                                                    .addOnCompleteListener(new OnCompleteListener<String>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<String> task) {
                                                            if (!task.isSuccessful()) {
                                                                //Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                                                return;
                                                            }

                                                            // Get new FCM registration token
                                                            String token = task.getResult();

                                                            // Log and toast

                                                            //Log.d(TAG, token);
                                                            //Toast.makeText(LoginActivity.this, token, Toast.LENGTH_SHORT).show();

                                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
                                                            FirebaseUser f = FirebaseAuth.getInstance().getCurrentUser();
                                                            Token token1 = new Token(token);

                                                            reference.child(f.getUid()).setValue(token1);

                                                        }
                                                    });

                                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else
                                        {
                                            user.sendEmailVerification();
                                            Toast.makeText(LoginActivity.this,"Resend verify mail, please check your email to verify",Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                    else
                                    {
                                        Toast.makeText(LoginActivity.this,"Authentication failed",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }

                //finish();
            }
        });
    }


}