package com.salman.myproject.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.salman.myproject.R;
import com.salman.myproject.databinding.ActivityLoginBinding;
import com.salman.myproject.firebase_pojo.UserInfo;
import com.tapadoo.alerter.Alerter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding mBinding;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mProgressDialog = new ProgressDialog(this);



        isConnectedToInternet();
        setOnClick();
        getUserSharedInformaion();

//        if(isGooglePlayServicesAvailable())
//        {
//
//            mBinding.loginInBtn.setEnabled(true);
//
//        }
//        else
//        {
//            Toast.makeText(getApplicationContext(),"For Login Google Play Services Are Required", Toast.LENGTH_LONG).show();
//            mBinding.loginInBtn.setEnabled(false);
//        }

    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        final int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if(status != ConnectionResult.SUCCESS) {
            if(googleApiAvailability.isUserResolvableError(status)) {
                Dialog dialog = googleApiAvailability.getErrorDialog(this, status, 2404);
                dialog.show();

            }
            return false;
        }
        return true;
    }

    private void setOnClick()
    {
        mBinding.loginInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = mBinding.emailEditText.getText().toString().trim();
                String pass = mBinding.passwordEditText.getText().toString();

                if(email.isEmpty())
                {
                    mBinding.emailTextInputLayout.setErrorEnabled(true);
                    mBinding.emailTextInputLayout.setError("This Field Should Not Be Empty.");
                    mBinding.emailEditText.requestFocus();
                }
                else if(pass.isEmpty())
                {
                    mBinding.passwordTextInputLayout.setErrorEnabled(true);
                    mBinding.passwordTextInputLayout.setError("Invalid Email");
                    mBinding.passwordEditText.requestFocus();
                }
                else if(email.contains("@"))
                    loginUser(email,pass);
                else
                {
                    mBinding.emailTextInputLayout.setErrorEnabled(true);
                    mBinding.emailTextInputLayout.setError("Invalid Email");
                }

            }
        });

        mBinding.emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                mBinding.emailTextInputLayout.setErrorEnabled(false);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mBinding.passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                mBinding.passwordTextInputLayout.setErrorEnabled(false);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mBinding.registerAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(i);
            }
        });

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
                                Alerter.create(LoginActivity.this)
                                        .setBackgroundColorInt(ContextCompat.getColor(LoginActivity.this,R.color.alerter_default_success_background))
                                        .setIcon(R.drawable.ic_wifi_connected)
                                        .enableSwipeToDismiss()
                                        .setDuration(500)
                                        .setTitle("Internet Established")
                                        .show();
                            }


                        }
                        else
                        {
                            if(mProgressDialog.isShowing())
                                mProgressDialog.dismiss();

                            Alerter.create(LoginActivity.this)
                                    .setBackgroundColorInt(ContextCompat.getColor(LoginActivity.this,R.color.alert_default_error_background))
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

                        }

                    }
                });
    }

    private void loginUser(String email, String pass) {

        mProgressDialog.setMessage("Please Wait . . .");
        mProgressDialog.show();

        mAuth.signInWithEmailAndPassword(email,pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        mUser = mAuth.getCurrentUser();
                        if(task.isSuccessful())
                        {
                            getUserInformation();
//                            if(mUser.isEmailVerified())
//                            {
//                                getUserInformation();
//                            }
//                            else
//                                verificationDialog();

                        }
                        else
                            Toast.makeText(getApplicationContext(),"invalid email or password",Toast.LENGTH_LONG).show();

                    }
                });

    }

    private void getUserInformation() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Users").child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mProgressDialog.dismiss();
                if(dataSnapshot.exists())
                {
                    UserInfo info = dataSnapshot.getValue(UserInfo.class);
                    setUserInformation(info.getName(),info.getEmail(),info.getLink(),info.getType());
                    String s = info.getType().toLowerCase();
                    switch (s) {
                        case "teacher": {
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            i.putExtra("tName", info.getName());
                            i.putExtra("tEmail", info.getEmail());
                            i.putExtra("tLink", info.getLink());
                            startActivity(i);
                            break;
                        }
                        case "student": {
                            Intent i = new Intent(LoginActivity.this, StudentActivity.class);
                            i.putExtra("studentName", info.getName());
                            i.putExtra("studentEmail", info.getEmail());
                            i.putExtra("studentLink", info.getLink());
                            startActivity(i);
                            break;
                        }
                        default: {
                            Intent i = new Intent(LoginActivity.this, PropertyActivity.class);
                            i.putExtra("pName",info.getName());
                            i.putExtra("pEmail",info.getEmail());
                            i.putExtra("pLink",info.getLink());
                            startActivity(i);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

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


        if(mUser!=null)
        {
            mProgressDialog.setMessage("Please wait . . .");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

            mUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    mProgressDialog.dismiss();
                    if(task.isSuccessful())
                        Toast.makeText(getApplicationContext(),"Verification email sent",Toast.LENGTH_LONG).show();
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Verification email not sent", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void setUserInformation(String name,String email,String link,String type)
    {
        SharedPreferences.Editor editor = getSharedPreferences("userInf",MODE_PRIVATE).edit();
        editor.putString("name",name);
        editor.putString("email",email);
        editor.putString("link",link);
        editor.putString("type",type);

        editor.apply();
        editor.commit();
    }

    private void getUserSharedInformaion()
    {
        SharedPreferences preferences = getSharedPreferences("userInf",MODE_PRIVATE);
        if(preferences!=null)
        {
            String name = preferences.getString("name",null);
            String email = preferences.getString("email",null);
            String link = preferences.getString("link",null);
            String type = preferences.getString("type",null);

            if(type!=null)
            {
                switch (type) {
                    case "Teacher": {
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        i.putExtra("tName", name);
                        i.putExtra("tEmail", email);
                        i.putExtra("tLink", link);
                        startActivity(i);
                        break;
                    }
                    case "Student": {
                        Intent i = new Intent(LoginActivity.this, StudentActivity.class);
                        i.putExtra("studentName", name);
                        i.putExtra("studentEmail", email);
                        i.putExtra("studentLink", link);
                        startActivity(i);
                        break;
                    }
                    default: {
                        Intent i = new Intent(LoginActivity.this, PropertyActivity.class);
                        i.putExtra("pName",name);
                        i.putExtra("pEmail",email);
                        i.putExtra("pLink",link);
                        startActivity(i);
                        break;
                    }
                }
            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        }
        else
            finish();

    }

    //
//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        if(mUser!=null)
//        {
//            if(mUser.isEmailVerified())
//                startActivity(new Intent(LoginActivity.this,MainActivity.class));
//        }
//
//    }


}
