package com.sahaprojects.drivechat.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sahaprojects.drivechat.Adapter.CollabTasksAdapter;
import com.sahaprojects.drivechat.Adapter.MyTasksAdapter;
import com.sahaprojects.drivechat.Models.CollabTasks;
import com.sahaprojects.drivechat.Models.MyTask;
import com.sahaprojects.drivechat.R;
import com.sahaprojects.drivechat.databinding.ActivityInboxTaskBinding;
import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicMarkableReference;

public class InboxTask extends AppCompatActivity {

    ActivityInboxTaskBinding binding;
     String taskReceiverPhotoStr;
     String taskRecieverGmailstr;
     String taskRecieverNamestr;
     String taskRecieverId;
     FirebaseDatabase database;
     GoogleSignInAccount currentuser;
     CollabTasksAdapter adapter;
    ArrayList<CollabTasks> collabTasksArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInboxTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE); //block screenshots
        currentuser = GoogleSignIn.getLastSignedInAccount(this);
        database = FirebaseDatabase.getInstance();

        ShimmerFrameLayout shimmerFrameLayout = binding.shimmer;                                    //shimmer effect
        shimmerFrameLayout.startShimmer();                                                          //shimmer effect start

        {
            taskReceiverPhotoStr = getIntent().getExtras().getString("reciever_photo");               //get value from another intent
            taskRecieverGmailstr = getIntent().getExtras().getString("reciever_gmail");
            taskRecieverNamestr = getIntent().getExtras().getString("reciever_name");
            taskRecieverId = getIntent().getExtras().getString("reciever_id");
        }
        binding.taskreceiverName.setText(taskRecieverNamestr);
        binding.taskreceiverGmail.setText(taskRecieverGmailstr);
        Glide.with(this).load(taskReceiverPhotoStr).placeholder(R.drawable.profiledp)
                .into(binding.taskreceiverPhoto);


        collabTasksArrayList = new ArrayList<>();

        adapter = new CollabTasksAdapter(this,collabTasksArrayList,taskRecieverId,taskRecieverGmailstr,taskReceiverPhotoStr,taskRecieverNamestr);

        binding.inboxtaskrecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.inboxtaskrecyclerView.setAdapter(adapter);


        String senderId = currentuser.getId();
        String receiverId = taskRecieverId;

        String senderRoom = senderId+receiverId;

        database.getReference().child("collabtasks").child(senderRoom).child("tasks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                collabTasksArrayList.clear();

                if (snapshot.exists())
                {
                    shimmerFrameLayout.stopShimmer();                                               //shimmer effect stop
                    shimmerFrameLayout.setVisibility(View.GONE);//shimmer effect invisible
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()) {

//                    CollabTasks collabTasksObj = dataSnapshot.getValue(CollabTasks.class);
//                    String collabTaskBody = collabTasksObj.getCtaskBody();
                        String collabTaskBody = dataSnapshot.child("ctaskBody").getValue(String.class);
////                    String collabTaskSenderId = collabTasksObj.getCtaskSenderId();
                        String collabTaskSenderId = dataSnapshot.child("ctaskSenderId").getValue(String.class);
////                    long collabTaskTime = collabTasksObj.getCtaskTime();
                        long collabTaskTime = dataSnapshot.child("ctaskTime").getValue(long.class);
////                    String collabTaskReceiverId = collabTasksObj.getCtaskReceiverId();
                        String collabTaskReceiverId = dataSnapshot.child("ctaskReceiverId").getValue(String.class);
////                    String CtaskCompletebySender = collabTasksObj.getCtaskIsCompletebySender();
                        String CtaskCompletebySender = dataSnapshot.child("ctaskIsCompletebySender").getValue(String.class);
////                    String CtaskCompletebyReceiver = collabTasksObj.getCtaskIsCompletebyReceiver();
                        String CtaskCompletebyReceiver = dataSnapshot.child("ctaskIsCompletebyReceiver").getValue(String.class);

                        String collabTaskBodyDecrypted = null;

                        try {
                            collabTaskBodyDecrypted = AESCrypt.decrypt(collabTaskSenderId+"^%$#"+collabTaskTime,collabTaskBody);
                            CollabTasks collabTasksDecrypted = new CollabTasks(collabTaskBodyDecrypted,collabTaskTime,collabTaskSenderId,collabTaskReceiverId,CtaskCompletebySender,CtaskCompletebyReceiver);

                            collabTasksArrayList.add(collabTasksDecrypted);
                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                        }

                        if (binding.inboxtaskrecyclerView.getAdapter().getItemCount()!=0)
                        {
                            binding.nocollabtasks.setVisibility(View.GONE);
                        }
                        binding.inboxtaskrecyclerView.smoothScrollToPosition(binding.inboxtaskrecyclerView.getAdapter().getItemCount());
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
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


















        database.getReference().child("presence").child(taskRecieverId).addValueEventListener(new ValueEventListener() {
            @Override                                                                               //chceking presence of another user and set indicator
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                {
                    String status = snapshot.getValue(String.class);

                    assert status != null;

                    if (status.equals("Online")||status.startsWith("Typing"))                       // is Online?
                    {

                        binding.taskInboxOnlineIndicator.setVisibility(View.VISIBLE);
                    }
                    else if (status.equals("Offline"))                                              // is Offline?
                    {
                        binding.taskInboxOnlineIndicator.setVisibility(View.GONE);
                    }
                    else                                                                            //else part (Offline)
                    {
                        binding.taskInboxOnlineIndicator.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        binding.gotochats.setOnClickListener((View v) -> {
            Intent reciever_chats = new Intent(InboxTask.this, InboxChat.class);
            reciever_chats.putExtra("reciever_gmail",taskRecieverGmailstr);
            reciever_chats.putExtra("reciever_name",taskRecieverNamestr);
            reciever_chats.putExtra("reciever_photo",taskReceiverPhotoStr);
            reciever_chats.putExtra("reciever_id",taskRecieverId);
            startActivity(reciever_chats);
            overridePendingTransition(R.anim.no_anim,R.anim.no_anim);
            finish();
        });




        binding.addtask.setOnClickListener(v -> {
            String nonEncryptedTask = binding.inboxtaskspace.getText().toString();

            if (nonEncryptedTask.isEmpty())
            {
                binding.inboxtaskspace.setError("Enter some task first...");
            }
            else
            {
                binding.inboxtaskspace.setText("");

                Date cdate = new Date();
                String enCryptedTask = null;
                try {
                    enCryptedTask = AESCrypt.encrypt(senderId+"^%$#"+cdate.getTime(),nonEncryptedTask);
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }


                CollabTasks collabTasksObj = new CollabTasks(enCryptedTask,cdate.getTime(),senderId,receiverId,"no","no");

                database.getReference().child("collabtasks").child(senderRoom).child("lasttasks").setValue(collabTasksObj); //upload last message to database
                database.getReference().child("collabtasks").child(receiverId+senderId).child("lasttasks").setValue(collabTasksObj);
                //upload encrypted message to database
                database.getReference().child("collabtasks").child(senderRoom).child("tasks").child(String.valueOf(cdate.getTime())).setValue(collabTasksObj);
                database.getReference().child("collabtasks").child(receiverId+senderId).child("tasks").child(String.valueOf(cdate.getTime())).setValue(collabTasksObj);

                database.getReference().child("collabtaskslist").child(senderId).child(receiverId).setValue("yes");
                database.getReference().child("collabtaskslist").child(receiverId).child(senderId).setValue("yes");
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