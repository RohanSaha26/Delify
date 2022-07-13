package com.sahaprojects.drivechat.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.FirebaseDatabase;
import com.sahaprojects.drivechat.R;
import com.sahaprojects.drivechat.databinding.ActivityProfileInfoBinding;

public class ProfileInfo extends AppCompatActivity {

    String user_gmailstr;
    String user_namestr;
    String user_photostr;
    String user_id;
    ActivityProfileInfoBinding binding;
    GoogleSignInAccount currentuser;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        currentuser = GoogleSignIn.getLastSignedInAccount(this);
        getvalue();
        buttons();
    }

    private void getvalue() {
         user_gmailstr = getIntent().getExtras().getString("user_gmail");
        binding.userGmail.setText(user_gmailstr);

         user_namestr = getIntent().getExtras().getString("user_name");
        binding.userName.setText(user_namestr);

         user_photostr = getIntent().getExtras().getString("user_photo");
        Glide.with(this).load(user_photostr).placeholder(R.drawable.profiledp)
                .into(binding.profiledp);

        user_id = getIntent().getExtras().getString("user_id");
    }
    private void buttons() {
        binding.chats.setOnClickListener((View v) -> {
            Intent intent1 = new Intent(ProfileInfo.this, Chats.class);
            startActivity(intent1);
            overridePendingTransition(R.anim.no_anim,R.anim.no_anim);
            finish();
        });


        binding.profile.setOnClickListener((View v) -> {
            Intent intent3 = new Intent(ProfileInfo.this, Profile.class);
            startActivity(intent3);
            overridePendingTransition(R.anim.no_anim,R.anim.no_anim);
            finish();
        });

        binding.qricon.setOnClickListener((View v) -> {
            Intent intent4 = new Intent(ProfileInfo.this, QrCode.class);
            startActivity(intent4);
            overridePendingTransition(R.anim.no_anim,R.anim.no_anim);
            finish();
        });

        binding.task.setOnClickListener(v ->
        {
            Intent intent4 = new Intent(ProfileInfo.this, TaskToDo.class);
            startActivity(intent4);
            overridePendingTransition(R.anim.no_anim,R.anim.no_anim);
            finish();
        });

        binding.messagebtn.setOnClickListener((View v) -> {
            Intent reciever_chats = new Intent(ProfileInfo.this, InboxChat.class);
            reciever_chats.putExtra("reciever_gmail",user_gmailstr);
            reciever_chats.putExtra("reciever_name",user_namestr);
            reciever_chats.putExtra("reciever_photo",user_photostr);
            reciever_chats.putExtra("reciever_id",user_id);
            startActivity(reciever_chats);
            overridePendingTransition(R.anim.no_anim,R.anim.no_anim);
            finish();
        });

        binding.commontasks.setOnClickListener((View v) -> {
            Intent collabtasks = new Intent(ProfileInfo.this, InboxTask.class);
            collabtasks.putExtra("reciever_gmail",user_gmailstr);
            collabtasks.putExtra("reciever_name",user_namestr);
            collabtasks.putExtra("reciever_photo",user_photostr);
            collabtasks.putExtra("reciever_id",user_id);
            startActivity(collabtasks);
            overridePendingTransition(R.anim.no_anim,R.anim.no_anim);
            finish();
        });

        binding.back.setOnClickListener((View v) -> {
//            Intent intent6 = new Intent(ProfileInfo.this, StartAChat.class);
//            startActivity(intent6);
//            overridePendingTransition(R.anim.no_anim,R.anim.no_anim);
            finish();
        });
        binding.sendgmailbtn.setOnClickListener((View v) -> {
            try {
                sendgmail();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

        });
    }
    private void sendgmail() throws PackageManager.NameNotFoundException {
        TextView user_gmail;
        user_gmail = findViewById(R.id.user_gmail);
        String receivergmailID = user_gmail.getText().toString();
        String[] receivergmailid = receivergmailID.split(",");
//        String extramsg = "Through Drive Chat App. Download this app: ";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, receivergmailid);
//        intent.putExtra(Intent.EXTRA_TEXT,extramsg);
        intent.setType("message/rfc822");

        PackageManager packageManager =getPackageManager();
        try {
            packageManager.getPackageInfo("com.google.android.gm",PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
//        startActivity(Intent.createChooser(intent, "Choose your Mail App"));
        startActivity(intent);
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Intent intent6 = new Intent(ProfileInfo.this, StartAChat.class);
//        startActivity(intent6);
//        overridePendingTransition(R.anim.no_anim,R.anim.no_anim);
        finish();
    }
}