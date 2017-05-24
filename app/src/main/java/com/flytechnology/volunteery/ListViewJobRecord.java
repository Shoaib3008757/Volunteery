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
import android.widget.ImageView;
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

public class ListViewJobRecord extends AppCompatActivity {


    ProgressBar bar;
    String URLRESULT;
    ListView list_view_report;

    private static final String url = "http://paireme.com/indexjobrecord.php";
    String myJSON;
    JSONArray jobArray = null;
    ArrayList<HashMap<String, String>> jobList;

    int checkUrl = 0; // 1 means url to view jobs and 2 mean url to aplly jobs

    int state = 1;
    private static final String TAG_RESULTS = "result";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_job_record);


        init();
        backButton();
        listViewItemClick();

    }


    public void init() {

        list_view_report = (ListView) findViewById(R.id.lv_volunteer_report);
        bar = (ProgressBar) findViewById(R.id.progressBar);
        jobList = new ArrayList<HashMap<String, String>>();
        //callisng web api

        AsyncDataClass asyncDataClass = new AsyncDataClass();
        asyncDataClass.execute(url, "searchrecord");


    }


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

                if (state==1) {
                    nameValuePairs.add(new BasicNameValuePair("searchrecord", params[1]));

                }
                if (state==2){

                    nameValuePairs.add(new BasicNameValuePair("id", params[1]));
                    nameValuePairs.add(new BasicNameValuePair("status", params[1]));


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
            if (result.equals("Completed successfully")) {
                Toast.makeText(ListViewJobRecord.this, "" + result, Toast.LENGTH_SHORT).show();
            }

            if (result.equals("") || result == null) {

                Toast.makeText(ListViewJobRecord.this, "Server connection failed",
                        Toast.LENGTH_LONG).show();

                return;

            }


            int jsonResult = returnParsedJsonObject(result);

            Log.e("TAG", "VVVVVVV " + result);

         //   Toast.makeText(ListViewJobRecord.this, ""+result, Toast.LENGTH_SHORT).show();


            String ress = returnParsedJsonObjectString(result);

            Log.e("TAGE", "VVVVVVV 1 1 1 " + ress);

            Log.e("TAGE", "VVVVVVV122 " + jsonResult);
            URLRESULT = result;

            showList(result);

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

                for (int i = 0; i < jobArray.length(); i++) {
                    JSONObject c = jobArray.getJSONObject(i);
                    String studentName = c.getString("studentname");
                    String jobtitle = c.getString("jobtitle");
                    String companyName = c.getString("companyname");
                    String id = c.getString("id");


                    HashMap<String, String> jobs = new HashMap<String, String>();

                    jobs.put("studentname", studentName);
                    jobs.put("jobtitle", jobtitle);
                    jobs.put("companyname", companyName);
                    jobs.put("id", id);

                    jobList.add(jobs);

                }


                ListAdapter adapter = new SimpleAdapter(
                        ListViewJobRecord.this, jobList, R.layout.list_volunteer_report,
                        new String[]{"studentname", "jobtitle", "companyname", "id"},
                        new int[]{R.id.list_report_student_name,
                                R.id.list_report_job_title, R.id.list_report_company_name, R.id.tvid}
                );


                list_view_report.setAdapter(adapter);



            } catch (JSONException e) {
                e.printStackTrace();

            }
        }

    public void backButton(){
        ImageView back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent adminControl = new Intent(ListViewJobRecord.this, AdminControl.class);
                startActivity(adminControl);
                finish();
            }
        });

    }


    public void listViewItemClick(){

        list_view_report.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long ids) {
                TextView tvid = (TextView)view.findViewById(R.id.tvid);
                final String id = tvid.getText().toString();

                final Dialog deleteDialog = new Dialog(ListViewJobRecord.this);
                deleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                deleteDialog.setContentView(R.layout.company_delete_alert_dialog);
                TextView dComapnyName = (TextView)deleteDialog.findViewById(R.id.tv_alert_delete_company);
                Button dConfirmButtom = (Button)deleteDialog.findViewById(R.id.bt_delete_comfirm);
                Button dCancel = (Button)deleteDialog.findViewById(R.id.bt_delete_cancel);

                dComapnyName.setText(" Activity Complete ");

                dCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteDialog.dismiss();
                    }
                });//end of cancel button

                dConfirmButtom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        state = 2;

                        AsyncDataClass asyncDataClass = new AsyncDataClass();
                        asyncDataClass.execute(url, id, "1");

                        deleteDialog.dismiss();
                    }
                });

                deleteDialog.show();
            }
        });

    }//end of onItemClick


}