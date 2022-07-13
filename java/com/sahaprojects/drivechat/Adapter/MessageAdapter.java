package com.sahaprojects.drivechat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.BaseMenuPresenter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sahaprojects.drivechat.Models.Message;
import com.sahaprojects.drivechat.R;
import com.sahaprojects.drivechat.databinding.InboxReceiverMsgBgBinding;
import com.sahaprojects.drivechat.databinding.InboxSenderMsgBgBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MessageAdapter extends RecyclerView.Adapter{

    Context context;
    ArrayList<Message> messages;

    final int ITEM_SENT =1;
    final int ITEM_RECEIVE =2;
     GoogleSignInAccount currentuser;


    SimpleDateFormat simpleTimeFormat =new SimpleDateFormat("hh:mm a", Locale.getDefault());
    SimpleDateFormat simpleDateFormat =new SimpleDateFormat("dd MMM YY", Locale.getDefault());
    Date currentdt = new Date();
    String currentdate = simpleDateFormat.format(currentdt.getTime());

    public MessageAdapter(Context context, ArrayList<Message> messages) {

        this.context = context;
        this.messages = messages;
        currentuser = GoogleSignIn.getLastSignedInAccount(context);


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        if(viewType==ITEM_SENT)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inbox_sender_msg_bg,parent,false);

            SentViewHolder sentViewHolder = new SentViewHolder(view);
            return sentViewHolder;
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inbox_receiver_msg_bg,parent,false);
            ReceiverViewHolder receiverViewHolder = new ReceiverViewHolder(view);
            return receiverViewHolder;
        }
    }

    @Override
    public int getItemViewType(int position) {

        Message message = messages.get(position);
        System.out.println("SENDER ID: "+message.getSenderId());
        System.out.println("Current ID: "+currentuser.getId());
        System.out.println("IF ID: "+currentuser.getId().equals(message.getSenderId()));
        if (currentuser.getId().equals(message.getSenderId()))
        {
            return ITEM_SENT;

        }
            return ITEM_RECEIVE;
    }

    @Override
    public void onBindViewHolder(@NonNull  RecyclerView.ViewHolder holder, int position) {

        Message message = messages.get(position);

        if (holder.getClass() == SentViewHolder.class)
        {
            SentViewHolder viewHolder = (SentViewHolder)holder;

//            System.out.println("S ADAPTER TEST: "+message.getMessage());
            viewHolder.binding.inboxsendertext.setText(message.getMessage());

            System.out.println("READ RECEIPT: "+message.getReadReceipt());
            if (message.getReadReceipt().equals("sent"))
            {
                viewHolder.binding.readreceipt.setImageResource(R.drawable.readreceiptsent);

            }
            else if (message.getReadReceipt().equals("delivered"))
            {
                viewHolder.binding.readreceipt.setImageResource(R.drawable.readreceiptdelivered);

            }
            else if (message.getReadReceipt().equals("seen"))
            {
                viewHolder.binding.readreceipt.setImageResource(R.drawable.readreceiptseen);

            }

            String date = simpleDateFormat.format(message.getTimestamp());
            String time = simpleTimeFormat.format(message.getTimestamp());



            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DATE,-1);

            String yesterdaydate = simpleDateFormat.format(yesterday.getTime());

            if (position!=0)
            {
                Message previousmessage = messages.get(position-1);
                String pdate = simpleDateFormat.format(previousmessage.getTimestamp());
                String ptime = simpleTimeFormat.format(previousmessage.getTimestamp());
                if (pdate.equals(date))
                {
                    viewHolder.binding.senderMsgDate.setVisibility(View.GONE);

                }
                else
                {
                    viewHolder.binding.senderMsgDate.setVisibility(View.VISIBLE);
                    viewHolder.binding.senderMsgTime.setVisibility(View.VISIBLE);

                }


            }

            if (currentdate.equals(date))                                               // today's message didn't showing the date
            {
                viewHolder.binding.senderMsgTime.setText(time);
                viewHolder.binding.senderMsgDate.setText("Today");
            }
            else if (yesterdaydate.equals(date)){                                       // yesterday message showing "Yesterday"
                viewHolder.binding.senderMsgTime.setText(time);
                viewHolder.binding.senderMsgDate.setText("Yesterday");
            }
            else
            {
                viewHolder.binding.senderMsgTime.setText(time);
                viewHolder.binding.senderMsgDate.setText(date);
            }
        }
        else
        {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder)holder;
