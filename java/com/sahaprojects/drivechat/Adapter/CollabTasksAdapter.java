package com.sahaprojects.drivechat.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.sahaprojects.drivechat.Models.CollabTasks;
import com.sahaprojects.drivechat.Models.MyTask;
import com.sahaprojects.drivechat.R;
import com.sahaprojects.drivechat.databinding.TaskCollabBoxBgBinding;
import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CollabTasksAdapter extends RecyclerView.Adapter<CollabTasksAdapter.CollabTaskViewHolder> {


    Context context;
    ArrayList<CollabTasks> collabTaskArrayList;
    GoogleSignInAccount currentuser;
    String receiverId,receiverGmail,receiverPhoto,receiverName;

    SimpleDateFormat simpleTimeFormat =new SimpleDateFormat("hh:mm a", Locale.getDefault());
    SimpleDateFormat simpleDateFormat =new SimpleDateFormat("dd MMM YY", Locale.getDefault());
    public CollabTasksAdapter(Context context, ArrayList<CollabTasks> collabTaskArrayList) {

        this.collabTaskArrayList = collabTaskArrayList;
        this.context = context;
    }


    public CollabTasksAdapter(Context context, ArrayList<CollabTasks> collabTaskArrayList, String receiverId, String receiverGmail, String receiverPhoto, String receiverName) {
        this.context = context;
        this.collabTaskArrayList = collabTaskArrayList;
        this.receiverId = receiverId;
        this.receiverGmail = receiverGmail;
        this.receiverPhoto = receiverPhoto;
        this.receiverName = receiverName;
    }

    @NonNull
    @Override
    public CollabTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.task_collab_box_bg, parent,false);

        return new CollabTaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollabTaskViewHolder holder, int position) {


        CollabTasks collabTasks = collabTaskArrayList.get(position);

        currentuser = GoogleSignIn.getLastSignedInAccount(context);
        holder.binding.taskbody.setText(collabTasks.getCtaskBody());

        String date = simpleDateFormat.format(collabTasks.getCtaskTime());
        String time = simpleTimeFormat.format(collabTasks.getCtaskTime());

        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE,-1);
        String yesterdaydate = simpleDateFormat.format(yesterday.getTime());

        Date currentdt = new Date();
        String currentdate = simpleDateFormat.format(currentdt.getTime());

        if (currentdate.equals(date))                                               // today's message didn't showing the date
        {
            holder.binding.taskcreatedate.setText("Today at "+ time);
        }
        else if (yesterdaydate.equals(date)){                                       // yesterday message showing "Yesterday"

            holder.binding.taskcreatedate.setText("Yesterday at "+time);
        }
        else
        {
            holder.binding.taskcreatedate.setText(date +" , "+time);
        }

        Glide.with(context).load(currentuser.getPhotoUrl()).placeholder(R.drawable.profiledp)
                .into(holder.binding.completebysender);
        Glide.with(context).load(receiverPhoto).placeholder(R.drawable.profiledp)
                .into(holder.binding.completebyreceiver);






        if (currentuser.getId().equals(collabTasks.getCtaskSenderId())) {
            Glide.with(context).load(currentuser.getPhotoUrl()).placeholder(R.drawable.profiledp)
                    .into(holder.binding.taskcreaterdp);
        }
        else {
            Glide.with(context).load(receiverPhoto).placeholder(R.drawable.profiledp)
                    .into(holder.binding.taskcreaterdp);
        }


        if (collabTasks.getCtaskIsCompletebyReceiver().equals("no")&&collabTasks.getCtaskIsCompletebySender().equals("no")) {
            holder.binding.completebytext.setVisibility(View.GONE);
            holder.binding.editTask.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.binding.completebytext.setVisibility(View.VISIBLE);
            holder.binding.editTask.setVisibility(View.GONE);
        }

        if (collabTasks.getCtaskIsCompletebySender().equals("yes")){
            holder.binding.completetask.setImageResource(R.drawable.incompletebtn);
            holder.binding.completebysender.setVisibility(View.VISIBLE);
        }
        else {
            holder.binding.completetask.setImageResource(R.drawable.completebtn);
            holder.binding.completebysender.setVisibility(View.GONE);
        }

        if (collabTasks.getCtaskIsCompletebyReceiver().equals("yes")) {
            holder.binding.completebyreceiver.setVisibility(View.VISIBLE);
        }
        else {
            holder.binding.completebyreceiver.setVisibility(View.GONE);
        }


        holder.binding.completetask.setOnClickListener(v -> {
            if (collabTasks.getCtaskIsCompletebySender().equals("no"))
            {
                FirebaseDatabase.getInstance().getReference().child("collabtasks").child(currentuser.getId()+receiverId).child("tasks")
                        .child(String.valueOf(collabTasks.getCtaskTime()))
                        .child("ctaskIsCompletebySender").setValue("yes").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context,"Well done..Task is completed",Toast.LENGTH_SHORT).show();
                    }
                });
                FirebaseDatabase.getInstance().getReference().child("collabtasks").child(receiverId+currentuser.getId()).child("tasks")
                        .child(String.valueOf(collabTasks.getCtaskTime()))
                        .child("ctaskIsCompletebyReceiver").setValue("yes").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                    }
                });
            }
            else
            {
                FirebaseDatabase.getInstance().getReference().child("collabtasks").child(currentuser.getId()+receiverId).child("tasks")
                        .child(String.valueOf(collabTasks.getCtaskTime()))
                        .child("ctaskIsCompletebySender").setValue("no").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context,"Task is incomplete.",Toast.LENGTH_SHORT).show();
                    }
                });
                FirebaseDatabase.getInstance().getReference().child("collabtasks").child(receiverId+currentuser.getId()).child("tasks")
                        .child(String.valueOf(collabTasks.getCtaskTime()))
                        .child("ctaskIsCompletebyReceiver").setValue("no").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                    }
                });

            }
        });


        holder.binding.editTask.setOnClickListener(v -> {


            final DialogPlus dialog = DialogPlus.newDialog(context)
                    .setGravity(Gravity.CENTER)
                    .setMargin(25,0,25,0)
                    .setContentHolder(new ViewHolder(R.layout.task_update_prompt))
                    .setExpanded(false)
                    .create();

            View holderView = (LinearLayout)dialog.getHolderView();


            EditText editTask = holderView.findViewById(R.id.edit_task);
            ImageView editTaskGo = holderView.findViewById(R.id.edit_task_go);
            editTask.setText(collabTasks.getCtaskBody());
            editTaskGo.setOnClickListener(v1 -> {

                String etaskstext = null;
                try {
                    etaskstext = AESCrypt.encrypt(collabTasks.getCtaskSenderId()+"^%$#"+collabTasks.getCtaskTime(),editTask.getText().toString());
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
                if (editTask.getText().toString().equals(""))
                {
                    editTask.setError("Task field can't be empty..");
                }
                else
                {

                    FirebaseDatabase.getInstance().getReference().child("collabtasks").child(currentuser.getId()+receiverId).child("tasks")
                            .child(String.valueOf(collabTasks.getCtaskTime()))
                            .child("ctaskBody").setValue(etaskstext).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            dialog.dismiss();
                            Toast.makeText(context,"Task updated sucessfully..",Toast.LENGTH_LONG).show();

                        }
                    });
                    FirebaseDatabase.getInstance().getReference().child("collabtasks").child(receiverId+currentuser.getId()).child("tasks")
                            .child(String.valueOf(collabTasks.getCtaskTime()))
                            .child("ctaskBody").setValue(etaskstext).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            dialog.dismiss();

                        }
                    });


                }


            });
            dialog.show();
        });


        holder.binding.deletetask.setOnClickListener(v -> {



            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("Are you sure to delete this task?")
                    .setMessage("After you click on 'Delete for Me' , "+receiverGmail+" can see your task. If you click on 'Delete for Everyone' this task will be deleted from both of you.")
                    .setNeutralButton("Cancel",null)
                    .setNegativeButton("Delete for Everyone", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseDatabase.getInstance().getReference().child("collabtasks").child(currentuser.getId()+receiverId).child("tasks")
                                    .child(String.valueOf(collabTasks.getCtaskTime())).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(context,"Task deleted successfully..",Toast.LENGTH_SHORT).show();
                                }
                            });
                            FirebaseDatabase.getInstance().getReference().child("collabtasks").child(receiverId+currentuser.getId()).child("tasks")
                                    .child(String.valueOf(collabTasks.getCtaskTime())).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(context,"Task deleted successfully..",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    })
                    .setPositiveButton("Delete for Me", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseDatabase.getInstance().getReference().child("collabtasks").child(currentuser.getId()+receiverId).child("tasks")
                                    .child(String.valueOf(collabTasks.getCtaskTime())).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(context,"Task deleted successfully..",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    })
                    .setCancelable(true)
                    .create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);



        });

    }

    @Override
    public int getItemCount() {
        return collabTaskArrayList.size();
    }



    public class CollabTaskViewHolder extends RecyclerView.ViewHolder{

        TaskCollabBoxBgBinding binding;

        public CollabTaskViewHolder(@NonNull  View itemView) {
            super(itemView);
            binding = TaskCollabBoxBgBinding.bind(itemView);
        }
    }
}
