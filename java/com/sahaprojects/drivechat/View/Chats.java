package com.sahaprojects.drivechat.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sahaprojects.drivechat.Adapter.UserAdapter;
import com.sahaprojects.drivechat.Models.User;
import com.sahaprojects.drivechat.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.sahaprojects.drivechat.databinding.ActivityChatsBinding;

import java.util.ArrayList;

public class Chats extends AppCompatActivity {

    GoogleSignInAccount currentuser;
    private long backPressedTime;
    private Toast backToast;
    ActivityChatsBinding binding;
    FirebaseDatabase database;
    ArrayList<User> users;
    UserAdapter userAdapter;

    String USERNAME;
    String USERID;
    String USERGMAIL;
    String USERPHOTO;

    String isChat = "no";

    @Override
    protected void onResume() {
        super.onResume();
        database.getReference().child("presence").child(currentuser.getId()).setValue("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        database.getReference().child("presence").child(currentuser.getId()).setValue("Offline");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());                                                          //View Binding
        currentuser = GoogleSignIn.getLastSignedInAccount(this);
        buttons();
        database = FirebaseDatabase.getInstance();
        users = new ArrayList<>();                                                                  //define users as an array-list.
        userAdapter =new UserAdapter(this, users);                                          //Initialize Adapter
        binding.chatRecyclerview.setAdapter(userAdapter);                                           //Set adapter with the recycler view
        binding.chatRecyclerview.setLayoutManager(new LinearLayoutManager(this));          //set linear layout manager
        ShimmerFrameLayout shimmerFrameLayout = binding.shimmer;                                    //shimmer effect
        shimmerFrameLayout.startShimmer();                                                          //shimmer effect start


//        database.getReference().child("chatlist").child(currentuser.getId()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull  DataSnapshot snapshot) {
//                if (snapshot.equals("yes"))
//                {
//                    isChat = "yes";
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull  DatabaseError error) {
//
//            }
//        });


        database.getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();


                for (DataSnapshot data : snapshot.child("users").getChildren())
                {
                    shimmerFrameLayout.stopShimmer();                                               //shimmer effect stop
                    shimmerFrameLayout.setVisibility(View.GONE);//shimmer effect invisible
                    binding.nochats.setVisibility(View.VISIBLE);
                    //Fetching Adapter data from firebase
                    USERNAME = data.child("user_name").getValue(String.class);
                    USERPHOTO = data.child("user_photo").getValue(String.class);
                    USERGMAIL = data.child("user_email").getValue(String.class);
                    USERID = data.child("user_id").getValue(String.class);

//                    Query sortbytime = snapshot.child("chats").child(currentuser.getId()+USERID).child("messages").getRef().orderByChild("timestamp");


//                    binding.nochats.setVisibility(View.VISIBLE);


                    if (binding.chatRecyclerview.getAdapter().getItemCount()!=0)
                    {
                        binding.nochats.setVisibility(View.GONE);
                    }

                    if (snapshot.child("chatlist").child(currentuser.getId()).child(USERID).exists())    // who chats with current user available these users only.
                    {
                        {
                            if(!currentuser.getId().equals(USERID))                                          //Current user didn't showing in the recycler view
                            {
                                users.add(new User(USERNAME,USERID,USERGMAIL,USERPHOTO));                    //add data in adapter of recycler view

//                            binding.nochats.setVisibility(View.GONE);

                            }
                        }

                    }
                }


                userAdapter.notifyDataSetChanged();                                                 //notify to adapter that data-set changed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void buttons() {

//        binding.chats.setOnClickListener((View v) -> {
//            Intent intent1 = new Intent(Chats.this, Chats.class);
//            startActivity(intent1);
//            overridePendingTransition(R.anim.no_anim,R.anim.no_anim);
//            finish();
//        });
        binding.profile.setOnClickListener((View v) -> {
            Intent intent3 = new Intent(Chats.this, Profile.class);
            startActivity(intent3);
            overridePendingTransition(R.anim.no_anim,R.anim.no_anim);
            finish();
        });

        binding.qricon.setOnClickListener((View v) -> {
            Intent intent4 = new Intent(Chats.this, QrCode.class);
            startActivity(intent4);
            overridePendingTransition(R.anim.no_anim,R.anim.no_anim);
            finish();
        });

        binding.task.setOnClickListener(v ->
        {
            Intent intent4 = new Intent(Chats.this, TaskToDo.class);
            startActivity(intent4);
            overridePendingTransition(R.anim.no_anim,R.anim.no_anim);
            finish();
        });
        binding.addchatsbtn.setOnClickListener((View v) -> {
            Intent intent5 = new Intent(Chats.this, StartAChat.class);
            startActivity(intent5);
            overridePendingTransition(R.anim.no_anim,R.anim.no_anim);
        });

        String currentuserPhotoUrl = String.valueOf(currentuser.getPhotoUrl());

        Glide.with(this).load(currentuserPhotoUrl).placeholder(R.drawable.profiledp)
                .into(binding.chatsUserdp);

        binding.chatsUserdp.setOnClickListener((View v) -> {
            Intent intent5 = new Intent(Chats.this, Profile.class);
            startActivity(intent5);
            overridePendingTransition(R.anim.no_anim,R.anim.no_anim);
        });

    }

    @Override
    public void onBackPressed() {                                                                   //When user double click 'back' button in 2sec ,then close the App.
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            this.finishAffinity();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }

        backPressedTime = System.currentTimeMillis();
    }
}