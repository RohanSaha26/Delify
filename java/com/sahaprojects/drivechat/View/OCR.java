package com.sahaprojects.drivechat.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.database.FirebaseDatabase;
import com.sahaprojects.drivechat.R;
import com.sahaprojects.drivechat.databinding.ActivityChatsBinding;
import com.sahaprojects.drivechat.databinding.ActivityOcrBinding;

import java.io.IOException;

public class OCR extends AppCompatActivity {

    ActivityOcrBinding binding;
    private CameraSource cameraSource;
    private static final int PERMISSION = 100;
    String cam="on";

    private FirebaseDatabase database;
    GoogleSignInAccount currentuser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOcrBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        currentuser = GoogleSignIn.getLastSignedInAccount(this);
        database = FirebaseDatabase.getInstance();

        startCameraSource();


        binding.ocrcopy.setOnClickListener(v -> {


            if (binding.ocrtext.getText().toString().equals(""))
            {
                Toast.makeText(getApplicationContext(),"No text found in this photo. Take photo again.",Toast.LENGTH_LONG).show();

            }
            else
            {
                ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Result",binding.ocrtext.getText().toString());
                clipboard.setPrimaryClip(clipData);
                Toast.makeText(getApplicationContext(),"Copied",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }




    private void startCameraSource()
    {

        final TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational())
        {
            System.out.println("Deependecies not loaeded for OCR");
        }
        else
        {
            cameraSource = new CameraSource.Builder(getApplicationContext(),textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1200,1024)
                    .setAutoFocusEnabled(true)
                    .setRequestedFps(2.0f)
                    .build();

            binding.ocrcamera.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(@NonNull SurfaceHolder holder) {
                    try {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
                        {
                            ActivityCompat.requestPermissions(OCR.this,new  String[]{Manifest.permission.CAMERA},PERMISSION);
                            return;
                        }
                        cameraSource.start(binding.ocrcamera.getHolder());
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

                    //
                }

                @Override
                public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

                    cameraSource.stop();
                }
            });

            binding.ocrcapture.setOnClickListener(v -> {

                if (cam.equals("on"))
                {
                    cameraSource.stop();
                    binding.ocrcapture.setImageResource(R.drawable.retakephoto);

                    cam = "off";
                }
                else
                {

                    try {
                        cameraSource.start(binding.ocrcamera.getHolder());
                        binding.ocrtext.setText("");
                        binding.ocrcapture.setImageResource(R.drawable.takephoto);
                        cam = "on";

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            });
            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(@NonNull  Detector.Detections<TextBlock> detections) {

                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if (items.size() != 0)
                    {
                        binding.ocrtext.post(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int i=0 ; i<items.size();i++)
                                {
                                    TextBlock item = items.valueAt(i);
                                    stringBuilder.append(item.getValue());
                                    stringBuilder.append("\n");
                                }
                                binding.ocrtext.setText(stringBuilder.toString());
                            }
                        });
                    }
                }
            });
        }
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