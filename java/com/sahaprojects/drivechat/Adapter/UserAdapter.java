package com.sahaprojects.drivechat.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.sahaprojects.drivechat.Models.Message;
import com.sahaprojects.drivechat.Models.User;
import com.sahaprojects.drivechat.R;
import com.sahaprojects.drivechat.View.InboxChat;
import com.sahaprojects.drivechat.View.InboxTask;
import com.sahaprojects.drivechat.View.ProfileInfo;
import com.sahaprojects.drivechat.databinding.CardChatDesignBinding;
import com.sahaprojects.drivechat.databinding.UsercardlayoutBinding;
import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.VIBRATOR_SERVICE;

public class UserAdapter extends  RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    Context context;
    ArrayList<User> users;
    private GoogleSignInAccount currentuser;

    public UserAdapter(Context context,ArrayList<User> users)
    {
        this.context = context;
        this.users = users;
    }
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_chat_design, parent ,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);

        currentuser = GoogleSignIn.getLastSignedInAccount(context);


        ArrayList<Message> messages = null;


        holder.binding.cardChatName.setText(user.getUser_name());

        Glide.with(context).load(user.getUser_photo()).placeholder(R.drawable.profiledp).into(holder.binding.profiledp);

        holder.binding.cardOnlineIndicator.setVisibility(View.GONE);
        holder.binding.cardTypingindicator.setVisibility(LottieAnimationView.GONE);                 //default set


       String senderRoom = currentuser.getId()+user.getUser_id();

        FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).child("lastmessage").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {

                if (snapshot.exists())
                {
                    Message NonDecryptedMessageObj = snapshot.getValue(Message.class);              //get whole class of message(encrypted) from database
                    long DecryptedMessageTime = NonDecryptedMessageObj.getTimestamp();              //get time
                    String NonDecryptedMessageTxt = NonDecryptedMessageObj.getMessage();            //get AES encrypted message
                    String DecryptedMessageSId = NonDecryptedMessageObj.getSenderId();

                    String DecryptedMessageTxt = null;
                    try {
                        DecryptedMessageTxt = AESCrypt.decrypt(DecryptedMessageSId,NonDecryptedMessageTxt);         //AES Decrypted message
                        System.out.println("DECRYPTED TEST:  "+ NonDecryptedMessageObj.getMessage());

                        if (DecryptedMessageSId.equals(currentuser.getId()))                        // If currentuser sent message.
                        {
                            holder.binding.cardChatMessages.setText("You: " +DecryptedMessageTxt);
                        }
                        else                                                                        //If receiver sent message.
                        {
                            holder.binding.cardChatMessages.setText(DecryptedMessageTxt);

                        }




                        SimpleDateFormat simpleTimeFormat =new SimpleDateFormat("hh:mm a", Locale.getDefault());
                        SimpleDateFormat simpleDateFormat =new SimpleDateFormat("dd MMM YY", Locale.getDefault());

                        String date = simpleDateFormat.format(DecryptedMessageTime);
                        String time = simpleTimeFormat.format(DecryptedMessageTime);

                        Date currentdt = new Date();

                        String currentdate = simpleDateFormat.format(currentdt.getTime());

                        Calendar yesterday = Calendar.getInstance();
                        yesterday.add(Calendar.DATE,-1);

                        String yesterdaydate = simpleDateFormat.format(yesterday.getTime());


                        if (currentdate.equals(date))                                               // today's message didn't showing the date
                        {
                            holder.binding.cardChatDate.setText(time);
                        }
                        else if (yesterdaydate.equals(date)){                                       // yesterday message showing "Ysterday"
                            holder.binding.cardChatDate.setText("Yesterday");
                        }
                        else
                        {
                            holder.binding.cardChatDate.setText(date);

                        }

                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }

                }
                else
                {
                    holder.binding.cardChatMessages.setText("Tap to Chat");
                    holder.binding.cardChatDate.setText("");

                }

            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });

        System.out.println("SENDER ROOM ID: "+ senderRoom);


        FirebaseDatabase.getInstance().getReference().child("presence").child(user.getUser_id()).addValueEventListener(new ValueEventListener() {
            @Override                                                                               //presence checking
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                {
                    String status = snapshot1.getValue(String.class);

                    if (status.equals("Typingto"+currentuser.getId()))
                    {
                        System.out.println("STATUS : " + status);
                        holder.binding.cardOnlineIndicator.setVisibility(View.VISIBLE);
                        holder.binding.cardTypingindicator.setVisibility(LottieAnimationView.VISIBLE);
                        holder.binding.cardChatMessages.setVisibility(View.GONE);
                    }
                    else if (status.startsWith("Online")||status.startsWith("Typing"))
                    {

                        holder.binding.cardOnlineIndicator.setVisibility(View.VISIBLE);
                        holder.binding.cardTypingindicator.setVisibility(LottieAnimationView.GONE);
                        holder.binding.cardChatMessages.setVisibility(View.VISIBLE);


                    }
                    else if (status.equals("Offline"))
                    {
                        holder.binding.cardOnlineIndicator.setVisibility(View.GONE);
                        holder.binding.cardTypingindicator.setVisibility(LottieAnimationView.GONE);
                        holder.binding.cardChatMessages.setVisibility(View.VISIBLE);



                    }
                    else
                    {
                        holder.binding.cardOnlineIndicator.setVisibility(View.GONE);
                        holder.binding.cardTypingindicator.setVisibility(LottieAnimationView.GONE);
                        holder.binding.cardChatMessages.setVisibility(View.VISIBLE);

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {


                Vibrator vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);


                vibrator.vibrate(VibrationEffect.createOneShot(100,VibrationEffect.DEFAULT_AMPLITUDE));

                final DialogPlus carddialog = DialogPlus.newDialog(context)
                        .setGravity(Gravity.CENTER)
                        .setMargin(50,0,50,0)
                        .setContentHolder(new ViewHolder(R.layout.usercardlayout))
                        .setExpanded(false)
                        .create();
                carddialog.show();

                View holderView = (LinearLayout)carddialog.getHolderView();

                ImageView cardclose = holderView.findViewById(R.id.cardclose);
                ImageView cardviewprofile = holderView.findViewById(R.id.cardviewprofile);
                ImageView carddeletefromlist = holderView.findViewById(R.id.carddeletefromthislist);
                ImageView cardmessage = holderView.findViewById(R.id.cardmessage);
                ImageView cardcollabtasks = holderView.findViewById(R.id.cardcollabtasks);
                ImageView carddp = holderView.findViewById(R.id.carddp);

                TextView cardname = holderView.findViewById(R.id.cardname);
                TextView cardgmail = holderView.findViewById(R.id.cardgmail);

                cardgmail.setText(user.getUser_email());
                cardname.setText(user.getUser_name());
                Glide.with(context).load(user.getUser_photo()).placeholder(R.drawable.profiledp).into(carddp);

                cardclose.setOnClickListener(v1 -> {
                    carddialog.dismiss();
                });
                cardmessage.setOnClickListener(v1 -> {
                    Intent toinbox = new Intent(context, InboxChat.class);                                  //pass value to InboxChat
                    toinbox.putExtra("reciever_name",user.getUser_name());
                    toinbox.putExtra("reciever_id",user.getUser_id());
                    toinbox.putExtra("reciever_gmail",user.getUser_email());
                    toinbox.putExtra("reciever_photo",user.getUser_photo());
                    context.startActivity(toinbox);
                    carddialog.dismiss();
                });

                cardcollabtasks.setOnClickListener(v1 -> {
                    Intent totasks = new Intent(context, InboxTask.class);                                  //pass value to Collabtasks
                    totasks.putExtra("reciever_name",user.getUser_name());
                    totasks.putExtra("reciever_id",user.getUser_id());
                    totasks.putExtra("reciever_gmail",user.getUser_email());
                    totasks.putExtra("reciever_photo",user.getUser_photo());
                    context.startActivity(totasks);
                    carddialog.dismiss();
                });
                cardviewprofile.setOnClickListener(v1 -> {
                    Intent toprofile = new Intent(context, ProfileInfo.class);                                  //pass value to ProfileInfo
                    toprofile.putExtra("user_name",user.getUser_name());
                    toprofile.putExtra("user_id",user.getUser_id());
                    toprofile.putExtra("user_gmail",user.getUser_email());
                    toprofile.putExtra("user_photo",user.getUser_photo());
                    context.startActivity(toprofile);
                    carddialog.dismiss();
                });
                carddeletefromlist.setOnClickListener(v1 -> {
                    AlertDialog deletefromlist = new AlertDialog.Builder(context)
                            .setTitle("Remove "+ user.getUser_email()+" from your chatlist?")
                            .setMessage(user.getUser_email() +" will be deleted from this chat list. All of your chats will not be deleted.")
                            .setNegativeButton("No",null)
                            .setPositiveButton("REMOVE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseDatabase.getInstance().getReference().child("chatlist").child(currentuser.getId()).child(user.getUser_id()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(context,user.getUser_email() +" removed from this list.",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    carddialog.dismiss();

                                }

                            })
                            .setCancelable(false)
                            .create();

                    deletefromlist.show();
                    deletefromlist.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);

                });
                return false;
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Intent toinbox = new Intent(context, InboxChat.class);                                  //pass value to InboxChat
            toinbox.putExtra("reciever_name",user.getUser_name());
            toinbox.putExtra("reciever_id",user.getUser_id());
            toinbox.putExtra("reciever_gmail",user.getUser_email());
            toinbox.putExtra("reciever_photo",user.getUser_photo());
            context.startActivity(toinbox);
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{

        CardChatDesignBinding  binding;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CardChatDesignBinding.bind(itemView);
        }
    }
}
