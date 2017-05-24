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
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class PostingJob extends AppCompatActivity {

    EditText ed_jobTitle, ed_jobType, ed_jobDescription, ed_noOfVecancies;
    EditText ed_companyName, ed_companyEmail;
    Button bt_post;
    TextView title;

    WebView webView;

    String URLRESULT;
    private final String postJobUrl = "http://paireme.com/jobsindex.php";
    private final String pushNotificationURL = "http://paireme.com/pushtostudents.php";
    String company_Name;
    String email;

    private static final String TAG_RESULTS = "success";
    SharedPreferences sharedPreferences;


    private ProgressBar bar;

    String myJSON;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting_job);

        init();
        buttonPst();
        backButton();

    }

   public void init(){

       ed_jobTitle = (EditText) findViewById(R.id.et_jobTitle);
       ed_jobType = (EditText) findViewById(R.id.et_jobType);
       ed_jobDescription = (EditText) findViewById(R.id.et_jobDescription);
       ed_noOfVecancies = (EditText) findViewById(R.id.et_noOfVecancies);
       ed_companyName = (EditText) findViewById(R.id.et_company_name);
       ed_companyEmail = (EditText) findViewById(R.id.et_company_email);
       title = (TextView) findViewById(R.id.tv_title);
       bt_post = (Button) findViewById(R.id.bt_post);
       webView = (WebView) findViewById(R.id.webViewForPush);

       bar = (ProgressBar) findViewById(R.id.progressBar);

       sharedPreferences = getSharedPreferences("company", 0);
       String companyName = sharedPreferences.getString("companyname", null);
       String mail = sharedPreferences.getString("email", null);

       if (companyName!=null){

           title.setText("Welcome " + companyName);
           company_Name = companyName;
           email = mail;


       }
   }

    //setting post click listener
    public void buttonPst(){



        bt_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 String jobTitle = ed_jobTitle.getText().toString();
                 String jobType = ed_jobType.getText().toString();
                 String jobDescription = ed_jobDescription.getText().toString();
                 String jobVecancy = ed_noOfVecancies.getText().toString();
                String companyName = ed_companyName.getText().toString();
                String companyEmail = ed_companyEmail.getText().toString();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (jobTitle.isEmpty() || jobType.isEmpty()
                        || jobDescription.isEmpty() || jobVecancy.isEmpty()){

                    Toast.makeText(PostingJob.this, "Look Like Some Field Missing", Toast.LENGTH_SHORT).show();
                }else if(companyName.length()==0){
                    Toast.makeText(PostingJob.this, "Look Like Some Field Missing", Toast.LENGTH_SHORT).show();
                }else if (companyEmail.length()==0){
                    Toast.makeText(PostingJob.this, "Look Like Some Field Missing", Toast.LENGTH_SHORT).show();
                }
                else if (!companyEmail.matches(emailPattern)){
                    Toast.makeText(PostingJob.this, "Please Enter Valid Email", Toast.LENGTH_SHORT).show();
                }
                else {

                    //posting data
                    AsyncDataClass asyn = new AsyncDataClass();
                    asyn.execute(postJobUrl, companyName, jobTitle, jobType, jobDescription, jobVecancy, companyEmail);
                }
            }
        });

    }


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
                nameValuePairs.add(new BasicNameValuePair("jobtitle", params[2]));
                nameValuePairs.add(new BasicNameValuePair("jobtype", params[3]));
                nameValuePairs.add(new BasicNameValuePair("description", params[4]));
                nameValuePairs.add(new BasicNameValuePair("vecancy", params[5]));
                nameValuePairs.add(new BasicNameValuePair("email", params[6]));

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

                Toast.makeText(PostingJob.this, "Server connection failed",
                        Toast.LENGTH_LONG).show();

                return;

            }




            int jsonResult = returnParsedJsonObject(result);

            Log.e("TAG", "VVVVVVV " + result);


            String ress = returnParsedJsonObjectString(result);

            Log.e("TAGE", "VVVVVVV 1 1 1 " + ress);

            Log.e("TAGE", "VVVVVVV122 " + jsonResult);
            URLRESULT = result;



            if(jsonResult == 1){

                bar.setVisibility(View.GONE);

                Toast.makeText(PostingJob.this, "Job faile to post", Toast.LENGTH_SHORT).show();


            }else {
                Toast.makeText(PostingJob.this, "Job Posted Successful", Toast.LENGTH_SHORT).show();


                //calling for push
                /*PushNotify asyncDataClass = new PushNotify();
                asyncDataClass.execute(pushNotificationURL);*/
                webView.loadUrl(pushNotificationURL);



                finish();
                Intent adminControl = new Intent(PostingJob.this, PostJob.class);
                startActivity(adminControl);



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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent adminControl = new Intent(PostingJob.this, AdminControl.class);
        startActivity(adminControl);
        finish();

    }
    public void backButton(){
        ImageView back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent adminControl = new Intent(PostingJob.this, AdminControl.class);
                startActivity(adminControl);
                finish();
            }
        });


}

    //public pushnotificatino call usrl task

    private class PushNotify extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            Log.e("TAG", "TEST CALL do in backgroudn");
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("TAG", "TEST CALL in postExecute");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.e("TAG", "TEST CALL in postExecute");
        }
    }//end of push notifiy class

}


