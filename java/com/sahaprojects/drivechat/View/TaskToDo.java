package com.sahaprojects.drivechat.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sahaprojects.drivechat.Adapter.TaskListAdapter;
import com.sahaprojects.drivechat.Adapter.UserAdapter;
import com.sahaprojects.drivechat.Models.User;
import com.sahaprojects.drivechat.R;
import com.sahaprojects.drivechat.databinding.ActivityTaskTodoBinding;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicMarkableReference;

public class TaskToDo extends AppCompatActivity {

    ActivityTaskTodoBinding binding;
    private FirebaseDatabase database;
    GoogleSignInAccount currentuser;
    ArrayList<User> users;
    TaskListAdapter tasklistadapter;
    String USERNAME,USERPHOTO,USERGMAIL,USERID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskTodoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        currentuser = GoogleSignIn.getLastSignedInAccount(this);
        database = FirebaseDatabase.getInstance();
        buttons();


        ShimmerFrameLayout shimmerFrameLayout = binding.shimmer;                                    //shimmer effect
        shimmerFrameLayout.startShimmer();                                                          //shimmer effect start

        users = new ArrayList<>();                                                                  //define users as an array-list.
        tasklistadapter =new TaskListAdapter(this, users);                                          //Initialize Adapter
        binding.taskRecyclerview.setAdapter(tasklistadapter);                                           //Set adapter with the recycler view
        binding.taskRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        database.getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();


                for (DataSnapshot data : snapshot.child("users").getChildren())
                {
                    shimmerFrameLayout.stopShimmer();                                               //shimmer effect stop
                    shimmerFrameLayout.setVisibility(View.GONE);//shimmer effect invisible
                    //Fetching Adapter data from firebase
                    USERNAME = data.child("user_name").getValue(String.class);
                    USERPHOTO = data.child("user_photo").getValue(String.class);
                    USERGMAIL = data.child("user_email").getValue(String.class);
                    USERID = data.child("user_id").getValue(String.class);


                    if (snapshot.child("collabtaskslist").child(currentuser.getId()).child(USERID).exists())    // who chats with current user available these users only.
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


                if (binding.taskRecyclerview.getAdapter().getItemCount()!=0)
                {
                    binding.notaskanim.setVisibility(View.GONE);
                }
                tasklistadapter.notifyDataSetChanged();                                                 //notify to adapter that data-set changed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void buttons() {
        binding.chats.setOnClickListener((View v) -> {
            Intent intent1 = new Intent(TaskToDo.this, Chats.class);
            startActivity(intent1);
            overridePendingTransition(R.anim.no_anim,R.anim.no_anim);
            finish();
        });

        binding.profile.setOnClickListener((View v) -> {
            Intent intent3 = new Intent(TaskToDo.this, Profile.class);
            startActivity(intent3);
            overridePendingTransition(R.anim.no_anim,R.anim.no_anim);
            finish();
        });

        binding.qricon.setOnClickListener((View v) -> {
            Intent intent4 = new Intent(TaskToDo.this, QrCode.class);
            startActivity(intent4);
            overridePendingTransition(R.anim.no_anim,R.anim.no_anim);
            finish();
        });

//        binding.task.setOnClickListener(v ->
//        {
//            Intent intent5 = new Intent(TaskToDo.this, TaskToDo.class);
//            startActivity(intent5);
//            overridePendingTransition(R.anim.no_anim,R.anim.no_anim);
//            finish();
//        });

        binding.mytasks.setOnClickListener(v -> {
            Intent intent6 = new Intent(TaskToDo.this, MyTasks.class);
            startActivity(intent6);
            overridePendingTransition(R.anim.no_anim,R.anim.no_anim);
        });
    }




    @Override                                                                                       //set currentuser online
    protected void onResume() {
        super.onResume();
        database.getReference().child("presence").child(currentuser.getId()).setValue("Online");
    }

    @Override                                                                                       //set currentuser offline
    protected void onPause() {
        super.onPause();
        database.getReference().child("presence").child(currentuser.getId()).setValue("Offline");

    }
}