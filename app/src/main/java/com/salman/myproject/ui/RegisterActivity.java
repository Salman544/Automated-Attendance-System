package com.salman.myproject.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.salman.myproject.BuildConfig;
import com.salman.myproject.databinding.ActivityRegisterBinding;
import com.salman.myproject.firebase_pojo.UserInfo;
import com.tapadoo.alerter.Alerter;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import com.salman.myproject.R;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding mBinding;
    private FirebaseAuth mAuth;
    private DatabaseReference mReference;
    private ProgressDialog mProgressDialog;
    private boolean isPassCorrect = false;
    private String link = "null",type="";
    private StorageReference mStorageReference;
    private File mFile;
    private static final String TAG = "RegisterActivity";
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        mReference = FirebaseDatabase.getInstance().getReference();
        mProgressDialog = new ProgressDialog(this);
        mStorageReference = FirebaseStorage.getInstance().getReference();


        isConnectedToInternet();
        setOnClick();

    }

    private void getImageDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String s = "    Take Picture,    Pick Picture";
        builder.setTitle("Picture");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,android.R.layout.simple_selectable_list_item,new ArrayList<>(Arrays.asList(s.split(",")))
        );
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (i == 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            try {
                                cameraTakePictureIntent();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
                    }
                } else {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 111);
                }
            }
        });

        builder.show();

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void cameraTakePictureIntent() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                return;
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(RegisterActivity.this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        createImageFile());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 456);
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if(requestCode == 111 && resultCode==RESULT_OK)
        {
            if(data.getData()!=null)
            {
                CropImage.activity(data.getData())
                        .start(RegisterActivity.this);
            }
        }
        else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode==RESULT_OK)
            {
                Uri uri = result.getUri();

                //mBinding.profilePic.setImageURI(uri);
                try
                {

                    mFile = new Compressor(RegisterActivity.this).compressToFile(new File(uri.getPath()));
                    mBinding.profilePic.setImageURI(Uri.fromFile(mFile));
                    Log.d(TAG, "uploadPhoto: "+mFile.getName());
                    Toast.makeText(getApplicationContext(),mFile.getName(),Toast.LENGTH_LONG).show();

                } catch (IOException e)
                {
                    Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }
        else if(requestCode == 456 && resultCode == RESULT_OK)
        {
            Log.d("MainActivity", "onActivityResult: called");

            Uri uri = Uri.parse(mCurrentPhotoPath);
            Log.d("MainActivity", "onActivityResult: "+uri.getPath());
            CropImage.activity(uri)
                    .start(RegisterActivity.this);

        }
    }

    private void uploadPhoto(final String name, final String email, final String pass)
    {
        if(mFile!=null)
        {
            mProgressDialog.setMessage("Uploading Image");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
            Uri uri = Uri.fromFile(mFile);
            String fileName = mFile.getName()+".jpg";
            StorageReference ref = mStorageReference.child("images/"+fileName);
            ref.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getApplicationContext(),"uploaded",Toast.LENGTH_LONG).show();
                            link = taskSnapshot.getDownloadUrl().toString();
                            registerUser(name,email,pass,false);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"image uploading failed",Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    registerUser(name,email,pass,false);
                }
            });

        }
        else
            registerUser(name,email,pass,true);
    }

    private void setRegisterUserConditions()
    {
        String name = mBinding.registerNameEditText.getText().toString();
        String email = mBinding.registerEmailEditText.getText().toString();
        String pass = mBinding.registerPasswordEditText.getText().toString();

        if(name.isEmpty())
        {
            mBinding.nameTextInput.setError("This Field is Required");
            mBinding.nameTextInput.setErrorEnabled(true);
            mBinding.registerNameEditText.requestFocus();
        }
        else if(name.length()<=4)
        {
            mBinding.nameTextInput.setError("Enter Full Name");
            mBinding.nameTextInput.setErrorEnabled(true);
            mBinding.registerNameEditText.requestFocus();
        }
        else if(email.isEmpty())
        {
            mBinding.registerEmailEditText.setError("This Field is Required");
            mBinding.registerEmailTextInputLayout.setErrorEnabled(true);
            mBinding.registerEmailEditText.requestFocus();
        }
        else if(!email.contains("@"))
        {
            mBinding.registerEmailEditText.setError("Invalid Email Address");
            mBinding.registerEmailTextInputLayout.setErrorEnabled(true);
            mBinding.registerEmailEditText.requestFocus();
        }
        else if(pass.isEmpty())
        {
            mBinding.registerPasswordTextInputLayout.setError("This Field is Required");
            mBinding.registerPasswordTextInputLayout.setErrorEnabled(true);
            mBinding.registerPasswordEditText.requestFocus();
        }
        else if(pass.length()<=8)
        {
            mBinding.registerPasswordTextInputLayout.setError("Short Password");
            mBinding.registerPasswordTextInputLayout.setErrorEnabled(true);
            mBinding.registerPasswordEditText.requestFocus();
        }
        else if(mBinding.registerConfirmPassword.getText().toString().isEmpty())
        {
            mBinding.registerConfirmPasswordTextInputLayout.setError("This Field is Required");
            mBinding.registerConfirmPasswordTextInputLayout.setErrorEnabled(true);
            mBinding.registerConfirmPassword.requestFocus();
        }
        else if(type.isEmpty())
            Toast.makeText(getApplicationContext(),"Type is Required",Toast.LENGTH_SHORT).show();
        else if(type!=null)
        {
            if(type.toLowerCase().equals("student"))
            {
                if(mFile == null)
                {
                    Toast.makeText(getApplicationContext(),"Image is required",Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                    uploadPhoto(name,email,pass);
            }
        }
    }


    private void setOnClick()
    {

        mBinding.selectTypeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String s = "    Teacher,    Student,    Property Dealer";
                ArrayAdapter<String> adapter = new ArrayAdapter<>(RegisterActivity.this,
                        android.R.layout.simple_selectable_list_item, Arrays.asList(s.split(",")));

                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                builder.setTitle("Select Type");
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(i==0)
                            type = "Teacher";
                        else if(i==1)
                            type = "Student";
                        else if(i==2)
                            type = "Property Dealer";

                        mBinding.selectTypeEditText.setText(type);

                        dialogInterface.dismiss();
                    }
                });

                builder.show();

            }
        });

        mBinding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                setRegisterUserConditions();

            }
        });

        mBinding.registerEmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!mBinding.registerEmailEditText.getText().toString().contains("@") ||
                        mBinding.registerEmailEditText.getText().toString().length() < 11)
                {
                    mBinding.registerEmailTextInputLayout.setErrorEnabled(true);
                    mBinding.registerEmailTextInputLayout.setError("Invalid Email Address");
                }
                else
                    mBinding.registerEmailTextInputLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        mBinding.registerNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(mBinding.registerNameEditText.getText().toString().trim().length() <= 4)
                {
                    mBinding.nameTextInput.setErrorEnabled(true);
                    mBinding.nameTextInput.setError("Enter Full Name");
                }
                else
                    mBinding.nameTextInput.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mBinding.registerPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(mBinding.registerPasswordEditText.getText().toString().length()<=8)
                {
                    mBinding.registerPasswordTextInputLayout.setError("Short Password");
                    mBinding.registerPasswordTextInputLayout.setErrorEnabled(true);
                }
                else
                    mBinding.registerPasswordTextInputLayout.setErrorEnabled(false);


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mBinding.registerConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(mBinding.registerConfirmPassword.getText().toString().equals(mBinding.registerPasswordEditText.getText().toString()))
                {
                    mBinding.registerConfirmPasswordTextInputLayout.setErrorEnabled(false);
                    Toast.makeText(getApplicationContext(),"Password Match",Toast.LENGTH_SHORT).show();
                    isPassCorrect = true;
                }
                else
                {
                    mBinding.registerConfirmPasswordTextInputLayout.setError("Password does not match");
                    mBinding.registerConfirmPasswordTextInputLayout.setErrorEnabled(true);
                    isPassCorrect = false;
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mBinding.profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImageDialog();
            }
        });


    }

    private void registerUser(final String name, final String email, String pass,boolean b) {

        if(isPassCorrect)
        {
            mProgressDialog.setMessage("Please Wait . . .");
            mProgressDialog.setCancelable(false);
            if(b)
                mProgressDialog.show();

            mAuth.createUserWithEmailAndPassword(email,pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            mProgressDialog.dismiss();
                            if(task.isSuccessful())
                            {
                                Toast.makeText(getApplicationContext(),"Registered",Toast.LENGTH_LONG).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                UserInfo info = new UserInfo(name.trim(),email.trim(),link,type);
                                mReference.child("Users").child(user.getUid()).setValue(info);
                                verificationDialog();
                            }
                            else
                            {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthWeakPasswordException e) {

                                    mBinding.registerPasswordTextInputLayout.setError("Weak Password");
                                    mBinding.registerPasswordTextInputLayout.setErrorEnabled(true);
                                    mBinding.registerPasswordEditText.requestFocus();
                                    mProgressDialog.dismiss();
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    mBinding.registerEmailTextInputLayout.setError("Invalid Email");
                                    mBinding.registerEmailTextInputLayout.setErrorEnabled(true);
                                    mBinding.registerEmailEditText.requestFocus();
                                    mProgressDialog.dismiss();
                                } catch (FirebaseAuthUserCollisionException e) {
                                    mBinding.registerEmailTextInputLayout.setError("The email address is already in use by another account");
                                    mBinding.registerEmailTextInputLayout.setErrorEnabled(true);
                                    mBinding.registerEmailEditText.requestFocus();
                                    mProgressDialog.dismiss();
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                                    Log.e(TAG, e.getMessage());
                                    mProgressDialog.dismiss();
                                }
                            }

                        }
                    });
        }

    }

    private void verificationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Verfication");
        builder.setMessage("Verify your self");

        builder.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sendVerificationEmail();
                dialogInterface.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mAuth.signOut();
                dialogInterface.dismiss();
            }
        });

        builder.show();

    }

    private void sendVerificationEmail() {

        final FirebaseUser user = mAuth.getCurrentUser();

        if(user!=null)
        {
            mProgressDialog.setMessage("Please wait . . .");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    mProgressDialog.dismiss();
                    String s = "Verification email send to "+user.getEmail();
                    String s1 = "Verification email not sent to "+user.getEmail();
                    if(task.isSuccessful())
                    {
                        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
                        finish();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),s1,Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onComplete: "+task.getException());
                        Log.d(TAG, "onComplete: "+task.getResult());
                    }
                }
            });
        }
    }
    private void isConnectedToInternet()
    {
        ReactiveNetwork.observeInternetConnectivity()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean b) throws Exception {

                        if(b)
                        {
                            if(Alerter.isShowing())
                            {
                                Alerter.hide();
                                Alerter.create(RegisterActivity.this)
                                        .setBackgroundColorInt(ContextCompat.getColor(RegisterActivity.this,R.color.alerter_default_success_background))
                                        .setIcon(R.drawable.ic_wifi_connected)
                                        .enableSwipeToDismiss()
                                        .setDuration(500)
                                        .setTitle("Internet Established")
                                        .show();

                            }

                            mBinding.signUpBtn.setEnabled(true);
                            mBinding.signUpBtn.setTextColor(ContextCompat.getColor(RegisterActivity.this,R.color.buttonTextColor));
                        }
                        else
                        {
                            if(mProgressDialog.isShowing())
                                mProgressDialog.dismiss();

                            Alerter.create(RegisterActivity.this)
                                    .setBackgroundColorInt(ContextCompat.getColor(RegisterActivity.this,R.color.alert_default_error_background))
                                    .setIcon(R.drawable.ic_wifi_not_connected)
                                    .enableInfiniteDuration(true)
                                    .disableOutsideTouch()
                                    .enableVibration(true)
                                    .setText("Your device is not connected to internet connection.\n" +
                                            "\n" +
                                            "Try:\n" +
                                            "Reconnecting to Wi-Fi or mobile network")
                                    .setTitle("Internet Not Avaliable")
                                    .show();

                            mBinding.signUpBtn.setEnabled(false);
                            mBinding.signUpBtn.setTextColor(ContextCompat.getColor(RegisterActivity.this,android.R.color.secondary_text_light_nodisable));

                        }

                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();

        mAuth.signOut();

    }




}
