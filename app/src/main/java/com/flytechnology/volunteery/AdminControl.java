package com.flytechnology.volunteery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AdminControl extends AppCompatActivity {


    Button btManageCompany;
    Button btManageStudents;
    Button btLogOut;
    Button btViewReport;
    Button btManageActivities;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_control);

        inin();
        manageCompanies();
        mangeStudents();
        btViewReportHandler();
        logOutHandler();
        backButton();
        ManageActivities();
    }

    public void inin(){
        btManageStudents = (Button) findViewById(R.id.bt_mange_students);
        btManageCompany= (Button) findViewById(R.id.bt_manage_companies);
        btLogOut = (Button) findViewById(R.id.bt_logout);
        btViewReport = (Button) findViewById(R.id.bt_view_report);
        btManageActivities = (Button) findViewById(R.id.bt_manage_activites);


    }


    public void logOutHandler(){

        btLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sharedPreferences = getSharedPreferences("admin", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();

                Intent mainActivty = new Intent(AdminControl.this, MainActivity.class);
                startActivity(mainActivty);
                finish();
            }
        });

    }//end of logoutHandler


    public void manageCompanies(){

        btManageCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent viewReport = new Intent(AdminControl.this, ManageCompanies.class);
                startActivity(viewReport);
                finish();

            }
        });
    }


    public void mangeStudents(){
        btManageStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent viewReport = new Intent(AdminControl.this, ManageStudents.class);
                startActivity(viewReport);
                finish();

            }
        });
    }

    public void btViewReportHandler(){
        btViewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent viewReport = new Intent(AdminControl.this, ListViewJobRecord.class);
                startActivity(viewReport);
            }
        });
    }

    public void backButton(){
        ImageView back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent adminControl = new Intent(AdminControl.this, MainActivity.class);
                startActivity(adminControl);
                finish();
            }
        });

    }

    public void ManageActivities(){
        btManageActivities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mangeActvities = new Intent(AdminControl.this, ManageActivities.class);
                startActivity(mangeActvities);

            }
        });
    }
}
