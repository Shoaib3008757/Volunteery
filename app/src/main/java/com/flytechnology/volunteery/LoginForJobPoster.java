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
import android.widget.ProgressBar;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoginForJobPoster extends AppCompatActivity {


    EditText etEmail, etPassword;
    TextView tv_regisratio;
    Button bt_login;

    boolean foo = false;

    String URLRESULT;
    private final String companyLoginUrl = "http://paireme.com/companyindex.php";

    private static final String TAG_RESULTS = "result";
    SharedPreferences sharedPreferences;

    private ProgressBar bar;

    String myJSON;
    JSONArray company = null;
    ArrayList<HashMap<String, String>> studentList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_for_job_poster);

        init();
        startingRegisrationActivity();
        loginClickHandler();

    }

    public void init() {
        etEmail = (EditText) findViewById(R.id.et_email);
        etPassword = (EditText) findViewById(R.id.et_password);
        tv_regisratio = (TextView) findViewById(R.id.tv_register);
        bt_login = (Button) findViewById(R.id.bt_login);


        bar = (ProgressBar) this.findViewById(R.id.progressBar);
    }

    public void startingRegisrationActivity(){
        tv_regisratio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activityRegiterPoster = new Intent(LoginForJobPoster.this, RegistringJobPoster.class);
                startActivity(activityRegiterPoster);
                finish();
            }
        });
    }

    public void loginClickHandler(){
        final String email = etEmail.getText().toString();
        final String password = etPassword.getText().toString();
        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        //handling clike for login button
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (email.length()==0){

                    Toast.makeText(LoginForJobPoster.this, "Plese Enter Email", Toast.LENGTH_SHORT).show();
                }
                else  if (!email.matches(emailPattern)){
                    Toast.makeText(LoginForJobPoster.this, "Plese Enter Valid  Email", Toast.LENGTH_SHORT).show();
                }
                else if (password.length()==0){

                    Toast.makeText(LoginForJobPoster.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                }
                else {

                    //stratinh view
                    AsyncDataClass asyncDataClass = new AsyncDataClass();
                    asyncDataClass.execute(companyLoginUrl, email, password);
                    Log.e("TAG" , "SSS EMAIL " + email);
                    Log.e("TAG" , "SSS password " + password);

                    //Toast.makeText(LoginForJobPoster.this, "Login Soon", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }//end of login button



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

                Toast.makeText(LoginForJobPoster.this, "Server connection failed",
                        Toast.LENGTH_LONG).show();

                return;

            }




            int jsonResult = returnParsedJsonObject(result);

            Log.e("TAG", "VVVVVVV " + result);


            String ress = returnParsedJsonObjectString(result);

            Log.e("TAGE", "VVVVVVV 1 1 1 " + ress);

            if (ress!=null){
                if (ress.equals("1")){
                    Toast.makeText(LoginForJobPoster.this, "Your Status Updated...", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            Log.e("TAGE", "VVVVVVV122 " + jsonResult);
            URLRESULT = result;
            showList(result);


            if (!foo){
                Toast.makeText(LoginForJobPoster.this, "Email Or password Is Invalid", Toast.LENGTH_SHORT).show();
            }


            if(jsonResult == 0){

                bar.setVisibility(View.GONE);


              //  Toast.makeText(LoginForJobPoster.this, "Email or Passwor is Invalid", Toast.LENGTH_SHORT).show();


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



            if (rel.equals("[]")){
                Log.e("TAG", "SSSSTT Emtpy");
                Toast.makeText(this, "No Record Found", Toast.LENGTH_SHORT).show();

            }else {

                company = jsonObj.getJSONArray(TAG_RESULTS);

                JSONObject c1 = company.getJSONObject(0);
                final String companyName = c1.getString("companyname");
                String companyType = c1.getString("companytype");
                String email = c1.getString("email");
                String password = c1.getString("password");

                foo = true;

                Log.e("TAG", "Company Name " + companyName);
                Log.e("TAG", "Company Type " + companyType);
                Log.e("TAG", "Email " + email);
                Log.e("TAG", "Password " + password);



                sharedPreferences = getSharedPreferences("company", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.clear();
                editor.putString("companyname", companyName);
                editor.putString("companytype", companyType);
                editor.putString("email", email);
                editor.putString("password", password);
                editor.commit();

                finish();


                Intent intent = new Intent(LoginForJobPoster.this, PostJob.class);
                startActivity(intent);

                Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show();

                //   statusKey = true;

            }


        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent adminControl = new Intent(LoginForJobPoster.this, MainActivity.class);
        startActivity(adminControl);
        finish();

    }
}