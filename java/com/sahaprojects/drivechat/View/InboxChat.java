package com.sahaprojects.drivechat.View;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sahaprojects.drivechat.HandleDrive.DriveServiceHelper;
import com.sahaprojects.drivechat.Adapter.MessageAdapter;
import com.sahaprojects.drivechat.Models.Message;
import com.sahaprojects.drivechat.R;
import com.sahaprojects.drivechat.databinding.ActivityInboxChatBinding;
import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class InboxChat extends AppCompatActivity {

    String recieverPhotostr;
    String recieverGmailstr;
    String recieverNamestr;
    String recieverId;
    String On,Off;
    ActivityInboxChatBinding binding;
    GoogleSignInAccount currentuser;
    FirebaseDatabase database;

    final String[] readReceipt = new String[1];

    String isChat;

    MessageAdapter adapter;
    ArrayList<Message> messages;
    String senderRoom,receiverRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInboxChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        On="Online";
        Off="Offline";
        database = FirebaseDatabase.getInstance();
        currentuser = GoogleSignIn.getLastSignedInAccount(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE); //block screenshots
        findViewById(R.id.back).setOnClickListener((View v) -> {                                    //"back" button on click listener
            overridePendingTransition(R.anim.no_anim,R.anim.no_anim);
            finish();
        });


        {
            recieverPhotostr = getIntent().getExtras().getString("reciever_photo");               //get value from another intent
            recieverGmailstr = getIntent().getExtras().getString("reciever_gmail");
            recieverNamestr = getIntent().getExtras().getString("reciever_name");
            recieverId = getIntent().getExtras().getString("reciever_id");
        }
        {
            binding.receiverGmail.setText(recieverGmailstr);                                            //set receiver data on topbar
            binding.receiverName.setText(recieverNamestr);
            Glide.with(this).load(recieverPhotostr).placeholder(R.drawable.profiledp)
                    .into(binding.receiverPhoto);
        }
        binding.receiverPhoto.setOnClickListener((View v) ->{                                       //To Profile Activity when click in Receiver Photo
            Intent recieverProfile = new Intent(InboxChat.this,ProfileInfo.class);

            {
                recieverProfile.putExtra("user_gmail" , recieverGmailstr);                       //pass value to another intent
                recieverProfile.putExtra("user_name" , recieverNamestr);
                recieverProfile.putExtra("user_photo" , recieverPhotostr);
                recieverProfile.putExtra("user_id" , recieverId);
            }
            startActivity(recieverProfile);
        });
        binding.gototasks.setOnClickListener((View v) ->{

            Intent collabtasks = new Intent(InboxChat.this, InboxTask.class);
            collabtasks.putExtra("reciever_gmail",recieverGmailstr);
            collabtasks.putExtra("reciever_name",recieverNamestr);
            collabtasks.putExtra("reciever_photo",recieverPhotostr);
            collabtasks.putExtra("reciever_id",recieverId);
            startActivity(collabtasks);
            overridePendingTransition(R.anim.no_anim,R.anim.no_anim);
            finish();
        });


        database.getReference().child("presence").child(recieverId).addValueEventListener(new ValueEventListener() {
            @Override                                                                               //chceking presence of another user and set indicator
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                {
                    String status = snapshot.getValue(String.class);

                    assert status != null;
                    if (status.equals("Typingto"+currentuser.getId()))                              //is Typing with me?
                    {
                        System.out.println("STATUS : " + status);
                        binding.chatInboxOnlineIndicator.setVisibility(View.VISIBLE);
                        binding.typingindicator.setVisibility(View.VISIBLE);
                    }
                    else if (status.startsWith("Online")||status.startsWith("Typing"))                  // is Online?
                    {
                        System.out.println("STATUS2 : " + status);

                        binding.chatInboxOnlineIndicator.setVisibility(View.VISIBLE);
                        binding.typingindicator.setVisibility(View.GONE);

                    }
                    else if (status.equals("Offline"))                                              // is Offline?
                    {
                        binding.chatInboxOnlineIndicator.setVisibility(View.GONE);
                        binding.typingindicator.setVisibility(View.GONE);


                    }
                    else                                                                            //else part (Offline)
                    {
                        binding.chatInboxOnlineIndicator.setVisibility(View.GONE);
                        binding.typingindicator.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        {

            Handler typinghandler = new Handler();
            binding.inboxeditmsg.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                                                                                                    // currentuser Typing to value set
                    database.getReference().child("presence").child(currentuser.getId()).setValue("Typingto"+recieverId);
                    typinghandler.removeCallbacksAndMessages(null);
                    typinghandler.postDelayed(stoptyping,500);

                }

                Runnable stoptyping =new Runnable() {                                               //Runnable for handle typing
                    @Override
                    public void run() {
                        database.getReference().child("presence").child(currentuser.getId()).setValue("Online"+recieverId);
                    }
                };
            });

        }                                                                                           //Typing Indicator Set.


        binding.ocrbtn.setOnClickListener(v -> {                                              //click on + button

            Intent intent = new Intent(InboxChat.this,OCR.class);
            startActivity(intent);
        });




        ShimmerFrameLayout shimmerFrameLayout = binding.inboxshimmer;                               //shimmer effect
        shimmerFrameLayout.startShimmer();                                                          //shimmer effect start





        messages = new ArrayList<>();
        adapter = new MessageAdapter(this,messages);
        Date date = new Date();
        String senderId=currentuser.getId();
        senderRoom = senderId + recieverId;
        receiverRoom = recieverId + senderId;

        isChat = "no";

        //Set Message Adapter for chatting in between currentuser and receiver.
        binding.inboxrecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.inboxrecyclerView.setAdapter(adapter);

        new Handler().postDelayed(() -> {
            shimmerFrameLayout.stopShimmer();                                               //shimmer effect stop
            shimmerFrameLayout.setVisibility(View.GONE);                                    //shimmer effect invisible
        },500);


        database.getReference().child("chats").child(senderRoom).child("messages").addValueEventListener(new ValueEventListener() {
            @Override                                                                               //Fetch chat data from Database
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                messages.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                {


                    Message NonDecryptedMessageObj = dataSnapshot.getValue(Message.class);          //get whole class of message(encrypted) from database
                    long DecryptedMessageTime = NonDecryptedMessageObj.getTimestamp();              //get time
                    String NonDecryptedMessageTxt = NonDecryptedMessageObj.getMessage();            //get AES encrypted message
                    String DecryptedMessageSId = NonDecryptedMessageObj.getSenderId();              //get SenderId
                    String DecryptedMessageRId = NonDecryptedMessageObj.getReceiverId();            //get ReceiverId
                    String DecryptedMessageRoom = NonDecryptedMessageObj.getSenderRoom();           //get SenderRoom

                    String ReadReceipt = NonDecryptedMessageObj.getReadReceipt();
                    String DecryptedMessageTxt = null;
                    try {
                        DecryptedMessageTxt = AESCrypt.decrypt(DecryptedMessageSId,NonDecryptedMessageTxt);         //AES Decrypted message
                        System.out.println("DECRYPTED TEST:  "+ NonDecryptedMessageObj.getMessage());
                                                                                                    //Set whole class of message n(decrypted) from Database
                        Message DecryptedMessageObj =
                                new Message(DecryptedMessageTxt,DecryptedMessageSId,DecryptedMessageRId,DecryptedMessageRoom,DecryptedMessageTime,ReadReceipt);
                        messages.add(DecryptedMessageObj);                                          //add to the arraylist which is set with the adapter

                        System.out.println("Decrypted Chats:"+ DecryptedMessageTxt);
                        System.out.println("nonDecrypted Chats:"+ NonDecryptedMessageTxt);

                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                                                                                                    //set recycler view to bottom to see last message in screen without scrolling
                    binding.inboxrecyclerView.smoothScrollToPosition(binding.inboxrecyclerView.getAdapter().getItemCount());


                    shimmerFrameLayout.stopShimmer();                                               //shimmer effect stop
                    shimmerFrameLayout.setVisibility(View.GONE);                                    //shimmer effect invisible


                    if (binding.inboxrecyclerView.getAdapter().getItemCount()!=0)
                    {
                        binding.nochats.setVisibility(View.GONE);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });







        binding.sentbtn.setOnClickListener(v -> {                                                   //click "send" button
            String nonEncryptedMessageTxt = binding.inboxeditmsg.getText().toString();              //get the text from user


            readReceipt[0]="sent";


            if (nonEncryptedMessageTxt.isEmpty())
            {
                binding.inboxeditmsg.setError("Enter a message first..");
            }
            else
            {
                binding.inboxeditmsg.setText("");                                                       //set inbox edittext box empty for next chat




                String SenderEncryptedMessageTxt = null;
                try {
                    SenderEncryptedMessageTxt = AESCrypt.encrypt(senderId,nonEncryptedMessageTxt);      //AES Encrypted
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }


                Date cdate = new Date();


                Message SenderEncryptedMessageObj = new Message(SenderEncryptedMessageTxt,senderId,recieverId,senderRoom,cdate.getTime(), readReceipt[0]); //Set message class for upload to database

                database.getReference().child("chats").child(senderRoom).child("lastmessage").setValue(SenderEncryptedMessageObj); //upload last message to database
                database.getReference().child("chats").child(receiverRoom).child("lastmessage").setValue(SenderEncryptedMessageObj);
                //upload encrypted message to database
                database.getReference().child("chats").child(senderRoom).child("messages").child(String.valueOf(cdate.getTime())).setValue(SenderEncryptedMessageObj);
                database.getReference().child("chats").child(receiverRoom).child("messages").child(String.valueOf(cdate.getTime())).setValue(SenderEncryptedMessageObj);

                database.getReference().child("chatlist").child(senderId).child(recieverId).setValue("yes");
                database.getReference().child("chatlist").child(recieverId).child(senderId).setValue("yes");

            }

        });



    }



    @Override                                                                                       //set currentuser online
    protected void onResume() {
        super.onResume();
        database.getReference().child("presence").child(currentuser.getId()).setValue(On+recieverId);
    }

    @Override                                                                                       //set currentuser offline
    protected void onPause() {
        super.onPause();
        database.getReference().child("presence").child(currentuser.getId()).setValue(Off);

    }

}

