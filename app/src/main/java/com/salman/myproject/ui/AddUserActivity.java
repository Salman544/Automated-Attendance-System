package com.salman.myproject.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.salman.myproject.R;
import com.salman.myproject.databinding.ActivityAddUserBinding;
import com.salman.myproject.realm_pojo.RealmStudent;
import com.salman.myproject.rest_api.EnrollUser;
import com.salman.myproject.rest_api.EnrollUserPost;
import com.salman.myproject.rest_api.Errors;
import com.salman.myproject.rest_api.KairosApi;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import id.zelory.compressor.Compressor;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddUserActivity extends AppCompatActivity {

    private ActivityAddUserBinding mBinding;
    private File mFile = null;
    private KairosApi mKairosApi;
    private Realm mRealm;
    private FirebaseUser mUser;
    private DatabaseReference mReference;
    private ProgressDialog mProgressDialog;
    private StorageReference mStorageReference;
    private String className;
    private String ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_add_user);

        mReference = FirebaseDatabase.getInstance().getReference();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mRealm = Realm.getDefaultInstance();
        mProgressDialog = new ProgressDialog(this);
        mStorageReference = FirebaseStorage.getInstance().getReference();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.kairos.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mKairosApi = retrofit.create(KairosApi.class);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null)
            className = bundle.getString("className");


        mBinding.addUserProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getImageDialog();

            }
        });

        mBinding.addUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setChecked();

            }
        });

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

                if(i==0)
                {
                    PackageManager pm = getApplicationContext().getPackageManager();
                    if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
                    {
                        if(cameraPermission())
                        {
                            Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(camera,333);
                        }
                        else
                            Toast.makeText(getApplicationContext(),"Need Camera Permission",Toast.LENGTH_LONG).show();
                    }
                    else
                        Toast.makeText(getApplicationContext(),"No camera detected",Toast.LENGTH_LONG).show();
                }
                else
                {

                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent,"Select Picture"),123);
                }
            }
        });

        builder.show();

    }

    private void setChecked() {

        String name = mBinding.addUserNameEditText.getText().toString();
        String id = mBinding.addIdEditText.getText().toString();

        if(name.isEmpty())
        {
            mBinding.nameTextInput.setError("Name is required");
            mBinding.nameTextInput.setErrorEnabled(true);
            mBinding.addUserNameEditText.requestFocus();
        }
        else if(id.isEmpty())
        {
            mBinding.addIdTextInputLayout.setErrorEnabled(true);
            mBinding.addIdTextInputLayout.setError("Id/Roll Number is Required");
            mBinding.addIdEditText.requestFocus();
        }
        else if(mFile == null)
            Toast.makeText(getApplicationContext(),"Image is required",Toast.LENGTH_LONG).show();
        else
        {
            RealmStudent student = mRealm.where(RealmStudent.class).equalTo("rollNumber",id).findFirst();
            if(student == null)
                uploadImage();
            else
            {
                mBinding.addIdTextInputLayout.setErrorEnabled(true);
                mBinding.addIdTextInputLayout.setError("Id/Roll Number already exists");
                mBinding.addIdEditText.requestFocus();
            }
        }


    }

    private void uploadImage()
    {
        mProgressDialog.setMessage("Adding User Please Wait");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        if(mFile!=null)
        {
            Uri uri = Uri.fromFile(mFile);
            StorageReference ref = mStorageReference.child("images/"+mFile.getName());
            ref.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            registerUser(taskSnapshot.getDownloadUrl().toString());


                        }
                    });
        }
    }

    private void registerUser(final String link) {

        EnrollUser user = new EnrollUser(link,mBinding.addUserNameEditText.getText().toString()
                ,className);

        mKairosApi.enrollUser(user).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                mProgressDialog.dismiss();
                if (response.isSuccessful())
                {
                    Gson gson = new Gson();
                    String s = gson.toJson(response.body());
                    if (s.contains("Errors"))
                    {
                        Errors errors = gson.fromJson(s, Errors.class);
                        String error = errors.getErrors().get(0).getMessage();

                        final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_INDEFINITE);

                        snackbar.setAction("Ok", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snackbar.dismiss();
                            }
                        });

                        snackbar.show();
                        mFile = null;

                        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(link);
                        ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(),"Image Deleted",Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                    else
                    {
                        final EnrollUserPost enroll = gson.fromJson(s,EnrollUserPost.class);
//                        AddStudent student = new AddStudent(
//                                mBinding.addUserNameEditText.getText().toString(),
//                                mBinding.addIdEditText.getText().toString(),
//                                link,enroll.getFace_id(),"",className
//                        );

//                        mReference.child("Students").child(mUser.getUid()).push().setValue(student);

                        mRealm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {

                                int i = 0;
                                Number number = mRealm.where(RealmStudent.class).max("id");
                                if(number!=null)
                                    i = number.intValue()+1;

                                RealmStudent realmStudent = new RealmStudent(i,
                                        mBinding.addUserNameEditText.getText().toString(),
                                        mBinding.addIdEditText.getText().toString(),
                                        link,enroll.getFace_id(),className);

                                realm.insertOrUpdate(realmStudent);

                            }
                        });

                        mProgressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"student added",Toast.LENGTH_LONG).show();
                        mFile = null;
                        mBinding.addUserProfilePic.setImageDrawable(ContextCompat.getDrawable(AddUserActivity.this,R.drawable.tranparent_image));
                        mBinding.addIdEditText.setText("");
                        mBinding.addUserNameEditText.setText("");
                        mBinding.addUserNameEditText.requestFocus();

                    }
                }

            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {

                Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_LONG).show();

            }
        });

    }

    private boolean cameraPermission()
    {
        boolean check = false;
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.CAMERA},12345);
        else
            check = true;

        return check;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 12345)
        {
            Intent camera = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(camera,333);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 123 && resultCode==RESULT_OK)
        {
            if(data.getData()!=null)
            {
                CropImage.activity(data.getData())
                        .start(AddUserActivity.this);
            }
        }
        else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode==RESULT_OK)
            {
                Uri uri = result.getUri();

                try
                {
                    mFile = new Compressor(AddUserActivity.this).compressToFile(new File(uri.getPath()));
                    Toast.makeText(getApplicationContext(),mFile.getName(),Toast.LENGTH_LONG).show();
                    mBinding.addUserProfilePic.setImageURI(Uri.fromFile(mFile));

                } catch (IOException e)
                {
                    Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }
        else if(requestCode == 333 && resultCode == RESULT_OK)
        {
            if(data.getData()!=null)
            {
                CropImage.activity(data.getData())
                        .start(AddUserActivity.this);
            }
        }

    }
}
