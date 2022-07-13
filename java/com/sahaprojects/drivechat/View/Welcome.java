package com.sahaprojects.drivechat.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.services.drive.DriveScopes;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.sahaprojects.drivechat.R;
import com.sahaprojects.drivechat.databinding.ActivityWelcomeBinding;

public class Welcome extends AppCompatActivity {
    private static final int RC_SIGN_IN = 26;
    GoogleSignInClient mGoogleSignInClient;
     FirebaseAuth mAuth;
     ActivityWelcomeBinding binding;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user!= null)
        {
            Intent intent = new Intent(getApplicationContext(), Chats.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        createGoogleSignInRequest();
        buttons();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from mGoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            System.out.println("INTENT DATA: "+ data );
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                assert account != null;
                firebaseAuthWithGoogle(account);
            } catch (ApiException ignored) {
            }
        }
    }
    private void createGoogleSignInRequest() {
                                                                                                    // Configure Google Sign In
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        binding.progressbar.setVisibility(LottieAnimationView.VISIBLE);
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user!= null)
                        {
                            Intent intent = new Intent(getApplicationContext(), Chats.class);
                            startActivity(intent);
                            binding.progressbar.setVisibility(LottieAnimationView.GONE);
                        }
                    }else {
                        Toast.makeText(Welcome.this,"Authentication Failed... ",Toast.LENGTH_SHORT).show();
                    }

                });
    }
    private void buttons() {
        binding.chooseaccountbtn.setOnClickListener((View v) ->{
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();                            //signIn
            startActivityForResult(signInIntent, RC_SIGN_IN);
            System.out.println("INTENT DATA1: "+ signInIntent );


        });
        binding.infobtn.setOnClickListener((View v) ->{
                Intent infoIntent = new Intent(Intent.ACTION_VIEW);
                String infolink= getString(R.string.infolink);
                infoIntent.setData(Uri.parse(infolink));
                startActivity(infoIntent);
        });
    }


}