//            System.out.println("R ADAPTER TEST: "+message.getMessage());
            viewHolder.binding.inboxreceivertext.setText(message.getMessage());

            String date = simpleDateFormat.format(message.getTimestamp());
            String time = simpleTimeFormat.format(message.getTimestamp());



            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DATE,-1);

            String yesterdaydate = simpleDateFormat.format(yesterday.getTime());

            if (position!=0)
            {
                Message previousmessage = messages.get(position-1);
                String pdate = simpleDateFormat.format(previousmessage.getTimestamp());
                String ptime = simpleTimeFormat.format(previousmessage.getTimestamp());
                if (pdate.equals(date))
                {
                    viewHolder.binding.receiverMsgDate.setVisibility(View.GONE);
                }
                else
                {
                    viewHolder.binding.receiverMsgDate.setVisibility(View.VISIBLE);
                    viewHolder.binding.receiverMsgTime.setVisibility(View.VISIBLE);

                }

            }

            if (currentdate.equals(date))                                               // today's message didn't showing the date
            {
                viewHolder.binding.receiverMsgTime.setText(time);
                viewHolder.binding.receiverMsgDate.setText("Today");

            }
            else if (yesterdaydate.equals(date)){                                       // yesterday message showing "Yesterday"
                viewHolder.binding.receiverMsgTime.setText(time);
                viewHolder.binding.receiverMsgDate.setText("Yesterday");
            }
            else
            {
                viewHolder.binding.receiverMsgTime.setText(time);
                viewHolder.binding.receiverMsgDate.setText(date);

            }

        }


        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child("chats").child(message.getSenderId()+message.getReceiverId()).exists())
                {
                    String presenceStatus = snapshot.child("presence").child(message.getReceiverId()).getValue(String.class);


                    if (message.getReadReceipt().equals("sent"))
                    {
                        if (presenceStatus.equals("Online"+currentuser.getId())||presenceStatus.equals("Typingto"+currentuser.getId()))
                        {
                            FirebaseDatabase.getInstance().getReference().child("chats")
                                    .child(message.getSenderId()+message.getReceiverId()).child("messages")
                                    .child(String.valueOf(message.getTimestamp())).child("readReceipt").setValue("seen");
                        }
                        else if (presenceStatus.startsWith("Online")||presenceStatus.startsWith("Typingto"))
                        {
                            FirebaseDatabase.getInstance().getReference().child("chats")
                                    .child(message.getSenderId()+message.getReceiverId()).child("messages")
                                    .child(String.valueOf(message.getTimestamp())).child("readReceipt").setValue("delivered");

                        }
//                        else if (presenceStatus.equals("Offline"))
//                        {
//                            FirebaseDatabase.getInstance().getReference().child("chats")
//                                    .child(message.getSenderId()+message.getReceiverId()).child("messages")
//                                    .child(String.valueOf(message.getTimestamp())).child("readReceipt").setValue("sent");
//
//                        }
                        else if (message.getReadReceipt().equals("delivered"))
                        {
                            if (presenceStatus.equals("Online"+currentuser.getId())||presenceStatus.equals("Typingto"+currentuser.getId()))
                            {
                                FirebaseDatabase.getInstance().getReference().child("chats")
                                        .child(message.getSenderId()+message.getReceiverId()).child("messages")
                                        .child(String.valueOf(message.getTimestamp())).child("readReceipt").setValue("seen");
                            }
//                            else if (presenceStatus.startsWith("Online")||presenceStatus.startsWith("Typingto"))
//                            {
//                                FirebaseDatabase.getInstance().getReference().child("chats")
//                                        .child(message.getSenderId()+message.getReceiverId()).child("messages")
//                                        .child(String.valueOf(message.getTimestamp())).child("readReceipt").setValue("delivered");
//
//                            }

                        }
                        else if (message.getReadReceipt().equals("seen"))
                        {
                                FirebaseDatabase.getInstance().getReference().child("chats")
                                        .child(message.getSenderId()+message.getReceiverId()).child("messages")
                                        .child(String.valueOf(message.getTimestamp())).child("readReceipt").setValue("seen");

                        }
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class SentViewHolder extends RecyclerView.ViewHolder{

        InboxSenderMsgBgBinding binding;

        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = InboxSenderMsgBgBinding.bind(itemView);
        }
    }

    public  class ReceiverViewHolder extends RecyclerView.ViewHolder{

        InboxReceiverMsgBgBinding binding;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = InboxReceiverMsgBgBinding.bind(itemView);
        }
    }
}
