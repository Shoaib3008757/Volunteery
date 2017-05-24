package com.flytechnology.volunteery;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.messaging.FirebaseMessaging;


public class MainActivity extends AppCompatActivity {

    Button bt_jobSeker, bt_jobPoster, bt_admin, btRegister;
    SharedPreferences sharedPreferences;

    String reg_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //calling Function
        init();

        checkingLoginStatus();

        onClickForButtonJobSeeker();
        onClickForButtonJobPoster();
        onClickForButtonAdmin();
        onClickForRegister();

        displayFirebaseRegId();



    }//end of onCreate

    //intializing and registring views
    public void init(){

            bt_jobSeker = (Button) findViewById(R.id.bt_jobseeker);
            bt_jobPoster = (Button) findViewById(R.id.bt_jobposter);
            bt_admin = (Button) findViewById(R.id.bt_admin);
        btRegister = (Button) findViewById(R.id.bt_jobseeker_register);

    }


    public void onClickForButtonJobSeeker(){
        //setting on Click for bt jobseeker
        bt_jobSeker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent startJobSeeker = new Intent(MainActivity.this, LoginForJobSeeker.class);
                startActivity(startJobSeeker);
                finish();


            }
        });

    }




    public void onClickForButtonJobPoster(){

        //setting on Click for bt jobseeker
        bt_jobPoster.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent startJobPster = new Intent(MainActivity.this, LoginForJobPoster.class);
                startActivity(startJobPster);
                finish();


            }
        });


    }

    public void onClickForButtonAdmin(){

        //setting on Click for bt jobseeker
        bt_admin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                Intent startAdmin = new Intent(MainActivity.this, LoginForAdmin.class);
                startActivity(startAdmin);
                finish();


            }
        });

    }

    public boolean isStudentLogin(){

        boolean isLogin = false;

        sharedPreferences = getSharedPreferences("student", 0);
        String firstName = sharedPreferences.getString("firstname", null);
        if (firstName!=null){


            isLogin = true;
        }else {
            isLogin = false;
        }

        return isLogin;

    }//end of check student loginState

    public void onClickForRegister(){
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerActivity = new Intent(MainActivity.this, RegistringJobSeeker.class);
                startActivity(registerActivity);
                finish();
            }
        });
    }

public boolean isCompanyLogin(){

    boolean isCompanyLogin = false;

    sharedPreferences = getSharedPreferences("company", 0);
    String companyName = sharedPreferences.getString("companyname", null);

    if (companyName!=null){


        isCompanyLogin = true;
    }else {
        isCompanyLogin = false;
    }

    return isCompanyLogin;
}

    public boolean isAdminLogin(){
        boolean isAdminLogin = false;

        sharedPreferences = getSharedPreferences("admin", 0);
        String admin = sharedPreferences.getString("admin", null);
        if (admin!=null){
            isAdminLogin = true;
        }else {
            isAdminLogin = false;
        }

        return isAdminLogin;
    }

    public void checkingLoginStatus(){
        //checking student loginTatus
        boolean isStudenLogin = isStudentLogin();
        if (isStudenLogin){
            Intent viewJobsActvity = new Intent(MainActivity.this, ViewJobs.class);
            startActivity(viewJobsActvity);
            finish();
        }else if (isCompanyLogin()){
            Intent postJobActvity = new Intent(MainActivity.this, PostJob.class);
            startActivity(postJobActvity);
            finish();
        }else if (isAdminLogin()){
            Intent adminActvity = new Intent(MainActivity.this, AdminControl.class);
            startActivity(adminActvity);
            finish();
        }
    }

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        reg_id = pref.getString("regId", null);

        Log.e("TAG", "Firebase reg id: " + reg_id);

    }
}
