package com.salman.myproject.ui;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.salman.myproject.BuildConfig;
import com.salman.myproject.R;
import com.salman.myproject.firebase_pojo.FirebaseImage;
import com.salman.myproject.firebase_pojo.FirebasePropertyDetail;
import com.salman.myproject.realm_pojo.PropertyDetail;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import id.zelory.compressor.Compressor;
import io.realm.Realm;

public class PropertyDetailActivity extends AppCompatActivity {

    private EditText editLocation,editContact,editEmail,editDetail,editPrice,editPropertyType,editDeal;
    private File mFile;
    private DatabaseReference mReference;
    private StorageReference mStorageReference;
    private ProgressDialog mProgressDialog;
    private Realm mRealm;
    private FirebaseUser mUser;
    private ImageView imageView,selectImage;
    private boolean change = false,somethingChanged = false,addChange = false;
    private String publicKey="",previousLink="",userKey="";
    private TextView location,contact,email,details,price,property_type,deal_type;
    private PropertyDetail mPropertyDetail;
    private int position;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_detail);

        TextView location1,contact1,email1,details1,price1,property_type1,deal_type1;

        mRealm = Realm.getDefaultInstance();
        mReference = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        location1 = findViewById(R.id.location_2);
        contact1 = findViewById(R.id.contact_number);
        email1 = findViewById(R.id.email);
        details1 = findViewById(R.id.property_detail);
        price1 = findViewById(R.id.property_price);
        property_type1 = findViewById(R.id.property_type);
        deal_type1 = findViewById(R.id.deal_type);


        location = findViewById(R.id.location_3);
        contact = findViewById(R.id.contact_number1);
        email = findViewById(R.id.email1);
        details = findViewById(R.id.details_1);
        price = findViewById(R.id.price1);
        property_type = findViewById(R.id.property_detail_1);
        deal_type = findViewById(R.id.deal_type_1);
        imageView = findViewById(R.id.property_inf_image);
        editLocation = findViewById(R.id.edit_location);
        editContact = findViewById(R.id.edit_contact_number);
        editEmail = findViewById(R.id.edit_email);
        editPrice = findViewById(R.id.edit_price);
        editPropertyType = findViewById(R.id.edit_property_detail);
        editDeal = findViewById(R.id.edit_deal_type);
        editDetail = findViewById(R.id.edit_details);
        selectImage = findViewById(R.id.select_image);
        Button fab = findViewById(R.id.add_fab_prop);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null)
        {
            addChange = bundle.getBoolean("addChange");
            if(!addChange)
            {
                location.setText(bundle.getString("location"));
                contact.setText(bundle.getString("contact"));
                email.setText(bundle.getString("email"));
                details.setText(bundle.getString("details"));
                price.setText(bundle.getString("price"));
                property_type.setText(bundle.getString("property_type"));
                deal_type.setText(bundle.getString("deal_type"));
                change = bundle.getBoolean("change");
                publicKey = bundle.getString("publicKey");
                userKey = bundle.getString("userKey");
                position = bundle.getInt("position");

                int key = bundle.getInt("realmKey");

                mPropertyDetail = mRealm.where(PropertyDetail.class).equalTo("key",key).findFirst();


                previousLink = bundle.getString("link");

                Glide.with(this)
                        .load(previousLink)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(imageView);
            }
            else
            {
                location.setVisibility(View.GONE);
                contact.setVisibility(View.GONE);
                email.setVisibility(View.GONE);
                details.setVisibility(View.GONE);
                price.setVisibility(View.GONE);
                property_type.setVisibility(View.GONE);
                deal_type.setVisibility(View.GONE);

                location1.setVisibility(View.GONE);
                contact1.setVisibility(View.GONE);
                email1.setVisibility(View.GONE);
                details1.setVisibility(View.GONE);
                price1.setVisibility(View.GONE);
                property_type1.setVisibility(View.GONE);
                deal_type1.setVisibility(View.GONE);

                selectImage.setVisibility(View.VISIBLE);
                editLocation.setVisibility(View.VISIBLE);
                editContact.setVisibility(View.VISIBLE);
                editEmail.setVisibility(View.VISIBLE);
                editDetail.setVisibility(View.VISIBLE);
                editPrice.setVisibility(View.VISIBLE);
                editPropertyType.setVisibility(View.VISIBLE);
                editDeal.setVisibility(View.VISIBLE);
                fab.setVisibility(View.VISIBLE);
            }
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkProperty();
            }
        });

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImageDialog();
            }
        });


        setTextClick();
    }

    private void updateRealm(final String s,final String value)
    {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                switch (s) {
                    case "location":
                        mPropertyDetail.setLocation(value);
                        break;
                    case "contact":
                        mPropertyDetail.setContactNumber(value);
                        break;
                    case "email":
                        mPropertyDetail.setEmail(value);
                        break;
                    case "details":
                        mPropertyDetail.setDetails(value);
                        break;
                    case "propertyType":
                        mPropertyDetail.setPropertyType(value);
                        break;
                    case "dealType":
                        mPropertyDetail.setDealType(value);
                        break;
                    case "price":
                        mPropertyDetail.setPrice(Integer.parseInt(value));
                        break;
                    case "photo":
                        mPropertyDetail.setPhotoLink(value);
                }

                mRealm.insertOrUpdate(mPropertyDetail);

            }
        });
    }

    private void setTextClick() {

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(change)
                    getImageDialog();
                else if(addChange)
                    getImageDialog();

            }
        });

        deal_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(change)
                {
                    setChangeDialog("dealType");
                }
            }
        });

        property_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(change)
                {
                    setChangeDialog("propertyType");
                }
            }
        });

        price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(change)
                {
                    setChangeDialog("price");
                }
            }
        });

        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(change)
                {
                    setChangeDialog("details");
                }
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(change)
                {
                    setChangeDialog("location");
                }
            }
        });

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(change)
                {
                    setChangeDialog("contact");
                }
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(change)
                {
                    setChangeDialog("email");
                }
            }
        });
    }

    private void setChangeDialog(final String s)
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog);

        final EditText editText = dialog.findViewById(R.id.editText);
        TextInputLayout textInputLayout = dialog.findViewById(R.id.textInput);
        String text = "Enter "+s;
        textInputLayout.setHint(text);
        Button save,cancel;
        save = dialog.findViewById(R.id.saveBtn);
        cancel = dialog.findViewById(R.id.cancelBtn);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(s.equals("price"))
                {
                    try
                    {

                        mReference.child("Properties").child("allProprieties").child(publicKey)
                                .child(s).setValue(Integer.parseInt(editText.getText().toString()));

                        mReference.child("Properties").child("user").child(mUser.getUid()).child(userKey)
                                .child(s).setValue(Integer.parseInt(editText.getText().toString()));

                    }catch (NumberFormatException e)
                    {
                        Toast.makeText(getApplicationContext(),"Only Interger Value",Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    mReference.child("Properties").child("allProprieties").child(publicKey)
                            .child(s).setValue(editText.getText().toString());

                    mReference.child("Properties").child("user").child(mUser.getUid()).child(userKey)
                            .child(s).setValue(editText.getText().toString());
                }

                somethingChanged = true;

                switch (s) {
                    case "location":
                        location.setText(editText.getText().toString());
                        break;
                    case "contact":
                        contact.setText(editText.getText().toString());
                        break;
                    case "email":
                        email.setText(editText.getText().toString());
                        break;
                    case "details":
                        details.setText(editText.getText().toString());
                        break;
                    case "propertyType":
                        property_type.setText(editText.getText().toString());
                        break;
                    case "dealType":
                        deal_type.setText(editText.getText().toString());
                        break;
                }

                updateRealm(s,editText.getText().toString());
                dialog.dismiss();



            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }



    private void checkProperty() {

        String location,contact,email,details,price,property_type,deal_type;

        location = editLocation.getText().toString();
        contact = editContact.getText().toString();
        details = editDetail.getText().toString();
        price = editPrice.getText().toString();
        property_type = editPropertyType.getText().toString();
        deal_type = editDeal.getText().toString();
        email = editEmail.getText().toString();


        if(location.isEmpty())
        {
            editLocation.setError("Location is Required");
            editLocation.requestFocus();
        }
        else if(contact.isEmpty())
        {
            editContact.setError("Contact Number is Required");
            editContact.requestFocus();
        }
        else if(email.isEmpty())
        {
            editEmail.setError("Email is Required");
            editEmail.requestFocus();
        }
        else if(details.isEmpty())
        {
            editDetail.setError("Detail is Required");
            editDetail.requestFocus();
        }
        else if(price.isEmpty())
        {
            editPrice.setError("Price is Required");
            editPrice.requestFocus();
        }
        else if(property_type.isEmpty())
        {
            editPropertyType.setError("Property Type is Required");
            editPropertyType.requestFocus();
        }
        else if(deal_type.isEmpty())
        {
            editDeal.setError("Deal Type is Required");
            editDeal.requestFocus();
        }
        else if(mFile == null)
        {
            Toast.makeText(getApplicationContext(),"Image is required",Toast.LENGTH_LONG).show();
        }
        else
            uploadImage();


    }

    private void getImageDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(PropertyDetailActivity.this);
        String s = "    Take Picture,    Pick Picture";
        builder.setTitle("Picture");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                PropertyDetailActivity.this,android.R.layout.simple_selectable_list_item,new ArrayList<>(Arrays.asList(s.split(",")))
        );
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(i==0)
                {

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

    private void uploadImage()
    {
        if(mFile!=null)
        {
            mProgressDialog.show();
            mProgressDialog.setMessage("Adding Image");
            StorageReference ref = mStorageReference.child("images/"+mFile.getName());
            ref.putFile(Uri.fromFile(mFile))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            String link = taskSnapshot.getDownloadUrl().toString();
                            if(change)
                            {
                                mReference.child("Properties").child("allProprieties").child(publicKey)
                                        .child("photoLink").setValue(link);

                                mReference.child("Properties").child("user").child(mUser.getUid()).child(userKey)
                                        .child("photoLink").setValue(link);

                                Toast.makeText(getApplicationContext(),"Picture Updated",Toast.LENGTH_LONG).show();

                                mProgressDialog.dismiss();

                                Glide.with(PropertyDetailActivity.this)
                                        .load(link)
                                        .into(imageView);

                                updateRealm("photo",link);

                                deleteImage();
                            }
                            else
                            {
                                addProperty(link);
                            }


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                }
            });
        }
        else
            Toast.makeText(PropertyDetailActivity.this,"Image not found",Toast.LENGTH_LONG).show();
    }

    private void deleteImage() {

        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(previousLink);
        ref.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(),"Previous Image Deleted",Toast.LENGTH_LONG).show();
            }
        });


    }

    private void addProperty(final String link) {

        final String location,contact,email,details,price,property_type,deal_type;

        location = editLocation.getText().toString();
        contact = editContact.getText().toString();
        details = editDetail.getText().toString();
        price = editPrice.getText().toString();
        property_type = editPropertyType.getText().toString();
        deal_type = editDeal.getText().toString();
        email = editEmail.getText().toString();

        FirebasePropertyDetail detail = new FirebasePropertyDetail(deal_type,property_type,
                Integer.parseInt(price),location,details,contact,email,"House For Sale");

        detail.setPhotoLink(link);
        mProgressDialog.dismiss();
        mFile = null;

        final DatabaseReference ref = mReference.child("Properties").child("user").child(mUser.getUid()).push();
        ref.setValue(detail);

        mReference.child("Properties").child("allProprieties").push().setValue(detail, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(final DatabaseError databaseError, final DatabaseReference databaseReference) {
                if(databaseError == null)
                {
                    Toast.makeText(getApplicationContext(),"Added",Toast.LENGTH_LONG).show();

                    mRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            int i = 0;
                            Number number = mRealm.where(PropertyDetail.class).max("key");
                            if(number!=null)
                                i = number.intValue()+1;

                            PropertyDetail propertyDetail = new PropertyDetail(i,deal_type,
                                    property_type,Integer.parseInt(price),location,details,
                                    databaseReference.getKey(),contact,email);

                            propertyDetail.setShortDesc("House For Sale");
                            propertyDetail.setPhotoLink(link);
                            propertyDetail.setUserKey(mUser.getUid());
                            propertyDetail.setImageKey(databaseReference.getKey());
                            realm.insertOrUpdate(propertyDetail);

                            ref.getRef().child("key").setValue(databaseReference.getKey());

                        }
                    });

                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 123 && resultCode==RESULT_OK)
        {
            if(data.getData()!=null)
            {
                CropImage.activity(data.getData())
                        .start(PropertyDetailActivity.this);
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
                    mFile = new Compressor(PropertyDetailActivity.this).compressToFile(new File(uri.getPath()));
                   // imageView.setImageURI(Uri.fromFile(mFile));
                    Toast.makeText(PropertyDetailActivity.this,mFile.getName(),Toast.LENGTH_LONG).show();
                    selectImage.setVisibility(View.GONE);

                    if(change)
                        uploadImage();

                } catch (IOException e)
                {
                    Toast.makeText(PropertyDetailActivity.this,"error",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }
        else if(requestCode == 456 && resultCode == Activity.RESULT_OK)
        {

            Uri uri = Uri.parse(mCurrentPhotoPath);

            CropImage.activity(uri)
                    .start(PropertyDetailActivity.this);
        }

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
                Uri photoURI = FileProvider.getUriForFile(PropertyDetailActivity.this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        createImageFile());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 456);
            }
        }
    }


    @Override
    public void onBackPressed() {

        if(change)
        {
            Intent i = new Intent();
            i.putExtra("bool",somethingChanged);
            i.putExtra("position",position);
            setResult(RESULT_OK,i);
            finish();
        }
        else
            super.onBackPressed();

    }
}
