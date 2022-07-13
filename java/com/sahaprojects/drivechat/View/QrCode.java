package com.sahaprojects.drivechat.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.sahaprojects.drivechat.R;
import com.sahaprojects.drivechat.databinding.ActivityQrcodeBinding;

import java.io.File;
import java.io.FileOutputStream;

public class QrCode extends AppCompatActivity {
    GoogleSignInAccount currentuser;
    FirebaseDatabase database;
    ActivityQrcodeBinding binding;
    Bitmap bitmap;
    private BitmapDrawable drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrcodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        currentuser = GoogleSignIn.getLastSignedInAccount(this);
        buttons();
    }

    @Override
    protected void onResume() {
        super.onResume();
        database.getReference().child("presence").child(currentuser.getId()).setValue("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        database.getReference().child("presence").child(currentuser.getId()).setValue("Offline");

    }



    private void buttons() {

        binding.chats.setOnClickListener((View v) -> {
            Intent intent1 = new Intent(QrCode.this, Chats.class);
            startActivity(intent1);
            overridePendingTransition(R.anim.no_anim,R.anim.no_anim);
            finish();
        });

        binding.profile.setOnClickListener((View v) -> {
            Intent intent3 = new Intent(QrCode.this, Profile.class);
            startActivity(intent3);
            overridePendingTransition(R.anim.no_anim,R.anim.no_anim);
            finish();
        });

//        binding.qricon.setOnClickListener((View v) -> {
//            Intent intent4 = new Intent(QrCode.this, QrCode.class);
//            startActivity(intent4);
//            overridePendingTransition(R.anim.no_anim,R.anim.no_anim);
//            finish();
//        });


        binding.task.setOnClickListener(v ->
        {
            Intent intent4 = new Intent(QrCode.this, TaskToDo.class);
            startActivity(intent4);
            overridePendingTransition(R.anim.no_anim,R.anim.no_anim);
            finish();
        });

        binding.qrcreatebtn.setOnClickListener(v -> {


            closeKeyboard();
            String qrtext = binding.qrinput.getText().toString().trim();
            if (qrtext.isEmpty())
            {
                binding.qrinput.setError("Enter something first..");
            }
            else
            {
                MultiFormatWriter writer = new MultiFormatWriter();
                try {
                    BitMatrix matrix = writer.encode(qrtext, BarcodeFormat.QR_CODE,400,400);
                    BarcodeEncoder encoder = new BarcodeEncoder();
                     bitmap = encoder.createBitmap(matrix);
                    binding.qrcodeimage.setVisibility(View.VISIBLE);
                    binding.qrshare.setVisibility(View.VISIBLE);
                    binding.view.setVisibility(View.VISIBLE);
                    binding.qrcodeimage.setImageBitmap(bitmap);
                    System.out.println("BITMAP: "+bitmap);

                } catch (WriterException e) {
                    e.printStackTrace();
                }

            }

        });


        binding.qrshare.setOnClickListener(v -> {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            drawable = (BitmapDrawable) binding.qrcodeimage.getDrawable();
            bitmap = drawable.getBitmap();
            File file = new File(getExternalCacheDir()+"QR.jpg");
            try {
                FileOutputStream outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
                outputStream.flush();
                outputStream.close();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(file));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(Intent.createChooser(intent,"Share code via.."));
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        binding.qrscan.setOnClickListener((View v) -> {
            startActivity(new Intent(getApplicationContext(), Capture.class));

        });


    }


    private void closeKeyboard()
    {
        View view = this.getCurrentFocus();

        if (view != null) {
            InputMethodManager manager
                    = (InputMethodManager)
                    getSystemService(
                            Context.INPUT_METHOD_SERVICE);
            manager
                    .hideSoftInputFromWindow(
                            view.getWindowToken(), 0);
        }
    }

}