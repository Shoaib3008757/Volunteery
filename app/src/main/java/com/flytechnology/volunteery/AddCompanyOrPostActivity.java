package com.flytechnology.volunteery;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class AddCompanyOrPostActivity extends AppCompatActivity {


    Button addCompany, postActivigty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_company_or_post);


        init();
        buttonHandling();
        backButton();
    }

    public void init(){
        addCompany = (Button) findViewById(R.id.bt_add_company);
        postActivigty = (Button) findViewById(R.id.bt_post_activity);
    }

    public void buttonHandling(){

        //add company button handling
        addCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addCompany = new Intent(AddCompanyOrPostActivity.this, RegistringJobPoster.class);
                startActivity(addCompany);
                finish();
            }
        });//end of add compnay button listener


        //post actvity button handler
        //add company button handling
        postActivigty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent postActivity = new Intent(AddCompanyOrPostActivity.this, PostingJob.class);
                startActivity(postActivity);
                finish();
            }
        });//end of post actvity
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();


        Intent adminControl = new Intent(AddCompanyOrPostActivity.this, ManageCompanies.class);
        startActivity(adminControl);
        finish();
    }

    public void backButton(){
        ImageView back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent adminControl = new Intent(AddCompanyOrPostActivity.this, ManageCompanies.class);
                startActivity(adminControl);
                finish();
            }
        });

    }
}
