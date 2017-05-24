package com.flytechnology.volunteery;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class LoginForJobSeeker extends AppCompatActivity {


    EditText etEmail, etPassword;
    TextView tv_regisratio, tv_register1;
    Button bt_login;

    private static final String adminEmail = "admin@gmail.com";
    private static final String adminPassword = "admin12admin";

    boolean foo = false;

    String URLRESULT;
    private final String studentLoginUrl = "http://paireme.com/studentsindex.php";

    private static final String TAG_RESULTS = "result";
    SharedPreferences sharedPreferences;

    private ProgressBar bar;

    String myJSON;
    JSONArray studdnt = null;
    ArrayList<HashMap<String, String>> studentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_for_job_seeker);

        init();
        startingRegisrationActivity();
        loginClickHandler();
        backButton();
        tvRegister1();

    }

    public void init() {
        etEmail = (EditText) findViewById(R.id.et_email);
        etPassword = (EditText) findViewById(R.id.et_password);
        tv_regisratio = (TextView) findViewById(R.id.tv_register);
        tv_register1 = (TextView) findViewById(R.id.tv_register1);
        bt_login = (Button) findViewById(R.id.bt_login);

        bar = (ProgressBar) this.findViewById(R.id.progressBar);
    }

    public void startingRegisrationActivity(){
        tv_regisratio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activityRegiterPoster = new Intent(LoginForJobSeeker.this, RegistringJobSeeker.class);
                startActivity(activityRegiterPoster);
                finish();
            }
        });
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

                    Toast.makeText(LoginForJobSeeker.this, "Plese Enter Email", Toast.LENGTH_SHORT).show();
                }
                else  if (!email.matches(emailPattern)){
                    Toast.makeText(LoginForJobSeeker.this, "Plese Enter Valid  Email", Toast.LENGTH_SHORT).show();
                }
                else if (password.length()==0){

                    Toast.makeText(LoginForJobSeeker.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                }
                else {

                    //stratinh view

                    if (email.equals(adminEmail) && password.equals(adminPassword)) {

                        Intent adminControl = new Intent(LoginForJobSeeker.this, AdminControl.class);
                        startActivity(adminControl);
                        finish();
                    }else {

                        AsyncDataClass asyncDataClass = new AsyncDataClass();
                    asyncDataClass.execute(studentLoginUrl, email, password);
                    Log.e("TAG" , "SSS EMAIL " + email);
                    Log.e("TAG" , "SSS password " + password);

                    //Toast.makeText(LoginForJobSeeker.this, "Login Soon", Toast.LENGTH_SHORT).show();
                }
                }
            }
        });
    }//end of login click hander

