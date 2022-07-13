package com.sahaprojects.drivechat.View;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sahaprojects.drivechat.Models.User;
import com.sahaprojects.drivechat.R;
import com.sahaprojects.drivechat.databinding.ActivityProfileBinding;

public class Profile extends AppCompatActivity {

    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth firebaseAuth;
    GoogleSignInAccount signInAccount;
    String USERNAME;
    String USERPHOTO;
    String USEREMAIL;
    String USERID;
    String USERRAWPHOTO;
    ActivityProfileBinding binding;
    FirebaseDatabase database;
//    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        database = FirebaseDatabase.getInstance();
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//
//         googleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this, (GoogleApiClient.OnConnectionFailedListener) this)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();

        getAuthDataAndSetDataAndSetFirebaseDatabase();
        buttons();
    }

    private void getAuthDataAndSetDataAndSetFirebaseDatabase() {
        FirebaseDatabase rootnode;
        DatabaseReference reference;
        rootnode = FirebaseDatabase.getInstance();
        reference = rootnode.getReference("users");
        if(signInAccount!=null){

             USERNAME = signInAccount.getDisplayName();
             USEREMAIL = signInAccount.getEmail();
             USERRAWPHOTO = String.valueOf(signInAccount.getPhotoUrl());
             USERID = signInAccount.getId();
            assert USEREMAIL != null;
            System.out.println(USEREMAIL);
            System.out.println("AUTH CODE" + signInAccount.getServerAuthCode());
             USERPHOTO = USERRAWPHOTO.replace("s96-c","s960-c");

             binding.userName.setText(USERNAME + " (You)");
            binding.userGmail.setText(USEREMAIL);
            Glide.with(this).load(USERPHOTO).placeholder(R.drawable.profiledp)
                    .into(binding.profiledp);


//            binding.profiledp.setOnClickListener(v -> {
//                Intent link = new Intent();
//                link.setAction(Intent.ACTION_VIEW);
//                link.addCategory(Intent.CATEGORY_BROWSABLE);
//                link.setData(Uri.parse(getString(R.string.infolink)));
//                startActivity(link);
//            });

            User userdetails = new User(USERNAME,USERID,USEREMAIL,USERPHOTO);

            reference.child(USERID).setValue(userdetails);
        }
    }


    private void buttons() {

        binding.chats.setOnClickListener((View v) -> {
            Intent intent1 = new Intent(Profile.this, Chats.class);
            startActivity(intent1);
            overridePendingTransition(R.anim.no_anim,R.anim.no_anim);
            finish();
        });

//        binding.profile.setOnClickListener((View v) -> {
//            Intent intent3 = new Intent(Profile.this, Profile.class);
//            startActivity(intent3);
//            overridePendingTransition(R.anim.no_anim,R.anim.no_anim);
//            finish();
//        });

        binding.qricon.setOnClickListener((View v) -> {
            Intent intent4 = new Intent(Profile.this, QrCode.class);
            startActivity(intent4);
            overridePendingTransition(R.anim.no_anim,R.anim.no_anim);
            finish();
        });

        binding.task.setOnClickListener(v ->
        {
            Intent intent4 = new Intent(Profile.this, TaskToDo.class);
            startActivity(intent4);
            overridePendingTransition(R.anim.no_anim,R.anim.no_anim);
            finish();
        });

        binding.logout.setOnClickListener((View v) -> {
            AlertDialog dialog = new AlertDialog.Builder(Profile.this)
                    .setTitle("Do you want to LOGOUT?")
                    .setMessage("After logged out, If you want to login again please reopen the App")
                    .setNegativeButton("STAY WITH THIS ACCOUNT",null)
                    .setPositiveButton("LOGOUT", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            database.getReference().child("presence").child(signInAccount.getId()).setValue("Offline");
                            clearAppData();

                        }

                    })
                    .setCancelable(false)
                    .create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
        });
    }
    private void clearAppData() {
            try {
                // clearing app data
                Runtime.getRuntime().exec("pm clear "+getApplicationContext().getPackageName());

            } catch (Exception e) {
                e.printStackTrace();
        }
    }

//    private void signOutfromApp() {
//
//        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
//                new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(Status status) {
//                        if (status.isSuccess()){
//                            Intent gotowelcome = new Intent(Profile.this, Welcome.class);
//                            startActivity(gotowelcome);
//                            finish();
//                        }else{
//                            Toast.makeText(getApplicationContext(),"Session not close", Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
//    }



    @Override                                                                                       //set currentuser online
    protected void onResume() {
        super.onResume();
        database.getReference().child("presence").child(signInAccount.getId()).setValue("Online");
    }

    @Override                                                                                       //set currentuser offline
    protected void onPause() {
        super.onPause();
        database.getReference().child("presence").child(signInAccount.getId()).setValue("Offline");

    }
}