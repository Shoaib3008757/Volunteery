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
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
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

public class ViewStudentProfile extends AppCompatActivity {

    TextView tv_ID, tv_Name;
    TextView tvHoursUsed, tvHoursLef;
    Button btLogout;

    String stEmail;
    JSONArray jobArray = null;

    String URLRESULT;
    private final String studentLoginUrl = "http://paireme.com/indexjobrecord.php";

    private static final String TAG_RESULTS = "result";
    SharedPreferences sharedPreferences;

    private ProgressBar bar;

    String myJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_student_profile);

        init();
        logoutClickListner();
    }

    public void init(){


        tv_ID = (TextView) findViewById(R.id.tv_studentID);
        tv_Name = (TextView) findViewById(R.id.tv_studentname);
        tvHoursUsed = (TextView) findViewById(R.id.tv_hour_used);
        tvHoursLef = (TextView) findViewById(R.id.tv_hour_left);
        btLogout = (Button) findViewById(R.id.bt_logout);
        Intent i = getIntent();

        bar = (ProgressBar) this.findViewById(R.id.progressBar);

        String studentId = i.getExtras().getString("stid", null);
        String studentName = i.getExtras().getString("stname", null);
        String studentEmail = i.getExtras().getString("stemail", null);

        Log.e("TAG", "ID " + studentId);

        if (studentId!=null){

            tv_ID.setText(studentId);
            tv_Name.setText(studentName);

            stEmail = studentEmail;

            //stratinh view
         AsyncDataClass asyncDataClass = new AsyncDataClass();
            asyncDataClass.execute(studentLoginUrl, stEmail);
        }
    }//end of init function


    public void logoutClickListner(){

        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


               SharedPreferences sharedPreferences = getSharedPreferences("student", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                finish();

                Intent mainActivty = new Intent(ViewStudentProfile.this, MainActivity.class);
                startActivity(mainActivty);
                finish();
            }
        });
    }//end of logoutbutton


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


                nameValuePairs.add(new BasicNameValuePair("stemail", params[1]));

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

                Toast.makeText(ViewStudentProfile.this, "Server connection failed",
                        Toast.LENGTH_LONG).show();

                return;

            }


            int jsonResult = returnParsedJsonObject(result);

            Log.e("TAG", "VVVVVVV " + result);


            String ress = returnParsedJsonObjectString(result);

            Log.e("TAGE", "VVVVVVV 1 1 1 " + ress);


            Log.e("TAGE", "VVVVVVV122 " + jsonResult);
            URLRESULT = result;
            showList(result);


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
    protected void showList(String result) {

            try {

                JSONObject jsonObj = new JSONObject(result);
                Log.e("TAGE", "RESULT " + result.toString());
                String rel = jsonObj.getString(TAG_RESULTS);
                Log.e("TAGE", "RESULT 123 " + rel);

                jobArray = jsonObj.getJSONArray(TAG_RESULTS);


                if (jobArray.toString().equals("[]")) {
                    Log.e("TAG", "SSSSTT Emtpy");
                    Toast.makeText(this, "No Record Found", Toast.LENGTH_SHORT).show();
                }

                jobArray = jsonObj.getJSONArray(TAG_RESULTS);

                JSONObject c1 = jobArray.getJSONObject(0);
                final String hoursLeft = c1.getString("left");
               final String hoursUsed = c1.getString("used");

                    Log.e("TAG", "hours Left " + hoursLeft);
                    Log.e("TAG", "hours Used " + hoursUsed);

                 tvHoursUsed.setText(""+hoursUsed);
                tvHoursLef.setText(""+hoursLeft);




            } catch (JSONException e) {
                e.printStackTrace();

            }

    }
}
