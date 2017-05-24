package com.flytechnology.volunteery;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
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

public class AdminViewCompanyActivities extends AppCompatActivity {

    ProgressBar bar;
    String URLRESULT;

    TextView tvTotalActivities;

    String studentEmail;
    String studentFullname;
    String studentDegreeTitle;
    String studenID;
    String nameOfCompany;
    String titleOfJob;
    String mCompanyEmail;

    String housrs;


    private static final String url = "http://paireme.com/jobsindex.php";

    String myJSON;
    JSONArray jobArray = null;
    ArrayList<HashMap<String, String>> jobList;
    ListView list_view_report;

    int checkUrl = 1; // 1 means url to view jobs and 2 mean url to aplly jobs

    private static final String TAG_RESULTS = "result";
    private static final String TAG_RESULTSAplly = "success";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_company_activities);
        init();
        listViewItemClickHandler();
    }

    public void init() {

        jobList = new ArrayList<HashMap<String, String>>();
        bar = (ProgressBar) findViewById(R.id.progressBar);
        list_view_report = (ListView) findViewById(R.id.lv_manage_company);

        tvTotalActivities = (TextView)findViewById(R.id.tv_totalactivity);
        //callisng web api


        SharedPreferences sharedPreferences = getSharedPreferences("student", 0);
        String stEmail = sharedPreferences.getString("email", null);
        String firstName = sharedPreferences.getString("firstname", null);
        String stID = sharedPreferences.getString("stid", null);
        String degreeTitle = sharedPreferences.getString("degreetitle", null);
        if (firstName != null) {


            studenID = stID;
            studentFullname = firstName;
            studentEmail = stEmail;
            studentDegreeTitle = degreeTitle;

        }


        Intent i = getIntent();
        String companyName = i.getExtras().getString("companyname", null);
        if (companyName!=null) {

            Log.e("TAG", "TTT " + companyName );

            TextView tvCompanyName = (TextView)findViewById(R.id.tv_volunteer_record);
            tvCompanyName.setText(companyName);


           AsyncDataClass asyncDataClass = new AsyncDataClass();
            asyncDataClass.execute(url, companyName);
        }


    }//end of init


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

                if (checkUrl==1) {

                    nameValuePairs.add(new BasicNameValuePair("companyactivities", params[1]));
                }

                if (checkUrl==2) {

                    nameValuePairs.add(new BasicNameValuePair("id", params[1]));
                }

                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpClient.execute(httpPost);

                jsonResult = inputStreamToString(response.getEntity().getContent()).toString();


                Log.d("TAG", "SSSSSSS " + jsonResult);


            } catch (ClientProtocolException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();

            } catch (ArrayIndexOutOfBoundsException e) {
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


            Log.e("RESULT ", " Result is " + result);


            if (result.equals("") || result == null) {

                Toast.makeText(AdminViewCompanyActivities.this, "Server connection failed",
                        Toast.LENGTH_LONG).show();

                return;

            }


            int jsonResult = returnParsedJsonObject(result);

            Log.e("TAG", "VVVVVVV " + result);


            String ress = returnParsedJsonObjectString(result);

            Log.e("TAGE", "VVVVVVV 1 1 1 " + ress);

            Log.e("TAGE", "VVVVVVV122 " + jsonResult);
            URLRESULT = result;

            if (checkUrl==1) {
                showList(result);
            }

            if (checkUrl==2){
                Toast.makeText(AdminViewCompanyActivities.this, "Activity Deleted Successfully", Toast.LENGTH_SHORT).show();

                finish();


                bar.setVisibility(View.GONE);

                myJSON = URLRESULT;

            }
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

    private int returnParsedJsonObject(String result) {

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


    private String returnParsedJsonObjectString(String result) {

        JSONObject resultObject = null;

        String returnedResult = null;

        try {

            if (checkUrl==1) {
                resultObject = new JSONObject(result);

                returnedResult = resultObject.getString("result");
            }

            if (checkUrl==2){


                resultObject = new JSONObject(result);
                returnedResult = resultObject.getString("already");

            }

        } catch (JSONException e) {

            e.printStackTrace();

        }

        return returnedResult;

    }

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    protected void showList(String result) {

        if (checkUrl==1){
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

                for (int i = 0; i < jobArray.length(); i++) {
                    JSONObject c = jobArray.getJSONObject(i);
                    String companyName = c.getString("companyname");
                    String companyTitle = c.getString("jobtitle");
                    String jobtype = c.getString("jobtype");
                    String description = c.getString("description");
                    String vecancy = c.getString("vecancy");
                    String email = c.getString("email");
                    String datetime = c.getString("update_at");
                    String id = c.getString("id");

                    Log.e("TAG", "DateTime " + datetime);
                    String[] d = datetime.split(" ");
                    String date = d[0];
                    Log.e("TAG", "DateTime " + date);



                    HashMap<String, String> jobs = new HashMap<String, String>();

                    jobs.put("companyname", companyName);
                    jobs.put("compaytitle", companyTitle);
                    jobs.put("jobtype", jobtype);
                    jobs.put("description", description);
                    jobs.put("vecany", vecancy);
                    jobs.put("email", email);
                    jobs.put("update_at", date);
                    jobs.put("id", id);


                    Log.e("TAG", "ID ID ID " + id);

                    jobList.add(jobs);


                }

                int siz =  jobList.size();



                Log.e("TAG", "Sise " + siz);
                //Toast.makeText(this, "total activites " + siz, Toast.LENGTH_SHORT).show();


                ListAdapter adapter = new SimpleAdapter(
                        AdminViewCompanyActivities.this, jobList, R.layout.company_activities_list_for_admin,
                        new String[]{"companyname", "compaytitle", "description", "vecany", "email", "jobtype", "update_at", "id"},
                        new int[]{R.id.tv_company_name,
                                R.id.tv_job_title, R.id.tv_job_description, R.id.tv_no_vecany, R.id.tv_company_email, R.id.tv_jobtype, R.id.tv_post_date, R.id.tv_id}
                );


                list_view_report.setAdapter(adapter);
                tvTotalActivities.setText(""+siz);





            } catch (JSONException e) {
                e.printStackTrace();

            }
        }

        if (checkUrl==2){


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
                    Toast.makeText(this, "You Have Already Applied", Toast.LENGTH_SHORT).show();
                }else if (successResult.equals("1")){
                    Toast.makeText(this, "Activity Applied Successfully", Toast.LENGTH_SHORT).show();

                }


            } catch (JSONException e) {
                e.printStackTrace();

            }
        }

    }

    public void listViewItemClickHandler() {

        list_view_report.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

                final TextView jobTitle = (TextView) view.findViewById(R.id.tv_job_title);
                TextView companyName = (TextView) view.findViewById(R.id.tv_company_name);
                final TextView jobDescription = (TextView) view.findViewById(R.id.tv_job_description);
                TextView vecancy = (TextView) view.findViewById(R.id.tv_no_vecany);
                TextView jobType = (TextView) view.findViewById(R.id.tv_jobtype);
                TextView companyEmail = (TextView) view.findViewById(R.id.tv_company_email);

                final TextView tvID = (TextView)view.findViewById(R.id.tv_id);

                final TextView viewJobs = (TextView) view.findViewById(R.id.bt_list_view_job);

                final String jJobTitle = jobTitle.getText().toString();
                final String jCompanyName = companyName.getText().toString();
                final String jDescrioption = jobDescription.getText().toString();
                final String jVecancy = vecancy.getText().toString();
                final String jJobType = jobType.getText().toString();
                final String jComapnyEmail = companyEmail.getText().toString();
                final String jID = tvID.getText().toString();

                mCompanyEmail  = jComapnyEmail;
                housrs = jVecancy;



                viewJobs.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        final Dialog dialog = new Dialog(AdminViewCompanyActivities.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.company_delete_alert_dialog);

                        Button dialogBtApplyNow = (Button) dialog.findViewById(R.id.bt_delete_comfirm);
                        Button dialogBtApplyLater = (Button) dialog.findViewById(R.id.bt_delete_cancel);




                        dialogBtApplyLater.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });


                        dialogBtApplyNow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(AdminViewCompanyActivities.this, "Deleting Activity...", Toast.LENGTH_SHORT).show();


                                checkUrl = 2;

                                Log.e("TAG", "HHHH  " + jID);

                                String deleteurl = "http://paireme.com/jobsindex.php";

                                AsyncDataClass asyn = new AsyncDataClass();
                                asyn.execute(deleteurl, jID);

                                

                                nameOfCompany = jCompanyName;
                                titleOfJob = jJobTitle;




                                dialog.dismiss();
                            }
                        });


                        dialog.show();

                        Log.e("TAG", "1234 " + jJobTitle);
                        Log.e("TAG", "1234 " + jCompanyName);
                        Log.e("TAG", "1234 " + jDescrioption);
                        Log.e("TAG", "1234 " + jVecancy);
                        Log.e("TAG", "1234 " + jJobType);
                        Log.e("TAG", "1234 " + jComapnyEmail);

                    }
                });//end of button Click listner



            }
        });

    }


}