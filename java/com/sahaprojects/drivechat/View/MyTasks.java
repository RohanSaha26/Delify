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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sahaprojects.drivechat.Adapter.MyTasksAdapter;
import com.sahaprojects.drivechat.Models.MyTask;
import com.sahaprojects.drivechat.R;
import com.sahaprojects.drivechat.databinding.ActivityMyTasksBinding;
import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;

public class MyTasks extends AppCompatActivity {

    ActivityMyTasksBinding binding;
    private FirebaseDatabase database;
    GoogleSignInAccount currentuser;

    ArrayList<MyTask> myTaskArrayList;
    MyTasksAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyTasksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        currentuser = GoogleSignIn.getLastSignedInAccount(this);
        database = FirebaseDatabase.getInstance();
        binding.back.setOnClickListener(v -> {
            finish();
        });
         currentuser = GoogleSignIn.getLastSignedInAccount(this);




        ShimmerFrameLayout shimmerFrameLayout = binding.shimmer;                                    //shimmer effect
        shimmerFrameLayout.startShimmer();                                                          //shimmer effect start


        String currentuserPhotoUrl = String.valueOf(currentuser.getPhotoUrl());

        Glide.with(this).load(currentuserPhotoUrl).placeholder(R.drawable.profiledp)
                .into(binding.mytaskPhoto);
        binding.mytaskPhoto.setOnClickListener((View v) -> {
            Intent intent5 = new Intent(MyTasks.this, Profile.class);
            startActivity(intent5);
            overridePendingTransition(R.anim.no_anim,R.anim.no_anim);
        });


        myTaskArrayList = new ArrayList<>();
        adapter = new MyTasksAdapter(this,myTaskArrayList);
        binding.mytaskrecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.mytaskrecyclerView.setAdapter(adapter);

        String senderid = currentuser.getId();

        binding.notaskanim.setVisibility(View.VISIBLE);
        database.getReference().child("mytasks").child(senderid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                myTaskArrayList.clear();

                if (snapshot.exists())
                {
                    for (DataSnapshot dataSnapshot:snapshot.getChildren())
                    {

                        shimmerFrameLayout.stopShimmer();                                               //shimmer effect stop
                        shimmerFrameLayout.setVisibility(View.GONE);//shimmer effect invisible
                        MyTask myTaskFromDB = dataSnapshot.getValue(MyTask.class);
                        assert myTaskFromDB != null;
                        long myTaskTimeFromDB = myTaskFromDB.getTaskTime();
                        String myTaskSenderIdFromDB = myTaskFromDB.getTaskSenderId();
                        String myTaskBodyFromDB = myTaskFromDB.getTaskBody();
                        String myTaskIsCompleteFromDB = myTaskFromDB.getTaskIsComplete();
                        long myTaskUpdateTimeFromDB = myTaskFromDB.getTaskUpdateTime();
                        String myTaskBodyFromDBDecrypted = null;
                        try {
                            myTaskBodyFromDBDecrypted = AESCrypt.decrypt(myTaskSenderIdFromDB+"#$%^"+myTaskTimeFromDB,myTaskBodyFromDB);


                            MyTask myTaskDecrypted = new MyTask(myTaskBodyFromDBDecrypted,myTaskTimeFromDB,myTaskSenderIdFromDB,myTaskIsCompleteFromDB,myTaskUpdateTimeFromDB);
                            myTaskArrayList.add(myTaskDecrypted);

                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                        }


                        System.out.println("GET ADAPTER ITEM COUNT : "+binding.mytaskrecyclerView.getAdapter().getItemCount());
                        if (binding.mytaskrecyclerView.getAdapter().getItemCount()!=0)
                        {
                            binding.notaskanim.setVisibility(View.GONE);
                        }
                        binding.mytaskrecyclerView.smoothScrollToPosition(binding.mytaskrecyclerView.getAdapter().getItemCount());

                    }
                }
                else
                {
                    shimmerFrameLayout.stopShimmer();                                               //shimmer effect stop
                    shimmerFrameLayout.setVisibility(View.GONE);//shimmer effect invisible
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });






        binding.addtask.setOnClickListener(v -> {

            Date date = new Date();
            String netaskstext = binding.inboxtaskspace.getText().toString();
            if (netaskstext.isEmpty())
            {
                binding.inboxtaskspace.setError("Enter some task first..");
            }
            else
            {
                binding.inboxtaskspace.setText("");

                String etaskstext = null;
                try {
                    etaskstext = AESCrypt.encrypt(senderid+"#$%^"+date.getTime(),netaskstext);
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                MyTask myTask = new MyTask(etaskstext,date.getTime(),senderid,"no",date.getTime());

                //upload encrypted message to database
                database.getReference().child("mytasks").child(senderid).child(String.valueOf(date.getTime())).setValue(myTask);

            }
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