//starting asynclass
    private class AsyncDataClass extends AsyncTask<String, Void, String> {

        @Override

        protected String doInBackground(String... params) {

            HttpParams httpParameters = new BasicHttpParams();

            HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);

            HttpConnectionParams.setSoTimeout(httpParameters, 5000);

            HttpClient httpClient = new DefaultHttpClient(httpParameters);

            HttpPost httpPost = new HttpPost(params[0]);


            String jsonResult = "";

            try {



                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);


                nameValuePairs.add(new BasicNameValuePair("email", params[1]));
                nameValuePairs.add(new BasicNameValuePair("password", params[2]));




                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpClient.execute(httpPost);

                jsonResult = inputStreamToString(response.getEntity().getContent()).toString();



                Log.d("TAG", "SSSSSSS " + jsonResult);






            } catch (ClientProtocolException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();

            }catch(ArrayIndexOutOfBoundsException e){
                e.printStackTrace();
            }

            return jsonResult;

        }

        @Override

        protected void onPreExecute() {

            super.onPreExecute();



            bar.setVisibility(View.VISIBLE);
        }

        @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        @Override

        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            bar.setVisibility(View.GONE);



            if(result.equals("") || result == null){

                Toast.makeText(LoginForJobSeeker.this, "Server connection failed",
                        Toast.LENGTH_LONG).show();

                return;

            }




            int jsonResult = returnParsedJsonObject(result);

            Log.e("TAG", "VVVVVVV " + result);


            String ress = returnParsedJsonObjectString(result);

            Log.e("TAGE", "VVVVVVV 1 1 1 " + ress);

            if (ress!=null){
                if (ress.equals("1")){
                    //Toast.makeText(LoginForJobSeeker.this, "Your Status Updated...", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            Log.e("TAGE", "VVVVVVV122 " + jsonResult);
            URLRESULT = result;

            Log.e("TAG", "112233 " + foo);

            showList(result);

            if (!foo){
                Toast.makeText(LoginForJobSeeker.this, "Email Or password Is Invalid", Toast.LENGTH_SHORT).show();
            }



            if(jsonResult == 1){

                bar.setVisibility(View.GONE);

                Toast.makeText(LoginForJobSeeker.this, "Email or Passwor is Invalid", Toast.LENGTH_SHORT).show();


                //do all fatch result funtion here

                // getData();

            }

            myJSON=URLRESULT;

        }

        private StringBuilder inputStreamToString(InputStream is) {

            String rLine = "";

            StringBuilder answer = new StringBuilder();

            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            try {

                while ((rLine = br.readLine()) != null) {

                    answer.append(rLine);

                }

            } catch (IOException e) {

// TODO Auto-generated catch block

                e.printStackTrace();

            }

            return answer;

        }

    }

    private int returnParsedJsonObject(String result){

        JSONObject resultObject = null;

        int returnedResult = 0;

        try {

            resultObject = new JSONObject(result);

            Log.e("TAG", "VALUES " + resultObject);

        } catch (JSONException e) {

            e.printStackTrace();

        }

        return returnedResult;

    }


    private String returnParsedJsonObjectString(String result){

        JSONObject resultObject = null;

        String returnedResult = null;

        try {

            resultObject = new JSONObject(result);



            returnedResult = resultObject.getString("status");


        } catch (JSONException e) {

            e.printStackTrace();

        }

        return returnedResult;

    }

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    protected void showList(String result){
        try {

            JSONObject jsonObj = new JSONObject(result);
            Log.e("TAGE", "RESULT " + result.toString());
            String rel =  jsonObj.getString(TAG_RESULTS);
            Log.e("TAGE", "RESULT 123 " + rel);





            if (rel.equals("null")){
                Log.e("TAG", "SSSSTT Emtpy");
                Toast.makeText(this, "No Record Found", Toast.LENGTH_SHORT).show();

            }else {

                studdnt = jsonObj.getJSONArray(TAG_RESULTS);

                JSONObject c1 = studdnt.getJSONObject(0);
                final String firstname = c1.getString("firstname");
                String lastName = c1.getString("lastname");
                String email = c1.getString("email");
                String password = c1.getString("password");
                String degreeTitle = c1.getString("degreetitle");

                    foo = true;

                Log.e("TAG", "First Name " + firstname);
                Log.e("TAG", "Last Name " + lastName);
                Log.e("TAG", "Email " + email);
                Log.e("TAG", "Password " + password);
                Log.e("TAG", "degreeTitle " + degreeTitle);


                sharedPreferences = getSharedPreferences("student", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.clear();
                editor.putString("firstname", firstname);
                editor.putString("stid", lastName);
                editor.putString("email", email);
                editor.putString("degreetitle", degreeTitle);
                editor.commit();

                finish();


                Intent intent = new Intent(LoginForJobSeeker.this, ViewJobs.class);
                startActivity(intent);

                Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show();

                finish();
             //   statusKey = true;

            }


        } catch (JSONException e) {
            e.printStackTrace();


        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent adminControl = new Intent(LoginForJobSeeker.this, MainActivity.class);
        startActivity(adminControl);
        finish();

    }

    public void backButton(){
        ImageView back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent adminControl = new Intent(LoginForJobSeeker.this, MainActivity.class);
                startActivity(adminControl);
                finish();
            }
        });

    }


    public void tvRegister1(){
        tv_register1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginForJobSeeker.this, RegistringJobSeeker.class);
                startActivity(i);
                finish();
            }
        });
    }
}