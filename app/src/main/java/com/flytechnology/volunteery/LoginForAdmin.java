package com.flytechnology.volunteery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginForAdmin extends AppCompatActivity {


    EditText etEmail, etPassword;
    Button bt_login;

    SharedPreferences sharedPreferences;

    private static final String adminEmail = "admin@gmail.com";
    private static final String adminPassword = "admin12admin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_for_admin);

        init();
        loginClickHandler();

    }

    public void init(){
        etEmail = (EditText) findViewById(R.id.et_email);
        etPassword = (EditText) findViewById(R.id.et_password);
        bt_login = (Button) findViewById(R.id.bt_login);

    }
    public void loginClickHandler(){


        //handling clike for login button
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 String email = etEmail.getText().toString();
                 String password = etPassword.getText().toString();
                 String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (email.length()==0){

                    Toast.makeText(LoginForAdmin.this, "Plese Enter Email", Toast.LENGTH_SHORT).show();
                }
                else  if (!email.matches(emailPattern)){
                    Toast.makeText(LoginForAdmin.this, "Plese Enter Valid  Email", Toast.LENGTH_SHORT).show();
                }
                else if (password.length()<3){

                    Toast.makeText(LoginForAdmin.this, "Password Should atleast 4 charaters leangth", Toast.LENGTH_SHORT).show();
                }
                else {

                    //stratinh view

                    if (email.equals(adminEmail) && password.equals(adminPassword)){

                        Intent adminControl = new Intent(LoginForAdmin.this, AdminControl.class);
                        startActivity(adminControl);

                        sharedPreferences = getSharedPreferences("admin", 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("admin", "admin");
                        editor.commit();

                        finish();
                    }else {

                         Toast.makeText(LoginForAdmin.this, "Email Or Password is Invalid", Toast.LENGTH_SHORT).show();

                    }

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent adminControl = new Intent(LoginForAdmin.this, MainActivity.class);
        startActivity(adminControl);
        finish();

    }
}