package com.sahaprojects.drivechat.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.sahaprojects.drivechat.R;
import com.sahaprojects.drivechat.databinding.ActivityCaptureBinding;

public class Capture extends AppCompatActivity {
    CodeScanner codeScanner;
    CodeScannerView scannView;
    TextView resultData;

    private FirebaseDatabase database;
    GoogleSignInAccount currentuser;

    ActivityCaptureBinding binding;
    String strresultData;
    Button qrscango;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        currentuser = GoogleSignIn.getLastSignedInAccount(this);
        database = FirebaseDatabase.getInstance();
        scannView = findViewById(R.id.qrcamera);
        codeScanner = new CodeScanner(this,scannView);
        resultData = findViewById(R.id.qrtext);
        qrscango = findViewById(R.id.qrscango);

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {

                runOnUiThread(new Runnable() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void run() {
                        resultData.setVisibility(View.VISIBLE);
                        resultData.setText(result.getText());
                        strresultData = result.getText();
                        qrscango.setVisibility(View.VISIBLE);
                        if (result.getText().startsWith("upi://pay"))
                        {

                            resultData.setVisibility(View.GONE);
                            qrscango.setBackgroundTintList(ContextCompat.getColorStateList(Capture.this, R.color.myyellow));
                            qrscango.setText("Send Money");

                        }
                        else if (result.getText().startsWith("https")||result.getText().startsWith("http")||result.getText().startsWith("www."))
                        {
                            resultData.setVisibility(View.GONE);
                            qrscango.setBackgroundTintList(ContextCompat.getColorStateList(Capture.this, R.color.mypink));

                            qrscango.setText("Visit Site");
                        }
                        else if (strresultData.startsWith("tel:"))
                        {
                            resultData.setVisibility(View.GONE);

                            qrscango.setBackgroundTintList(ContextCompat.getColorStateList(Capture.this, R.color.mygreen));

                            qrscango.setText("Call");

                        }
                        else if (strresultData.startsWith("SMSTO:")||strresultData.startsWith("smsto:"))
                        {
                            resultData.setVisibility(View.GONE);

                            qrscango.setBackgroundTintList(ContextCompat.getColorStateList(Capture.this, R.color.myskyblue));

                            qrscango.setText("SEND MESSAGE");
                        }

                        else if (strresultData.startsWith("MAILTO:")||strresultData.startsWith("MATMSG:")||strresultData.startsWith("mailto:"))
                        {


                            resultData.setVisibility(View.GONE);
                            qrscango.setBackgroundTintList(ContextCompat.getColorStateList(Capture.this, R.color.myred));

                            qrscango.setText("Send mail");
                        }
                        else if (strresultData.startsWith("WIFI:T:WPA;"))
                        {


                            resultData.setVisibility(View.GONE);
                            qrscango.setBackgroundTintList(ContextCompat.getColorStateList(Capture.this, R.color.mylightgreen));

                            qrscango.setText("Copy Wifi Password");
                        }
                        else
                        {
                            qrscango.setBackgroundTintList(ContextCompat.getColorStateList(Capture.this, R.color.myblue));

                            qrscango.setText("Copy");
                        }
                    }
                });

            }
        });


        scannView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultData.setVisibility(View.GONE);
                qrscango.setVisibility(View.GONE);
                codeScanner.startPreview();

            }
        });

        qrscango.setOnClickListener(v -> {



            if (strresultData.startsWith("upi://pay"))
            {
                Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(strresultData));
            Intent chooser = Intent.createChooser(intent, "Pay with...");
            if (null == chooser.resolveActivity(getPackageManager())) {
                    Toast.makeText(Capture.this,"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
                }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                startActivityForResult(chooser, 1, null);
            }
            }
            else if (strresultData.startsWith("https://")||strresultData.startsWith("http://")||strresultData.startsWith("www."))
            {
                if (strresultData.startsWith("www."))
                {
                    strresultData = "https://"+strresultData+"/";
                }
                Uri uritext = Uri.parse(strresultData);
                Intent launchActivity = new Intent(Intent.ACTION_VIEW, uritext);
                startActivity(launchActivity);
            }

            else if (strresultData.startsWith("SMSTO:")||strresultData.startsWith("smsto:"))
            {
                String[] strings;
                Intent intent = new Intent( Intent.ACTION_VIEW);
                if (strresultData.startsWith("SMSTO:"))
                    strresultData = strresultData.replace("SMSTO:","");
                else
                    strresultData = strresultData.replace("smsto:","");

                if (strresultData.endsWith(":"))
                {
                    strresultData.replace(":","");
                    intent.putExtra("address",strresultData);

                }
                else
                {
                    strings = strresultData.split(":");
                    intent.putExtra("address",strings[0]);
                    intent.putExtra("sms_body",strings[1]);
                }
                intent.setType("vnd.android-dir/mms-sms");
                startActivity(intent);
            }
            else if (strresultData.startsWith("tel:"))
            {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(strresultData));
                startActivity(intent);
            }
            else if (strresultData.startsWith("MATMSG:")||strresultData.startsWith("mailto:")||strresultData.startsWith("MAILTO:"))
            {
                String mail = null, body = null,subject = null;
                String[] mailid = new String[0];
                if(strresultData.startsWith("MATMSG:"))
                {
                    strresultData = strresultData.replace("MATMSG:","");
                    String[] strmailqrarr = strresultData.split(";");
                    mail = strmailqrarr[0].replace("TO:","");
                    if (!strmailqrarr[1].isEmpty())
                        subject = strmailqrarr[1].replace("SUB:","");
                    if (!strmailqrarr[2].isEmpty())
                        body = strmailqrarr[2].replace("BODY:","");
                    mailid = mail.split(",");
                    System.out.println("MAIL TO: "+mailid);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_EMAIL, mailid);
                    if (!strmailqrarr[1].isEmpty())
                        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                    if (!strmailqrarr[2].isEmpty())
                        intent.putExtra(Intent.EXTRA_TEXT,body);
                    intent.setType("message/rfc822");
                    PackageManager packageManager =getPackageManager();
                    try {
                        packageManager.getPackageInfo("com.google.android.gm",PackageManager.GET_ACTIVITIES);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    startActivity(intent);
                }
                else if (strresultData.startsWith("mailto:")||strresultData.startsWith("MAILTO:"))
                {
                    if (strresultData.startsWith("MAILTO:"))
                        strresultData = strresultData.replace("MAILTO:","mailto:");
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setData(Uri.parse(strresultData));
                    startActivity(intent);
                }

            }
            else if (strresultData.startsWith("WIFI:T:WPA;"))
            {
                String strresultDataarr[] = strresultData.split(";");
                String pass = strresultDataarr[1].replace("P:","");
                ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Result",pass);
                clipboard.setPrimaryClip(clipData);

                Toast.makeText(getApplicationContext(),"Copied",Toast.LENGTH_SHORT).show();
            }
            else
            {
                ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Result",strresultData);
                clipboard.setPrimaryClip(clipData);

                Toast.makeText(getApplicationContext(),"Copied",Toast.LENGTH_SHORT).show();
            }

        });
    }


    public void requestForCamera() {
        Dexter.withActivity(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                codeScanner.startPreview();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText(Capture.this, "Camera Permission is Required.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();

            }
        }).check();
    }


    @Override                                                                                       //set currentuser online
    protected void onResume() {
        super.onResume();
        requestForCamera();
        database.getReference().child("presence").child(currentuser.getId()).setValue("Online");
    }

    @Override                                                                                       //set currentuser offline
    protected void onPause() {
        super.onPause();
        database.getReference().child("presence").child(currentuser.getId()).setValue("Offline");

    }
}