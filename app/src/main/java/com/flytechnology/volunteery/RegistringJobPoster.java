package com.flytechnology.volunteery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RegistringJobPoster extends AppCompatActivity {


    EditText et_companyName, et_companyType, et_email;
    EditText et_password, et_passwordAgain;
    Button bt_registring;

    String reg_id =  "";


    String URLRESULT;
    private final String companyRegisterUrl = "http://paireme.com/companyindex.php";
    private final String pushNotificationURL = "http://paireme.com/pushtostudents.php";

    private static final String TAG_RESULTS = "success";
    SharedPreferences sharedPreferences;

    String pcompanymane, pcompanytype, pemail = "", ppassword = "12345";

    private ProgressBar bar;

    String myJSON;
    JSONArray company = null;
    ArrayList<HashMap<String, String>> studentList;

    String fromAdmin = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registring_job_poster);

        init();
        registringPoster();

        Intent i = getIntent();
        if (i.getExtras()==null){

        }else {
            String isFromAdmin = i.getExtras().getString("admin", null);
            if (isFromAdmin!=null){
                fromAdmin = isFromAdmin;
            }
        }



        displayFirebaseRegId();
        backButton();
    }

    public void init(){

        et_companyName = (EditText) findViewById(R.id.et_company_name);
        et_companyType = (EditText) findViewById(R.id.et_company_type);
        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        et_passwordAgain = (EditText) findViewById(R.id.et_password_again);

        bt_registring = (Button) findViewById(R.id.bt_registration);

        bar = (ProgressBar) this.findViewById(R.id.progressBar);



    }

    //handling click lister

    public void registringPoster(){
        final String companyName = et_companyName.getText().toString();
        final String companyType = et_companyType.getText().toString();
        final String email = et_email.getText().toString();
        final String password = et_password.getText().toString();
        final String passwordAgain = et_passwordAgain.getText().toString();
        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        //setting click for bt registration
        bt_registring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String companyName = et_companyName.getText().toString();
                String companyType = et_companyType.getText().toString();
                String email = et_email.getText().toString();
                String password = et_password.getText().toString();
                String passwordAgain = et_passwordAgain.getText().toString();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (companyName.length()==0 || companyType.length()==0
                        || email.length()==0){

                    Toast.makeText(RegistringJobPoster.this, "Look's like you miss something", Toast.LENGTH_SHORT).show();
                }
                else if (!email.matches(emailPattern)){
                    Toast.makeText(RegistringJobPoster.this, "Please Enter Valid Email", Toast.LENGTH_SHORT).show();
                }
                else {
                    //registratin code

                    AsyncDataClass asyncDataClass = new AsyncDataClass();
                    asyncDataClass.execute(companyRegisterUrl, companyName, companyType, email, ppassword, reg_id);

                    pcompanymane = companyName;
                    pcompanytype = companyType;
                    pemail = email;
                    ppassword = password;


                    //Toast.makeText(RegistringJobPoster.this, "Regiser Soon", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }//end of regiser button



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


                nameValuePairs.add(new BasicNameValuePair("companyname", params[1]));
                nameValuePairs.add(new BasicNameValuePair("companytype", params[2]));
                nameValuePairs.add(new BasicNameValuePair("email", params[3]));
                nameValuePairs.add(new BasicNameValuePair("password", params[4]));
                nameValuePairs.add(new BasicNameValuePair("reg_id", params[5]));

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

                Toast.makeText(RegistringJobPoster.this, "Server connection failed",
                        Toast.LENGTH_LONG).show();

                return;

            }




            int jsonResult = returnParsedJsonObject(result);

            Log.e("TAG", "VVVVVVV " + result);


            String ress = returnParsedJsonObjectString(result);

            Log.e("TAGE", "VVVVVVV 1 1 1 " + ress);

            if (ress!=null){
                if (ress.equals("1")){

                    sharedPreferences = getSharedPreferences("student", 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.clear();
                    editor.putString("companyname", pcompanymane);
                    editor.putString("companytype", pcompanytype);
                    editor.putString("email", pemail);
                    editor.putString("password", ppassword);
                    editor.commit();


                    finish();
                }
            }

            Log.e("TAGE", "VVVVVVV122 " + jsonResult);
            URLRESULT = result;
            showList(result);



            if(jsonResult == 1){

                bar.setVisibility(View.GONE);




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



            returnedResult = resultObject.getString("success");


        } catch (JSONException e) {

            e.printStackTrace();

        }

        return returnedResult;

    }

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    protected void showList(String result){
        try {

            JSONObject jsonObj = new JSONObject(result);
            Log.e("TAGE", "RESULT 11 " + result.toString());
            String successResult =  jsonObj.getString(TAG_RESULTS);
            String checkingUser = jsonObj.getString("already");
            Log.e("TAGE", "RESULT 123 " + successResult);
            Log.e("TAGE", "RESULT 123 " + checkingUser);



            if (checkingUser.equals("null")){
                Log.e("TAG", "SSSSTT Emtpy");
                Toast.makeText(this, "No Record Found", Toast.LENGTH_SHORT).show();

            }else if (checkingUser.equals("1")){
                Toast.makeText(this, "Email Already Registered", Toast.LENGTH_SHORT).show();
            }else if (successResult.equals("1")){
                Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show();



                finish();


                if (fromAdmin.equals("cadmin")){
                    Intent intent = new Intent(RegistringJobPoster.this, AdminControl.class);
                    startActivity(intent);

                }else {
                    Intent intent = new Intent(RegistringJobPoster.this, AdminControl.class);
                    startActivity(intent);
                    finish();
                }


            }
            else {

                company = jsonObj.getJSONArray(TAG_RESULTS);


                JSONObject c1 = company.getJSONObject(0);
                /*final String firstname = c1.getString("firstname");

                Log.e("TAG", "First Name " + firstname);
                Log.e("TAG", "Last Name " + lastname);
                Log.e("TAG", "Email " + email);
                Log.e("TAG", "Password " + password);
                Log.e("TAG", "degreeTitle " + degreeTitle);*/


                sharedPreferences = getSharedPreferences("student", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                /*editor.clear();
                editor.putString("firstname", firstname);
                editor.putString("lastname", lastname);
                editor.putString("email", email);
                editor.putString("degreetitle", degreeTitle);
                editor.commit();

                finish();



                Intent intent = new Intent(LoginForJobSeeker.this, ViewJobs.class);
                startActivity(intent);

                //   statusKey = true;
*/
            }


        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        if (fromAdmin.equals("cadmin")){
            Intent intent = new Intent(RegistringJobPoster.this, AdminControl.class);
            startActivity(intent);

        }else {
            Intent mainActivity = new Intent(RegistringJobPoster.this, AdminControl.class);
            startActivity(mainActivity);
        }
    }

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        reg_id = pref.getString("regId", null);

        Log.e("TAG", "Firebase reg id: " + reg_id);

    }



    public void backButton(){
        ImageView back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent adminControl = new Intent(RegistringJobPoster.this, AddCompanyOrPostActivity.class);
                startActivity(adminControl);
                finish();
            }
        });

    }
}