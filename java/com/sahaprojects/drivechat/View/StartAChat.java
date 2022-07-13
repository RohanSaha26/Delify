package com.sahaprojects.drivechat.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.sahaprojects.drivechat.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sahaprojects.drivechat.databinding.ActivitySplashScreenBinding;
import com.sahaprojects.drivechat.databinding.ActivityStartAChatBinding;
import com.sahaprojects.drivechat.databinding.ActivityStartAChatBindingImpl;

public class StartAChat extends AppCompatActivity {

    String SEARCHUSERGMAIL;                                                                         //define variable
    String SEARCHUSERPHOTO;
    String SEARCHUSERNAME;
    String SEARCHUSERID;
    String gmailstr;
    String onlygmailstr;
    private FirebaseDatabase database;
    GoogleSignInAccount currentuser;
    ActivityStartAChatBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {                                            //oncreate
        super.onCreate(savedInstanceState);
        binding = ActivityStartAChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        currentuser = GoogleSignIn.getLastSignedInAccount(this);
        database = FirebaseDatabase.getInstance();
        String currentusermail = currentuser.getEmail();
        String currentusername = currentuser.getDisplayName();
        ImageView gobtn = findViewById(R.id.gobtn);                                                 //"gobtn" button on click listener
        gobtn.setOnClickListener((View v) -> {

            binding.progressbar.setVisibility(LottieAnimationView.VISIBLE);

                                               //input from user in enter gmail

            onlygmailstr = binding.gmailinputspace.getText().toString();
            if (onlygmailstr.isEmpty())
            {
                binding.gmailinputspace.setError("Please enter a gmail");
                findViewById(R.id.progressbar).setVisibility(LottieAnimationView.GONE);

            }
            else
            {
                if(onlygmailstr.contains("@gmail.com"))                                             //check if user enter with gmail
                {                                                                                   //by default no @gmail.com in the end of onlygmailstr
                    onlygmailstr=onlygmailstr.replace("@gmail.com","");
                    onlygmailstr=onlygmailstr.replace(" ","");
                    if (onlygmailstr.endsWith("."))
                    {
                        onlygmailstr=onlygmailstr.replace(".","");

                    }
                }
                gmailstr = onlygmailstr + "@gmail.com";                                             //merge with @gmail.com

                Query searchquery = FirebaseDatabase.getInstance().getReference("users").orderByChild("user_email").equalTo(gmailstr);
                searchquery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {                              //data snapshot(download required query data from out firebase db in our app)
                        for (DataSnapshot data:dataSnapshot.getChildren()) {
                            SEARCHUSERNAME = data.child("user_name").getValue(String.class);
                            SEARCHUSERPHOTO = data.child("user_photo").getValue(String.class);
                            SEARCHUSERGMAIL = data.child("user_email").getValue(String.class);
                            SEARCHUSERID = data.child("user_id").getValue(String.class);

                            System.out.println("USER ID: " +SEARCHUSERID);
                            System.out.println("USER ID: " +currentuser.getId());
                        }


                        if (SEARCHUSERID==null)
                        {

//                            Toast.makeText(getApplicationContext(),"Sorry. We didn't found any data of user: \n"+ gmailstr, Toast.LENGTH_SHORT).show();
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup)findViewById(R.id.custom_toast_body));
                            TextView toasttext = layout.findViewById(R.id.custom_toast_text);
                            toasttext.setText(gmailstr+" isn't available on "+getString(R.string.app_name)+"\n You can invite via gmail.");
                            final Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.CENTER_VERTICAL, 0,0);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();
                            binding.inviteviagmail.setVisibility(View.VISIBLE);
                            binding.progressbar.setVisibility(LottieAnimationView.GONE);
                        }
                       else if(!SEARCHUSERID.equals(currentuser.getId()))          //check if user exixts or not & not his own account.
                        {
                            binding.inviteviagmail.setVisibility(View.GONE);

                            Intent intent = new Intent(StartAChat.this, ProfileInfo.class);     //PASSVALUE
                            overridePendingTransition(R.anim.no_anim,R.anim.no_anim);
                            intent.putExtra("user_gmail" , SEARCHUSERGMAIL);
                            intent.putExtra("user_name" , SEARCHUSERNAME);
                            intent.putExtra("user_photo" , SEARCHUSERPHOTO);
                            intent.putExtra("user_id" , SEARCHUSERID);
                            startActivity(intent);
                            binding.gmailinputspace.setText("");
                            SEARCHUSERID = null;
                            SEARCHUSERPHOTO = null;
                            SEARCHUSERGMAIL = null;
                            SEARCHUSERNAME = null;
                            binding.progressbar.setVisibility(LottieAnimationView.GONE);
                        }
                        else if (SEARCHUSERID.equals(currentuser.getId()))                          // If User search his own account.
                        {
                            binding.inviteviagmail.setVisibility(View.GONE);

                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup)findViewById(R.id.custom_toast_body));
                            TextView toasttext = layout.findViewById(R.id.custom_toast_text);
                            toasttext.setText(gmailstr + "\n is your account. So you redirect to your Profile.");
                            final Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.CENTER_VERTICAL, 0,0);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();
                            binding.progressbar.setVisibility(LottieAnimationView.GONE);
                            Intent intent1 = new Intent(StartAChat.this, Profile.class);
                            startActivity(intent1);
                            finish();
                            binding.gmailinputspace.setText("");
                            SEARCHUSERID = null;
                            SEARCHUSERPHOTO = null;
                            SEARCHUSERGMAIL = null;
                            SEARCHUSERNAME = null;
                        }
                        else
                        {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

//            Toast.makeText(getApplicationContext(),"Sorry !! No Data Found...", Toast.LENGTH_SHORT).show();
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup)findViewById(R.id.custom_toast_body));
                        TextView toasttext = layout.findViewById(R.id.custom_toast_text);
                        toasttext.setText("Sorry !! No Data Found...");
                        final Toast toast = new Toast(getApplicationContext());
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0,0);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();
                    }
                });
                binding.gmailinputspace.onEditorAction(EditorInfo.IME_ACTION_DONE);

            }

        });

        binding.back.setOnClickListener((View v) -> {                                    //"back" button on click listener
            overridePendingTransition(R.anim.no_anim,R.anim.no_anim);
            finish();
        });

        binding.inviteviagmail.setOnClickListener((View v) ->{



            onlygmailstr = binding.gmailinputspace.getText().toString();
            if (onlygmailstr.isEmpty())
            {
                binding.gmailinputspace.setError("Please enter a gmail");
            }
            else
            {
                if(onlygmailstr.endsWith("@gmail.com"))                                             //check if user enter with gmail
                {                                                                                   //by default no @gmail.com in the end of onlygmailstr
                    String[]  ONLYGMAILSTR = onlygmailstr.split("@gmail.com");
                    onlygmailstr=ONLYGMAILSTR[0];
                }
                gmailstr = onlygmailstr + "@gmail.com";                                             //merge with @gmail.com
                String extramsg = "Download " + getString(R.string.app_name)  +  " App: " + getString(R.string.download_link) + " -------  Invited by " + currentusername +  ". Gmail: " + currentusermail + ".";
                String extrasub = "Invitation of " + getString(R.string.app_name) +  " App. From " + currentusername + ".";
                String[] receivergmailid = gmailstr.split(",");
                Intent intent = new Intent(Intent.ACTION_SEND);                                     //Intent for sending an invite via mail.
                intent.putExtra(Intent.EXTRA_EMAIL, receivergmailid);
                intent.putExtra(Intent.EXTRA_TEXT,extramsg);
                intent.putExtra(Intent.EXTRA_SUBJECT, extrasub);
                intent.setType("message/rfc822");
                PackageManager packageManager =getPackageManager();
                try {
                    packageManager.getPackageInfo("com.google.android.gm",PackageManager.GET_ACTIVITIES);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
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