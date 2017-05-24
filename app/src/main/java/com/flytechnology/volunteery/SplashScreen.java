package com.flytechnology.volunteery;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class SplashScreen extends AppCompatActivity {

    private static int SplashScreenTimeOut = 3000;//3 seconds8
    private int timer = 2;
    Handler mHandler;

    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mHandler = new Handler();


        useHandler();
        //checkingLoginStatus();
    }

    //Thread for starting mainActivity
    private Runnable mRunnableStartMainActivity = new Runnable() {
        @Override
        public void run() {
            Log.d("Handler", " Calls");
            timer--;
            mHandler = new Handler();
            mHandler.postDelayed(this, 1000);

            if (timer == 2) {
                //loading.setText("Loading...");
            }
            if (timer == 1) {
                //loading.setText("Loading.");
            }
            if (timer == 0) {


                if (isStudentLogin()){
                    Intent viewJobsActvity = new Intent(SplashScreen.this, ViewJobs.class);
                    startActivity(viewJobsActvity);
                    finish();
                }else if (isCompanyLogin()){
                    Intent postJobActvity = new Intent(SplashScreen.this, PostJob.class);
                    startActivity(postJobActvity);
                    finish();
                }else if (isAdminLogin()){
                    Intent adminActvity = new Intent(SplashScreen.this, AdminControl.class);
                    startActivity(adminActvity);
                    finish();
                }
                else {

                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);
                finish();

                }
            }
        }
    };


    //handler for the starign activity
    Handler newHandler;
    public void useHandler(){

        newHandler = new Handler();
        newHandler.postDelayed(mRunnableStartMainActivity, 1000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnableStartMainActivity);
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


}