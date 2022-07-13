package com.sahaprojects.drivechat.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sahaprojects.drivechat.Models.CollabTasks;
import com.sahaprojects.drivechat.Models.User;
import com.sahaprojects.drivechat.R;
import com.sahaprojects.drivechat.View.InboxChat;
import com.sahaprojects.drivechat.View.InboxTask;
import com.sahaprojects.drivechat.databinding.CardChatDesignBinding;
import com.sahaprojects.drivechat.databinding.TaskCollabBoxBgBinding;
import com.sahaprojects.drivechat.databinding.TaskCollabCardDesignBinding;
import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskListViewHolder> {



    Context context;
    ArrayList<User> collabtaskuserlist;
     GoogleSignInAccount currentuser;


    public TaskListAdapter(Context context, ArrayList<User> collabtaskuserlist) {
        this.context = context;
        this.collabtaskuserlist = collabtaskuserlist;
    }

    @NonNull
    @Override
    public TaskListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_collab_card_design, parent ,false);
        return new TaskListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskListViewHolder holder, int position) {

        User user = collabtaskuserlist.get(position);
        currentuser = GoogleSignIn.getLastSignedInAccount(context);

        holder.binding.cardTaskName.setText(user.getUser_name());
        Glide.with(context).load(user.getUser_photo()).placeholder(R.drawable.profiledp).into(holder.binding.taskprofiledp);
        holder.binding.cardOnlineIndicator.setVisibility(View.GONE);

        String senderRoom = currentuser.getId()+user.getUser_id();


        holder.itemView.setOnClickListener(v -> {
            Intent toinbox = new Intent(context, InboxTask.class);                                  //pass value to InboxChat
            toinbox.putExtra("reciever_name",user.getUser_name());
            toinbox.putExtra("reciever_id",user.getUser_id());
            toinbox.putExtra("reciever_gmail",user.getUser_email());
            toinbox.putExtra("reciever_photo",user.getUser_photo());
            context.startActivity(toinbox);
        });



        holder.binding.removefromlist.setOnClickListener(v -> {
            AlertDialog deletefromlist = new AlertDialog.Builder(context)
                    .setTitle("Remove "+ user.getUser_email()+" from your taskslist?")
                    .setMessage(user.getUser_email() +" will be deleted from this tasks list. All of your tasks will not be deleted.")
                    .setNegativeButton("No",null)
                    .setPositiveButton("REMOVE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseDatabase.getInstance().getReference().child("collabtaskslist").child(currentuser.getId()).child(user.getUser_id()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(context,user.getUser_email() +" removed from this list.",Toast.LENGTH_SHORT).show();
                                }
                            });

                        }

                    })
                    .setCancelable(false)
                    .create();

            deletefromlist.show();
            deletefromlist.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
        });

//        FirebaseDatabase.getInstance().getReference().child("collabtasks").child(senderRoom).child("lasttasks").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists())
//                {
//                    String collabTaskBody = dataSnapshot.child("ctaskBody").getValue(String.class);
//                    long collabTaskTime = dataSnapshot.child("ctaskTime").getValue(long.class);
//                    String collabTaskSenderId = dataSnapshot.child("ctaskSenderId").getValue(String.class);
//                    String CtaskCompletebyReceiver = dataSnapshot.child("ctaskIsCompletebyReceiver").getValue(String.class);
//                    String CtaskCompletebySender = dataSnapshot.child("ctaskIsCompletebySender").getValue(String.class);
//
//
//
//                    String collabTaskBodyDecrypted = null;
//
//
//                    try {
//                        collabTaskBodyDecrypted = AESCrypt.decrypt(collabTaskSenderId+"^%$#"+collabTaskTime,collabTaskBody);
//
//
//
//
//                    } catch (GeneralSecurityException e) {
//                        e.printStackTrace();
//                    }
//
//
//                    if (currentuser.getId().equals(collabTaskSenderId))
//                    {
//                        holder.binding.cardTaskMessage.setText("YOU: "+collabTaskBodyDecrypted);
//                        if (CtaskCompletebySender.equals("yes"))
//                        {
//                            holder.binding.taskiscompletebyyou.setVisibility(View.VISIBLE);
//                        }
//                        else
//                        {
//                            holder.binding.taskiscompletebyyou.setVisibility(View.GONE);
//
//                        }
//                    }
//                    else
//                    {
//                        holder.binding.cardTaskMessage.setText(collabTaskBodyDecrypted);
//                        if (CtaskCompletebyReceiver.equals("yes"))
//                        {
//                            holder.binding.taskiscompletebyyou.setVisibility(View.VISIBLE);
//                        }
//                        else
//                        {
//                            holder.binding.taskiscompletebyyou.setVisibility(View.GONE);
//
//                        }
//                    }
//
//
//
//
//                    SimpleDateFormat simpleTimeFormat =new SimpleDateFormat("hh:mm a", Locale.getDefault());
//                    SimpleDateFormat simpleDateFormat =new SimpleDateFormat("dd MMM YY", Locale.getDefault());
//                    String date = simpleDateFormat.format(collabTaskTime);
//                    String time = simpleTimeFormat.format(collabTaskTime);
//                    Date currentdt = new Date();
//                    String currentdate = simpleDateFormat.format(currentdt.getTime());
//                    Calendar yesterday = Calendar.getInstance();
//                    yesterday.add(Calendar.DATE,-1);
//                    String yesterdaydate = simpleDateFormat.format(yesterday.getTime());
//
//                    if (currentdate.equals(date))                                               // today's message didn't showing the date
//                    {
//                        holder.binding.cardTaskDate.setText(time);
//                    }
//                    else if (yesterdaydate.equals(date)){                                       // yesterday message showing "Ysterday"
//                        holder.binding.cardTaskDate.setText("Yesterday");
//                    }
//                    else {
//                        holder.binding.cardTaskDate.setText(date);
//                    }
//
//
//                }
//                else
//                {
//                    holder.binding.cardTaskMessage.setText("Tap to Chat");
//                    holder.binding.cardTaskDate.setText("");
//                }
//                FirebaseDatabase.getInstance().getReference().child("presence").child(user.getUser_id()).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                        String status = snapshot.getValue(String.class);
//
//                        if (status.startsWith("Online")||status.startsWith("Typing"))
//                        {
//                            holder.binding.cardOnlineIndicator.setVisibility(View.VISIBLE);
//
//                        }
//                        else
//                        {
//                            holder.binding.cardOnlineIndicator.setVisibility(View.GONE);
//
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        }); //



    }

    @Override
    public int getItemCount() {
        return collabtaskuserlist.size();
    }


    public class TaskListViewHolder extends RecyclerView.ViewHolder{

        TaskCollabCardDesignBinding binding;
        public TaskListViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = TaskCollabCardDesignBinding.bind(itemView);
        }
    }
}
