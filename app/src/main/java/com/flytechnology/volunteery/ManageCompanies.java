package com.flytechnology.volunteery;

import android.app.Dialog;
import android.content.Intent;
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

public class ManageCompanies extends AppCompatActivity {

    ProgressBar bar;
    String URLRESULT;
    ListView list_view_report;
    ImageView addCompany;

    int tagCompany = 0;

    private static final String url = "http://paireme.com/companyindex.php";
    String myJSON;
    JSONArray jobArray = null;
    ArrayList<HashMap<String, String>> jobList;

    int checkUrl = 0; // 1 means url to view jobs and 2 mean url to aplly jobs

    private static final String TAG_RESULTS = "result";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_companies);


        init();
        listViewItemClickLister();
        addCompanyButtonclick();
        backButton();

    }

    public void init() {

        list_view_report = (ListView) findViewById(R.id.lv_manage_company);
        addCompany = (ImageView) findViewById(R.id.im_add_company);
        bar = (ProgressBar) findViewById(R.id.progressBar);
        jobList = new ArrayList<HashMap<String, String>>();
        //callisng web api

        tagCompany = 1;

        AsyncDataClass asyncDataClass = new AsyncDataClass();
        asyncDataClass.execute(url, "searchrecord");


    }

    //add company button call
    public void addCompanyButtonclick(){
        addCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addCompany = new Intent(ManageCompanies.this, AddCompanyOrPostActivity.class);
                addCompany.putExtra("admin", "cadmin");
                startActivity(addCompany);
                finish();
            }
        });
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

                if (tagCompany==1) {

                    nameValuePairs.add(new BasicNameValuePair("searchrecord", params[1]));
                }
                if (tagCompany==2){
                    nameValuePairs.add(new BasicNameValuePair("email", params[1]));
                    nameValuePairs.add(new BasicNameValuePair("delete", params[2]));nameValuePairs.add(new BasicNameValuePair("delete", params[2]));

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

                    Toast.makeText(ManageCompanies.this, "Server connection failed",
                            Toast.LENGTH_LONG).show();

                    return;

                }




                int jsonResult = returnParsedJsonObject(result);

                Log.e("TAG", "VVVVVVV " + result);

            if (tagCompany==1) {

                String ress = returnParsedJsonObjectString(result);

                Log.e("TAGE", "VVVVVVV 1 1 1 " + ress);

                Log.e("TAGE", "VVVVVVV122 " + jsonResult);
                URLRESULT = result;

                showList(result);
            }

            if (tagCompany==2){

                if(result.equals("company deleted successfully")){
                    Toast.makeText(ManageCompanies.this, "company deleted successfully" , Toast.LENGTH_SHORT).show();

                    Intent startAgain = new Intent(ManageCompanies.this, ManageCompanies.class);
                    startActivity(startAgain);
                    finish();
                }
            }
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
                String companyname = c.getString("companyname");
                String companytype = c.getString("companytype");
                String companyEmail = c.getString("email");


                HashMap<String, String> jobs = new HashMap<String, String>();

                jobs.put("companyname", companyname);
                jobs.put("companytype", companytype);
                jobs.put("companyemail", companyEmail);

                Log.e("TAG", "companyname " + companyname);
                Log.e("TAG", "companytype " + companytype);
                Log.e("TAG", "companyEmail " + companyEmail);



                jobList.add(jobs);

            }


            ListAdapter adapter = new SimpleAdapter(
                    ManageCompanies.this, jobList, R.layout.manage_companies_list,
                    new String[]{"companyname", "companytype", "companyemail"},
                    new int[]{R.id.list_item_company_name, R.id.list_item_compay_type, R.id.company_email}
            );


            list_view_report.setAdapter(adapter);



        } catch (JSONException e) {
            e.printStackTrace();

        }
    }


    public void listViewItemClickLister(){

        list_view_report.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView listCompanyname = (TextView)view.findViewById(R.id.list_item_company_name);
                TextView listCompanyType = (TextView)view.findViewById(R.id.list_item_compay_type);
                TextView listCompanyEmail = (TextView)view.findViewById(R.id.company_email);

                String mcompany = listCompanyname.getText().toString();
                String mCompanyType = listCompanyType.getText().toString();
               final String mCompanyEmail = listCompanyEmail.getText().toString();

                Log.e("TAG", "Com Email: " + mCompanyEmail);

                final Dialog deleteDialog = new Dialog(ManageCompanies.this);
                deleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                deleteDialog.setContentView(R.layout.company_delete_alert_dialog);
                TextView dComapnyName = (TextView)deleteDialog.findViewById(R.id.tv_alert_delete_company);
                Button dConfirmButtom = (Button)deleteDialog.findViewById(R.id.bt_delete_comfirm);
                Button dCancel = (Button)deleteDialog.findViewById(R.id.bt_delete_cancel);

                dComapnyName.setText("Do you Really Want To Remove " +"'"+ mcompany + "'");

                dCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteDialog.dismiss();
                    }
                });//end of cancel button

                dConfirmButtom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        tagCompany = 2;

                        AsyncDataClass asyncDataClass = new AsyncDataClass();
                        asyncDataClass.execute(url, mCompanyEmail, "delete");

                        deleteDialog.dismiss();
                    }
                });

                deleteDialog.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        Intent i = new Intent(ManageCompanies.this, AdminControl.class);
        startActivity(i);
    }

    public void backButton(){
        ImageView back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                Intent i = new Intent(ManageCompanies.this, AdminControl.class);
                startActivity(i);
            }
        });

    }

}