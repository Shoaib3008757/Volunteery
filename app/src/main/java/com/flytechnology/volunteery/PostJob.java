package com.flytechnology.volunteery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PostJob extends AppCompatActivity {

    Button btPostJob;
    Button btLogOut;
    TextView tvCompanyName;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_job);

        init();
        postButtonClickLister();
        logOutHandler();
    }

    public void init(){
        btPostJob = (Button) findViewById(R.id.bt_post_job);
        btLogOut = (Button) findViewById(R.id.bt_logout);
        tvCompanyName = (TextView) findViewById(R.id.tv_title_company_name);

        sharedPreferences = getSharedPreferences("company", 0);
        String companyName = sharedPreferences.getString("companyname", null);

        if (companyName!=null){

            tvCompanyName.setText("Welcome " + companyName);
        }
    }

    public void logOutHandler(){

        btLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                sharedPreferences = getSharedPreferences("company", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                finish();
                Intent mainActivty = new Intent(PostJob.this, MainActivity.class);
                startActivity(mainActivty);
            }
        });

    }

    public void postButtonClickLister(){
        btPostJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent postingJob = new Intent(PostJob.this, PostingJob.class);
                startActivity(postingJob);
                finish();
            }
        });
    }
}
