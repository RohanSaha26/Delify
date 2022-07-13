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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.sahaprojects.drivechat.Models.MyTask;
import com.sahaprojects.drivechat.R;
import com.sahaprojects.drivechat.databinding.TaskMyBoxBgBinding;
import com.sahaprojects.drivechat.databinding.TaskUpdatePromptBinding;
import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyTasksAdapter extends RecyclerView.Adapter<MyTasksAdapter.MyTaskViewHolder> {

    Context context;
    ArrayList<MyTask> myTaskArrayList;

    GoogleSignInAccount currentuser;

    SimpleDateFormat simpleTimeFormat =new SimpleDateFormat("hh:mm a", Locale.getDefault());
    SimpleDateFormat simpleDateFormat =new SimpleDateFormat("dd MMM YY", Locale.getDefault());
    Date currentdt = new Date();
    String currentdate = simpleDateFormat.format(currentdt.getTime());


    public MyTasksAdapter(Context context, ArrayList<MyTask> myTaskArrayList)
    {

        this.context = context;
        this.myTaskArrayList = myTaskArrayList;
    }

    @NonNull
    @Override
    public MyTaskViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.task_my_box_bg, parent,false);

        System.out.println("TASK BODY: a : " +view);


        return new MyTaskViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull  MyTasksAdapter.MyTaskViewHolder holder, int position) {

        MyTask myTask = myTaskArrayList.get(position);

         currentuser = GoogleSignIn.getLastSignedInAccount(context);
        holder.binding.taskbody.setText(myTask.getTaskBody());


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("mytasks").child(currentuser.getId());

        String date = simpleDateFormat.format(myTask.getTaskTime());
        String time = simpleTimeFormat.format(myTask.getTaskTime());

        String updateDate = simpleDateFormat.format(myTask.getTaskUpdateTime());
        String updateTime = simpleTimeFormat.format(myTask.getTaskUpdateTime());


        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE,-1);

        String yesterdaydate = simpleDateFormat.format(yesterday.getTime());



        if (currentdate.equals(updateDate))                                               // today's message didn't showing the date
        {
            holder.binding.taskupdatedate.setText("Today,\n"+updateTime);

        }
        else if (yesterdaydate.equals(updateDate)){                                       // yesterday message showing "Yesterday"

            holder.binding.taskupdatedate.setText("Yesterday,\n"+updateTime);

        }
        else
        {
            holder.binding.taskupdatedate.setText(""+updateDate+",\n"+updateTime);
        }


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


        if (myTask.getTaskIsComplete().equals("no"))
        {
            holder.binding.completeindicatororedittask.setImageResource(R.drawable.editablebtn);
            holder.binding.completetask.setImageResource(R.drawable.completebtn);
            holder.binding.taskupdatedate.setVisibility(View.GONE);


            holder.binding.completeindicatororedittask.setOnClickListener(v -> {

                final DialogPlus dialog = DialogPlus.newDialog(context)
                        .setGravity(Gravity.CENTER)
                        .setMargin(25,0,25,0)
                        .setContentHolder(new ViewHolder(R.layout.task_update_prompt))
                        .setExpanded(true)
                        .create();

                View holderView = (LinearLayout)dialog.getHolderView();


                EditText editTask = holderView.findViewById(R.id.edit_task);
                ImageView editTaskGo = holderView.findViewById(R.id.edit_task_go);
                editTask.setText(myTask.getTaskBody());
                editTaskGo.setOnClickListener(v1 -> {

                    String etaskstext = null;
                    try {
                        etaskstext = AESCrypt.encrypt(myTask.getTaskSenderId()+"#$%^"+myTask.getTaskTime(),editTask.getText().toString());
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                    if (editTask.getText().toString().equals(""))
                    {

                        AlertDialog editTaskDialog = new AlertDialog.Builder(context)
                                .setTitle("Your edited task is empty. Delete it?")
                                .setMessage("After you delete this task.This task will be removed from database.")
                                .setNegativeButton("No",null)
                                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface editTaskDialog, int which) {
                                        FirebaseDatabase.getInstance().getReference().child("mytasks").child(currentuser.getId()).child(String.valueOf(myTask.getTaskTime())).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                dialog.dismiss();
                                                Toast.makeText(context,"Task deleted successfully..",Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }

                                })
                                .setCancelable(true)
                                .create();
                        editTaskDialog.show();
                        editTaskDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
                    }
                    else
                    {

                        FirebaseDatabase.getInstance().getReference().child("mytasks").child(currentuser.getId()).child(String.valueOf(myTask.getTaskTime()))
                                .child("taskBody").setValue(etaskstext).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                dialog.dismiss();
                                Toast.makeText(context,"Task updated sucessfully..",Toast.LENGTH_LONG).show();


                            }
                        });

                    }


                });
                dialog.show();
            });



        }
        else if (myTask.getTaskIsComplete().equals("yes"))
        {
            holder.binding.completeindicatororedittask.setImageResource(R.drawable.tickimg);
            holder.binding.completetask.setImageResource(R.drawable.incompletebtn);
            holder.binding.taskupdatedate.setVisibility(View.VISIBLE);

            holder.binding.completeindicatororedittask.setOnClickListener(v -> {
                Toast.makeText(context,"You already completed this task.",Toast.LENGTH_SHORT).show();
            });

        }

        holder.binding.completetask.setOnClickListener(v -> {


            if (myTask.getTaskIsComplete().equals("no"))
            {

                FirebaseDatabase.getInstance().getReference().child("mytasks").child(currentuser.getId()).child(String.valueOf(myTask.getTaskTime()))
                        .child("taskIsComplete").setValue("yes").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context,"Well done..Task is completed",Toast.LENGTH_SHORT).show();
                    }
                });

                Date cdate = new Date();
                FirebaseDatabase.getInstance().getReference().child("mytasks").child(currentuser.getId()).child(String.valueOf(myTask.getTaskTime()))
                        .child("taskUpdateTime").setValue(cdate.getTime()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context,"Well done..Task is completed",Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else
            {


                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Are you sure to incomplete this task?")
                        .setMessage("Once you incomplete this task the completed time will be reset.")
                        .setNegativeButton("No",null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseDatabase.getInstance().getReference().child("mytasks").child(currentuser.getId()).child(String.valueOf(myTask.getTaskTime())).child("taskIsComplete").setValue("no").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context,"Task is not completed",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        })
                        .setCancelable(false)
                        .create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);



            }

        });
        holder.binding.deletetask.setOnClickListener(v -> {



            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("Are you sure to delete this task?")
                    .setMessage("After you delete this task.This task will be removed from database.")
                    .setNegativeButton("No",null)
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseDatabase.getInstance().getReference().child("mytasks").child(currentuser.getId()).child(String.valueOf(myTask.getTaskTime())).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
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



        });




    }

    @Override
    public int getItemCount() {
        return myTaskArrayList.size();
    }

    public class MyTaskViewHolder extends RecyclerView.ViewHolder{

          TaskMyBoxBgBinding binding;

          public MyTaskViewHolder(@NonNull  View itemView) {
              super(itemView);
              binding = TaskMyBoxBgBinding.bind(itemView);
          }
      }
}
